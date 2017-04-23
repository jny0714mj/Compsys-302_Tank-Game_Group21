/**
 * Entity is the abstract parent class of all items and players in the game. 
 * It houses information position(x,y,a) and forces children to implement tick and render methods which are called from the gamestates
 * It also holds a lot of useful classes which are used for objects such as...
 * 		rotate hitbox
 * 		collision dectection and move object up against closest colliding object
 * 		randomise position
 */

package objs;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.util.ArrayList;
import java.util.UUID;

import game.GameHandle;

public abstract class BaseObject {
	
	protected GameHandle gameHandle;
	protected float x,y,a,dx,dy,da;
	protected int width,height;
	protected long timeAlive;
	protected long timeGenerated;
	protected String id;			// unique 8 digit alpha-numeric ID
	protected int type, state;
	protected int alive;
	private int ticks;				// ensures that master sends new objs to slave
	
	public abstract void tick();
	public abstract void render(Graphics g);
	
	public BaseObject(GameHandle gameHandle, 
			float x, float y, float a, 
			int width, int height, 
			boolean toRandPos, int type, int state) {
		this.gameHandle = gameHandle;
		this.x = x;
		this.y = y;
		this.a = a;
		this.dx = this.dy = this.da = 0;	// initialise as zero
		this.width = width;
		this.height = height;
		this.timeAlive = 0;
		this.timeGenerated = System.currentTimeMillis();
		this.id = randId();
		this.type = type;
		this.state = state;
		this.alive = 1;		// objects are dynamically deleted in our game and so will always be alive
		this.ticks = 0;
		
		if(toRandPos)
			randPos();
	}
	
	protected String randId() {
		// generate a random id (of length 8) for each object
		String id = UUID.randomUUID().toString().substring(0, 7);
		return id;
	}
	
	protected void randPos() {
		// keep randomly generating positions until a collision free placement is found
		
		// TODO: really messy. perhaps objects should have randPos and randAnd boolean static fields???
		if(this instanceof Tank) {
			a = (float) (22.5*Math.round((Math.random() * 360 / 22.5))); //round to nearest 22.5
		}
		
		x = (int) (Math.random() * 1024);
		y = (int) (Math.random() * 768);
		
		while( !(checkListCollisions(0f, 0f, 0f).isEmpty()) ) {
			System.out.println("collide in test placement");
			x = (int) (Math.random() * 1024);
			y = (int) (Math.random() * 768);
		}
		
	}
	
	// gets the shape object of (this) at a specified offset. can be thought of as a rotated rectangle
	protected Shape getShape(float dx, float dy, float da) {
		Shape shape = new Rectangle((int) (x + dx), (int) (y + dy), width, height);
		AffineTransform transform = new AffineTransform();
		transform.rotate(Math.toRadians(a + da), x + dx + width/2, y + dy + height/2);
		Shape movedShape = transform.createTransformedShape(shape);
		
		return movedShape;
	}
	
	// checks whether two shapes are colliding
	protected boolean isColliding(Shape s1, Shape s2) {
		Area as1 = new Area(s1);
		Area as2 = new Area(s2);
		
		as1.intersect(as2);
		
		if (!as1.isEmpty()) {
			return true;
		} else {
			return false;
		}
		
	}
	
	// return all objects collided with
	// If a wall is collided with, it must be the first object in the returned ArrayList
	protected ArrayList<BaseObject> checkListCollisions(float dxCheck, float dyCheck, float daCheck) {
		ArrayList<BaseObject> collidedObjects = new ArrayList<BaseObject>();
		
		Shape s1 = getShape(dxCheck, dyCheck, daCheck);
		for(BaseObject o : gameHandle.getGameWorld().getObjectList()){
			if (o.equals(this)) {
				continue;
			} else {
				Shape s2 = o.getShape(0f, 0f, 0f);
				if (isColliding(s1,s2)) {
					collidedObjects.add(o);
					
					if ( !(o instanceof Wall) )
						return collidedObjects;		//don't bother checking the rest of the objects (we only need to check for multiple wall collision)
				}
			}
		}
		
		return collidedObjects;
	}
	
	// moves (this) into it's nearest collision along it's path of movement and returns the object collided with
	protected BaseObject moveToCollision(ArrayList<BaseObject> collidedObjects, float mySpeed) {
		//BaseObject minCollider;
		Shape phantomShape;
		float ddx, ddy;
		
		// create a 'phantom shape' one pixel ahead and see if it collides
		for (int i=0; i<=mySpeed; i++) {
			ddx = (float) (Math.cos(Math.toRadians(a)) * i);
			ddy = (float) (Math.sin(Math.toRadians(a)) * i);
			phantomShape = this.getShape( ddx, ddy, 0f);
			for (BaseObject w : collidedObjects) {
				Shape sWall = w.getShape(0, 0f, 0f);
				
				if (isColliding(phantomShape, sWall)) {
					// dcMin = i;
					if ( gameHandle.isDebug() )
						System.out.println("minimum i to collision: " + i);
					x += (float) (Math.cos(Math.toRadians(a)) * (i - 1));	//TODO: set to i-1
					y += (float) (Math.sin(Math.toRadians(a)) * (i - 1));
					return w; 	// min collider
				}
			}
		}
		// if here is reached, no collisions have occurred- return large number
		System.out.println("i haven't collided with anything!");
		return null;
	}
	
	// GETTERS and SETTERS
	
	public int getTicks() {
		return ticks;
	}
	public void incrTicks() {
		this.ticks ++;
	}
	public float getDx() {
		return dx;
	}
	public void setDx(float dx) {
		this.dx = dx;
	}
	public float getDy() {
		return dy;
	}
	public void setDy(float dy) {
		this.dy = dy;
	}
	public float getDa() {
		return da;
	}
	public void setDa(float da) {
		this.da = da;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public int getState() {
		return state;
	}
	public void setState(int state) {
		this.state = state;
	}
	public int getAlive() {
		return alive;
	}
	public void setAlive(int alive) {
		this.alive = alive;
	}
	public float getA() {
		return a;
	}
	public void setA(float a) {
		this.a = a;
	}
	public float getX() {
		return x;
	}
	public void setX(float x) {
		this.x = x;
	}
	public float getY() {
		return y;
	}
	public void setY(float y) {
		this.y = y;
	}
	
	
}
