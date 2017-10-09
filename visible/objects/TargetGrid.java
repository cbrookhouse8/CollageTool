package visible.objects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import persistence.CollageActionEntry;
import persistence.CollageActionStore;
import processing.core.PApplet;
import processing.core.PImage;
import processing.data.Table;
import processing.data.TableRow;
import small.data.structures.Buffer;
import small.data.structures.Color;
import small.data.structures.Vec2;
import utilities.Logger;

/**
 * Holds and displays current state of the Collage
 *
 */
public class TargetGrid extends Grid {
	
	// Target Grid Index => Source Grid Index
	HashMap<Integer, Integer> gridMap;
	PImage imgRef;
	
	public TargetGrid(PApplet _p, PImage img, int _startX, int _startY, int _w, int _h, int _side) {
		super(_p, _startX, _startY, _w, _h, _side);
		gridMap = new HashMap<>();
		imgRef = img;
		this.setLogger(new Logger(this));
	}
	
	/**
	 * 
	 * @param buffer
	 * @param offset of grid square mouse is over from the top left corner
	 * of the grid
	 */
	public void updateMap(Buffer buffer, CollageActionStore actionStore) {
		if (!mouseOverSquare()) {
			return;
		}
		
		HashMap<Integer, Vec2> selection = buffer.getMap();
		
		if (selection.isEmpty()) {
			log.info("Tried to updateMap but found buffer was empty");
			return;
		}
		
		Vec2 relativeOrigin = buffer.getRelativeOrigin();
		
//		Color[] sourceColors = sourceGrid.colors;
		
		log.info("square has been clicked");
		
		// Calculate offset
		Vec2 offset = screenSpaceToGridPos(p.mouseX, p.mouseY);
		
		log.info("updating final map");
		
		List<CollageActionEntry> group = new ArrayList<>();
		
		for (Map.Entry<Integer, Vec2> gridSq : selection.entrySet()) {
			int sourceIdx = gridSq.getKey();
			Vec2 sourceVec = gridSq.getValue();
			
			// translate by grid space position of mouse over target grid 
			Vec2 targetLoc = Vec2.add(Vec2.sub(sourceVec, relativeOrigin), offset);
			
			if (isOutsideGrid(targetLoc)) {
				String msg = "Offset (relativeOrigin) resulted in translation ";
				msg += "of source grid square out of target grid range.";
				log.info(msg);
				continue;
			}
			
			int targetIdx = gridPosToGridIndex(targetLoc);
			
			// if a mapping exists for this index in the 
			// target grid, then over-write it
			if (gridMap.containsKey(targetIdx)) {
				gridMap.replace(targetIdx, sourceIdx);
			} else {
				log.info("Inserting "+targetIdx+"<=>"+sourceIdx+" into gridMap");
				gridMap.put(targetIdx, sourceIdx);
			}
			
			CollageActionEntry entry = new CollageActionEntry(
					sourceIdx, targetIdx, 
					sourceVec.x, sourceVec.y, 
					targetLoc.x, targetLoc.y, 
					1, this.w, this.h, this.side, 
					this.verticals, this.horizontals);
			
			group.add(entry);
		}
				
		actionStore.addActionGroup(group);
		buffer.flush();
	}
	
	public void setGridMap(HashMap<Integer, Integer> gridMap) {
		this.gridMap = gridMap;
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
