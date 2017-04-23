/* This class simply renders an object at the specified location.
 * It does not do any computation on the object 
 * 
 */

package objs;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import game.GameHandle;
import gfx.ImageManager;

public class SlaveObject extends BaseObject{
	
	private BufferedImage skin;
	
	public SlaveObject(GameHandle gameHandle, float x, float y, float a, int type, int state, int alive, String id) {
		//BaseObject(GameHandle gameHandle, float x, float y, float a, int width, int height, boolean toRandPos, int type, int state) {
		super(gameHandle, x, y, a, 0, 0, false, type, state);
		// also send the following info to super
		super.alive = alive;
		super.id = id;
		// and update super.width and super.height in the following func
		setSkin();
	}
	
	private void setSkin() {		
		switch (this.type) {
		case 0:
			this.skin = ImageManager.loadImage("/imgs/tank/tank_grey.png");
			super.width = super.height = 48;
			break;
		case 1:
			this.skin = ImageManager.loadImage("/imgs/wall1.png");
			super.width = super.height = 12;
			break;
		case 2:
			this.skin = ImageManager.loadImage("/imgs/bullet.png");
			super.width = super.height = 8;
			break;
		case 3:
			this.skin = ImageManager.loadImage("/imgs/powerup.png");
			super.width = super.height = 24;
			break;
        default: 
        	throw new IllegalStateException("I don't recognise this object type");	
		}
	}

	@Override
	public void tick() {
		// Nothing! I'm a slave
		
	}

	@Override
	public void render(Graphics g) {
		// Draw me!
		
		Graphics2D g2d = (Graphics2D) g.create();
		g2d.rotate(Math.toRadians(a), x + width/2, y + height/2); //rotate about centre
		
		if (gameHandle.isDebug()) {
			g2d.drawRect((int) x, (int) y, width, height);
		} else {
			g2d.drawImage(skin, (int) x, (int) y, null);
		}
		
		g2d.dispose();
		
	}
	
	
}
