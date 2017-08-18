package visible.objects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import processing.core.PApplet;
import processing.core.PImage;
import processing.data.Table;
import processing.data.TableRow;
import small.data.structures.Buffer;
import small.data.structures.Color;
import small.data.structures.Vec2;

/**
 * Holds and displays current state of the Collage
 *
 */
public class TargetGrid extends Grid {
	
	// Target Grid Index => Source Grid Index
	HashMap<Integer, Integer> gridMap;
	PImage imgRef;
	Table table;
	
	
	public TargetGrid(PApplet _p, PImage img, Table _table, int _startX, int _startY, int _w, int _h, int _side) {
		super(_p, _startX, _startY, _w, _h, _side);
		gridMap = new HashMap<>();
		imgRef = img;
		table = _table;
	}
	
	/**
	 * 
	 * @param buffer
	 * @param offset of grid square mouse is over from the top left corner
	 * of the grid
	 */
	public void updateMap(Buffer buffer) {
		if (!squareIsBeingClicked()) {
			return;
		}
				
		HashMap<Integer, Vec2> selection = buffer.getMap();
		Vec2 relativeOrigin = buffer.getRelativeOrigin();
		
//		Color[] sourceColors = sourceGrid.colors;
		
		System.out.println("TargetGrid square has been clicked");
		
		// Calculate offset
		Vec2 offset = screenSpaceToGridPos(p.mouseX, p.mouseY);
		
		System.out.println("Updating final map in TargetGrid");
		
		for (Map.Entry<Integer, Vec2> gridSq : selection.entrySet()) {
			int sourceIdx = gridSq.getKey();
			Vec2 sourceVec = gridSq.getValue();
			
			// translate by grid space position of mouse over target grid 
			Vec2 targetLoc = Vec2.add(Vec2.sub(sourceVec, relativeOrigin), offset);
			
			if (isOutsideGrid(targetLoc)) {
				String msg = "Offset (relativeOrigin) resulted in translation ";
				msg += "of source grid square out of target grid range.";
				System.out.println(msg);
				continue;
			}
			
			int targetIdx = gridPosToGridIndex(targetLoc);
			
			// if a mapping exists for this index in the 
			// target grid, then over-write it
			if (gridMap.containsKey(targetIdx)) {
				gridMap.replace(targetIdx, sourceIdx);
			} else {
				System.out.println("Inserting "+targetIdx+"<=>"+sourceIdx+" into gridMap");
				gridMap.put(targetIdx, sourceIdx);
			}
			
			// write to file
			TableRow newMap = table.addRow();
			
			/**
			 * TODO: check that id is set correctly for first row
			 */
			newMap.setInt("id", table.lastRowIndex());
			
			newMap.setInt("source_grid_id", sourceIdx);	// int
			newMap.setInt("target_grid_id", targetIdx);	// int
			
			newMap.setInt("source_grid_x", sourceVec.x);	// int
			newMap.setInt("source_grid_y", sourceVec.y);	// int
			newMap.setInt("target_grid_x", targetLoc.x);	// int
			newMap.setInt("target_grid_y", targetLoc.y);	// int
			
			newMap.setInt("row_major", 1);				// int (representing boolean) 
			newMap.setInt("img_width", w);				// int (pixels)
			newMap.setInt("img_height", h);				// int (pixels)
			newMap.setInt("grid_square_width", side);	// int (pixels)
			newMap.setInt("verticals", verticals);
			newMap.setInt("horizontals", horizontals);
			p.saveTable(table, "data/collage_map.csv");
		}
		buffer.flush();
	}
	
	/**
	 * Initializes from the passed file.
	 */
	public void initializeFromTable() {
		
	}
	
	public void showImageSegments() {
		p.noStroke();
		
		for (Integer targetIdx : gridMap.keySet()) {
			Vec2 segmentPos = gridIndexToGridPos(gridMap.get(targetIdx));
			int xcorn = segmentPos.x * side;
			int ycorn = segmentPos.y * side;
			PImage imgSegment = imgRef.get(xcorn, ycorn, side, side);
			Vec2 outputLoc = gridIndexToScreenSpace(targetIdx);
			p.image(imgSegment, outputLoc.x, outputLoc.y); 
		}
		p.noFill();
	}
	
	// ------------------------------------
	// 			CLASS UTILITIES
	// ------------------------------------
	
	private boolean isOutsideGrid(Vec2 v) {
		boolean xInRange = inRange(v.x, 0, this.verticals - 1);
		boolean yInRange = inRange(v.y, 0, this.horizontals - 1);
		return !(xInRange && yInRange);
	}
	
}
