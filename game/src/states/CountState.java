/**
 * The state that counts down before the game.
 * Loads the world when it runs so also acts as a loader.
 */

package states;

import java.awt.Graphics;
import game.GameHandle;
import game.tickTock;
import levels.World;

public class CountState extends State {

	private tickTock clock;
	private World world;
	
	public CountState(GameHandle gameHandle) {
		super(gameHandle);
		clock = new tickTock(gameHandle);
	}
	
	public void loadWorld() {
		System.out.println("loading world");
		world = new World(gameHandle, 1, 1);
		gameHandle.setGameWorld(world);
		world.init();
	}


	public void tick() {
		clock.tick_start();
	}
	
	public void render(Graphics g) {
		world.render(g);
		clock.render_start(g);
	}

}
