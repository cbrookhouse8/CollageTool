package manipulation;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import small.data.structures.Vec2;
import small.data.structures.VecToVec;
import utilities.Logger;

public class Translation implements Transform {
	Vec2 r;
	Logger log;
	
	public Translation(int x, int y) { 
		r = new Vec2(x, y);
		log = new Logger(this);
	}
	
	public Translation(Vec2 v) { 
		r = new Vec2(v.x, v.y);
		log = new Logger(this);
	}

	@Override
	public List<VecToVec> applyTo(List<VecToVec> map) {
		List<VecToVec> translated = new ArrayList<>();
		for (VecToVec v_w : map) {
			// preserve the original source
			Vec2 targetLoc = Vec2.add(v_w.getTo(), this.r);
			translated.add(new VecToVec(v_w.getFrom(), targetLoc));
		}
		
		return translated;
	}
}
