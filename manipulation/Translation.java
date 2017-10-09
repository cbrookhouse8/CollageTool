package manipulation;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import small.data.structures.Vec2;
import small.data.structures.VecToVec;

public class Translation implements Transform {
	Vec2 r;
	
	public Translation(int x, int y) { 
		r = new Vec2(x, y);
	}
	
	public Translation(Vec2 v) { 
		r = new Vec2(v.x, v.y);
	}

	@Override
	public List<VecToVec> applyTo(List<VecToVec> map) {
		
		for (VecToVec v_w : map) {
			// preserve the original source
			Vec2 targetLoc = Vec2.add(v_w.getTo(), this.r);
			v_w.setTo(targetLoc);
		}
		
		return map;
	}
}
