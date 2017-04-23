/** 
 * Tank is the basic object of a tank in the game, which extends BaseObject.
 * It is abstract and has children; Player, AIPlayer, DumbPlayer which are generated in the various modes
 * It holds many classes useful to tanks such as
 * 		movement and handle collisions
 * 		fire bullets
 * 		die and award points to players
 */

package objs;

import java.util.ArrayList;

import music.SoundEffects;
import game.GameHandle;

public abstract class Tank extends BaseObject{
	
	public static final int DEF_health = 1;
	public static final float DEF_speed = 3f;
	public static final float DEF_fireSpeed = 9f;	//pixels per frame
	public static final float DEF_rotateAngle = 22.5f;
	private static final int MY_TYPE = 0;
	
	protected float speed, fireSpeed, rotateAngle;
	protected int health, leftTrackFrame, rightTrackFrame;
	public int score = 0;
	public static int player1_score, player2_score;
	protected boolean stopped;
	// each tank stores the bullets it has fired
	protected ArrayList<Bullet> firedBullets = new ArrayList<Bullet>();
	private boolean toFireBullet;
	protected boolean isPowerupActive;
	protected long powerupTime;
	protected long pickupTime;
	protected boolean hitWall;
	
	public Tank(GameHandle gameHandle, float x, float y, float a, boolean toRandPos, int state) {
		super(gameHandle, x, y, a, 48, 48, toRandPos, MY_TYPE, state);
		this.health = DEF_health;
		this.speed = DEF_speed;
		this.fireSpeed = DEF_fireSpeed; 
		this.rotateAngle = DEF_rotateAngle;
		leftTrackFrame = 0;
		rightTrackFrame = 0;
		score = 0;
		toFireBullet = false;
		stopped = false;
		isPowerupActive = false;
		hitWall = false;
	}
	
	// firing
	public void prepBullet() {
		toFireBullet = true;
	}
	
	public void emptyClip() {
		
		if (toFireBullet) {
			// must find correct bullet generation point. see handwritten note Jack has
			Bullet bullet = new Bullet(gameHandle, 
					(float) (x + 24 - 4 + (24 + 4)*Math.cos(Math.toRadians(a)) ),
					(float) (y + 24 - 4 + (24 + 4)*Math.sin(Math.toRadians(a)) ), 
					a,
					fireSpeed,
					this);
			firedBullets.add(bullet);
			SoundEffects.shot.play();
			toFireBullet = false;
			System.out.println("shots fired!");
		}
		
	}
	
	// movements
	public int trackForward(int trackFrame) {
		trackFrame++;
		if(trackFrame >= 3)
			trackFrame = 0;
		return trackFrame;
	}
	
	public int trackBack(int trackFrame) {
		trackFrame--;
		if(trackFrame < 0)
			trackFrame = 2;
		return trackFrame;
	}

	public void prepMoveForward() {
		dx = (float) (Math.cos(Math.toRadians(a)) * speed);
		dy = (float) (Math.sin(Math.toRadians(a)) * speed);
		
		// TODO: if timer is exceeded, moveFrame
		// why not just have a frame count and do code if mod(frameCount,30) is exceeded?
		// or have a timer class in utility package?
		leftTrackFrame = trackForward(leftTrackFrame);
		rightTrackFrame = trackForward(rightTrackFrame);
		
	}
	
	public void prepMoveBackward() {
		dx = -(float) (Math.cos(Math.toRadians(a)) * speed);
		dy = -(float) (Math.sin(Math.toRadians(a)) * speed);
		
		leftTrackFrame = trackBack(leftTrackFrame);
		rightTrackFrame = trackBack(rightTrackFrame);
	}
	
	public void prepRotateLeft() {
		da = -rotateAngle;
		
		leftTrackFrame = trackBack(leftTrackFrame);
		rightTrackFrame = trackForward(rightTrackFrame);
	}
	
	public void prepRotateRight() {
		da = rotateAngle;
		
		leftTrackFrame = trackForward(leftTrackFrame);
		rightTrackFrame = trackBack(rightTrackFrame);
	}
	
	public void move() {
			
		// deal with collisions
		ArrayList<BaseObject> collidedObjects = checkListCollisions(dx, dy, da);
		
		if (!collidedObjects.isEmpty()) {
			// push up against collidedObject. collider = null if player tries to rotate into the wall, which is OK.
			//BaseObject collider = moveToCollision(collidedObjects, speed);
			BaseObject collider = collidedObjects.get(0);
			if ( collidedObjects.get(0) instanceof Wall) {
				hitWall = true;
				handleWallCollision(collider);
			} else {
				handleCollision(collider);
			}
		} else {	// not hitting anything
			hitWall = false;
		}
		
		// no collision- move
		x += dx;
		y += dy;	
//		dx = 0;
//		dy = 0;
	}
	
	public void stop() {
		stopped = true;
	}
	
	public void start() {
		stopped = false;
	}
	
	public void rotate() {
		a += da;
//		da = 0;
		// make sure [0,360] is returned for a
		if (a > 360) {
			a -= 360;
		} else if (a < 0) {
			a += 360;
		}
	}
	
	// collision
	private void handleWallCollision(BaseObject w) {
		dx = 0;
		dy = 0;
		da = 0;
	}
	
	private void startPowTimer() {
		System.out.println("powerup pick up :)");
		isPowerupActive = true;
		powerupTime = 0;
		pickupTime = System.currentTimeMillis();
	}
	
	protected void removePowAffects() {
		System.out.println("powerup ended :(");
		state = 0;
		isPowerupActive = false;
		health = DEF_health;
		speed = DEF_speed;
		fireSpeed = DEF_fireSpeed; 
		rotateAngle = DEF_rotateAngle;
	}
	
	private void handleCollision(BaseObject collidedObject) {
		
		if (collidedObject instanceof Powerup) {
			SoundEffects.item.play();
			removePowAffects();
			startPowTimer();
			// apply powerup
			if (collidedObject instanceof SpeedBoost) {
				speed *= 1.5;
				state = 2;
			}
			if (collidedObject instanceof SpeedPenalty) {
				speed *= 0.5;
				state = 3;
			}
			if (collidedObject instanceof FireBoost) {
				fireSpeed *= 1.5;
				state = 4;
			}
			if (collidedObject instanceof FirePenalty) {
				fireSpeed *= 0.5;
				state = 5;
			}
			if (collidedObject instanceof BubbleShield) {
				health += 1;
				state = 1;
			}

			//delete powerup icon
			gameHandle.getGameWorld().delPow(collidedObject);
		}
		
		
		if (collidedObject instanceof Tank) {
			// both tanks will register collision and lose health
			
			Tank collidedTank = (Tank) collidedObject;
			System.out.println("tank collision!");
			health--;
			if (health == 0)
				die(0);
			collidedTank.reduceHealth();
			if (collidedTank.getHealth() == 0)
				collidedTank.die(0);
			
		}
	}
	
	public void die(int awardedPoints) {
		SoundEffects.explode.play();
		System.out.println("player died!");
		health = 1;
		randPos();
		timeGenerated = System.currentTimeMillis();
		stop();
		// award everyone else a point
		for (Tank p : gameHandle.getGameWorld().getPlayerList()) {
			System.out.println(p.score);
			if (p.equals(this)) {
				continue;
			} else {
				p.score += awardedPoints;
			}
			//player1_score = p.score;
			player1_score = gameHandle.getGameWorld().getPlayerList().get(0).score;
			player2_score = gameHandle.getGameWorld().getPlayerList().get(1).score;
		//	System.out.println("Player 1: "+ player1_score + "			Player 2: "+ player2_score);
			
		}
	}
	
	
	// GETTERS SETTERS

	public float getHealth() {
		return health;
	}

	public void setHealth(int health) {
		this.health = health;
	}
	public void reduceHealth() {
			health--;
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

	public ArrayList<Bullet> getFiredBullets() {
		return firedBullets;
	}

	public void setFiredBullets(ArrayList<Bullet> firedBullets) {
		this.firedBullets = firedBullets;
	}
	
	

}
