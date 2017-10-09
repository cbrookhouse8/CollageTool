package persistence;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import processing.data.TableRow;
import small.data.structures.Vec2;

public class CollageActionEntry {
	// May remain uninitialised
	private int id;
	private int groupId;
	
	// Must be initialized
	private final Map <String, Integer> rowMap;
	private final List<String> columnHeadings;
	
	public CollageActionEntry(int sourceGridId, int targetGridId, int sourceGridX, int sourceGridY, int targetGridX,
			int targetGridY, int rowMajor, int imgWidth, int imgHeight, int gridSquareWidth, int verticals,
			int horizontals) {
		
		rowMap = new LinkedHashMap<>();
		
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
	
	public static CollageActionEntry of(TableRow row) {
		CollageActionEntry entry = new CollageActionEntry(
				row.getInt("source_grid_id"),
				row.getInt("target_grid_id"),
				row.getInt("source_grid_x"),
				row.getInt("source_grid_y"),
				row.getInt("target_grid_x"),
				row.getInt("target_grid_y"),
				row.getInt("row_major"),
				row.getInt("img_width"),
				row.getInt("img_height"),
				row.getInt("grid_square_width"),
				row.getInt("verticals"),
				row.getInt("horizontals")
		);
		entry.setId(row.getInt("id"));
		entry.setGroupId(row.getInt("group_id"));
		
		return entry;
	}
	
	/**
	 * Method called as part of this pattern:
	 * 
	 * CollageActionEntry.of(tableRow).getCollageConfiguration();
	 * 
	 * @return
	 */
	public CollageConfiguration extractConfigurationData() {
		CollageConfiguration config = new CollageConfiguration();
		
		config.setRowMajor(rowMap.get("row_major"));
		config.setImgWidth(rowMap.get("img_width"));
		config.setImgHeight(rowMap.get("img_height"));
		config.setGridSquareWidth(rowMap.get("grid_square_width"));
		config.setVerticals(rowMap.get("verticals"));
		config.setHorizontals(rowMap.get("horizontals"));
		
		return config;
	}
	
	public List<String> getColumnHeadings() {
		return columnHeadings;
	}

	public Map<String, Integer> getRowMap() {
		return rowMap;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getGroupId() {
		return groupId;
	}

	public void setGroupId(int groupId) {
		this.groupId = groupId;
	}
	
	public int getSourceIdx() {
		return rowMap.get("source_grid_id");
	}

	public int getTargetIdx() {
		return rowMap.get("target_grid_id");
	}
	
}
