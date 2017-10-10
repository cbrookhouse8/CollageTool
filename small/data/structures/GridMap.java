package small.data.structures;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

import utilities.Logger;

/**
 * Boolean Matrix as a Map from one grid to another 
 * with the constraint that a column may only have
 * a single non-zero value in it but a row may have
 * any number of 1s in it.
 * 
 * The rows represent the squares of the source grid
 * The columns represent the squares of the target grid
 * 
 * Each row may have values in multiple columns
 * 
 * But each column will have only a single value in it
 * 
 * This is because a single source grid square can be mapped
 * to multiple target grid squares but a single target grid
 * square can only display a single source grid square.
 */
public class GridMap {
	
	/**
	 * Boolean Matrix
	 * 
	 * [cols][rows] like [x][y]
	 * 
	 * [targetIdx][sourceIdx]
	 */
	private int[][] map;
	private final Logger log;
	
	/**
	 * The map is always represented by a square matrix
	 * @param span length of unwrapped input grid
	 */
	public GridMap(int span) {
		log = new Logger(this);
		resetMap(span);
	}
	
	public static GridMap of(int gridCols, int gridRows) {
		int span = gridCols * gridRows;
		return new GridMap(span);
	}
	
	private void resetMap(int side) {
		this.map = new int[side][side];
		
		// These lines potentially unnecessary as
		// by default, initialisation is to 0
		
//		for (int j = 0; j < map.length; j++) {
//			// rows
//			for (int i = 0; i < map[0].length; i++) {
//				this.map[j][i] = 0;
//			}
//		} 
	}
	
	public void insert(Vec2 sourcePos, Vec2 targetPos) {
		throw new UnsupportedOperationException();
	}
	
	public void insert(int sourceIdx, int targetIdx) {
		// TODO: insertion checks...
		
		// Check if there is already a value mapped to
		// this particular target grid square
		
		int[] targetCol = map[targetIdx];
		
		// check if there is already a value
		
		int mappings = IntStream.of(targetCol).sum();
		
		if (mappings > 1) {
			String msg = "There should only be a single "
						+ "mapping but found " + mappings;
			log.info(msg);
			throw new RuntimeException(msg);
		}
		
		if (mappings == 1) {
			log.info("Overwriting existing mapping");
		}
		
		log.info("Insertion [sourceIdx <=> targetIdx]: ["
				+ sourceIdx + " <=> " + targetIdx + "]");
		
		map[targetIdx] = new int[map[0].length];
		map[targetIdx][sourceIdx] = 1;
	}
	
	public void remove(int targetIdx) {
		int[] targetCol = map[targetIdx];
		int mappings = IntStream.of(targetCol).sum();
		
		if (mappings > 1) {
			String msg = "There should only be a single "
						+ "mapping but found " + mappings;
			log.info(msg);
			throw new RuntimeException(msg);
		} else if (mappings == 0) {
			log.info("There was no value to remove at targetIdx "+targetIdx);
		} else {
			log.info("Removing value assigned to targetIdx "+targetIdx);
			resetColumn(targetIdx);
		}
	}
	
	private void resetColumn(int j) {
		map[j] = new int[map.length];
	}
	
	private void resetRow(int i) {
		for (int j = 0; j < map[0].length; j++) {
			map[j][i] = 0;
		}
	}
	
	public int[] getRow(int i) {
		int[] row = new int[map.length];
		for (int j = 0; j < map.length; j++) {
			row[j] = map[j][i];
		}
		return row;
	}
	
	public int[] getColumn(int j) {
		return map[j];
	}
	
	/**
	 * Array of source grid squares that are currently
	 * mapped to a target grid square.
	 * 
	 * Useful for displaying which squares have already
	 * been mapped.
	 */
	public List<Integer> getMappedSourceIds() {
		Set<Integer> uniqueIds = new HashSet<Integer>();
		
		// cols
		for (int j = 0; j < map.length; j++) {
			// rows
			for (int i = 0; i < map[0].length; i++) {
				if (map[j][i] == 1) {
					uniqueIds.add(i);
					// by the mapping constraint, there
					// will be no more 1s in this column
					break;
				}
			}
		}
		
		return new ArrayList<Integer>(uniqueIds);
	}
	
	/**
	 * TODO: could use HashMap
	 * 
	 * For each square, count how many times it is 
	 * mapped to the target grid.
	 * @return where the index is the sourceIdx and the value is the count
	 */
	public int[] getSourceMapCounts() {
		int[] counts = new int[map[0].length];
		
		// cols
		for (int j = 0; j < map.length; j++) {
			// rows
			for (int i = 0; i < map[0].length; i++) {
				if (map[j][i] == 1) {
					counts[i]++;
					// by the mapping constraint, there
					// will be no more 1s in this column
					break;
				}
			}
		}

		return counts;
	}
	
	/**
	 * @return
	 */
	public int[][] getMatrix() {
		return this.map;
	}
	
}
