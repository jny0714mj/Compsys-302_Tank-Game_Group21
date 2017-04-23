/**
 * This class renders and deals with the logic of navigating the menu. 
 * When mode and level is selected, state is changed to countDownState and the world loaded.
 * This way the count down acts as a loading screen.
 */

package game;

import gfx.ImageManager;
import ipc.PythonStarter;
import ipc.PythonListener;
import ipc.SocketManager;
import levels.Map1;
import levels.Map2;
import levels.Map3;
import levels.Map4;
import levels.Map;

import java.awt.Color;
import java.awt.Desktop;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import states.State;

public class Menu //implements KeyListener
{

	private String[] options = {"Training","Singleplayer","Multiplayer","Online Multiplay","Help","Quit"};
	private String[] choose = {"level 1","level 2","level 3","level 4","back"};
	private int select = 0;
	public static int number = 0;
	public static int menu = 0;
	public static int selection = 0;
	private GameHandle gameHandle;
	private Map map1, map2, map3, map4;
	private int myCherryPort, oCherryPort;
	
	public BufferedImage background = ImageManager.loadImage("/imgs/menu_bg1.png");
	
	public Menu(GameHandle gameHandle) {
		this.gameHandle = gameHandle;
		this.map1 = new Map1(gameHandle);
		this.map2 = new Map2(gameHandle);
		this.map3 = new Map3(gameHandle);
		this.map4 = new Map4(gameHandle);
	}
	
	public void render(Graphics g) {
		g.drawImage(background,0,0,null);
		
		if (number == 0) {	//change colour
			for (int i = 0 ; i <options.length; i++)
			{
				if (select == i)
				{
					g.setColor(Color.CYAN);
				}
				else
				{
					g.setColor(Color.black);
				}
				g.setFont(new Font("Arial",Font.BOLD,40));
				g.drawString(options[i], 412, 270+i*75);
			}
		}
		if (number == 1) {
			for (int i = 0 ; i <choose.length; i++)
			{
				if (select == i)
				{
					g.setColor(Color.CYAN);
				}
				else
				{
					g.setColor(Color.black);
				}
				g.setFont(new Font("Arial",Font.BOLD,40));
				g.drawString(choose[i], 412+30, 270+i*100);
			}
		}
		g.dispose();
	}
	
	public void tick() {	

		if (number == 0) {	
			if(gameHandle.getGame().getKeyManager().menuUp) {
				
				//	System.out.println(s);
				select--;
				if (select < 0) {
					select = options.length -1;
				}
			}
			else if(gameHandle.getGame().getKeyManager().menuDown) {
				select++;
				if (select >= options.length)
				{
					select = 0;
				}
			}

			//select the game mode
			if (select == 0 && gameHandle.getGame().getKeyManager().enter) {//training
				Logs.getInstance().Process("Select Training Mode");
				State.setState(gameHandle.getGame().getCountState());
				gameHandle.setGameMap(map1);
				gameHandle.setGameMode("training");
				gameHandle.getGame().getCountState().loadWorld();
			}
			if (select == 1 && gameHandle.getGame().getKeyManager().enter) {//single player
				Logs.getInstance().Process("Select Single Player Mode");
				select = 0;
				number = 1;
				menu = 1;
			}
			if (select == 2 && gameHandle.getGame().getKeyManager().enter) {//single player
				Logs.getInstance().Process("Select Single Player Mode");
				select = 0;
				number = 1;
				menu = 2;
			}
			if (select == 3 && gameHandle.getGame().getKeyManager().enter) {//multiplayer
				Logs.getInstance().Process("Select Online MultiPlayer Mode");
				select = 0;
				number = 1;
				menu = 3;
				
				//start socket manager (handle recv msgs and send msgs)
				startSocketManager();
				//starts java listening to connections from python
				int j_listen_port = startPListener();
				//runs server and opens browser automatically.
				startServer(j_listen_port);
				//block while challenge/respond occurs on browser
				//waitForGameAcceptance()
				boolean test = true;
				
			}
			if (select == 3 && gameHandle.getGame().getKeyManager().enter){ //help section
				Logs.getInstance().Process("Select Help Menu");
				select = 0;
				number = 0;
				selection = 3;
				State.setState(gameHandle.getGame().getHelpState());
			}
			if (select == 4 && gameHandle.getGame().getKeyManager().enter){//exit game
				Logs.getInstance().Process("Exit Game");
				System.exit(0);
			}
			if (gameHandle.getGame().getKeyManager().exit) {
				Logs.getInstance().Process("Exit Game");
				System.exit(0);
			}
		}
		// choose level from single player state
		else if (number == 1 && menu == 1){
			if(gameHandle.getGame().getKeyManager().menuUp)
			{
				//System.out.println("called");
				select--;
				if (select < 0)
				{
					select = choose.length -1;
				}
			}
			else if(gameHandle.getGame().getKeyManager().menuDown)
			{
				select++;
				if (select >= choose.length)
				{
					select = 0;
				}
			}
			//select the level
			if (select == 0 && gameHandle.getGame().getKeyManager().enter){
				selection = 0;
				select = 0;
				number = 0;
				Logs.getInstance().Process("Select Level 1 in Single Player Mode");
				State.setState(gameHandle.getGame().getCountState());
				gameHandle.setGameMap(map1);
				gameHandle.setGameMode("single");
				gameHandle.getGame().getCountState().loadWorld();
			}
			if (select == 1 && gameHandle.getGame().getKeyManager().enter){
				selection = 1;
				select = 0;
				number = 0;
				Logs.getInstance().Process("Select Level 2 in Single Player Mode");
				State.setState(gameHandle.getGame().getCountState());
				gameHandle.setGameMap(map2);
				gameHandle.setGameMode("single");
				gameHandle.getGame().getCountState().loadWorld();
			}
			if (select == 2 && gameHandle.getGame().getKeyManager().enter){
				selection = 2;
				number = 0;
				select = 0;
				Logs.getInstance().Process("Select Level 3 in Single Player Mode");
				State.setState(gameHandle.getGame().getCountState());
				gameHandle.setGameMap(map3);
				gameHandle.setGameMode("single");
				gameHandle.getGame().getCountState().loadWorld();
			}
			if (select == 3 && gameHandle.getGame().getKeyManager().enter){
				selection = 3;
				number = 0;
				select = 0;
				Logs.getInstance().Process("Select Level 4 in Single Player Mode");
				State.setState(gameHandle.getGame().getCountState());
				gameHandle.setGameMap(map4);
				gameHandle.setGameMode("single");
				gameHandle.getGame().getCountState().loadWorld();
			}
			if (select == 4 && gameHandle.getGame().getKeyManager().enter){
				number = 0;
				select = 0;
				Logs.getInstance().Process("Back to Main Menu");
			}
			if (gameHandle.getGame().getKeyManager().exit){
				Logs.getInstance().Process("Exit Game");
				System.exit(0);
				}
		}
		//choose level from multi-player state
		else if (number == 1 && menu == 2){
			if(gameHandle.getGame().getKeyManager().menuUp) {
				//System.out.println("called");
				select--;
				if (select < 0) {
					select = choose.length -1;
				}
			}
			else if(gameHandle.getGame().getKeyManager().menuDown) {
				select++;
				if (select >= choose.length) {
					select = 0;
				}
			}
			//select the level
			if (select == 0 && gameHandle.getGame().getKeyManager().enter){
				Logs.getInstance().Process("Select Level 1 in MultiPlayer Mode");
				State.setState(gameHandle.getGame().getCountState());
				gameHandle.setGameMap(map1);
				gameHandle.setGameMode("localMult");
				gameHandle.getGame().getCountState().loadWorld();
			}
			if (select == 1 && gameHandle.getGame().getKeyManager().enter){
				Logs.getInstance().Process("Select Level 2 in MultiPlayer Mode");
				State.setState(gameHandle.getGame().getCountState());
				gameHandle.setGameMap(map2);
				gameHandle.setGameMode("localMult");
				gameHandle.getGame().getCountState().loadWorld();
			}
			if (select == 2 && gameHandle.getGame().getKeyManager().enter){
				Logs.getInstance().Process("Select Level 3 in MultiPlayer Mode");
				State.setState(gameHandle.getGame().getCountState());
				gameHandle.setGameMap(map3);
				gameHandle.setGameMode("localMult");
				gameHandle.getGame().getCountState().loadWorld();
			}
			if (select == 3 && gameHandle.getGame().getKeyManager().enter){
				selection = 3;
				number = 0;
				select = 0;
				Logs.getInstance().Process("Select Level 4 in Multi Player Mode");
				State.setState(gameHandle.getGame().getCountState());
				gameHandle.setGameMap(map4);
				gameHandle.setGameMode("localMult");
				gameHandle.getGame().getCountState().loadWorld();
			}
			if (select == 4 && gameHandle.getGame().getKeyManager().enter){
				Logs.getInstance().Process("Exit Game");
				number = 0;
			}
			if (gameHandle.getGame().getKeyManager().exit){
				System.exit(0);
				Logs.getInstance().Process("Exit Game");
				}
		}
		
		else if (number == 1 && menu == 3){//online mode
			if(gameHandle.getGame().getKeyManager().menuUp) {
				//System.out.println("called");
				select--;
				if (select < 0) {
					select = choose.length -1;
				}
			}
			else if(gameHandle.getGame().getKeyManager().menuDown) {
				select++;
				if (select >= choose.length) {
					select = 0;
				}
			}
			//select the level
			if (select == 0 && gameHandle.getGame().getKeyManager().enter){
				Logs.getInstance().Process("Select Level 1 in Online MultiPlayer Mode");
				State.setState(gameHandle.getGame().getCountState());
				gameHandle.setGameMap(map1);
				gameHandle.setGameMode("onlineMulti");
				gameHandle.getGame().getCountState().loadWorld();
			}
			if (select == 1 && gameHandle.getGame().getKeyManager().enter){
				Logs.getInstance().Process("Select Level 2 in Online MultiPlayer Mode");
				State.setState(gameHandle.getGame().getCountState());
				gameHandle.setGameMap(map2);
				gameHandle.setGameMode("onlineMulti");
				gameHandle.getGame().getCountState().loadWorld();
			}
			if (select == 2 && gameHandle.getGame().getKeyManager().enter){
				Logs.getInstance().Process("Select Level 3 in Online MultiPlayer Mode");
				State.setState(gameHandle.getGame().getCountState());
				gameHandle.setGameMap(map3);
				gameHandle.setGameMode("onlineMulti");
				gameHandle.getGame().getCountState().loadWorld();
			}
			if (select == 3 && gameHandle.getGame().getKeyManager().enter){
				selection = 3;
				number = 0;
				select = 0;
				Logs.getInstance().Process("Select Level 4 in Online Multi Player Mode");
				State.setState(gameHandle.getGame().getCountState());
				gameHandle.setGameMap(map4);
				gameHandle.setGameMode("onlineMulti");
				gameHandle.getGame().getCountState().loadWorld();
			}
			if (select == 4 && gameHandle.getGame().getKeyManager().enter){
				Logs.getInstance().Process("Exit Game");
				number = 0;
			}
			if (gameHandle.getGame().getKeyManager().exit){
				System.exit(0);
				Logs.getInstance().Process("Exit Game");
				}
		}
	}
	
	private void startSocketManager() {
		gameHandle.getGame().setSocketManager(new SocketManager(gameHandle));
	}
	
	private int startPListener() {
		BlockingQueue<String> queue = new ArrayBlockingQueue<>(10);
		gameHandle.getGame().setQueue(queue);
		
		int j_listen_bind_port = 0;
		PythonListener pListener = new PythonListener(queue, j_listen_bind_port);
		pListener.start();
		int j_listen_port = pListener.getPort();
		System.out.println("listening for connections on addr: "+ pListener.getIP() + " port: " + j_listen_port);
		
		return j_listen_port;
	}
	
	private void startServer(int j_listen_port) {
		int myCherryPort = gameHandle.getGame().getMyCherryPort();
		int oCherryPort = gameHandle.getGame().getOCherryPort();
		
		Process proc = null;
		//represent bash command as a string[]
		String[] args = {
			"/bin/sh",
			"-c",
			//need to specify cherrypy_example.py relative to /game directory
			"python -u ../webserver/ce_server_launcher.py" + " " + j_listen_port + " " + myCherryPort + " " + oCherryPort
		};
		try {
			PythonStarter myRunner = new PythonStarter(args);
			myRunner.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
}