/**
 * This class works as a clock. It is called to count down by 3 before the game begins and as the main game clock
 */

package game;

import java.awt.Font;
import java.awt.Graphics;

import objs.Tank;
import states.State;

public class tickTock {

	private GameHandle gameHandle;
	private final static int FPS = 30;
	private int tickTock = 0;
	public static int second = 0;
	public static int minutes = 0;
	private int count = 3;
	
	public tickTock(GameHandle gameHandle) {
		this.gameHandle = gameHandle;
	}

	//timer for during the game
	public void tick() {
		if (gameHandle.getGame().getKeyManager().pgDown) {
			minutes = 1;
			second = 59;
		}
			
		tickTock++;
		if (tickTock >= FPS) {
			tickTock = 0;
			second++;
			if (second == 60) {
				second = 0;
				minutes++;
			}
			if (minutes == 2) {
				System.out.println("end game");
				State.setState(gameHandle.getGame().getEndGameState());
				second = 0;
				minutes = 0;
			}
		}
	}
	
	//counting down before game starts
	public void tick_start() {
		tickTock++;
		if (tickTock >= FPS) {
			tickTock = 0;
			count--;
			
			if (count == 2) {
				Tank.player1_score = 0;
				Tank.player2_score = 0;
			}
			if (count == -1) {
				count = 3;
				State.setState(gameHandle.getGame().getSinglePlayerState());
			}
		}
	}
	
	//display timer during the game playing
	public void render(Graphics g) {
		g.setFont(new Font("Arial",Font.BOLD,30));
		g.drawString((minutes + ((second <= 9)?":0":":") + second), 467, 50);
	}
	
	//display counting down before game starts
	public void render_start(Graphics g) {
		g.setFont(new Font("Arial",Font.BOLD,100));
		g.drawString((minutes + ((count <= 9)?":0":":") + count), 400, 300);
		if (count == 0) {
			g.setFont(new Font("Arial",Font.BOLD,100));
			g.drawString("Start!", 360, 500);
		}
	}

}
