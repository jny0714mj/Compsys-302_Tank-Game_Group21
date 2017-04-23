/**
 * Manages the sound affects in the game and menu
 */

package music;

import java.applet.Applet;
import java.applet.AudioClip;

public class SoundEffects {
	
	public static AudioClip shot = instance().loadEffects("/sounds/gun-gunshot-01.wav");
	public static AudioClip explode = instance().loadEffects("/sounds/explode.wav");
	public static AudioClip item = instance().loadEffects("/sounds/item.wav");
	
	public AudioClip loadEffects(String path)
	{
		return Applet.newAudioClip(getClass().getResource(path));
	}
	
	private volatile static SoundEffects instance;
	
	public static SoundEffects instance()
	{
		if (instance == null)
			instance = new SoundEffects();
		return instance;
	}
}
