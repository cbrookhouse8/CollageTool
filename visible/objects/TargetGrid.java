package visible.objects;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import manipulation.Constrain;
import manipulation.Transform;
import manipulation.Translation;
import persistence.CollageActionEntry;
import persistence.CollageActionStore;
import processing.core.PApplet;
import processing.core.PImage;
import small.data.structures.Buffer;
import small.data.structures.GridMap;
import small.data.structures.Vec2;
import small.data.structures.VecToVec;
import utilities.Logger;

/**
 * Holds and displays current state of the Collage
 */
public class TargetGrid extends Grid {
	
	// Grid Map Reference
	
	private final GridMap gridMap;
	private final PImage imgRef;
	
	public TargetGrid(PApplet _p, PImage img, GridMap gridMap, int _startX, int _startY, int _w, int _h, int _side) {
		super(_p, _startX, _startY, _w, _h, _side);
		this.gridMap = gridMap;
		this.imgRef = img;
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
		
		LinkedHashMap<Integer, Vec2> selection = buffer.getMap();
		
		if (selection.isEmpty()) {
			log.info("Tried to updateMap but found buffer was empty");
			return;
		}
		
		// -- TRANSFORMATIONS --
		
		// Transformations must implement the Transform Interface 
		// The input is a list describing the source to target
		// vector mappings. The transformation re-specifies the mapping
		// between the source and target grid.
		
		Vec2 relativeOrigin = buffer.getRelativeOrigin();
		
		log.info("square has been clicked");
		
		// Calculate offset
		Vec2 offset = screenSpaceToGridPos(p.mouseX, p.mouseY);
		
		// TODO: use lambdas
		
		// TODO: Beneath all of this is a could be a much better
		// data structure for representing this grid mapping
		
		Transform mouseTrans = new Translation(Vec2.sub(offset, relativeOrigin));
		Transform insideGrid = new Constrain(0, 0, this.verticals - 1, this.horizontals - 1);
		
		// Apply transformations to Vectors
		
		log.info("Applying transformations...");
		
		List<VecToVec> identityMap = new ArrayList<>();
		
		for (Map.Entry<Integer, Vec2> gridSq : selection.entrySet()) {
			Vec2 v = gridSq.getValue();
			identityMap.add(new VecToVec(v, v));
		}
		
		List<VecToVec> translated = mouseTrans.applyTo(identityMap);
		List<VecToVec> constrained = insideGrid.applyTo(translated);
		
		log.info("Updating final map...");
		
		List<CollageActionEntry> group = new ArrayList<>();
		
		for (VecToVec vw : constrained) {
			Vec2 sourceVec = vw.getFrom();
			Vec2 targetLoc = vw.getTo();
			int sourceIdx = gridPosToGridIndex(sourceVec);
			int targetIdx = gridPosToGridIndex(targetLoc);
			
			// View
			
			log.info("Inserting [sourceIdx <=> targetIdx]: "+sourceIdx+"<=>"+targetIdx+" into gridMap");
			gridMap.insert(sourceIdx, targetIdx);
			
			// Persistence
			
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
	
	public void showImageSegments() {
		p.noStroke();
		LinkedHashMap<Integer, Integer> activeMappings = gridMap.getActiveMappings();
		
		for (Map.Entry<Integer, Integer> targetIdxToSourceIdx : activeMappings.entrySet()) {
			int targetIdx = targetIdxToSourceIdx.getKey();
			int sourceIdx = targetIdxToSourceIdx.getValue();
			
			Vec2 segmentPos = gridIndexToGridPos(sourceIdx);
			int xcorn = segmentPos.x * side;
			int ycorn = segmentPos.y * side;
			PImage imgSegment = imgRef.get(xcorn, ycorn, side, side);
			
			Vec2 outputLoc = gridIndexToScreenSpace(targetIdx);
			p.image(imgSegment, outputLoc.x, outputLoc.y); 
		}
		p.noFill();
	}
	
	/**
	 * TODO: Potentially this logic could be moved to Grid parent class
	 * if Grid also took GridMap as an initialisation parameter.
	 * 
	 * This does break the separation of concerns a bit
	 * 
	 * Inputs: mouse position, targetGrid, GridMap
	 * 
	 * @param targetGrid
	 */
	public void showMapFrom(Grid sourceGrid) {
		if (sourceGrid.isOutsideGrid(p.mouseX, p.mouseY)) {
			return;
		}
		
		// Mouse is in the Source Grid area...
		
		Vec2 sourcePos = sourceGrid.screenSpaceToGridPos(p.mouseX, p.mouseY);
		int sourceIdx = sourceGrid.gridPosToGridIndex(sourcePos);
		
		List<Integer> targetIds = gridMap.getTargetIdxForSourceIdx(sourceIdx);
		
		if (targetIds.size() == 0) {
			return;
		}
		
		Vec2 squareCentreTranslation = new Vec2((int) (side / 2), (int) (side / 2));
		
		// scale to screen space
		Vec2 sp = Vec2.mult(sourcePos, sourceGrid.getSide());
		
		// translate to sourceGrid position on screen
		sp.add(new Vec2(sourceGrid.getStartX(), sourceGrid.getStartY()));
		
		// find the centre of the square rather than the corner
		Vec2 sourcePosCentred = Vec2.add(sp, squareCentreTranslation);
		
		for (Integer targetIdx : targetIds) {
			Vec2 targetPos = gridIndexToScreenSpace(targetIdx);
			
			// find the centre of the square rather than the corner
			Vec2 targetPosCentred = Vec2.add(targetPos, squareCentreTranslation);
			
			p.rect(targetPos.x, targetPos.y, side - 1, side - 1);
			p.line(sourcePosCentred.x, sourcePosCentred.y, 
				   targetPosCentred.x, targetPosCentred.y);
			
		}		
	}
	
}
