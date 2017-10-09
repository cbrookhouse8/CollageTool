package manipulation;

import java.util.List;

import small.data.structures.VecToVec;

public interface Transform {
	List<VecToVec> applyTo(List<VecToVec> map);
}
