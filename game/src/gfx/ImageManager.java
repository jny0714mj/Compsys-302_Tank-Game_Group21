/**
 * A utility class that is simply used to load images
 */

package gfx;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

public class ImageManager {
	
	public static BufferedImage loadImage (String path)
	{
		try {
			return ImageIO.read(ImageManager.class.getResource(path));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(1);
		}
		return null;
	}
}