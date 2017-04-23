/**
 *  Makes either a single player or multiplayer level by initialising players in random locations and walls
 *  Also acts as an object manager by looping through objects to tick()
 */

package levels;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

import game.GameHandle;
import game.Launcher;
import game.tickTock;
import gfx.ImageManager;
import objs.BaseObject;
import objs.BubbleShield;
import objs.DumbPlayer;
import objs.FireBoost;
import objs.FirePenalty;
import objs.GPS;
import objs.AIPlayer;
import objs.Player;
import objs.SpeedBoost;
import objs.SpeedPenalty;
import objs.Tank;
import objs.Wall;

public class World {
	private ArrayList<BaseObject> objectList;
	private ArrayList<Tank> playerList;
	private ArrayList<BaseObject> powList;
	private tickTock ticktock;
	private GameHandle gameHandle;
	private GPS gps;
	private int minPow, maxPow, numPow;
	private int[] powTicks;
	private int currentTick = 0;
	private int powInd = 0;
	private Image bg;
	private String body;
	// use the following for testing
	private boolean isFirstTick = true;
	private boolean isMaster = Launcher.isMaster;	// TODO::: update this to choose master
	private boolean isSlave = Launcher.isSlave;

	public World(GameHandle gameHandle, int minPow, int maxPow) {
		// should take level and gameMode in constructor
		this.gameHandle = gameHandle;
		this.bg = ImageManager.loadImage("/imgs/bg.png");
		this.minPow = minPow;
		this.maxPow = maxPow;
		
		objectList = new ArrayList<BaseObject>();
		playerList = new ArrayList<Tank>();
		powList = new ArrayList<BaseObject>();
		
		ticktock = new tickTock(gameHandle);
		
	}
	
	public void init() {
		// initialize all the objects in the level
		
		if (isSlave) {
			// don't initialize objects
			return;
		}
		
		// ADD WALLS
		gameHandle.getGameMap().init();
		
		// ADD PLAYERS BASED ON GAMEMODE
		addPlayer(new Player(gameHandle, 100, 100, 90, ImageManager.loadImage("/imgs/tank/tank_green.png"), 1, true));	//arrow player
		if (gameHandle.getGameMode() == "training")
			addPlayer(new DumbPlayer(gameHandle, 300,300, 0, ImageManager.loadImage("/imgs/tank/tank_grey.png"), true));
		
		if (gameHandle.getGameMode() == "localMult")
			addPlayer(new Player(gameHandle, 200, 200, 0, ImageManager.loadImage("/imgs/tank/tank_blue.png"), 2, true));	//wasd player
		
		if (gameHandle.getGameMode() == "single")
			addPlayer(new AIPlayer(gameHandle, 200, 200, 0, ImageManager.loadImage("/imgs/tank/tank_grey.png"), true, "smartMode"));
		
		if (gameHandle.getGameMode() == "onlineMulti")
			addPlayer(new DumbPlayer(gameHandle, 400, 400, 0, ImageManager.loadImage("/imgs/tank/tank_blue.png"), true));	//wasd player + online player
		// GENERATE RANDOM TIMES TO PLACE POWER UPS
		genPowerupTimes();
	}
	
	public void genPowerupTimes() {
		numPow = ThreadLocalRandom.current().nextInt(minPow, maxPow + 1);
		System.out.println("i'll make " + numPow + " powerups this game");
		powTicks = new int[numPow];
		for (int i=0; i<numPow; i++) {
			powTicks[i] = ThreadLocalRandom.current().nextInt(0, 120 * 30 + 1);	// total number of ticks in the game
		}
		Arrays.sort(powTicks);
	}
	
	public void genRandomPowerup() {
		int randNum = ThreadLocalRandom.current().nextInt(0, 5);	// 5 powerups total
		System.out.println("gen powerup");
		switch (randNum) {
			case 0: addPow(new BubbleShield(gameHandle, 0, 0, true));
			break;
			case 1: addPow(new SpeedBoost(gameHandle, 0, 0, true));
			break;
			case 2: addPow(new SpeedPenalty(gameHandle, 0, 0, true));
			break;
			case 3: addPow(new FireBoost(gameHandle, 0, 0, true));
			break;
			case 4: addPow(new FirePenalty(gameHandle, 0, 0, true));
			break;
			default: System.out.println("couldn't make powerup");
			break;
		}
	}
	
	public void tick() {
		
//		if (gameHandle.getGameMode().equals("onlineMulti") &&  
		// tick depending on what game mode
		//masterTick()
		//slaveTick()
		//regTick()

		// MASTER
		if (isMaster) {
			// update clock
			ticktock.tick();
			currentTick++;
			
			// add powerup if correct time
			if (powInd < numPow) {
				if (currentTick == powTicks[powInd]) {
					//addObject(new SpeedBoost(gameHandle, 200, 200, true));
					genRandomPowerup();
					powInd++;
					// System.out.println("generated powerup number " + powInd);			//number generated
					// System.out.println("time generated " + currentTick/30);				//time generated
				}
			}
			
			
			// tick all objects in level
			for(int i=0; i<objectList.size(); i++) {
				BaseObject o = objectList.get(i);
				//BaseObject oldObj = o.clone();
				
//				if (o instanceof Wall) {
//					// don't send walls
//					continue;
//				}
				//only tick if object hasn't been deleted
				if (o != null) {
					o.tick();
					o.incrTicks();
					
					// if (gameHandle.getGameMode() == "onlineMult" && isMaster) {
					if (isMaster && (o.getAlive() == 0 || Math.abs(o.getDa()) > 0.5 || Math.abs(o.getDx()) > 0.5 || Math.abs(o.getDy()) > 0.5 || o.getTicks() == 1)) {
						// only send message if object changes position
						
						// TODO: does this include walls- YES. why do I need to tick walls?
						// send this object to python server!!
						// NB '+' operator will silently convert to string using toString() method
						
						//StringBuilder stringBuilder = new StringBuilder(); 
						//for (String s : list) {
						//	
						//}
						
						String msg = "1?" + o.getId()+"&" + o.getType()+"&" + o.getX()+"&" + o.getY()+"&" + o.getA()+"&" + o.getState()+"&" + o.getAlive();
						gameHandle.getSocketManager().sendMsgToPython(msg);	// should be threaded
					}
				}
			}
			isFirstTick = false;
			feed();
			gps.tick();
		}
		
		// SLAVE
		if (isSlave) {
			// TODO::: issue is that body will only be called once. we really want to get all objects at once
			// tick all objects in level. should all be dumb objects
			for(int i=0; i<objectList.size(); i++) {
				BaseObject o = objectList.get(i);
				if (o.getAlive() == 0) {
					delObject(o);
				}
				
				//only tick if object hasn't been deleted
				if (o != null) {
					o.tick();
					o.incrTicks();
				}
			}
		}
	}
	
	public void feed()
	{	
		// public GPS(GameHandle gameHandle, float xplay, float yplay, float xAI, float yAI, float angle)
		gps = new GPS (gameHandle, 
				playerList.get(0).getX(), playerList.get(0).getY(),	//player
				playerList.get(1).getX(), playerList.get(1).getY(), //ai
				playerList.get(1).getRotateAngle());
	}
	
	
	public void render(Graphics g) {
		g.drawImage(bg, 0, 0, null);
		
//		//get different type of maps
//		if (Menu.selection == 0)
//			level1.render(g);
//		
//		if (Menu.selection == 1)
//			level2.render(g);
//		
//		if (Menu.selection == 2)
//			level3.render(g);
		
		ticktock.render(g);
		
		for(int i=0; i<objectList.size(); i++) {
			BaseObject o = objectList.get(i);
			//only render if object hasn't been deleted
			if (o != null)
				o.render(g);
		}
		
		//Displaying Scores
		g.setColor(Color.black);
		g.setFont(new Font("Arial",Font.BOLD,20));
		g.drawString("Player 1: "+Tank.player1_score,100,100);
		g.drawString("Player 2: "+Tank.player2_score,600,100);
	}
	
	// GETTERS SETTERS
	
	public BaseObject getObjectById(String id) {
		// possibly should use a Map instead of a List to speed up computation.
		// else could override equals() in BaseObject to compare id
		 for (BaseObject o : objectList) {
			 if (o.getId().equals(id)) {
				 return o;
			 }
		 }
		 return null;
	}
	
	public void addObject(BaseObject o) {
		objectList.add(o);
	}
	
	public void addPlayer(Tank p) {
		playerList.add(p);
		objectList.add(p);
	}
	
	public void addPow(BaseObject o) {
		powList.add(o);
		objectList.add(o);
	}
	
	public void delPow(BaseObject o) {
		powList.remove(o);
		delObject(o);
	}
	
	public void delObject(BaseObject o) {
		o.setAlive(0);
		if (isMaster) {
			String msg = "1?" + o.getId()+"&" + o.getType()+"&" + o.getX()+"&" + o.getY()+"&" + o.getA()+"&" + o.getState()+"&" + o.getAlive();
			gameHandle.getSocketManager().sendMsgToPython(msg);
		}
		objectList.remove(o);
	}
	
	public ArrayList<BaseObject> getObjectList() {
		return objectList;
	}
	
	public ArrayList<BaseObject> getPowList() {
		return powList;
	}

	public void setObjectList(ArrayList<BaseObject> objectList) {
		this.objectList = objectList;
	}

	public ArrayList<Tank> getPlayerList() {
		return playerList;
	}

	public void setPlayerList(ArrayList<Tank> playerList) {
		this.playerList = playerList;
	}
}

