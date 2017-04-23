/*
 * Connects and sends a single string message to python in it's own thread.
 * Called by SocketManager.java
 */
package ipc;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class PythonSender implements Runnable {
	private Thread thread;
	private String msg;
	private int p_send_port;
	private Socket soc;
	private PrintWriter out;
	private boolean running = false;
	
	public PythonSender(int p_send_port, String msg) {
		this.msg = msg;
		this.p_send_port = p_send_port;
	}
	
	@Override
	public void run() {
		// just sends a message to python
        running = true;
		try {
			// create a socket and connect it to python server.
			// NB: p_send_port must be set with handleNewPort first
			System.out.println("sending message: " + msg + " to port: " + p_send_port);
		    soc = new Socket("localhost", p_send_port);
		   			
		    out = new PrintWriter(soc.getOutputStream(), true); //best for sending text data
		    out.println(msg);
		    out.flush();
		    
		    // equiv to
		    // dout = new DataOutputStream(soc.getOutputStream());	//best for sending binary data		    
			// dout.writeUTF(msg);
			// dout.flush();
		    
		} catch (Exception e) {
			System.out.println("Could not send message to port: " + p_send_port);
			e.printStackTrace();
		} finally {
			stop();
		}
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
		thread.interrupt();
	}
}
