package small.data.structures;

import java.util.LinkedHashMap;

import persistence.CollageActionEntry;

/**
 * Bounding rects are useful when performing
 * simple geometric transformations to the map
 */
public class BoundingRect {
	
	private Vec2 topLeft;
	private Vec2 bottomRight;
	
	public BoundingRect(Vec2 tl, Vec2 br) {
		topLeft = tl;
		bottomRight = br;
	}

	public static BoundingRect of(Buffer buffer) {
		return BoundingRect.of(buffer.getMap());
	}
	
	public static BoundingRect of(LinkedHashMap<Integer, Vec2> map) {
		
		// Initial value
		
		Integer firstKey = map.keySet().iterator().next();
		Vec2 firstValue = map.get(firstKey);
		
		// copies
		Vec2 tl = new Vec2(firstValue);
		Vec2 br = new Vec2(firstValue);
		
		// Converge on the rectangle
		for (Vec2 v : map.values()) {
			if (v.x < tl.x) {
				tl.x = v.x;
			} else if (v.x > br.x) {
				br.x = v.x;
			} else if (v.y < tl.y) {
				tl.y = v.y;
			} else if (v.y > br.y) {
				br.y = v.y;
			}
		}
		
		return new BoundingRect(tl, br);
	}
	
	public Vec2 getTopLeft() {
		return topLeft;
	}

	public Vec2 getBottomRight() {
		return bottomRight;
	}
	
}
