package levels;

import game.GameHandle;
import objs.Wall;

public class Map4 extends Map {
	
	//public ArrayList<Rectangle> wall = new ArrayList<Rectangle>();
	private GameHandle gameHandle;
	
	public Map4(GameHandle gameHandle) {
		this.gameHandle = gameHandle;
	}
	
	public void init()
	{

		for (int i = 0; i < 85; i++)
		{
			gameHandle.getGameWorld().addObject(new Wall(gameHandle, i * scale, 0));
			gameHandle.getGameWorld().addObject(new Wall(gameHandle, i * scale, 768 - scale));
		}
		for (int i = 0; i < 64; i++)
		{
			gameHandle.getGameWorld().addObject(new Wall(gameHandle, 0, i * scale));
			gameHandle.getGameWorld().addObject(new Wall(gameHandle, 1024 - scale, i * scale));
		}
		
		
	}
}