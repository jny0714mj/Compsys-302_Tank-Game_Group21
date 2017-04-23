/*
 * Starts a python script in a new process using runtime exec (or alternatively, process builder (hidden))
 * This class runs in it's own thread, which just waits for the python process to complete
 * In order to print python to console, reading from the inputStream and errorStream of python has to be 
 * done simultaneously, and so is threaded.
 * 
 *  This function is called from Menu when the user presses "Online Multiplayer"
 */
package ipc;

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.ProcessBuilder.Redirect;
import java.net.URI;
import java.net.URISyntaxException;

public class PythonStarter implements Runnable {
	private String[] args;
	private boolean running = false;
	private Thread thread;
	private BufferedReader br;
	private Process proc;
	private Process p = null;
	private ReadStream s1;
	private ReadStream s2;
	private String line;
	
	public PythonStarter(String[] args) {
		this.args = args;
	}
	
	public void run() {
		try {
			p = Runtime.getRuntime().exec(args);
			s1 = new ReadStream("stdin", p.getInputStream());
			s2 = new ReadStream("cherrypy", p.getErrorStream());
			s1.start();
			s2.start();
			
//			// alternatively, automatically merge input stream
//			System.out.println("Starting python script");
//			ProcessBuilder pb = new ProcessBuilder(args);
//			pb.redirectOutput(Redirect.INHERIT);
//			pb.redirectError(Redirect.INHERIT);
//			p = pb.start();
////			System.out.println("pb running");
////			//buffered reader to read the output of the command
//			BufferedReader br = new BufferedReader(new OutputStreamReader(p.getOutputStream()));
//			
//			//String line = "";
//			System.out.println("reading lines");
//			String line = br.readLine();
//			while(true) {
//				System.out.println("read line");
//				// System.out.print(line + "\n");
//				
//				if (line == null) {
//					continue;
//				}
//	
//				//if the output contains "http://" then open default browser and navigate to given ip address.
//				if(line.contains("http://")){
//					System.out.println("found the line i want");
//					if(Desktop.isDesktopSupported()){
//						Desktop.getDesktop().browse(new URI(line));
//						System.out.println("open browser");
//						break;
//					} else {
//						System.out.println("!! Was not able to open browser, please do so yourself");
//						System.out.println(line);
//					}
//				}
//				line = br.readLine();
//			}
			
			p.waitFor();	//block thread while python process runs
			System.out.println("Python script is done");
//			s1 = new ReadStream("stdin", p.getInputStream());
//			s1.start();
//			
//			//proc.getOutputStream().flush();
//			proc.waitFor();
		} catch (Exception e) {  
			e.printStackTrace();  
		} finally {
		    if(proc != null)
		        proc.destroy();
		}
	}
	
	public synchronized void start() {
		if (running)
			return;
		running = true;
		thread = new Thread(this);
		thread.start();
	}
}
