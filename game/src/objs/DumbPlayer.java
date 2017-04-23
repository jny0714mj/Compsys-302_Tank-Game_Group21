/**
 * Just a player that doesn't take any input
 */

package objs;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.util.Objects;

import game.GameHandle;
import gfx.ImageManager;

public class DumbPlayer extends Tank {
	
	private static final int INIT_STATE = 0;
	
	private BufferedImage skin;
	private BufferedImage[] leftTrackSkins = {ImageManager.loadImage("/imgs/tank/track_back.png"),ImageManager.loadImage("/imgs/tank/track_middle.png"),ImageManager.loadImage("/imgs/tank/track_forward.png")};
	private BufferedImage[] rightTrackSkins; //mirror from leftTrackSkin
	private String body;
	private boolean doLeft,doRight,doUp,doDown,doShoot;

	public DumbPlayer(GameHandle gameHandle, float x, float y, float a, BufferedImage skin, boolean toRandPos) {
		super(gameHandle, x, y, a, toRandPos, INIT_STATE);
		this.skin = skin;
		this.rightTrackSkins = flipTrackArray(leftTrackSkins);
	}

	@Override
	public void tick() {
		dx = dy = da = 0;
		// can safely start the tank any time
		if (stopped) {
			start();
		}
		
		// PREPARE MOVEMENTS (once)
		if (doLeft) { 
			prepRotateLeft();
			doLeft = false;
		}
		if (doRight) {
			prepRotateRight();
			doRight = false;
		}
		if (doUp) {
			prepMoveForward();
		}
		if (doDown) {
			prepMoveBackward();
		}
		if (doShoot) {
			prepBullet();
			doShoot = false;
		}
		
		rotate();
		move();
		emptyClip();
		
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
	
	// GETTERS SETTERS

	public boolean isDoLeft() {
		return doLeft;
	}

	public void setDoLeft(boolean doLeft) {
		this.doLeft = doLeft;
	}

	public boolean isDoRight() {
		return doRight;
	}

	public void setDoRight(boolean doRight) {
		this.doRight = doRight;
	}

	public boolean isDoUp() {
		return doUp;
	}

	public void setDoUp(boolean doUp) {
		this.doUp = doUp;
	}

	public boolean isDoDown() {
		return doDown;
	}

	public void setDoDown(boolean doDown) {
		this.doDown = doDown;
	}

	public boolean isDoShoot() {
		return doShoot;
	}

	public void setDoShoot(boolean doShoot) {
		this.doShoot = doShoot;
	}

}
