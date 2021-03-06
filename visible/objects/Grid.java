package visible.objects;

import processing.core.PApplet;
import small.data.structures.Color;
import small.data.structures.Vec2;
import utilities.Logger;

public class Grid {
	protected PApplet p;
	protected int w;		// width
	protected int h;		// height
	protected int side;	// length of square side
	protected int verticals;
	protected int horizontals;
	
	protected int startX;
	protected int startY;
	
	protected Logger log;
	
	public Grid(PApplet p, int startX, int startY, int w, int h, int side) {
		this.p = p;
		this.w = w;
		this.h = h;
		this.side = side;
	
//		int n_squares = (w / side) * (h / side);
		
		verticals = w / side;
		horizontals = h / side;
		
		this.startX = startX;
		this.startY = startY;
		
		this.setLogger(new Logger(this));
	}
	
	public void showGridLines() {
		// Grid Lines
		
		// verticals
		for (int i = 0; i <= verticals * side; i++) {
			p.line(startX + i * side - 1, startY, startX + i * side - 1, startY + h);
			p.line(startX + i * side, startY, startX + i * side, startY + h);
		}
		// horizontals
		for (int i = 0; i <= horizontals * side; i++) {
			p.line(startX, startY + i * side - 1, startX + w, startY + i * side - 1);
			p.line(startX, startY + i * side, startX + w, startY + i * side);
		}
	}

	public void showHoverSelection() {
		
		if (isOutsideGrid(p.mouseX, p.mouseY)) {
			return;
		}
		
		Vec2 gridLoc = screenSpaceToGridPos(p.mouseX, p.mouseY);
		int xcorn = startX + side * gridLoc.x;
		int ycorn = startY + side * gridLoc.y;
		
		// This is a white square with a cross in it
		
//		p.strokeWeight(2);
//		p.stroke(32, 216, 51);
//		p.noFill();
		p.rect(xcorn - 1, ycorn - 1, side + 1, side + 1);
		
		p.strokeWeight(1);
		
//		p.noStroke();
//		p.rect(xcorn + 1, 
//			   ycorn + 1,
//			   side - 2, side - 2);
//		
//		p.stroke(0);
//		p.line(xcorn + 1, ycorn + 1, xcorn + side - 2, ycorn + side - 2);
//		p.line(xcorn + side - 2, ycorn + 1, xcorn + 1, ycorn + side - 2);
	}
	
	protected boolean mouseOverSquare() {
		return inRange(p.mouseX, startX, startX + side * verticals) && 
				inRange(p.mouseY, startY, startY + side * horizontals);
	}
	
	/**
	 * 
	 * @param t must strictly be greater than lower, and less than upper
	 * @param lower t cannot equal this value. It must be greater
	 * @param upper t cannot equal this value. It must be lower
	 * @return
	 */
	protected boolean inRange(int t, int lower, int upper) {
		if (t < lower || t > upper) return false;
		return true;
	}
	
	/**
	 * 
	 * @param mx MouseX in screen space
	 * @param my MouseY in screen space
	 * @return
	 */
	public boolean isOutsideGrid(int mx, int my) {
		if (mx < startX || mx > startX + w) {
			return true;
		}
		
		if (my < startY || my > startY + h) {
			return true;
		}
		
		return false;
	}
	
	public Vec2 gridIndexToGridPos(int idx) {
		Vec2 loc = new Vec2(idx % verticals, idx / horizontals);
		if (!inRange(loc.x, 0, verticals)) {
			log.info("Input index is out of grid space. Constraining x");
			// is there off by one here?
			loc.x = verticals;
		}
		if (!inRange(loc.y, 0, horizontals)) {
			log.info("Input index is out of grid space. Constraining y");
			// is there off by one here?
			loc.y = horizontals;
		}
		return loc;
	}
	
	public int gridPosToGridIndex(Vec2 pos) {
		int idx = pos.y * verticals + pos.x;
		if (!inRange(idx, -1, verticals * horizontals)) {
			log.info("Grid position in the index range of the grid. Constraining to 0.");
			return 0;
		}
		return idx;
	}
	
	/**
	 * Takes screen space inputs and returns Vec2 of Grid space position.
	 * Therefore subtracts the `offset` from the input params
	 * @param x raw screen space x position
	 * @param y raw screen space y position
	 * @return Position of the screen space input in terms of Grid Space
	 */
	protected Vec2 screenSpaceToGridPos(int x, int y) {
		return new Vec2((x - startX) / side, (y - startY) / side);
	}
	
	protected Vec2 screenSpaceToGridPos(Vec2 pos) {
		int x = (int) pos.x;
		int y = (int) pos.y;
		return new Vec2((x - startX) / side, (y - startY) / side);
	}
	
	/**
	 * Converts from screen coordinates to grid index (row major)
	 * TODO: could use gridIndexToGridPos or gridPosToGridIndex etc
	 * @param px
	 * @param py
	 * @return index into grid (row major)
	 */
	protected int screenSpaceToGridIndex(int px, int py) {
			// row major
			int idx = ((px - startX) / side) + ((py - startY) / side) * (w / side);
			if (!inRange(idx, -1, verticals * horizontals)) {
				String msg = "Input index is out of range of the ranges of values for the grid index."
							 + "Constraining index to 0";
				log.info(msg);
				idx = 0;
			}
			return idx;
	}
	
	/**
	 * Converts from grid index to raw screen space coordinates
	 * @param idx
	 * @return screen space coordinates vector
	 */
	protected Vec2 gridIndexToScreenSpace(int idx) {
		if (!inRange(idx, -1, verticals * horizontals)) {
			log.info("Index is out of bounds. Returning the 0 Vector");
			return new Vec2(0, 0);
		}
		int grid_col = idx % horizontals;
		int grid_row = idx / verticals;
		
//		boolean conditions = inRange(grid_col, -1, verticals);
//		conditions = conditions && inRange(grid_row, -1, horizontals);
		
		// top left corner
		return new Vec2(startX + grid_col * side, startY + grid_row * side);
	}
	
	protected void setLogger(Logger log) {
		this.log = log;
	}

	public int getSide() {
		return side;
	}
	
	public int getStartX() {
		return startX;
	}
	
	public int getStartY() {
		return startY;
	}
	
}
