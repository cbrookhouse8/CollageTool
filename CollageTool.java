import java.io.File;
import java.util.HashMap;

import persistence.CollageActionStore;
import persistence.CollageConfiguration;
import processing.core.PApplet;
import processing.core.PImage;
import processing.data.Table;
import small.data.structures.Buffer;
import visible.objects.Grid;
import visible.objects.SourceGrid;
import visible.objects.TargetGrid;
import utilities.Logger;

/**
 * TODO: handle the offsets properly
 * 
 * @author charlesbrookhouse1
 *
 */
public class CollageTool extends PApplet {
	
	Logger log;
	
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
        size(1280, 640);     
    }
 
    public void setup() {
    	
    		log = new Logger(this);
    	// Load or Initialize Table
    	//table = loadTable("collage.csv");
    	
//    		String file_name = "collage_map.csv";
    		String file_name = "river_dev.csv";
    	
//	    img = loadImage("IMG_1951.jpg");
//	    image(img, 0, 0);
	    //img = loadImage("IMG_1721.jpg");
    		img = loadImage("IMG_1661_sq_small.JPG");
	    
	    actionStore = new CollageActionStore(this, file_name);
	    
	    boolean loadedFromFile = actionStore.loadFromFile();
	    
	    // Defaults
//	    int imgWidth = 500;		// px
//	    int imgHeight = 500;		// px
	    
	    int imgWidth = 640;		// px
	    int imgHeight = 640;		// px
	    
	    gridSquareWidth = 16;	// px
	    
	    if (loadedFromFile) {
	    		CollageConfiguration config = actionStore.getCollageConfiguration();
	    		
	    		imgWidth = config.getImgWidth();
	    		imgHeight = config.getImgHeight();
	    		
	    		if (img.width != imgWidth) {
	    			throw new RuntimeException("Loaded image has different width from imgWidth in configuration file.");
	    		}
	    		
	    		if (img.height != imgHeight) {
	    			throw new RuntimeException("Loaded image has different height from imgHeight in configuration file.");
	    		}
	    		
	    		gridSquareWidth = config.getGridSquareWidth();
	    }
	    
	    /**
	     * TODO: add method such as TargetGrid.of(CollageActionStore c, PImage img);
	     */
	    sourceGrid = new SourceGrid(this, 0, 0, imgWidth, imgHeight, gridSquareWidth);
	    targetGrid = new TargetGrid(this, img, imgWidth, 0, imgWidth, imgHeight, gridSquareWidth);
	    
	    if (loadedFromFile) { 
	    		HashMap<Integer, Integer> storedGridMap = actionStore.getGridMap();
	    		targetGrid.setGridMap(storedGridMap);
	    }
		
	    // initialise to max length
	    buffer = new Buffer(imgWidth / gridSquareWidth, imgHeight / gridSquareWidth);
    }
 
    public void draw() {
        background(0);
        image(img, 0, 0);
        
        stroke(0);
        sourceGrid.showGridLines();
//        fill(0,179,255,100);
        
        stroke(0);
        targetGrid.showGridLines();
//        targetGrid.showColors();
        targetGrid.showImageSegments();
        
        // When squares are clicked on the source
        // update the buffer with their indexes
        sourceGrid.updateBufferOnSelect(buffer);
//        sourceGrid.showColors();
        
        // sourceGrid provides the view 
        // context for the buffer
        fill(42, 182, 242);
        sourceGrid.showSelectedSquares(buffer);
        
        fill(250);
        sourceGrid.showHoverSelection();
        
        noFill();
        	targetGrid.updateMap(buffer, actionStore);
    }
    
}	// end of PApplet extension
