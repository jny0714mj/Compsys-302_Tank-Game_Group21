package objs;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import game.GameHandle;
import gfx.ImageManager;

public class Wall extends BaseObject {
	
	private static final int MY_TYPE = 1;
	private static final int MY_STATE = 0;
	
	static float a = 0;
	static boolean toRandPos = false;
	static int width = 12;
	static int height = 12;
	
	private BufferedImage texture = ImageManager.loadImage("/imgs/wall1.png");
	
	public Wall(GameHandle gameHandle, float x, float y) {
		super(gameHandle, x, y, a, width, height, toRandPos, MY_TYPE, MY_STATE);
	}
	public void tick(){
		
	}
	
	public void render(Graphics g){
		if (gameHandle.isDebug()) {
			g.drawRect((int) x, (int) y, width, height);
		} else {
			g.drawImage(texture, (int) x, (int) y, width, height, null);
		}
		
	}
}
