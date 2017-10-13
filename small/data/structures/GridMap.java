package small.data.structures;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.IntStream;

import persistence.CollageActionStore;
import persistence.CollageConfiguration;
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
	 * The boolean matrix is an all right idea although
	 * repeatedly iterating through the column arrays to
	 * find mappings is linear time. So will be a better
	 * data structure to use involving Hashing although
	 * this current one is nice and visual.
	 */
	
	/**
	 * The map is always represented by a square matrix
	 * @param span length of unwrapped input grid
	 */
	public GridMap(int span) {
		log = new Logger(this);
		resetMap(span);
	}
	
	public static GridMap of(CollageActionStore actionStore) {
		CollageConfiguration config = actionStore.getCollageConfiguration();
		int span = config.getHorizontals() * config.getVerticals();
		
		GridMap gridMap = new GridMap(span);
		
		// representation of current state
		LinkedHashMap<Integer, Integer> targetToSourceIdxMap = actionStore.getTargetToSourceMap();
		
		// insert current state into GridMap
		for (Map.Entry<Integer, Integer> targetToSource : targetToSourceIdxMap.entrySet()) {
			Integer targetIdx = targetToSource.getKey();
			Integer sourceIdx = targetToSource.getValue();
			gridMap.insert(sourceIdx, targetIdx);
		}
		
		return gridMap;
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
	 * TODO: use cache for this
	 * 
	 * Array of source grid squares that are currently
	 * mapped to some target grid square. The value of
	 * the mapping is not returned however.
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
	 * TODO: 'active' is a tautology given the separation of concerns
	 * TODO: use cache for this
	 * 
	 * Key method used by the TargetGrid for build the view
	 * 
	 * Far too much iteration here. This is the problem with using 
	 * an int[][] as the primary data structure. Some kind of hash
	 * could be better.
	 * 
	 * @return Active mappings as key: targetIdx, value: sourceIdx
	 */
	public LinkedHashMap<Integer, Integer> getActiveMappings() {
		LinkedHashMap<Integer, Integer> activeMappings = new LinkedHashMap<>();
		
		// Things get a little too loopy here...
		
		for (int j = 0; j < map.length; j++) {
			for (int i = 0; i < map[0].length; i++) {
				if (map[j][i] == 1) {
					activeMappings.put(j, i);
					// by the mapping constraint, there
					// will be no more 1s in this column
					break;
				}
			}
		}
		
		return activeMappings;
	}
	
	/**
	 * Given a targetIdx, find the index of the source square
	 * mapped to it if there is an active mapping.
	 * @return Optional of sourceIdx (i.e. could be empty)
	 */
	public Optional<Integer> getSourceIdxForTargetIdx(int targetIdx) {
		int j = targetIdx;
		Integer sourceIdx = null;
		for (int i = 0; i < map[0].length; i++) {
			if (map[j][i] == 1) {
				sourceIdx = i;
				// by the mapping constraint, there
				// will be no more 1s in this column
				break;
			}
		}
		
		return Optional.ofNullable(sourceIdx);
	}
	
	/**
	 * Given a sourceIdx, return a list of all the targetIdx's
	 * to which it is mapped, if any.
	 * 
	 * @param sourceIdx
	 * @return
	 */
	public List<Integer> getTargetIdxForSourceIdx(int sourceIdx) {
		List<Integer> targetIds = new ArrayList<>();
		int[] row = getRow(sourceIdx);
		
		for (int j = 0; j < row.length; j++) {
			if (row[j] == 1) {
				targetIds.add(j);
			}
		}
		
		return targetIds;
	}
	
	/**
	 * TODO: create suitable abstractions so that this
	 * method does not really need to be called.
	 * @return
	 */
	public int[][] getMatrix() {
		return this.map;
	}
	
}
