package visible.objects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import processing.core.PApplet;
import processing.core.PImage;
import small.data.structures.Buffer;
import small.data.structures.Color;
import small.data.structures.Vec2;

public class TargetGrid extends Grid {
	
	// Target Grid Index => Source Grid Index
	HashMap<Integer, Integer> gridMap;
	PImage imgRef;
	
	
	public TargetGrid(PApplet _p, PImage img, int _startX, int _startY, int _w, int _h, int _side) {
		super(_p, _startX, _startY, _w, _h, _side);
		gridMap = new HashMap<>();
		imgRef = img;
	}
	
	/**
	 * 
	 * @param sourceBuffer
	 * @param offset of grid square mouse is over from the top left corner
	 * of the grid
	 */
	public void updateMap(SourceGrid sourceGrid) {
		if (!squareIsBeingClicked()) {
			return;
		}
		
		Buffer sourceBuffer = sourceGrid.getBuffer();
		
		HashMap<Integer, Vec2> selection = sourceBuffer.getMap();
		Vec2 relativeOrigin = sourceBuffer.getRelativeOrigin();
		
//		Color[] sourceColors = sourceGrid.colors;
		
		System.out.println("TargetGrid square has been clicked");
		
		// Calculate offset
		Vec2 offset = screenSpaceToGridPos(p.mouseX, p.mouseY);
		
		System.out.println("Updating final map in TargetGrid");
		
		for (Map.Entry<Integer, Vec2> gridSq : selection.entrySet()) {
			int sourceIdx = gridSq.getKey();
			Vec2 targetLoc = Vec2.add(Vec2.sub(gridSq.getValue(), relativeOrigin), offset);
			int targetIdx = gridPosToGridIndex(targetLoc);
			
			if (targetIdx < 0 || targetIdx >= (verticals * horizontals)) continue;
			
			// maybe add a check to see if sourceIdx is in the correct range
			if (gridMap.containsKey(targetIdx)) {
				gridMap.replace(targetIdx, sourceIdx);
			} else {
				System.out.println("Inserting "+targetIdx+"<=>"+sourceIdx+" into gridMap");
				gridMap.put(targetIdx, sourceIdx);
			}
		}
		sourceGrid.clearSelection();
	}
	
	public void showImageSegments() {
		p.noStroke();
		// not sure why Map.Entry<Integer, Vec2> gridSq : selection.entrySet() isn't working
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
	
}
