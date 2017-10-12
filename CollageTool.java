import manipulation.Identity;
import manipulation.ReflectLocalX;
import manipulation.ReflectLocalY;
import manipulation.Shuffle;
import manipulation.Transform;
import persistence.CollageActionStore;
import persistence.CollageConfiguration;
import processing.core.PApplet;
import processing.core.PImage;
import small.data.structures.Buffer;
import small.data.structures.GridMap;
import visible.objects.SourceGrid;
import visible.objects.TargetGrid;
import utilities.Logger;

/**
 * TODO: handle the offsets properly
 */
public class CollageTool extends PApplet {
	
	Logger log;
	
	int gridSquareWidth;
	
	SourceGrid sourceGrid;
	TargetGrid targetGrid;
	
	GridMap gridMap;
	
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
	
	Transform userTransform;
	
	boolean viewAllMappedSquares;
	boolean viewSpecificMappings;
	
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
	    
	    userTransform = new Identity();
	    
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
	    
	    if (loadedFromFile) { 
	    		gridMap = GridMap.of(actionStore);
	    } else {
	    		// Length of the flattened grid
	    		int span = (imgWidth / gridSquareWidth) * (imgWidth / gridSquareWidth);
	    		gridMap = new GridMap(span);
	    }
	    
	    // initialise to max length
	    buffer = new Buffer(imgWidth / gridSquareWidth, imgHeight / gridSquareWidth);
	    
	    sourceGrid = new SourceGrid(this, buffer, gridMap, 0, 0, imgWidth, imgHeight, gridSquareWidth);
	    targetGrid = new TargetGrid(this, img, buffer, gridMap, imgWidth, 0, imgWidth, imgHeight, gridSquareWidth);
	    
	    // View options
	    viewAllMappedSquares = true;
	    viewSpecificMappings = true;
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
        
        targetGrid.showCurrentMappingGroup();
        
        // When squares are clicked on the source
        // update the buffer with their indexes
//        sourceGrid.showColors();
        
        noFill();
        stroke(102, 102, 255);
        sourceGrid.showPreviousSelection();
        noFill();
        
        noFill();
        stroke(219, 0, 172);
        sourceGrid.showMappedSquares(viewAllMappedSquares);
        
        // sourceGrid provides the view 
        // context for the buffer
		strokeWeight(1);
		stroke(32, 216, 51);
        sourceGrid.showCurrentSelection();
        
        strokeWeight(1);
		stroke(255);
		noFill();
		if (viewSpecificMappings) {
			sourceGrid.showMapFrom(targetGrid);
			targetGrid.showMapFrom(sourceGrid);
		}
		
		strokeWeight(2);
		stroke(32, 216, 51);
		noFill();
        sourceGrid.showHoverSelection();
        targetGrid.showHoverSelection();
        strokeWeight(1);
    }
    
    // Processing enforces the logic:
    // mouseClicked xor MouseDragged
    public void mouseClicked() {
    		// don't use mouseReleased to detect mouseClicked
    		sourceGrid.updateBufferOnClick();
    		targetGrid.updateAndPersistMap(actionStore);
    }
    
    // Logic: mouseClicked && mouseReleased
//    public void mouseReleased() {
//    		log.info("Mouse released at frame " + frameCount);
//    }
    
    // Logic: at the end of the drag action, mouseReleased
    public void mouseDragged() {
    		sourceGrid.updateBufferOnDrag();
    }
    
    public void keyPressed() {
    		if (key == 's') {
    			String state = viewAllMappedSquares ? "off" : "on";
    			String msg = "Turning " + state + " all mapped squares view.";
    			viewAllMappedSquares = !viewAllMappedSquares;
    			log.info(msg);
    		}
    		
    		if (key == 'm') {
    			String state = viewSpecificMappings ? "off" : "on";
    			String msg = "Turning " + state + " view of specific mappings.";
    			viewSpecificMappings = !viewSpecificMappings;
    			log.info(msg);
    		}
    		
    		if (key == 'c') {
    			log.info("Removing user transforms");
    			targetGrid.setUserTransform(new Identity());
    		}
    		
    		if (key == 'x') {
    			log.info("Reflect about local X Axis transform");
    			targetGrid.setUserTransform(new ReflectLocalX());
    		}
    		
    		if (key == 'y') {
    			log.info("Reflect about local Y Axis transform");
    			targetGrid.setUserTransform(new ReflectLocalY());
    		}
    		
    		if (key == 'r') {
    			log.info("Shuffle the squares");
    			targetGrid.setUserTransform(new Shuffle());
    		}
    		
    		if (key == 'f') {
    			buffer.flush();
    		}
    		
    }
    
}	// end of PApplet extension
