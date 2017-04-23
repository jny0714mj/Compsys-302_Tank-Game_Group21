/**
 * A help state showing what button presses do what. Called from within the menu.
 */

package states;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import game.GameHandle;
import game.Logs;
import gfx.ImageManager;

public class HelpState extends State {

	private BufferedImage help;
	
	public HelpState(GameHandle gameHandle) {
		super(gameHandle);
		help = ImageManager.loadImage("/imgs/help.png");
	}

	@Override
	public void tick() {
		if (gameHandle.getGame().getKeyManager().back)
		{
			State.setState(gameHandle.getGame().getWelcomeState());
			Logs.getInstance().Process("Back to Main Menu");
		}
		
	}

	@Override
	public void render(Graphics g) {

		g.drawImage(help, 0, 0, null);
	}

}
