/**
 * Called when the game is paused during action.
 */

package states;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import game.GameHandle;
import game.Logs;
import gfx.ImageManager;

public class PauseState extends State {
	
	BufferedImage pause;
	
	public PauseState(GameHandle gameHandle) {
		super(gameHandle);
		pause = ImageManager.loadImage("/imgs/pause.png");
	}

	@Override
	public void tick() {
		if(gameHandle.getKeyManager().p){
			State.setState(gameHandle.getGame().getSinglePlayerState());
			Logs.getInstance().Process("Game Resume");
		}
		
	}

	@Override
	public void render(Graphics g) {
		// TODO Auto-generated method stub
		g.drawImage(pause,0,0,null);
	}

}
