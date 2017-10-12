package manipulation;

import java.util.ArrayList;
import java.util.List;

import small.data.structures.Vec2;
import small.data.structures.VecToVec;
import utilities.Logger;

/**
 * Vertical reflection axis is defined
 * as the horizontal centre, C, of the cluster:
 * 		C = minX + (maxX - minX) / 2
 */
public class ReflectLocalY implements Transform {
	
	private Logger log;
	
	public ReflectLocalY() {
		log = new Logger(this);
	}
	
	@Override
	public List<VecToVec> applyTo(List<VecToVec> mapList) {
		
		if (mapList.size()  == 0) {
			log.info("List<VecToVec> passed transform contains no mappings.");
		}
		
		int minX = mapList.get(0).getTo().getX();
		int maxX = minX;
		
		// Find max and min
		for (VecToVec pair : mapList) {
			int x = pair.getTo().getX();
			if (x < minX) {
				minX = x;
			} else if (x > maxX) {
				maxX = x;
			}
		}
		
		// Reflect about "local" Y-Axis
		// i.e. the centre of the cluster
		// of grid squares
		
		List<VecToVec> reflectedMapList = new ArrayList<>();
		
		// The x position of the reflection axis in
		// Grid space is: C = minX + (maxX - minX) / 2
		
		for (VecToVec pair : mapList) {
			// TODO: express as vector operation
			int x = pair.getTo().getX();
			int y = pair.getTo().getY();
			int rx = maxX + minX - x;
			Vec2 rTo = new Vec2(rx, y);
			reflectedMapList.add(new VecToVec(pair.getFrom(), rTo));
		}
		
		return reflectedMapList;
	}
}
