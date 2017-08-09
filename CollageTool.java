import processing.core.PApplet;
import processing.core.PImage;
import visible.objects.Grid;
import visible.objects.SourceGrid;
import visible.objects.TargetGrid;

/**
 * TODO: handle the offsets properly
 * 
 * @author charlesbrookhouse1
 *
 */
public class CollageTool extends PApplet {
	
	SourceGrid sourceGrid;
	TargetGrid targetGrid;
	PImage img;
	
    // Run this project as Java application and this
    // method will launch the sketch
	public static void main(String[] args) {
		  PApplet.main("CollageTool");
	}
	
    public void settings() {
        size(1000, 500);     
    }
 
    public void setup() {
//	    img = loadImage("IMG_1951.jpg");
	    img = loadImage("IMG_1721.jpg");
//	    image(img, 0, 0);
	    
	    sourceGrid = new SourceGrid(this, 0, 0, 500, 500, 20);
	    targetGrid = new TargetGrid(this, img, 500, 0, 500, 500, 20);
    }
 
    public void draw() {
        background(255);
        image(img, 0, 0);
        
        stroke(200);
        sourceGrid.showGridLines();
//        fill(0,179,255,100);
        
        stroke(100);
        targetGrid.showGridLines();
//        targetGrid.showColors();
        targetGrid.showImageSegments();
        
        sourceGrid.listenForSelection();
//        sourceGrid.showColors();
        
        fill(42, 182, 242);
        sourceGrid.showSelectedSquares();
        
        fill(250);
        sourceGrid.showHoverSelection();
        
        noFill();
        if (mousePressed) {
        	targetGrid.updateMap(sourceGrid);
        }
    }
    
}	// end of PApplet extension
