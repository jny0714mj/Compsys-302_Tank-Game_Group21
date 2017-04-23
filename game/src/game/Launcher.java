/**
 * Contains the main class. Simply starts a new game.
 */

package game;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class Launcher {
	public static boolean isMaster, isSlave;
	private static int myCherryPort, oCherryPort;

	public static void main(String[] args){
		
		if (args.length > 0) {
			if (args[0].equals("master")) {
				isMaster = true;
				isSlave = false;
				System.out.println("i'm the master");
			} else if (args[0].equals("slave")) {
				isMaster = false;
				isSlave = true;
				System.out.println("i'm the slave");
			}
			myCherryPort = Integer.parseInt((args[1]));
			oCherryPort = Integer.parseInt(args[2]);
			
		} else {
			// probably will break some shit
			isMaster = true;
			isSlave = true;
		}
				
//		BlockingQueue<String> queue = new ArrayBlockingQueue<>(10);
		Game game = new Game("Combat Evolved", 1024, 768, myCherryPort, oCherryPort);
		game.start();
		
//		PythonListener pListener = new PythonListener(queue, 14001);
//		pListener.start();
	}
	
}