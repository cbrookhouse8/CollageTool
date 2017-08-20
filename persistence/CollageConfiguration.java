package persistence;

public class CollageConfiguration {
	private int rowMajor;
	private int imgWidth;
	private int imgHeight;
	private int gridSquareWidth;
	private int verticals;
	private int horizontals;
	
	public CollageConfiguration(int rowMajor, int imgWidth, int imgHeight, int gridSquareWidth, int verticals,
			int horizontals) {
		this.rowMajor = rowMajor;
		this.imgWidth = imgWidth;
		this.imgHeight = imgHeight;
		this.gridSquareWidth = gridSquareWidth;
		this.verticals = verticals;
		this.horizontals = horizontals;
	}
	
	public CollageConfiguration() {
		
	}
	
	public int getRowMajor() {
		return rowMajor;
	}

	public void setRowMajor(int rowMajor) {
		this.rowMajor = rowMajor;
	}

	public int getImgWidth() {
		return imgWidth;
	}

	public void setImgWidth(int imgWidth) {
		this.imgWidth = imgWidth;
	}

	public int getImgHeight() {
		return imgHeight;
	}

	public void setImgHeight(int imgHeight) {
		this.imgHeight = imgHeight;
	}

	public int getGridSquareWidth() {
		return gridSquareWidth;
	}

	public void setGridSquareWidth(int gridSquareWidth) {
		this.gridSquareWidth = gridSquareWidth;
	}

	public int getVerticals() {
		return verticals;
	}

	public void setVerticals(int verticals) {
		this.verticals = verticals;
	}

	public int getHorizontals() {
		return horizontals;
	}

	public void setHorizontals(int horizontals) {
		this.horizontals = horizontals;
	}
	
}
