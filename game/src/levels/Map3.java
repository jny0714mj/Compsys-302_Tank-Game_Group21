package levels;

import game.GameHandle;
import objs.Wall;

public class Map3 extends Map {

	//public ArrayList<Rectangle> wall = new ArrayList<Rectangle>();
	private GameHandle gameHandle;
	
	public Map3(GameHandle gameHandle) {
		this.gameHandle = gameHandle;
	}
	
	public void init()
	{
		
		for (int i = 0; i < 85; i++)
		{
			gameHandle.getGameWorld().addObject(new Wall(gameHandle, i * scale, 0));
			gameHandle.getGameWorld().addObject(new Wall(gameHandle, i * scale, 63 * scale));
		}
		for (int i = 0; i < 63; i++)
		{
			gameHandle.getGameWorld().addObject(new Wall(gameHandle, 0, i * scale));
			gameHandle.getGameWorld().addObject(new Wall(gameHandle, 85 * scale, i * scale));
		}
		for (int i = 7; i < 13 ; i++)
		{
			gameHandle.getGameWorld().addObject(new Wall(gameHandle, i * scale, 7 * scale));
			gameHandle.getGameWorld().addObject(new Wall(gameHandle, i * scale, 8 * scale));
			gameHandle.getGameWorld().addObject(new Wall(gameHandle, i * scale, 9 * scale));
			gameHandle.getGameWorld().addObject(new Wall(gameHandle, i * scale, 55 * scale));
			gameHandle.getGameWorld().addObject(new Wall(gameHandle, i * scale, 56 * scale));
			gameHandle.getGameWorld().addObject(new Wall(gameHandle, i * scale, 57 * scale));
		}
		for (int i = 66; i < 73; i++)
		{
			gameHandle.getGameWorld().addObject(new Wall(gameHandle, i * scale, 7 * scale));
			gameHandle.getGameWorld().addObject(new Wall(gameHandle, i * scale, 8 * scale));
			gameHandle.getGameWorld().addObject(new Wall(gameHandle, i * scale, 9 * scale));
			gameHandle.getGameWorld().addObject(new Wall(gameHandle, i * scale, 55 * scale));
			gameHandle.getGameWorld().addObject(new Wall(gameHandle, i * scale, 56 * scale));
			gameHandle.getGameWorld().addObject(new Wall(gameHandle, i * scale, 57 * scale));
		}
		for (int i = 9; i <18; i++)
		{
			gameHandle.getGameWorld().addObject(new Wall(gameHandle, i * scale, 16 * scale));
			gameHandle.getGameWorld().addObject(new Wall(gameHandle, (i+1) * scale, 46 * scale));
		}
		
		for (int i = 64; i < 73; i++)
		{
			gameHandle.getGameWorld().addObject(new Wall(gameHandle, i * scale, 16 * scale));
			gameHandle.getGameWorld().addObject(new Wall(gameHandle, i * scale, 46 * scale));
		}
		for (int i = 16; i < 46; i++)
		{
			gameHandle.getGameWorld().addObject(new Wall(gameHandle, 18 * scale, i * scale));
			gameHandle.getGameWorld().addObject(new Wall(gameHandle, 64 * scale, i * scale));
		}
		for (int i = 25; i < 31; i++)
		{
			gameHandle.getGameWorld().addObject(new Wall(gameHandle, i * scale, 13 * scale));
			gameHandle.getGameWorld().addObject(new Wall(gameHandle, i * scale, 14 * scale));
			gameHandle.getGameWorld().addObject(new Wall(gameHandle, i * scale, 15 * scale));
			gameHandle.getGameWorld().addObject(new Wall(gameHandle, i * scale, 43 * scale));
			gameHandle.getGameWorld().addObject(new Wall(gameHandle, i * scale, 44 * scale));
			gameHandle.getGameWorld().addObject(new Wall(gameHandle, i * scale, 45 * scale));
		}
		for (int i= 50; i < 56; i++)
		{
			gameHandle.getGameWorld().addObject(new Wall(gameHandle, i * scale, 13 * scale));
			gameHandle.getGameWorld().addObject(new Wall(gameHandle, i * scale, 14 * scale));
			gameHandle.getGameWorld().addObject(new Wall(gameHandle, i * scale, 15 * scale));
			gameHandle.getGameWorld().addObject(new Wall(gameHandle, i * scale, 43 * scale));
			gameHandle.getGameWorld().addObject(new Wall(gameHandle, i * scale, 44 * scale));
			gameHandle.getGameWorld().addObject(new Wall(gameHandle, i * scale, 45 * scale));
		}
		for (int i = 25; i < 28; i++)
		{
			gameHandle.getGameWorld().addObject(new Wall(gameHandle, i * scale, 16 * scale));
			gameHandle.getGameWorld().addObject(new Wall(gameHandle, i * scale, 17 * scale));
			gameHandle.getGameWorld().addObject(new Wall(gameHandle, i * scale, 18 * scale));
			gameHandle.getGameWorld().addObject(new Wall(gameHandle, i * scale, 40 * scale));
			gameHandle.getGameWorld().addObject(new Wall(gameHandle, i * scale, 41 * scale));
			gameHandle.getGameWorld().addObject(new Wall(gameHandle, i * scale, 42 * scale));
		}
		for (int i = 53; i < 56; i++)
		{
			gameHandle.getGameWorld().addObject(new Wall(gameHandle, i * scale, 16 * scale));
			gameHandle.getGameWorld().addObject(new Wall(gameHandle, i * scale, 17 * scale));
			gameHandle.getGameWorld().addObject(new Wall(gameHandle, i * scale, 18 * scale));
			gameHandle.getGameWorld().addObject(new Wall(gameHandle, i * scale, 40 * scale));
			gameHandle.getGameWorld().addObject(new Wall(gameHandle, i * scale, 41 * scale));
			gameHandle.getGameWorld().addObject(new Wall(gameHandle, i * scale, 42 * scale));
		}
	}
	
}
