package persistence;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import small.data.structures.Vec2;

public class CollageActionEntry {
	private final Map <String, Integer> rowMap;
	private final List<String> columnHeadings;
	
	public CollageActionEntry(int sourceGridId, int targetGridId, int sourceGridX, int sourceGridY, int targetGridX,
			int targetGridY, int rowMajor, int imgWidth, int imgHeight, int gridSquareWidth, int verticals,
			int horizontals) {
		
		rowMap = new HashMap<>();
		
		// One source grid square may be referenced
		// by many target grid squares
				
		// Index of square into grid given
		// the row_major / col_major convention
		// adopted in the program
		rowMap.put("source_grid_id", sourceGridId);
		rowMap.put("target_grid_id", targetGridId);
		
		// Store the vector mapping for convenience
		// even though this can be easily derived
		// from other fields in the table
		rowMap.put("source_grid_x", sourceGridX);
		rowMap.put("source_grid_y", sourceGridY);
		
		rowMap.put("target_grid_x", targetGridX);
		rowMap.put("target_grid_y", targetGridX);
		
		// Generic configuration info. This should really
		// be in a separate file to avoid duplication
		rowMap.put("row_major", rowMajor);
		
		rowMap.put("img_width", imgWidth);
		rowMap.put("img_height", imgHeight);
		rowMap.put("grid_square_width", gridSquareWidth);
		
		// These can also be derived from the above
		// but store for convenience
		rowMap.put("verticals", verticals);
		rowMap.put("horizontals", horizontals);
		
		columnHeadings = new ArrayList<String>(rowMap.keySet());
	}
	
	public List<String> getColumnHeadings() {
		return columnHeadings;
	}

	public Map<String, Integer> getRowMap() {
		return rowMap;
	}
	
}
