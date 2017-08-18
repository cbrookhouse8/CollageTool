package visible.objects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import processing.core.PApplet;
import small.data.structures.Buffer;
import small.data.structures.Color;
import small.data.structures.Vec2;

/**
 * TODO: buffer and relativeOrigin should be independent data structure
 * with a .flush() and .getRelativeOrigin()
 * @author charliebrookhouse
 *
 */
public class SourceGrid extends Grid {
	
	public Color[] colors;
	
	public SourceGrid(PApplet _p, int _startX, int _startY, int _w, int _h, int _side) {
		super(_p, _startX, _startY, _w, _h, _side);
		
		// Initialise with arbitrary values
		colors = new Color[horizontals * verticals];
		
		for (int i = 0; i < colors.length; i++) {
			colors[i] = new Color((int) (p.random(0,1) * 255), 
								(int) (p.random(0,1) * 255),
								(int) (p.random(0,1) * 255));
		}
	}
	
	/**
	 * Method to modify the buffer (by reference)
	 * 
	 * @param buffer stores (row-major) indices of current selection
	 */
	public Buffer updateBufferOnSelect(Buffer buffer) {
		if (!squareIsBeingClicked()) {
			return buffer;
		}
		
		toggleFrame = p.frameCount;
		int idx = screenSpaceToGridIndex(p.mouseX, p.mouseY);
		
		System.out.println("SourceGrid: Toggling square at index "+idx);
		
		Vec2 gridPos = screenSpaceToGridPos(p.mouseX, p.mouseY);

		if (buffer.containsPosition(gridPos)) {
			buffer.remove(gridPos);
		} else {
			buffer.add(gridPos);
		}
		
		return buffer;
	}
	
	public void showColors() {
		p.noStroke();
		for (int k = 0; k < colors.length; k++) {
			Vec2 pos = gridIndexToScreenSpace(k);
			p.fill(colors[k].r, colors[k].g, colors[k].b);
			p.rect(pos.x + 1, 
				   pos.y + 1, 
				   side - 2, side - 2);
		}
		p.noFill();
	}
	
	public void showSelectedSquares(Buffer buffer) {
		p.noStroke();
		Set<Integer> selectedSquares = buffer.getKeySet();
		for (Integer idx : selectedSquares) {
			Vec2 pos = gridIndexToScreenSpace(idx);
			p.rect(pos.x + 1, 
				   pos.y + 1,
				   side - 2, side - 2);
		}
	}
}
