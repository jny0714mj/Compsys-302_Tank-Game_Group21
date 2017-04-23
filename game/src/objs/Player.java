/**
 * Player is the basic usable tank in the game.
 * Two control schemes are possible, corresponding to arrow keys and wasd.
 * Player gets key input and prepares movement (which is then checked for collsions in Tank)
 * It has tick and render methods, the render methods deals with rotating images
 */

package objs;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import game.GameHandle;
import game.KeyManager;
import game.Logs;
import gfx.ImageManager;

public class Player extends Tank {
	
	private static final int INIT_STATE = 0;
	
	private BufferedImage skin;
	private BufferedImage[] leftTrackSkins = {ImageManager.loadImage("/imgs/tank/track_back.png"),ImageManager.loadImage("/imgs/tank/track_middle.png"),ImageManager.loadImage("/imgs/tank/track_forward.png")};
	private BufferedImage[] rightTrackSkins; //mirror from leftTrackSkin
	private int controlScheme;
	private KeyManager kM;

	public Player(GameHandle gameHandle, float x, float y, float a, BufferedImage skin, int controllScheme, boolean toRandPos) {
		super(gameHandle, x, y, a, toRandPos, INIT_STATE);
		this.skin = skin;
		this.controlScheme = controllScheme;
		this.rightTrackSkins = flipTrackArray(leftTrackSkins);
	}
	
	public void tick() {
		
		timeAlive = System.currentTimeMillis() - timeGenerated;
		kM = gameHandle.getKeyManager();
		
		// if powerup affect timer > xseconds, remove affect
		if (isPowerupActive) {
			powerupTime = System.currentTimeMillis() - pickupTime;
			if (powerupTime > 15 * 1000) {
				removePowAffects();
			}
				
		}
		
		// wait for all key releases to start tank again
		if (stopped && controlScheme == 1 && !kM.up && !kM.down) {
			start();
		}
		if (stopped && controlScheme == 2 && !kM.w && !kM.s) {
			start();
		}
		// if the player hasn't released in one second, start the tank anyway
		if (stopped && timeAlive > 1000) {
			start();
		}
		
		if (!stopped) {
			dx = dy = da = 0;
			processInput();
			rotate();
			move();
			emptyClip();
		}
	}
	
	// BUG:TODO: when player one moves forward or back player two cannot shoot.
	// FIX: only occurs when numeric keys are used???
	private void processInput() {
		// prepare movements (dx,dy). calls move methods in super
		if (controlScheme == 1) {
			if(kM.up && !kM.down)
				prepMoveForward();
			if(kM.down && !kM.up)
				prepMoveBackward();
			if(kM.left){
				Logs.getInstance().Process("Player 1: Turn Left");
				prepRotateLeft();
			}
			if(kM.right){
				Logs.getInstance().Process("Player 1: Turn Right");
				prepRotateRight();
			}
			if(kM.m){
				Logs.getInstance().Process("Player 1: Shoot Bullet");
				prepBullet();
			}
			
			// LOGS
			if (kM.menuUp)
				Logs.getInstance().Process("Player 1: Move Forward");
			if (kM.menuDown)
				Logs.getInstance().Process("Player 1: Move Backward");
			
		} else if (controlScheme == 2) {
			if(kM.w && !kM.s)
				prepMoveForward();
			if(kM.s && !kM.w)
				prepMoveBackward();
			if(kM.a){
				prepRotateLeft();
				Logs.getInstance().Process("Player 2: Turn Left");
			}
			if(kM.d){
				prepRotateRight();
				Logs.getInstance().Process("Player 2: Turn Right");
			}
			if(kM.one){
				prepBullet();
				Logs.getInstance().Process("Player 2 : Shoot Bullet");
			}
			
			//for logs
			if (kM.p2Up)
				Logs.getInstance().Process("Player 2: Move Forward");
			if (kM.p2Down)
				Logs.getInstance().Process("Player 2: Move Backwards");
		}
		
	}
	
	
	public void render(Graphics g) {
		Graphics2D g2d = (Graphics2D) g.create();
		g2d.rotate(Math.toRadians(a), x + width/2, y + height/2); //rotate about centre
		
		if (gameHandle.isDebug()) {
			g2d.drawRect((int) x, (int) y, 48, 48);
		} else {
			g2d.drawImage(skin, (int) x, (int) y, null);
		}
		
		g2d.drawImage(leftTrackSkins[leftTrackFrame], (int) x, (int) (y), null);
		g2d.drawImage(rightTrackSkins[rightTrackFrame], (int) x, (int) (y + height - 10), null);  // track is 10 pixels wide, img is facing right
		g2d.dispose();
	}
	
	public BufferedImage[] flipTrackArray(BufferedImage[] skins) {
		// flip each image in a bufferedimage array. used to mirror the left track images to right track images
		
		BufferedImage[] flippedSkins = new BufferedImage[3];
		AffineTransform at = AffineTransform.getScaleInstance(1, -1);
		at.translate(0, -skins[0].getHeight());
		AffineTransformOp op = new AffineTransformOp(at, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
		
		for(int i=0; i<3; i++) {
			flippedSkins[i] = op.filter(skins[i], null);
		}
		return flippedSkins;
	}
}
