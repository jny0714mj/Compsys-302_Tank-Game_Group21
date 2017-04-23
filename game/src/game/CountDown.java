/**
 * This class deals with the counting down from 3 before the game begins. It renders the numbers to the screen.
 * It runs on it's own thread for efficiency
 */

package game;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

import states.State;

public class CountDown {

	private GameHandle gameHandle;
	Thread thread = new Thread();
	int seconds;
	boolean run = false;
	String[] numbers = {"3","2","1"};
	
	public CountDown(GameHandle gameHandle,int seconds) {
		this.gameHandle = gameHandle;
		this.seconds = seconds;
	}
	
	public void tick() throws InterruptedException
	{
		if (!run)
		{
			for (int i = seconds; i > 0; i--)
			{
				Thread.sleep(1000);
				System.out.println(i);
				if (i == 1)
				{
					run = true;
					State.setState(gameHandle.getGame().getSinglePlayerState());
				}
			}
		}
	}
	public void render(Graphics g)
	{
		for (int i = 0 ; i <numbers.length; i++)
		{
			g.setColor(Color.black);
			g.setFont(new Font("Arial",Font.BOLD,80));
			g.drawString(numbers[i], 412+30, 275);
		}
	}
	
}
