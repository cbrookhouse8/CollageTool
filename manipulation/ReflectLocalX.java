package manipulation;

import java.util.ArrayList;
import java.util.List;

import small.data.structures.Vec2;
import small.data.structures.VecToVec;
import utilities.Logger;

/**
 * Horizontal reflection axis is defined
 * at the vertical centre, C, of the cluster:
 * 		C = minY + (maxY - minY) / 2
 */
public class ReflectLocalX implements Transform {
	
	private Logger log;
	
	public ReflectLocalX() {
		log = new Logger(this);
	}
	
	@Override
	public List<VecToVec> applyTo(List<VecToVec> mapList) {
		
		if (mapList.size()  == 0) {
			log.info("List<VecToVec> passed transform contains no mappings.");
		}
		
		// some initial values
		int minY = mapList.get(0).getTo().getY();
		int maxY = minY;
		
		// Converge on max and min
		for (VecToVec pair : mapList) {
			int y = pair.getTo().getY();
			if (y < minY) {
				minY = y;
			} else if (y > maxY) {
				maxY = y;
			}
		}
		
		// Reflect about "local" Y-Axis
		// i.e. the centre of the cluster
		// of grid squares
		
		List<VecToVec> reflectedMapList = new ArrayList<>();
		
		// The y position of the reflection axis in
		// Grid space is: C = minY + (maxY - minY) / 2
		
		for (VecToVec pair : mapList) {
			// TODO: express as vector operation
			int x = pair.getTo().getX();
			int y = pair.getTo().getY();
			int ry = maxY + minY - y;
			Vec2 rTo = new Vec2(x, ry);
			
			// re-specify the mapping to the target grid
			reflectedMapList.add(new VecToVec(pair.getFrom(), rTo));
		}
		
		return reflectedMapList;
	}
}
