import java.io.File;

import persistence.CollageActionStore;
import processing.core.PApplet;
import processing.core.PImage;
import processing.data.Table;
import small.data.structures.Buffer;
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
	
	int gridSquareWidth;
	
	SourceGrid sourceGrid;
	TargetGrid targetGrid;
	
	/**
	 * holds squares temporarily selected from the sourceGrid
	 */
	Buffer buffer;
	
	PImage img;
	
	/**
	 * Stores every successful paste action of squares
	 * from the source grid to the target grid
	 */
	CollageActionStore actionStore;
	
    // Run this project as Java application and this
    // method will launch the sketch
	public static void main(String[] args) {
		  PApplet.main("CollageTool");
	}
	
    public void settings() {
        size(1000, 500);     
    }
 
    public void setup() {
    	// Load or Initialize Table
    	//table = loadTable("collage.csv");
    	
    		String file_name = "collage_map.csv";
//    	File f = new File(dataPath(file_name));
//    	if (!f.exists()) {
//    		System.out.println(file_name + " does not exist in sketch folder");
//    		System.out.println("Creating Table " + file_name + " for Collage storage");
//    		
//    	} else {
//    		// "header" option indicates the file has a header row
//    		table = loadTable(file_name, "header");
//    	}
    	
//	    img = loadImage("IMG_1951.jpg");
//	    image(img, 0, 0);
	    img = loadImage("IMG_1721.jpg");
	    
	    actionStore = new CollageActionStore(this, "data/collage_map.csv");
	    
	    /**
	     * TODO: add logic to crop image to rect / square
	     */
	    
	    int imgWidth = 500;		// px
	    int imgHeight = 500;		// px
	    gridSquareWidth = 20;	// px
	    
	    sourceGrid = new SourceGrid(this, 0, 0, imgWidth, imgHeight, gridSquareWidth);
	    targetGrid = new TargetGrid(this, img, imgWidth, 0, imgWidth, imgHeight, gridSquareWidth);
		
	    // initialise to max length
	    buffer = new Buffer(imgWidth / gridSquareWidth, imgHeight / gridSquareWidth);
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
        
        sourceGrid.updateBufferOnSelect(buffer);
//        sourceGrid.showColors();
        
        // sourceGrid provides the view 
        // context for the buffer
        fill(42, 182, 242);
        sourceGrid.showSelectedSquares(buffer);
        
        fill(250);
        sourceGrid.showHoverSelection();
        
        noFill();
        if (mousePressed) {
        		targetGrid.updateMap(buffer, actionStore);
        }
    }
    
}	// end of PApplet extension
