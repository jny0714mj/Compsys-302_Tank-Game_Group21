/**
 * Abstract parent class of each powerup.
 * Alternatively may have enum Effect {} field in this class, but by modularizing, we may change individual powerups later.
 * Implements a timer to delete powerup after 10 seconds
 */

package objs;

import java.awt.image.BufferedImage;

import game.GameHandle;
import gfx.ImageManager;

public abstract class Powerup extends BaseObject {
	// TODO: make powerup have an enum affect? or, possibly better to keep separate classes and just have tick method inside this
//	public enum Effect {
//		SPEEDBOOST, SPEEDPENALTY, BUBBLESHIELD, FIREBOOST, FIREPENALTY
//	}
	
	private static final int MY_TYPE = 3;
	
	protected float lifeTime = 10 * 1000;	//10 seconds
	protected BufferedImage skin = ImageManager.loadImage("/imgs/powerup.png");

	public Powerup(GameHandle gameHandle, float x, float y, boolean toRandPos, int state) {
		super(gameHandle, x, y, 0, 24, 24, toRandPos, MY_TYPE, state);
	}
	
	// warning: may be overridden by child.tick();
	public void tick() {
		timeAlive = System.currentTimeMillis() - timeGenerated;
		
		if(timeAlive > lifeTime)
			gameHandle.getGameWorld().delPow(this);
	}
	
}
