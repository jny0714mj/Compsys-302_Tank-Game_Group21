package objs;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import game.GameHandle;
import gfx.ImageManager;
import music.SoundEffects;

public class Bullet extends BaseObject {
	
	private static final int MY_TYPE = 2;
	private static final int MY_STATE = 0;
	
	//private BufferedImage skin = LoadImage.loadImage();
	protected float speed;//, dx, dy;
	private static boolean toRandPos = false;
	private float lifeTime = 1024 / 9 / 30 * 1000;  //pixels / pixels per frame / frames / second * 1000 ms
	private Tank myTank;
	private Boolean stop;
	private static BufferedImage skin = ImageManager.loadImage("/imgs/bullet.png");
	
	public Bullet(GameHandle gameHandle, float x, float y, float a, float speed, Tank myTank) {
		super(gameHandle, x, y, a, 8, 8, toRandPos, MY_TYPE, MY_STATE);
		this.speed = speed;
		this.myTank = myTank;
		stop = false;
		
		gameHandle.getGameWorld().addObject(this);
	}

	@Override
	public void tick() {
		dx = dy = da = 0;
		timeAlive = System.currentTimeMillis() - timeGenerated;
		if(timeAlive > lifeTime) {
			delete(this);
		}
			
		moveForward();
		move();
	}

	@Override
	public void render(Graphics g) {
		Graphics2D g2d = (Graphics2D) g.create();
		g2d.rotate(Math.toRadians(a), x + width/2, y + height/2); //rotate about centre
		
		if (gameHandle.isDebug()) {
			g2d.drawRect((int) x, (int) y, width, height);
		} else {
			g2d.drawImage(skin, (int) x, (int) y, null);
		}
		
		g2d.dispose();
		
	}
	
	// movement
	public void move() {
		// handle collisions that would occur in next tick
		ArrayList<BaseObject> collidedObjects = checkListCollisions(dx, dy, 0f);
		
		if (!collidedObjects.isEmpty()) {
			// System.out.println("collided with number obj: " + collidedObjects.size());
			BaseObject collider = moveToCollision(collidedObjects, speed);
			if ( collidedObjects.get(0) instanceof Wall) {
				handleWallCollision(collider);
			} else {
				handleObjCollision(collider);
			}
		}
		
		// update position
		if (!stop) {
			x += dx;
			y += dy;
		}
		
//		dx = 0;
//		dy = 0;
	}
	
	public void moveForward() {
		dx = (float) (Math.cos(Math.toRadians(a)) * speed);
		dy = (float) (Math.sin(Math.toRadians(a)) * speed);
	}
	
	public void delete(Bullet b) {
		gameHandle.getGameWorld().delObject(b);
		myTank.getFiredBullets().remove(b);
	}
	
	// collisions
	private void handleWallCollision(BaseObject w) {
		if (w == null)	// no collision
			return;
		
		Shape sWall = w.getShape(0f, 0f, 0f);
		// create phantom objects in each direction to see where collision occurred
		Shape sJitterLeft = this.getShape(-speed, 0f, 0);
		Shape sJitterRight = this.getShape(speed, 0f, 0);
		Shape sJitterUp = this.getShape(0f, -speed, 0);
		Shape sJitterDown = this.getShape(0f, speed, 0);
		
		// only one of the following should run
		if (isColliding(sJitterLeft, sWall)) {
			wallCollideLeft();
		}
		
		if (isColliding(sJitterUp, sWall)) {
			wallCollideAbove();
		}
		
		if (isColliding(sJitterRight, sWall)) {
			wallCollideRight();
		}
		
		if (isColliding(sJitterDown, sWall)) {
			wallCollideBelow();
		}
		
	}
	
	private void handleObjCollision(BaseObject collidedObject) {
		if (collidedObject instanceof Bullet) {
			delete(this);
			delete((Bullet) collidedObject);
			System.out.println("bullet collision");
		}
		
		if (collidedObject instanceof Tank) {
			Tank t = (Tank) collidedObject;
			delete(this);
			t.reduceHealth();
			SoundEffects.explode.play();
			if (t.getHealth() == 0)
				t.die(1);
		}
		
	}
	
	private void wallCollideLeft() {
		float incidenceAngle;
		
		if (gameHandle.isDebug())
			System.out.println("moving left collision");
		incidenceAngle = 270 - a;
		a += 2*incidenceAngle;
		moveForward(); //now that angle has changed, update position for next tick also
	}
	
	private void wallCollideAbove() {
		float incidenceAngle;
		
		if (gameHandle.isDebug())
			System.out.println("moving up collision");
		incidenceAngle = a - 180;
		a -= 2*incidenceAngle;
		moveForward(); 
	}
	
	private void wallCollideRight() {
		float incidenceAngle;
		
		if (gameHandle.isDebug())
			System.out.println("moving right collision");
		incidenceAngle = a - 270;
		a -= 2*incidenceAngle;
		moveForward(); 
	}
	
	private void wallCollideBelow() {
		float incidenceAngle;
		
		if (gameHandle.isDebug())
			System.out.println("moving down collision");
		incidenceAngle = 180 - a;
		a += 2*incidenceAngle;
		moveForward(); 
	}

}
