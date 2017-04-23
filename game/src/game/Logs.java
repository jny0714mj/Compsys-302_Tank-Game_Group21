/**
 * This class logs all game events and saves a textfile- MyLogFile.text.
 * For efficiency, it runs on its own thread
 */

package game;

import java.io.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class Logs implements Runnable {
	private BlockingQueue<String> logQueue = new ArrayBlockingQueue<String>(100);
	private Logger logger = Logger.getLogger("MyLog");
	private FileHandler fh;
	private Thread thread;

	// ============================================================================================
	// Singleton pattern
	private static Logs instance;
	public static Logs getInstance() {
		if (instance == null) {
			instance = new Logs();
		}
		return instance;
	}

	static String s = "GAME START";

	private Logs() {
		try {
			fh = new FileHandler("MyLogFile.text");
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		logger.addHandler(fh);
		SimpleFormatter formatter = new SimpleFormatter();
		fh.setFormatter(formatter);
		thread = new Thread(this);
		thread.start();
	}

	// ============================================================================================

	public void Process(String msg) {
		System.out.println(logQueue.size());
		logQueue.add(msg);
	}

	@Override
	public void run() {
		while (true) {
			if(logQueue.peek() != null){
				try {

					logger.info(logQueue.take());
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
}
