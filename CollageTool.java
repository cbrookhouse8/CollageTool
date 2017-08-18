import java.io.File;

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
	Table table;
	
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
    	File f = new File(dataPath(file_name));
    	if (!f.exists()) {
    		System.out.println(file_name + " does not exist in sketch folder");
    		System.out.println("Creating Table " + file_name + "for Collage storage");
    		
    		table = new Table();
    		table.addColumn("id");				// int
    		
    		// One source grid square may be referenced
    		// by many target grid squares
    		
    		// Index of square into grid given
    		// the row_major / col_major convention
    		// adopted in the program
    		table.addColumn("source_grid_id");	// int
    		table.addColumn("target_grid_id");	// int
    		
//       	table.addColumn("relativeOrigin");	// int
    		
    		// Store the vector mapping for convenience
    		// even though this can be easily derived
    		// from other fields in the table
    		table.addColumn("source_grid_x");	// int
    		table.addColumn("source_grid_y");	// int
    		
    		table.addColumn("target_grid_x");	// int
    		table.addColumn("target_grid_y");	// int
    		
//    		table.addColumn("time");
    		
    		// Generic configuration info. This should really
    		// be in a separate file to avoid duplication
    		table.addColumn("row_major");			// int (1 or 0 => true or false) 
    		table.addColumn("img_width");			// int (pixels)
    		table.addColumn("img_height");			// int (pixels)
    		table.addColumn("grid_square_width");	// int (pixels)
    		
    		// These can also be derived from the above
    		// but store for convenience
    		table.addColumn("verticals");
    		table.addColumn("horizontals");
    	} else {
    		table = loadTable(file_name);
    	}
    	
//	    img = loadImage("IMG_1951.jpg");
//	    image(img, 0, 0);
	    img = loadImage("IMG_1721.jpg");
	    
	    /**
	     * TODO: add logic to crop image to rect / square
	     */
	    
	    int imgWidth = 500;		// px
	    int imgHeight = 500;	// px
	    gridSquareWidth = 20;	// px
	    
	    sourceGrid = new SourceGrid(this, 0, 0, imgWidth, imgHeight, gridSquareWidth);
	    targetGrid = new TargetGrid(this, img, table, imgWidth, 0, imgWidth, imgHeight, gridSquareWidth);
		
	    // initialize to max length
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
        	targetGrid.updateMap(buffer);
        }
    }
    
}	// end of PApplet extension
