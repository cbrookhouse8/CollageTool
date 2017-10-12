package visible.objects;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import manipulation.Constrain;
import manipulation.Identity;
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
	private final Buffer buffer;
	
	private Transform userTransform;
	
	public TargetGrid(PApplet p, PImage img, Buffer buffer, GridMap gridMap, 
					 int startX, int startY, int w, int h, int side) {
		
		super(p, startX, startY, w, h, side);
		
		this.imgRef = img;
		this.gridMap = gridMap;
		this.buffer = buffer;
		
		// TODO: this whole transform thing is hacky and requires
		// better abstraction and separation of concerns
		this.userTransform = new Identity();
		
		this.setLogger(new Logger(this));
	}
	/**
	 * 
	 * @param buffer
	 * @return key: targetIdx, value: sourceIdx
	 */
	public LinkedHashMap<Integer, Integer> getCurrentMappingGroup(Vec2 offset) {
		LinkedHashMap<Integer, Vec2> selection = buffer.getMap();
		
		LinkedHashMap<Integer, Integer> group = new LinkedHashMap<>();
		
		if (selection.isEmpty()) {
//			log.info("Tried to construct group of mappings but found buffer was empty");
			return group;
		}
		
		// -- TRANSFORMATIONS --
		
		// Transformations must implement the Transform Interface 
		// The input is a list describing the source to target
		// vector mappings. The transformation re-specifies the mapping
		// between the source and target grid.
		
		Vec2 relativeOrigin = buffer.getRelativeOrigin();
		
//		log.info("square has been clicked");
		
		// TODO: use lambdas
		
		// TODO: Beneath all of this is a could be a much better
		// data structure for representing this grid mapping
		
		Transform mouseTrans = new Translation(Vec2.sub(offset, relativeOrigin));
		Transform insideGrid = new Constrain(0, 0, this.verticals - 1, this.horizontals - 1);
		
		// Apply transformations to Vectors
		
//		log.info("Applying transformations...");
		
		List<VecToVec> identityMap = new ArrayList<>();
		
		for (Map.Entry<Integer, Vec2> gridSq : selection.entrySet()) {
			Vec2 v = gridSq.getValue();
			identityMap.add(new VecToVec(v, v));
		}
		
		// Default transformation
		List<VecToVec> translated = mouseTrans.applyTo(identityMap);
		
		// User specified transformation
		List<VecToVec> intermediate = userTransform.applyTo(translated);
		
		// Final default transformation
		List<VecToVec> constrained = insideGrid.applyTo(intermediate);
		
//		log.info("Creating map for insertion into GridMap...");
		
		for (VecToVec vw : constrained) {
			Vec2 sourceVec = vw.getFrom();
			Vec2 targetLoc = vw.getTo();
			int sourceIdx = gridPosToGridIndex(sourceVec);
			int targetIdx = gridPosToGridIndex(targetLoc);
			group.put(targetIdx, sourceIdx);
		}
		
		return group;
	}
	
	public void updateAndPersistMap(CollageActionStore actionStore /*, List<Transform> userTransforms */) {
		if (!mouseOverSquare()) {
			return;
		}
		
		// Calculate offset
		Vec2 offset = screenSpaceToGridPos(p.mouseX, p.mouseY);
		
		LinkedHashMap<Integer, Integer> group = this.getCurrentMappingGroup(offset);
		
		log.info("Updating final map...");
		
		List<CollageActionEntry> actionGroup = new ArrayList<>();
		
		for (Map.Entry<Integer, Integer> targetIdxToSourceIdx : group.entrySet()) {
			int sourceIdx = targetIdxToSourceIdx.getValue();
			int targetIdx = targetIdxToSourceIdx.getKey();
			
			// TODO: this back and forth conversion has happened quite
			// a few times already. So not very efficient currently.
			
			// assuming that target and source grids are the same dim
			Vec2 sourceVec = this.gridIndexToGridPos(sourceIdx);
			Vec2 targetLoc = this.gridIndexToGridPos(targetIdx);
			
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
			
			actionGroup.add(entry);	
		}
				
		actionStore.addActionGroup(actionGroup);
		buffer.flush();
	}
	
	public void showCurrentMappingGroup() {
		if (!mouseOverSquare()) {
			return;
		}
		
		// Calculate offset
		Vec2 offset = screenSpaceToGridPos(p.mouseX, p.mouseY);
		
		LinkedHashMap<Integer, Integer> currentMappingGroup = getCurrentMappingGroup(offset);
		
		for (Map.Entry<Integer, Integer> targetIdxToSourceIdx : currentMappingGroup.entrySet()) {
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
	
	public void setUserTransform(Transform userTfm) {
		this.userTransform = userTfm;
	}
	
}
