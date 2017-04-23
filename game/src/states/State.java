/**
 * The abstract parent class of all states. It is intended that the game may be only in one state at a time.
 * Tick and render from the game loop calls the specific tick and render, which must be implemented in each child state
 * And so switching states changes what is updated and painted to the screen.
 */

package states;

import java.awt.Graphics;

import game.GameHandle;


public abstract class State {

	private static State currentState = null;
	protected GameHandle gameHandle;
	
	public abstract void tick();
	public abstract void render(Graphics g);
	
	public State(GameHandle gameHandle) {
		this.gameHandle = gameHandle;
	}
	
	
	// SETTERS GETTERS
	
	public static void setState(State state){
		currentState = state;
	}
	
	public static State getState(){
		return currentState;
	}
	
	public void loadWorld() {
		
	}
}