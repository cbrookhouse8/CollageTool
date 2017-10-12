package manipulation;

import java.util.List;

import small.data.structures.VecToVec;


/**
 * TODO: improve this abstraction so that Transforms 
 * can be concatenated / composed etc
 * 
 * Probably should use Lambdas
 */
public interface Transform {
	List<VecToVec> applyTo(List<VecToVec> mapList);
}
