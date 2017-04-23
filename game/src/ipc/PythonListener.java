/* This functions runs in it's own thread and continually listens to connections from python
 * The messages sent from python should have first line as name of client API method that sent it, 
 * and remaining lines corresponding to function parameters, in order
 * eg.
 * 
 * receiveObject
 * <sender string>
 * <objectID string>
 * <objType string>
 * <x int>
 * <y int>
 * <ang int>
 * <stat int>
 * <alive int>
 * 
 * Each API function needs an invoking method on python, and a handling method on java.
 * eg sendObject runs on master, /receiveObject runs on slave,  updateObj runs on java under tick
 * This thread is started under Menu.java when player selects Online Multiplayer
 */

package ipc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Objects;
import java.util.concurrent.BlockingQueue;

public class PythonListener implements Runnable{
	
	private ServerSocket server;
	private String msg;
	private boolean running = false;
	private Thread thread;
	private int port;
	private BlockingQueue<String> queue;

	public PythonListener(BlockingQueue<String> q, int p) {
		this.queue = q;
		this.port = p;
		try {
			this.server = new ServerSocket(port, 0, InetAddress.getByName("localhost"));
		} catch (IOException e) {
			System.out.println("I could not bind to this port: " + server.getLocalPort());
			e.printStackTrace();
		}
	}
	
	@Override
	public void run() {
		// constantly listens to connections from python prints message when it does
        running = true;
        while(running) {
        	try (
	            Socket client = server.accept();	// blocking
	            BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
	            PrintWriter out = new PrintWriter(client.getOutputStream(),true);
			) { 
	            msg = in.readLine();
	            queue.put(msg);
	            System.out.println("got conn on port : " + server.getLocalPort() + ". Received and put into queue: " + msg);
        	} catch (Exception e) {
                System.out.println("Exception caught when trying to listen on port " + server.getLocalPort() + " or listening for a connection, or trying to put into queue");
                System.out.println(e.getMessage());

        	}
        }
	}
	
	public synchronized void start() {
		if (running)
			return;
		running = true;
		thread = new Thread(this);
		thread.start();
	}
	
	public int getPort() {
		return server.getLocalPort();
	}
	
	public InetAddress getIP() {
		return server.getInetAddress();
	}
	
}
