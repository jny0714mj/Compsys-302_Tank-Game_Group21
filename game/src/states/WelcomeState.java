/**
 * State that beings when the game starts. Shows the main menu.
 */

package states;

import java.awt.Graphics;

import music.Music;
import game.*;
import game.Menu;

public class WelcomeState extends State {

	private Menu menu;
	
	public WelcomeState(GameHandle gameHandle) {
		super(gameHandle);
		menu = new Menu(gameHandle);
		init();
	}
	
	public static void init()
	{
		Music music = new Music("/sounds/main.wav");
//		music.start();
	}

	@Override
	public void tick() {
		menu.tick();
		
	}

	@Override
	public void render(Graphics g) {
		menu.render(g);
	}
	
	

}
