/**
 * A particular map (set of wall locations) which is hardcoded in.
 * When loaded through init() call, simply adds Wall objects to the World.
 */

package levels;

import game.GameHandle;
import objs.Wall;

public class Map1 extends Map {
	
	//public ArrayList<Rectangle> wall = new ArrayList<Rectangle>();
	
	private GameHandle gameHandle;
	private World world;
	
	public Map1(GameHandle gameHandle) {
		this.gameHandle = gameHandle;
	}
	
	public void init() {
		//level.addObject(wall);
		world = gameHandle.getGameWorld();
		
		// hardcoded wall positions
		for (int i = 0; i < 85; i++) {
			gameHandle.getGameWorld().addObject(new Wall(gameHandle, i*scale, 0));
			gameHandle.getGameWorld().addObject(new Wall(gameHandle, i*scale, 768 - scale));
//			g.drawImage(tile, i*scale, 0, null);
//			g.drawImage(tile, i*scale, 63*scale, null);
		}
		
		for (int i = 0; i < 64; i++) {
			gameHandle.getGameWorld().addObject(new Wall(gameHandle, 0, i*scale));
			gameHandle.getGameWorld().addObject(new Wall(gameHandle, 1024 - scale, i*scale));
//			g.drawImage(tile, 0, i*scale, null);
//			g.drawImage(tile, 85*scale, i*scale, null);
		}
		
		for (int i = (10); i < (26); i++) {
			gameHandle.getGameWorld().addObject(new Wall(gameHandle, i*scale, 24*scale));
			gameHandle.getGameWorld().addObject(new Wall(gameHandle, i*scale, 45*scale));
//			g.drawImage(tile, i*scale, 24*scale, null);
//			g.drawImage(tile, i*scale, 45*scale, null);
		}	
		
		for (int i = 24; i< 46; i++ ) {
			gameHandle.getGameWorld().addObject(new Wall(gameHandle, 26*scale, i*scale));
//			g.drawImage(tile, 26*scale, i*scale, null);
		}
		
		for (int i = (59); i < (74); i++) {
			gameHandle.getGameWorld().addObject(new Wall(gameHandle, i*scale, 24*scale));
			gameHandle.getGameWorld().addObject(new Wall(gameHandle, i*scale, 45*scale));
//			g.drawImage(tile, i*scale, 24*scale, null);
//			g.drawImage(tile, i*scale, 45*scale, null);
		}
		
		for (int i = 24; i< 46; i++ ) {
			gameHandle.getGameWorld().addObject(new Wall(gameHandle, 59*scale, i*scale));
//			g.drawImage(tile, 59*scale, i*scale, null);
		}
	}
	
}
