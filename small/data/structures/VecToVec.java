package small.data.structures;

/**
 * This is basically a map
 */
public class VecToVec {

	Vec2 from;
	Vec2 to;

	public VecToVec(Vec2 from, Vec2 to) {
		this.from = new Vec2(from.x, from.x);
		this.to = new Vec2(to.x, to.y);
	}

	public Vec2 getFrom() {
		return from;
	}

	public void setFrom(Vec2 from) {
		this.from = from;
	}

	public Vec2 getTo() {
		return to;
	}

	public void setTo(Vec2 to) {
		this.to = to;
	}
	
}
