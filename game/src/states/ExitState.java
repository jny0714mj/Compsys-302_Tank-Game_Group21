/**
 * Called when escape is pressed from the game mode. Checks if the user really wants to exit.
 */

package states;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import game.GameHandle;
import game.Logs;
import gfx.ImageManager;

public class ExitState extends State {
	
	BufferedImage exit;
	
	public ExitState(GameHandle gameHandle) {
		super(gameHandle);
		exit = ImageManager.loadImage("/imgs/exit.png");
	}

	@Override
	public void tick() {
		if(gameHandle.getKeyManager().exit){
			Logs.getInstance().Process("Exit Game");
			System.exit(0);
		}
		if(gameHandle.getKeyManager().p){
			State.setState(gameHandle.getGame().getSinglePlayerState());
			Logs.getInstance().Process("Game Resume");
		}
			
		if (gameHandle.getKeyManager().home)
		{
			Logs.getInstance().Process("Back to Main Menu");
			State.setState(gameHandle.getGame().getWelcomeState());
		}
		
	}

	@Override
	public void render(Graphics g) {
		g.drawImage(exit, 0, 0, null);
		}

}
