package visible.objects;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import processing.core.PApplet;
import small.data.structures.Buffer;
import small.data.structures.GridMap;
import small.data.structures.Vec2;
import utilities.Logger;

/**
 * TODO: buffer and relativeOrigin should be independent data structure
 * with a .flush() and .getRelativeOrigin()
 */
public class SourceGrid extends Grid {
	
	private final GridMap gridMap;
	private final Buffer buffer;
	
	public SourceGrid(PApplet p, Buffer buffer, GridMap gridMap, int startX, int startY, int w, int h, int side) {
		super(p, startX, startY, w, h, side);
		this.gridMap = gridMap;
		this.buffer = buffer;
		this.setLogger(new Logger(this));
	}
	
	/**
	 * Method to modify the buffer (by reference)
	 * 
	 * @param buffer stores (row-major) indices of current selection
	 */
	public Buffer updateBufferOnClick() {
		if (!mouseOverSquare()) {
			return buffer;
		}
		
		int idx = screenSpaceToGridIndex(p.mouseX, p.mouseY);
		
		log.info("Toggling square at index "+idx);
		
		Vec2 gridPos = screenSpaceToGridPos(p.mouseX, p.mouseY);
		
		// mouseClicked toggle logic
		if (buffer.containsPosition(gridPos)) {
			buffer.remove(gridPos);
		} else {
			buffer.insert(gridPos);
		}
		
		return buffer;
	}
	
	public Buffer updateBufferOnDrag() {
		if (!mouseOverSquare()) {
			return buffer;
		}
		
		int idx = screenSpaceToGridIndex(p.mouseX, p.mouseY);
		
		log.info("Adding square at index "+idx+" from the drag area.");
		
		Vec2 gridPos = screenSpaceToGridPos(p.mouseX, p.mouseY);
		
		// Note difference between this logic
		// and mouse clicked logic
		if (!buffer.containsPosition(gridPos)) {
			buffer.insert(gridPos);
		}
		
		return buffer;
	}
	
	public void showCurrentSelection() {
		Set<Integer> selectedSquares = buffer.getKeySet();
		for (Integer idx : selectedSquares) {
			Vec2 pos = gridIndexToScreenSpace(idx);
			p.rect(pos.x, 
				   pos.y,
				   side - 1, side - 1);
		}
	}
	
	public void showPreviousSelection() {
		Set<Integer> previouslySelectedSquares = buffer.getPreviousKeySet();
		for (Integer idx : previouslySelectedSquares) {
			Vec2 pos = gridIndexToScreenSpace(idx);
			p.rect(pos.x, 
				   pos.y,
				   side - 1, side - 1);
		}
	}
	
	public void showMappedSquares(boolean on) {
		if (on == false) return;
		
		List<Integer> mappedSquares = gridMap.getMappedSourceIds();
		for (Integer idx : mappedSquares) {
			Vec2 pos = gridIndexToScreenSpace(idx);
			p.rect(pos.x, 
				   pos.y,
				   side - 1, side - 1);
		}
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
	public void showMapFrom(Grid targetGrid) {
		if (targetGrid.isOutsideGrid(p.mouseX, p.mouseY)) {
			return;
		}
		
		// Mouse is in the TargetGrid area...
		
		Vec2 targetGridPos = targetGrid.screenSpaceToGridPos(p.mouseX, p.mouseY);
		int targetIdx = targetGrid.gridPosToGridIndex(targetGridPos);
		Vec2 targetPos = targetGrid.gridIndexToScreenSpace(targetIdx);
		
		Optional<Integer> opt = gridMap.getSourceIdxForTargetIdx(targetIdx);
		
		if (opt.isPresent() == false) {
			return;
		}
		
		int sourceIdx = opt.get();
		Vec2 sourcePos = gridIndexToScreenSpace(sourceIdx);
		
		Vec2 squareCentreTranslation = new Vec2((int) (side / 2), (int) (side / 2));
		
		Vec2 sourcePosCentred = Vec2.add(sourcePos, squareCentreTranslation);
		
		// find the centre of the square rather than the corner
		Vec2 targetPosCentred = Vec2.add(targetPos, squareCentreTranslation);
		
		p.rect(sourcePos.x, sourcePos.y, side - 1, side - 1);
		p.line(sourcePosCentred.x, sourcePosCentred.y, 
			   targetPosCentred.x, targetPosCentred.y);
	}
}
