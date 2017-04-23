/**
 * The main game state, where tanks are active.
 */

package states;

import game.GameHandle;
import game.Logs;
import levels.World;
import objs.Powerup;

import java.awt.Graphics;
import java.util.ArrayList;

public class GameState extends State {
	
	private State pauseState = new PauseState(gameHandle);
	protected ArrayList<Powerup> activePowerups = new ArrayList<Powerup>();

	public GameState(GameHandle gameHandle){
		super(gameHandle);
	}
	
	@Override
	public void tick() {
		if(gameHandle.getKeyManager().exit){
			Logs.getInstance().Process("Exit Game");
			State.setState(gameHandle.getGame().getExitState());
		}
			
		if(gameHandle.getKeyManager().p){
			Logs.getInstance().Process("Pause Game");
			State.setState(pauseState);
		}
		
		if(gameHandle.getKeyManager().switchDebugger) {
			gameHandle.toggleDebug();
		}
		
		gameHandle.getGameWorld().tick();
	}

	@Override
	public void render(Graphics g) {
		gameHandle.getGameWorld().render(g);
	}
	
}