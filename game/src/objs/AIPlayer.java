/**
 * AI player is a self moving player, which acts on simple rules.
 * It may be in two different modes-
 * 		blitzmode, where it simply runs and shoots at the player
 * 		smartmode, where it will back away from wall collisions and will grab nearby powerups
 */

package objs;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.util.Random;

import game.GameHandle;
import gfx.ImageManager;

public  class AIPlayer extends Tank {
	private static final int INIT_STATE = 0;
	
	// following finals are probably unneeded as defined in super class. may be used to make AI slower than regular players though
	public static final float DEF_speed = 3f;
	public static final float DEF_fireSpeed = 9f;	//pixels per frame
	public static final float DEF_rotateAngle = 22.5f;
	
	private BufferedImage skin;
	private BufferedImage[] leftTrackSkins = {ImageManager.loadImage("/imgs/tank/track_back.png"),ImageManager.loadImage("/imgs/tank/track_middle.png"),ImageManager.loadImage("/imgs/tank/track_forward.png")};
	private BufferedImage[] rightTrackSkins; //mirror from leftTrackSkin
	protected float dx, dy, da, cx,cy;
	Random rand = new Random();
	boolean right;
	boolean up;
	String mode;
	// TODO: put into new class
	private boolean timing = false;
	private long timeStart;
	private long currentTime;
	private long endTime = 2000;	//2seconds
	private boolean reverseMode;
	private float vision = 400;
	private float powDist;
	private float diffX;
	private float diffY;
	
	
	public AIPlayer(GameHandle gameHandle, float x, float y, float a, BufferedImage skin, boolean toRandPos, String mode) {
		super(gameHandle, x, y, a, toRandPos, INIT_STATE);
		this.skin = skin;
		this.speed = DEF_speed;
		this.fireSpeed = DEF_fireSpeed; 
		this.rotateAngle = DEF_rotateAngle;
		this.mode = mode;
		this.rightTrackSkins = flipTrackArray(leftTrackSkins);
	}
	
	
	public void tick() {
		timeAlive = System.currentTimeMillis() - timeGenerated;
		// stop for one second when respawn
		if (stopped && timeAlive > 1000) {
			start();
		}
		
		if (timing) {
			currentTime = System.currentTimeMillis() - timeStart;
			if (currentTime > endTime/2) {
				reverseMode = false;
			}
			if (currentTime > endTime) {
				timing = false;
			}
		}
		
		if (gameHandle.getKeyManager().switchAIMode) {
			switchMode();
		}
		
		if (mode == "blitzMode") {
			if (!stopped) {
				// once every 500ms...
				//only allow 3 bullets to be alive
				if (timeAlive % 500 < 30 && timeAlive % 500 > 0 && this.getFiredBullets().size() <= 3) {
					prepBullet();
				}
				facePlayer();
				prepMoveForward();
				move();
				rotate();
				emptyClip();
			}
		}
		
		if (mode == "smartMode") {
			if (!stopped) {
				BaseObject closestPow = findClosestPow();
				if (closestPow != null) {
					diffX = closestPow.getX() - x;
					diffY = closestPow.getY() - y;
					powDist = (float) Math.sqrt(diffX*diffX + diffY*diffY);
				} else {
					powDist = 999;
				}
				
				if (!timing) {
					if (!hitWall) {
						//if player is far enough away, and powerup is within range, go for powerup
						if (GPS.distance > vision && powDist <= vision) {
							//System.out.println("moving towards pow");
							facePow(closestPow);
							prepMoveForward();
							move();
						} else {
							facePlayer();
							prepMoveForward();
							move();
							// fire bullet if close to player
							if (GPS.distance < vision && timeAlive % 500 < 30 && timeAlive % 500 > 0 && this.getFiredBullets().size() <= 3) {
								prepBullet();
							}
						}
					} else { //hitwall- reverse
						timing = true;
						timeStart = System.currentTimeMillis();
						reverseMode = true;
					}
				} else {	// timing
					if (reverseMode) {
						prepMoveBackward();
						move();
					}
				}
				
				rotate();
				emptyClip();
			}
		}
	}
	
	public BaseObject findClosestPow() {
		float diffX, diffY;
		float dist = 9999;
		BaseObject closestPow = null;
		
		for(BaseObject p : gameHandle.getGameWorld().getPowList()) {
			diffX = p.getX() - x;
			diffY = p.getY() - y;
			
			if ((float) Math.sqrt(diffX*diffX + diffY*diffY) < dist) {
				dist = (float) Math.sqrt(diffX*diffX + diffY*diffY);
				closestPow = p;
			}
		}
		return closestPow;
	}
	
	public void facePow(BaseObject p) {
		float diffX = p.getX() - x;
		float diffY = p.getY() - y;
		// direction = (float) Math.toDegrees(Math.atan2(ydiff, xdiff));
		float powDirection = (float) Math.toDegrees(Math.atan2(diffY, diffX));
		
		if (powDirection < 0) {
			// ensure direction is positive
			powDirection += 360;
		}
		
		float angleDiff = powDirection - a;
		
		if (angleDiff > 180) {
			angleDiff -= 360;
		} else if (angleDiff < -180) {
			angleDiff += 360;
		}
		
		// rotate if outside 22.5 angle allowance
		if (angleDiff > 22.5) {
			prepRotateRight();
		} else if (angleDiff < - 22.5) {
			prepRotateLeft();
		}
	}
	
	public void facePlayer() {
		
		float angleDiff = GPS.direction - a;
		
		if (angleDiff > 180) {
			angleDiff -= 360;
		} else if (angleDiff < -180) {
			angleDiff += 360;
		}
		
		// rotate if outside 22.5 angle allowance
		if (angleDiff > 22.5) {
			prepRotateRight();
		} else if (angleDiff < - 22.5) {
			prepRotateLeft();
		}
	}
	
	public void render(Graphics g) {
		Graphics2D g2d = (Graphics2D) g.create();
		g2d.rotate(Math.toRadians(a), x + width/2, y + height/2); //rotate about centre
		
		if (gameHandle.isDebug()) {
			g2d.drawRect((int) x, (int) y, 48, 48);
			drawCenteredCircle(g2d, (int) x + width/2, (int) y + width/2, (int) GPS.distance*2);
			drawCenteredCircle(g2d, (int) x + width/2, (int) y + width/2, (int) vision*2);
		} else {
			g2d.drawImage(skin, (int) x, (int) y, null);
		}
		
		g2d.drawImage(leftTrackSkins[leftTrackFrame], (int) x, (int) (y), null);
		g2d.drawImage(rightTrackSkins[rightTrackFrame], (int) x, (int) (y + height - 10), null);  // track is 10 pixels wide, img is facing right
		g2d.dispose();
	}
	
	public void drawCenteredCircle(Graphics2D g2d, int x, int y, int r) {
		  x = x - (r/2);
		  y = y - (r/2);
		  g2d.drawOval(x, y, r, r);
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
	
	public void switchMode() {
		if (mode == "blitzMode") {
			mode = "smartMode";
		} else {
			mode = "blitzMode";
		}
	}
	
	public float getSpeed() {
		return speed;
	}

	public void setSpeed(float speed) {
		this.speed = speed;
	}

	public float getRotateAngle() {
		return rotateAngle;
	}

	public void setRotateAngle(float rotateAngle) {
		this.rotateAngle = rotateAngle;
	}
}
 