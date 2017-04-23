/**
 * The state after a particular game (2min) ends
 */

package states;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import objs.Tank;
import game.GameHandle;
import game.Logs;
import gfx.ImageManager;

public class EndGameState extends State{

	BufferedImage player1, player2, draw;
	
	public EndGameState(GameHandle gameHandle) {
		super(gameHandle);
		player1 = ImageManager.loadImage("/imgs/player1.png");
		player2 = ImageManager.loadImage("/imgs/player2.png");
		draw = ImageManager.loadImage("/imgs/draw.png");
	}

	@Override
	public void tick() {
		if(gameHandle.getKeyManager().exit) {
			Logs.getInstance().Process("Exit Game");
			System.exit(0);
		}
		if(gameHandle.getKeyManager().home) {
			Logs.getInstance().Process("Back to Main Menu");
			State.setState(gameHandle.getGame().getWelcomeState());
		}
	}

	@Override
	public void render(Graphics g) {
		g.setColor(Color.black);
		g.setFont(new Font("Arial",Font.BOLD,30));
		g.drawString("END GAME", 280, 300);
		
		if (Tank.player1_score == Tank.player2_score)
			g.drawImage(draw, 0, 0, null);
		if (Tank.player1_score > Tank.player2_score)
			g.drawImage(player1, 0, 0, null);
		if (Tank.player1_score < Tank.player2_score)
			g.drawImage(player2, 0, 0, null);
		
	}

}
