package small.data.structures;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class Buffer {
	// stores (row-major) indices of current map
//	public List<Vec2> buffer = new ArrayList<>();
	
	/**
	 * Integer row-major grid index of square
	 * Vec2 relative position of square within grid
	 */
	public HashMap<Integer, Vec2> map;
	
	// leftmost upper most square pointed to by the buffer
	public Vec2 relativeOrigin;

	private int gridCols;
	private int gridRows;

	public Buffer(int cols, int rows) {
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
			System.out.println("Could not add to buffer. Grid square is out of range.");
		}
		
		if (map.containsKey(idx)) {
			System.out.println("Could not add to buffer. Key already exists in buffer.");
			return;
		}
		
		map.put(idx, gridPos);
		
		// check whether relativeOrigin needs updating
		if (gridPos.x < relativeOrigin.x) {
			System.out.println("Updated relativeOrigin in buffer");
			relativeOrigin = gridPos;
		} else if (gridPos.x == relativeOrigin.x && gridPos.y < relativeOrigin.y) {
			System.out.println("Updated relativeOrigin in buffer");
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
			System.out.println("Could not remove from buffer. Key is not in buffer.");
			return;
		}
		
		// check whether relativeOrigin needs updating
		if (gridPos.equals(relativeOrigin)) {
			System.out.println("Removing current relative origin requires the relative origin to be updated.");
			System.out.println("Updating relative origin.");
			
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
			System.out.println("Removing index "+idx+" from buffer.");
			map.remove(idx);
		} else {
			System.out.println("Could not find index "+idx+" to remove from buffer");
		}
	}
	
	public void flush() {
		System.out.println("Flush buffer");
		map = new HashMap<>();
		relativeOrigin = new Vec2(gridCols - 1, gridRows - 1);
	}
	
	public Set<Integer> getKeySet() {
		return map.keySet();
	}
	
	public HashMap<Integer, Vec2> getMap() {
		return map;
	}
	
	public Vec2 getRelativeOrigin() {
		return relativeOrigin;
	}
	
	public void setRelativeOrigin(Vec2 manualOverride) {
		relativeOrigin = manualOverride;
	}
}
