/**
 * This Class is a container for the most important variables in our game. 
 * It is intended to hold all the managers and controllers, or pass on get/ set calls to other classes for ease of use.
 */

package game;

import ipc.SocketManager;
import levels.Map;
import levels.World;

public class GameHandle {
	private Game game;
	private boolean debug = false;
	private String gameMode;
	private World gameWorld;
	private Map gameMap;
	
	//private soundManager soundManager;
	//private musicManager musicManager;
	
	public boolean isDebug() {
		return debug;
	}

	public void setDebug(boolean debug) {
		this.debug = debug;
	}

	public void toggleDebug() {
		debug = !debug;
	}
	
	public GameHandle(Game game) {
		this.game = game;
	}

	public Game getGame() {
		return game;
	}

	public void setGame(Game game) {
		this.game = game;
	}
	
	public KeyManager getKeyManager() {
		return game.getKeyManager();
	}
	
	public SocketManager getSocketManager() {
		return game.getSocketManager();
	}

	public String getGameMode() {
		return gameMode;
	}

	public void setGameMode(String gameMode) {
		this.gameMode = gameMode;
	}

	public World getGameWorld() {
		return gameWorld;
	}

	public void setGameWorld(World gameWorld) {
		this.gameWorld = gameWorld;
	}

	public Map getGameMap() {
		return gameMap;
	}

	public void setGameMap(Map gameMap) {
		this.gameMap = gameMap;
	}
	
}
