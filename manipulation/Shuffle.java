package manipulation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import small.data.structures.Vec2;
import small.data.structures.VecToVec;

/**
 * Deterministic shuffle that takes a seed.
 * 
 * The seed is useful in case the Transform
 * new Shuffle() is called on the same grid 
 * cluster multiple times by, for example,
 * the draw loop. 
 * 
 * We don't want them jiggling around.
 * 
 * With the correct abstraction, this seed won't be necessary anyhow.
 */
public class Shuffle implements Transform {
	
	private int seed;
	
	public Shuffle() {
		// life, the universe, and everything
		this.seed = 42;
	}
	
	public Shuffle(int seed) {
		this.seed = seed;
	}

	@Override
	public List<VecToVec> applyTo(List<VecToVec> mapList) {
		
		List<Vec2> sources = new ArrayList<>();
		List<Vec2> targets = new ArrayList<>();
		List<VecToVec> shuffledMappings = new ArrayList<>();
		
		// Split apart the VecToVec into parallel lists
		// TODO: explore streams API
		for (VecToVec pair : mapList) {
			sources.add(pair.getFrom());
			targets.add(pair.getTo());
		}
		
		// Shuffle the target of the mapping
		Collections.shuffle(targets, new Random(seed));
		
		// Recombine
		for (int i = 0; i < targets.size(); i++) {
			VecToVec remapped = new VecToVec(sources.get(i), targets.get(i));
			shuffledMappings.add(remapped);
		}
		
		return shuffledMappings;
	}
	
}
