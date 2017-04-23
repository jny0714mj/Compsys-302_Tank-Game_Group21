/**
 * Manages the background music in the game and menu.
 * Music runs on its own thread for efficiency.
 */

package music;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

public class Music implements Runnable {
	
	private Clip clip;
	private String musicPath;
	private Thread thread;
	private boolean playing = false;
	
	public Music(String musicPath) {
		this.musicPath = musicPath;
	}

	@Override
	public void run() {
		try {
			clip = AudioSystem.getClip();
			AudioInputStream inputStream = AudioSystem.getAudioInputStream(getClass().getResourceAsStream(musicPath));
			clip.open(inputStream);
			clip.start();
			clip.loop(Clip.LOOP_CONTINUOUSLY);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public synchronized void start() {
		if (playing)
			return;
		playing = true;
		thread = new Thread(this);
		thread.start();
	}
	
	public synchronized void stop() {
		if(!playing)
			return;
		playing = false;
		try {
			thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		clip.close();
	}

}
