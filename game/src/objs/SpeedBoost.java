package objs;

import java.awt.Graphics;

import game.GameHandle;

public class SpeedBoost extends Powerup {
	
	private static final int MY_STATE = 2;
	
	public SpeedBoost(GameHandle gameHandle, float x, float y, boolean toRandPos) {
		super(gameHandle, x, y, toRandPos, MY_STATE);
		// TODO Auto-generated constructor stub
	}

	
	@Override
	public void render(Graphics g) {
		g.drawImage(skin, (int) x, (int) y, null); 
	}
	
}
