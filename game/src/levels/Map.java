/**
 * This class is the parent of all maps. It simply ensures that all maps implement an init() state so they may be loaded correctly
 */

package levels;

public abstract class Map {
	public abstract void init();
	
	protected final int scale = 12;
}
