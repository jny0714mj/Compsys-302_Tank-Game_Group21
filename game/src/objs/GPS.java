/**
 * A utility class used for the AI calculations that finds the direction and distance to the player from the AI tank
 */

package objs;

import game.GameHandle;

public class GPS {

	float xplay, yplay, xAI, yAI, angle;
	private GameHandle gameHandle;
	public static float xdiff, ydiff, direction, distance;
	
	public GPS(GameHandle gameHandle, float xplay, float yplay, float xAI, float yAI, float angle)
	{
		this.gameHandle = gameHandle;
		this.xplay = xplay;
		this.yplay = yplay;
		this.xAI = xAI;
		this.yAI = yAI;
		this.angle = angle;
	}
	
	public void tick()
	{
		calculate();
	}
	public void calculate()
	{
		//get difference between ai and player
		xdiff = xplay - xAI;
		ydiff = yplay - yAI;
		//get angle bewteen ai and player
		direction = (float) Math.toDegrees(Math.atan2(ydiff, xdiff));
		distance = (float) Math.sqrt(xdiff*xdiff + ydiff*ydiff);
		
		if (direction < 0) {
			// ensure direction is positive
			direction += 360;
		}
	}
}
