package manipulation;

import java.util.List;

import small.data.structures.VecToVec;
import utilities.Logger;

/**
 * TODO: remove the need for this. Currently a
 * hack to prevent passing no transform / null for
 * the transform to the TargetGrid.
 */
public class Identity implements Transform {
	
	private Logger log;
	public Identity() {
		// TODO Auto-generated constructor stub
		log = new Logger(this);
	}

	@Override
	public List<VecToVec> applyTo(List<VecToVec> mapList) {
		return mapList;
	}
	
	

}
