package small.data.structures;

public class Vec2 {
	public int x;
	public int y;
	
	public Vec2(int _x, int _y) {
		x = _x;
		y = _y;
	}
	
	// Create a copy
	public Vec2(Vec2 v) {
		this.x = v.getX();
		this.y = v.getY();
	}
	
	// cast to int
	public Vec2(float _x, float _y) {
		x = (int) _x;
		y = (int) _y;
	}
	
	public void add(Vec2 t) {
		x += t.x;
		y += t.y;
	}
	
	public void sub(Vec2 t) {
		x -= t.x;
		y -= t.y;
	}
	
	public boolean equals(Vec2 v2) {
		return (v2.x == x && v2.y == y);
	}
	
	public static Vec2 add(Vec2 a, Vec2 b) {
		return new Vec2(a.x + b.x, a.y + b.y);
	}
	
	public static Vec2 sub(Vec2 a, Vec2 b) {
		return new Vec2(a.x - b.x, a.y - b.y);
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}
	
}
