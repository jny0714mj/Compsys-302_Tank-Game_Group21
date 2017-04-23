/**
 * This class houses the main game loop and deals with game mechanics. 
 * When start() is called, a new thread begins run().
 * The game loop updates FPS times per second. It simply calls State.tick() and State.render(g) which updates and repaints objects respectively from the current state
 */

package game;

import java.awt.Graphics;
import java.awt.image.BufferStrategy;
import java.util.concurrent.BlockingQueue;

import ipc.SocketManager;
import states.*;

public class Game implements Runnable {

	public final int FPS = 30;
	public final int width, height;
	public String title;
	private boolean running = false;
	private final boolean showFPScounter = false;
	private Thread thread;
	private GameHandle gameHandle;
	private Display display;
	private BufferStrategy bs;
	private Graphics g;
	private KeyManager keyManager;
	
	private SocketManager socketManager;
	private BlockingQueue<String> queue;
	
	private boolean isOnline;
	private int myCherryPort, oCherryPort;
	
	//implement states
	private State gameSate;
	private State pauseState;
	private State welcomeState;
	private State countState;
	private State exitState;
	private State helpState;
	private State endGameState;
	static Logs log;
	
	public Game(String title, int width, int height, int myCherryPort, int oCherryPort){
		this.width = width;
		this.height = height;
		this.title = title;
		this.isOnline = false;
		this.myCherryPort = myCherryPort;
		this.oCherryPort = oCherryPort;
//		this.queue = q;	//set when pythonListener is init in Menu.java
	}
	
	// Called when game begins running
	private void init() {
		
		// set overarching handle- only one exists
		gameHandle = new GameHandle(this);
		// create JFrame and canvas
		display = new Display(title, width, height);
		// listen for button presses on JFrame
		keyManager = new KeyManager(gameHandle);
		display.getFrame().addKeyListener(keyManager);
		
		// initialize states- tick and render is passed to here.
		pauseState = new PauseState(gameHandle);
		welcomeState = new WelcomeState(gameHandle);
		gameSate = new GameState(gameHandle);
		countState = new CountState(gameHandle);
		exitState = new ExitState(gameHandle);
		//multiPlayerState = new MultiPlayerState(gameHandle);
		helpState = new HelpState(gameHandle);
		endGameState = new EndGameState(gameHandle);
		State.setState(welcomeState);
		
		// only need this 
		//socketManager = new SocketManager(gameHandle);
	}
	
	// UPDATE POSITIONS, GAME INFO
	private void tick() {
		
		keyManager.tick(); 		//update key info in keyManager.keys[]
		
		if (socketManager != null) {
			socketManager.tick(); 	//listen for connections from python
		}
		
		if(State.getState() != null) {
			State.getState().tick();
		}
	}
	
	// REPAINT GRAPHICS
	private void render() {
		bs = display.getCanvas().getBufferStrategy();
		if (bs == null) {
			display.getCanvas().createBufferStrategy(3);
			return;
		}
		g = bs.getDrawGraphics();
		//Clear Screen
		g.clearRect(0, 0, width, height);
		
		//RENDER
		
		if(State.getState() != null)
			State.getState().render(g);
		
		//End Drawing!
		bs.show();
		g.dispose();
	}
	
	
	// GAMELOOP
	public void run() {
		init();
		
		double timePerTick = 1000000000 / FPS;
		double delta = 0;
		long now;
		long lastTime = System.nanoTime();
		long timer = 0;
		int ticks = 0;
		
		while(running){
			now = System.nanoTime();
			delta += (now - lastTime) / timePerTick;
			timer += now - lastTime;
			lastTime = now;
			
			if(delta >= 1){
				tick();
				render();
				ticks++;
				delta--;
			}
			
			if(showFPScounter) {
				if(timer >= 1000000000){
					System.out.println("Ticks and Frames: " + ticks);
					ticks = 0;
					timer = 0;
				}
			}
		}
		
		stop();
		
	}
	
	public synchronized void start() {
		if (running)
			return;
		running = true;
		thread = new Thread(this);
		thread.start();
	}
	
	public synchronized void stop() {
		if(!running)
			return;
		running = false;
		try {
			thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	
	// GETTERS and SETTERS
	
	public int getMyCherryPort() {
		return myCherryPort;
	}

	public void setMyCherryPort(int myCherryPort) {
		this.myCherryPort = myCherryPort;
	}

	public int getOCherryPort() {
		return oCherryPort;
	}

	public void setOCherryPort(int oCherryPort) {
		this.oCherryPort = oCherryPort;
	}

	public BlockingQueue<String> getQueue() {
		if (queue == null) {
			System.out.println("Tried to get queue and it doesn't exist");
			throw new NullPointerException();
		}
		return queue;
	}

	public void setQueue(BlockingQueue<String> queue) {
		this.queue = queue;
	}

	public KeyManager getKeyManager() {
		return keyManager;
	}

	public SocketManager getSocketManager() {
		if (socketManager == null) {
			System.out.println("Tried to get socketManager and it doesn't exist");
			throw new NullPointerException();
		}
		return socketManager;
	}

	public void setSocketManager(SocketManager socketManager) {
		this.socketManager = socketManager;
	}

	public State getPauseState() {
		return pauseState;
	}

	public void setPauseState(State pauseState) {
		this.pauseState = pauseState;
	}
	
	public State getExitState() {
		return exitState;
	}

	public void setExitState(State exitState) {
		this.exitState = exitState;
	}

	public State getSinglePlayerState() {
		return this.gameSate;
	}
	
	public State getCountState(){
		return this.countState;
	}
	
	public State getWelcomeState(){
		return this.welcomeState;
	}
	
	public State getHelpState(){
		return this.helpState;
	}
	
	public State getEndGameState(){
		return this.endGameState;
	}
	
	public State getState() {
		return State.getState();
	}
	
}





