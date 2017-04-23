/** This class implements a KeyListener interface to handle user input.
 * Every tick() method, it sets values in a boolean array (representing each key) to true if the button is being pressed or false otherwise.
 * These class holds two important arrays- keyDown, an element of which is true while a key is held down, and keyPress, which fires for a single tick at a button press
 */

package game;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import ipc.SocketManager;

public class KeyManager implements KeyListener{
	
	private GameHandle gameHandle;
	
	private boolean[] keyDown;
	private boolean[] justPressed;
	private int id;
	
	public boolean p,w,s,a,d,one,up,down,left,right,m;
	public boolean lastUp, lastDown, lastLeft, lastRight, lastM;
	public boolean switchDebugger;
	public boolean switchAIMode;
	public boolean menuUp,menuDown,enter,exit,back,pgDown,home,p2Up,p2Down;
	public boolean e;
	
	public KeyManager(GameHandle gameHandle) {
		this.gameHandle = gameHandle;
		keyDown = new boolean[256];
		justPressed = new boolean[256];
	}
	
	public void tick() {
		// use keyDown to fire event every tick or justPressed to fire once until released
		p = justPressed[KeyEvent.VK_P];
		
		//control scheme 2
		w = keyDown[KeyEvent.VK_W];
		s = keyDown[KeyEvent.VK_S];
		a = justPressed[KeyEvent.VK_A];
		d = justPressed[KeyEvent.VK_D];
		one = justPressed[KeyEvent.VK_T];
		
		// control scheme 1
		up = keyDown[KeyEvent.VK_UP];
		down = keyDown[KeyEvent.VK_DOWN];
		left = justPressed[KeyEvent.VK_LEFT];
		right = justPressed[KeyEvent.VK_RIGHT];
		m = justPressed[KeyEvent.VK_M];
		
		menuUp = justPressed[KeyEvent.VK_UP];
		menuDown = justPressed[KeyEvent.VK_DOWN];
		enter = justPressed[KeyEvent.VK_ENTER];
		exit = justPressed[KeyEvent.VK_ESCAPE];
		
		switchDebugger = justPressed[KeyEvent.VK_BACK_SLASH];
		switchAIMode = justPressed[KeyEvent.VK_CLOSE_BRACKET];
		back = justPressed[KeyEvent.VK_BACK_SPACE];
		pgDown = justPressed[KeyEvent.VK_PAGE_DOWN];
		home = justPressed[KeyEvent.VK_HOME] || justPressed[KeyEvent.VK_OPEN_BRACKET];	// for testing- Jack's laptop doesn't have HOME
		p2Up = justPressed[KeyEvent.VK_UP];
		p2Down = justPressed[KeyEvent.VK_DOWN];
		
		// test echo
		e = justPressed[KeyEvent.VK_E];
		
		// action will occur- reset justPressed to false
		for(int i=0; i<256; i++) {
			justPressed[i] = false;
		}
		
	}

	@Override
	public void keyTyped(KeyEvent ke) {
	}

	@Override
	public void keyPressed(KeyEvent ke) {
		id = ke.getKeyCode();
		
		// test if python is listening
		if (id == KeyEvent.VK_E) {
			gameHandle.getSocketManager().sendMsgToPython("8?test");
		}
		
		// SEND NEWLY DOWN KEY PRESSES 
		if (Launcher.isSlave && "onlineMulti".equals(gameHandle.getGameMode()) && keyDown[id] == false) {
			SocketManager sM = gameHandle.getSocketManager();
			if (id == KeyEvent.VK_LEFT) {
				sM.sendMsgToPython("0?0&1");
			} else if (id == KeyEvent.VK_RIGHT) {
				sM.sendMsgToPython("0?1&1");
			} else if (id == KeyEvent.VK_UP) {
				System.out.println("fire up press");
				sM.sendMsgToPython("0?2&1");
			} else if (id == KeyEvent.VK_DOWN) {
				sM.sendMsgToPython("0?3&1");	
			} else if (id == KeyEvent.VK_M) {
				sM.sendMsgToPython("0?4&1");
			}
		}
		
		// UPDATE ARRAYS WHICH tick() REFERENCES
		if(keyDown[id] == true && justPressed[id] == true) {
			justPressed[id] = false;
		} else if(keyDown[id] == false) {
			keyDown[id] = true;
			justPressed[id] = true;
		}
		

		
	}

	@Override
	public void keyReleased(KeyEvent ke) {
		id = ke.getKeyCode();
		
		// SEND NEWLY UP KEY RELEASES 
		if (Launcher.isSlave && "onlineMulti".equals(gameHandle.getGameMode()) && keyDown[id] == true) {
			SocketManager sM = gameHandle.getSocketManager();
			if (id == KeyEvent.VK_LEFT) {
				sM.sendMsgToPython("0?0&0");
			} else if (id == KeyEvent.VK_RIGHT) {
				sM.sendMsgToPython("0?1&0");
			} else if (id == KeyEvent.VK_UP) {
				sM.sendMsgToPython("0?2&0");
			} else if (id == KeyEvent.VK_DOWN) {
				sM.sendMsgToPython("0?3&0");	
			} else if (id == KeyEvent.VK_M) {
				sM.sendMsgToPython("0?4&0");
			}
		}
		
		keyDown[id] = false;
		justPressed[id] = false;
	}
	
	
}
