/**
 * Intended as the state which draws the time. UNFINISHED
 */

package states;

import game.GameHandle;
import gfx.ImageManager;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

public class TimerState extends State
{

	//private Counting counting;
	private BufferedImage time;
	Graphics g;
	
	public TimerState(GameHandle gameHandle) {
		super(gameHandle);
		time = ImageManager.loadImage("/imgs/time.jpg");
		//counting = new Counting(gameHandle, 3);
	}

	public void render(Graphics g) {
		
		g.drawImage(time, 200, 200, null);
		g.dispose();
	}

	public void tick() {
		
//		try {
//			//counting.tick();
//			
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
	}


	
}
