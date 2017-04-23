/**
 * A utility class that simply acts as a timer. TODO: UNFINISHED
 */

package game;

public class Timer {
	
	private long endTime;
	private long timeStart;
	private long currentTime;
	private boolean timerUp = false;
	
	public Timer(long endTime) {
		this.endTime = endTime;
		timeStart = System.currentTimeMillis();
	}
	
	public void startTimer() {
	}
	
	public void tick() {
		currentTime = System.currentTimeMillis() - timeStart;
	}
	
	public void check() {
		
	}
	
	
	
}
