/*
 * Reads a particular stream in it's own thread
 * Used to read pythons inputStream and errorStream simultaneously.
 * Used by PythonStarter.java
 */

package ipc;

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;

public class ReadStream implements Runnable {
    String name;
    InputStream is;
    Thread thread;
    boolean urlOpened = false;
    
    public ReadStream(String name, InputStream is) {
        this.name = name;
        this.is = is;
    }       
    public void start () {
        thread = new Thread (this);
        thread.start ();
    }       
    public void run () {
        try {
            InputStreamReader isr = new InputStreamReader (is);
            BufferedReader br = new BufferedReader (isr);   
            while (true) {
                String s = br.readLine ();
                if (s == null) break;
                System.out.println ("[" + name + "] " + s);
                
				if(s.contains("http://") && !urlOpened && name.equals("stdin")){
					if(Desktop.isDesktopSupported()){
						System.out.println("Opening url");
						Desktop.getDesktop().browse(new URI(s));
						urlOpened = true;
					} else {
						System.out.println("!! Was not able to open browser, please do so yourself");
						System.out.println(s);
					}
				}
                
            }
            is.close ();    
        } catch (Exception ex) {
            System.out.println ("Problem reading stream " + name + "... :" + ex);
            ex.printStackTrace ();
        }
    }
}