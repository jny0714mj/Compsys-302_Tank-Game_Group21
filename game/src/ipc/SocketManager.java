/*
 * Primarily used as first stop to send messages to python. sendMsg sends the message on a new PythonSender stream,
 * so as not to slow down gameplay.
 * This function also handles messages sent from python and put into array blocking queue.
 * Every tick it continually polls the message queue until it is empty, and deals with these messages
 */
package ipc;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import game.GameHandle;
import gfx.ImageManager;
import levels.World;
import objs.BaseObject;
import objs.DumbPlayer;
import objs.SlaveObject;
import objs.Tank;
import objs.Wall;

public class SocketManager {
	
	private Socket soc;
	private DataOutputStream dout;
	private PrintWriter out;
	private GameHandle gameHandle;
	private int p_send_port;
	
	
	public SocketManager(GameHandle gameHandle) {
		this.gameHandle = gameHandle;
	}
	
	
	public void sendMsgToPython(String msg) {
		// SENDS A MSG TO PYTHON- threaded
		if (p_send_port > 0) {
			PythonSender pSender = new PythonSender(p_send_port, msg);
			pSender.start();
		} else {
			System.out.println("Tried to send a method to python without initialising port");
			throw new IllegalArgumentException("Bad port");
		}
	}
	
	public void tick() {
		// HANDLES MSG SEND FROM PYTHON AND PUT INTO QUEUE BY SERVERSOCKET
		
		String msg, head, body;
		String[] parts;
		
		// continually poll queue and then handle result
		msg = gameHandle.getGame().getQueue().poll();
		
		while (msg != null) {
			
//			if (msg == null) {
//				// no message
//				return;
//			}
			
			try {
				System.out.println("message is " + msg);
		        parts = msg.split("\\?");		
		        head = parts[0];
		        body = parts[1];
		        
		        switch (head) {
		        case "0": 
		        	handleReceiveKey(body);
		        	break;
		        case "1": 
		        	handleReceiveObject(body);
		        	break;
		        case "8":
		        	handleEcho(body);
		        	break;
		        case "9":
		        	handleNewPort(body);
		        	break;
		        default: 
		        	throw new IllegalStateException("I don't recognise this message head");
		        }
			} catch (Exception e) {
				System.out.println("I cannot handle this message!");
				continue;
			} finally {
				msg = gameHandle.getGame().getQueue().poll();
			}
		}
		
	}
	
	private void handleEcho(String body) {
		System.out.println("Echo recieved: " + body);
	}
	
	private void handleNewPort(String body) {
		p_send_port = Integer.parseInt(body);
		System.out.println("Setting port to send to python as " + p_send_port);
	}
	
	private void handleReceiveKey(String body) {
		// runs on master. moves dumbplayer using key input from slave
		String[] parts;
		String keyType, value;
		DumbPlayer dP;
		
		// get dumbplayer using downcasting with check
		try {
			Tank tank = gameHandle.getGameWorld().getPlayerList().get(1);
			dP = (DumbPlayer) tank;
		} catch (Exception e) {
			System.out.println("Could not retrieve slave player");
			return;
		}
		
		// parse arguments
		try {
			// will come in form keyType&value
			parts = body.split("&");
			keyType = parts[0];
			value = parts[1];
		} catch (Exception e) {
			System.out.println("Could not handle this message's arguments");
			return;
		}
		
		System.out.println("Slave player got a message!!");
		if (keyType.equals("0") && value.equals("1"))  {
			dP.setDoLeft(true);
		} else if (keyType.equals("0") && value.equals("0")) {
			dP.setDoLeft(false);
		} else if (keyType.equals("1") && value.equals("1")) {
			dP.setDoRight(true);
		} else if (keyType.equals("1") && value.equals("0")) {
			dP.setDoRight(false);
		} else if (keyType.equals("2") && value.equals("1")) {
			dP.setDoUp(true);
		} else if (keyType.equals("2") && value.equals("0")) {
			dP.setDoUp(false);
		} else if (keyType.equals("3") && value.equals("1")) {
			dP.setDoDown(true);
		} else if (keyType.equals("3") && value.equals("0")) {
			dP.setDoDown(false);
		} else if (keyType.equals("4") && value.equals("1")) {
			dP.setDoShoot(true);
		} else if (keyType.equals("4") && value.equals("0")) {
			dP.setDoShoot(false);
		}
		
	}
	
	private void handleReceiveObject(String body) {
		System.out.println("receive object");
		// runs on slave. allows slave to update positions based on computation from master
		String[] parts;
		String id;
		int type, state, alive;
		Float x, y, a;
		World gW = gameHandle.getGameWorld();
		
		try {
			// parse object
			// will come in form id&type&x&y&a&state&alive
			parts = body.split("&");
			id = parts[0];
			type = Integer.parseInt(parts[1]);
			x = Float.parseFloat(parts[2]);
			y = Float.parseFloat(parts[3]);
			a = Float.parseFloat(parts[4]);
			state = Integer.parseInt(parts[5]);
			alive = Integer.parseInt(parts[6]);
		} catch (Exception e) {
			System.out.println("Could not handle this message's arguments");
			return;
		}
		
		//use this to update a list of objects or create a list of objects
		// probably need to just render "a thing" using only the information above
		
		// TODO:: test for player cheating- ie xNow - xBefore is small
		// if is new object, create.
		// else, get object, update positions
		
		BaseObject recvObj = gW.getObjectById(id);
		if (recvObj == null) {
			System.out.println("new obj");
			// NEW SLAVE OBJECT, create
//			if (type == 0) {
//				//DumbPlayer(GameHandle gameHandle, float x, float y, float a, BufferedImage skin, boolean toRandPos)
//				DumbPlayer dp = new DumbPlayer(gameHandle, x, y, a, ImageManager.loadImage("/imgs/tank/tank_grey.png"), false);
//				dp.setState(state);
//				dp.setId(id);			//overrides generated id
//				dp.setAlive(alive);
//				gW.addPlayer(dp);
//			}
			
//			SlaveObject(GameHandle gameHandle, float x, float y, float a, int type, int state, int alive, String id)
			if (type == 0) {
				// make a tank
			}
			gW.addObject(new SlaveObject(gameHandle, x, y, a, type, state, alive, id));
			// still need to make a dumbplayer so that it actually moves
		} else {
			System.out.println("I know this obj!");
			// EXISTING SLAVE OBJ, update pos
			// only update is pos has changed
			recvObj.setX(x);
			recvObj.setY(y);
			recvObj.setA(a);
			recvObj.setAlive(alive);
			recvObj.setState(state);
		}
	}
	
	
}
