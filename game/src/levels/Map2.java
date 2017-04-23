package levels;

import game.GameHandle;
import objs.Wall;

public class Map2 extends Map {
	
	//public ArrayList<Rectangle> wall = new ArrayList<Rectangle>();
	private GameHandle gameHandle;
	
	public Map2(GameHandle gameHandle) {
		this.gameHandle = gameHandle;
	}
	
	@Override
	public void init() {
		for (int i = 0; i < 85; i++)
		{
			gameHandle.getGameWorld().addObject(new Wall(gameHandle,i * scale, 0));
			gameHandle.getGameWorld().addObject(new Wall(gameHandle,i * scale, 63 * scale));
		}
		for (int i = 0; i < 63; i++)
		{
			gameHandle.getGameWorld().addObject(new Wall(gameHandle,0, i * scale));
			gameHandle.getGameWorld().addObject(new Wall(gameHandle,85 * scale, i * scale));
		}
		
		for (int i = 10; i < 20; i++)
		{
			gameHandle.getGameWorld().addObject(new Wall(gameHandle,i*scale,11*scale));
			gameHandle.getGameWorld().addObject(new Wall(gameHandle,i*scale,55*scale));
		}	
		for (int i = 35; i< 50; i++ )
		{
			gameHandle.getGameWorld().addObject(new Wall(gameHandle, i*scale, 11*scale));
			gameHandle.getGameWorld().addObject(new Wall(gameHandle, i*scale, 55*scale));
		}
		for (int i = 65; i < (75); i++)
		{
			gameHandle.getGameWorld().addObject(new Wall(gameHandle,i*scale,11*scale));
			gameHandle.getGameWorld().addObject(new Wall(gameHandle,i*scale,55*scale));
		}
		for (int i = 20; i< 43; i++ )
		{
			gameHandle.getGameWorld().addObject(new Wall(gameHandle, 10*scale, i*scale));
			gameHandle.getGameWorld().addObject(new Wall(gameHandle, 75*scale, i*scale));
		}
		for (int i = 25; i<38 ; i++)
		{
			gameHandle.getGameWorld().addObject(new Wall(gameHandle, 43*scale, i*scale));
		}
		
	}
	
}
