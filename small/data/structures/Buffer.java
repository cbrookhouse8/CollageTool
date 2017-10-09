package small.data.structures;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import utilities.Logger;

/**
 * Holds the squares selected from the source grid
 * to be placed in the target grid
 * 
 * Once the square indices have been inserted into
 * the target grid's map, then the buffer is flushed
 */
public class Buffer {
	
	/**
	 * Integer row-major grid index of square
	 * Vec2 relative position of square within grid
	 */
	public HashMap<Integer, Vec2> map;
	public HashMap<Integer, Vec2> previousMap;
	
	// leftmost upper most square pointed to by the buffer
	public Vec2 relativeOrigin;

	private int gridCols;
	private int gridRows;
	
	private Logger log;

	public Buffer(int cols, int rows) {
		log = new Logger(this);
		gridCols = cols;
		gridRows = rows;
		
		// initialize class variables
		flush();
	}
	
	/**
	 * 
	 * @param gridPos relative position within the grid
	 */
	public void add(Vec2 gridPos) {
		
		// get index
		int idx = gridPos.y * gridRows + gridPos.x;
		
		if (idx < 0 || idx > gridCols * gridRows - 1) {
			log.info("Could not add. Grid square is out of range.");
		}
		
		// May be better to use a HashSet here
		if (map.containsKey(idx)) {
			log.info("Could not add. Key already exists in buffer.");
			return;
		}
		
		map.put(idx, gridPos);
		
		// check whether relativeOrigin needs updating
		if (gridPos.x < relativeOrigin.x) {
			log.info("Updated relativeOrigin");
			relativeOrigin = gridPos;
		} else if (gridPos.x == relativeOrigin.x && gridPos.y < relativeOrigin.y) {
			log.info("Updated relativeOrigin");
			relativeOrigin = gridPos;
		}
		
	}
	
	/**
	 * 
	 * @param gridPos relative position within the grid
	 */
	public void remove(Vec2 gridPos) {
		
		// get index
		int idx = gridPos.y * gridRows + gridPos.x;

		if (!map.containsKey(idx)) {
			log.info("Could not remove. Key is not in buffer.");
			return;
		}
		
		// check whether relativeOrigin needs updating
		if (gridPos.equals(relativeOrigin)) {
			log.info("Removing current relative origin requires the relative origin to be updated.");
			log.info("Updating relative origin.");
			
			// find a better way of doing this
			
			// start bottom right
			relativeOrigin = new Vec2(gridCols - 1, gridRows - 1);
			for (Vec2 v : map.values()) {
			    if (v.x < relativeOrigin.x) {
			    	relativeOrigin = v;
			    } else if (v.x == relativeOrigin.x && v.y < relativeOrigin.y) {
			    	relativeOrigin = v;
			    }
			}
		}
		
		map.remove(idx);
	}
	
	public boolean containsIndex(int idx) {
		return map.containsKey(idx);
	}
	
	public boolean containsPosition(Vec2 pos) {
		// get index
		int idx = pos.y * gridRows + pos.x;
		return map.containsKey(idx);
	}
	
	public void removeIndex(int idx) {
		if (map.containsKey(idx)) {
			log.info("Removing index "+idx);
			map.remove(idx);
		} else {
			log.info("Could not find index "+idx+" to remove from buffer");
		}
	}
	
	public void flush() {
		log.info("Flush");
		
		previousMap = new HashMap<>();
		
		// keep a copy of the current map
		if (map != null && map.size() != 0) {
		    for (Map.Entry<Integer, Vec2> entry : map.entrySet()) {
		    		previousMap.put(entry.getKey(), entry.getValue());
		    }
		}
		
		// reset the map
		map = new HashMap<>();
		relativeOrigin = new Vec2(gridCols - 1, gridRows - 1);
	}
	
	public Set<Integer> getKeySet() {
		return map.keySet();
	}
	
	public Set<Integer> getPreviousKeySet() {
		return previousMap.keySet();
	}
	
	/**
	 * Integer row-major grid index of square
	 * Vec2 relative position of square within grid
	 */
	
	/**
	 * 
	 * @return
	 */
	public HashMap<Integer, Vec2> getMap() {
		return map;
	}
	
	public HashMap<Integer, Vec2> getPreviousMap() {
		return previousMap;
	}

	/**
	 * @return buffer entry position that would be top left relative to source grid space
	 */
	public Vec2 getRelativeOrigin() {
		return relativeOrigin;
	}
	
	public void setRelativeOrigin(Vec2 manualOverride) {
		relativeOrigin = manualOverride;
	}
	
}
