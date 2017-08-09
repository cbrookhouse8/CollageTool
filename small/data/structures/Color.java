package small.data.structures;

public class Color {
	public int r;
	public int g;
	public int b;
	
	public int greyscale;
	
	public Color(int _r, int _g, int _b) {
		r = _r;
		g = _g;
		b = _b;
	}
	
	public Color(int grey) {
		this.greyscale = grey;
	}
}