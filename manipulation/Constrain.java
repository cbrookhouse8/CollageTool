package manipulation;

import java.util.ArrayList;
import java.util.List;

import small.data.structures.Vec2;
import small.data.structures.VecToVec;
import utilities.Logger;

public class Constrain implements Transform {
	
	int minX;
	int minY; 
	int maxX; 
	int maxY;
	Logger log;
	
	public Constrain(int minX, int minY, int maxX, int maxY) {
		this.minX = minX;
		this.minY = minY; 
		this.maxX = maxX; 
		this.maxY = maxY;
		log = new Logger(this);
	}
	
	@Override
	public List<VecToVec> applyTo(List<VecToVec> map) {
		List<VecToVec> inrange = new ArrayList<>();
		
		for (VecToVec v_w : map) {
			Vec2 w = v_w.getTo();
			
			if (w.x < minX || w.x > maxX) {
				continue;
			} else if (w.y < minY || w.y > maxY) {
				continue;	
			}
				
			inrange.add(v_w);
		}
		
//		log.info((map.size() - inrange.size()) +	" squares were out of bounds");
		
		return inrange;
	}

}
