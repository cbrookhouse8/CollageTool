package persistence;

import java.io.File;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import utilities.Logger;

import processing.core.PApplet;
import processing.data.Table;
import processing.data.TableRow;

/**
 * Sugar over the Processing Table API
 */
public class CollageActionStore {
	protected PApplet p;
	protected Table table;
	protected String file_name;
	protected String path;
	protected Logger log;

	public CollageActionStore(PApplet p, String file_name) {
		super();
		this.p = p;
		this.file_name = file_name;
		this.path = "data/" + file_name;
		table = new Table();
		
		this.log = new Logger(this);
	}
	
	public boolean loadFromFile() {
		String dataPath = p.dataPath(file_name);
	    	File f = new File(dataPath);
	    	
		log.info("Looking for collage data in "+dataPath);
		
		/**
		 * If table has 0 rows on file, then this will probably cause
		 * an error somewhere else in the program as we'll be assuming that
		 * there exists row of collage configuration data and encounter
		 * problems when trying to retrieve it.
		 */
	    	if (f.exists()) {
	    		log.info("Loading collage data into CollageActionStore from file: "+dataPath);
	    		
	       	// "header" option indicates the file has a header row
	    		table = p.loadTable(file_name, "header");
	    		int rowCount = table.getRowCount();
	    		log.info("Loaded table from file. Table has "+rowCount+" rows");
	    		
	    		if (rowCount == 0) throw new RuntimeException("Loaded file does not contain Collage config data.");
	    		
	    		return true;
	    	} else {
	    		log.info(file_name + " does not exist in sketch folder");
	    		log.info("Initialising new Table " + file_name + " for Collage storage");
	    		
	    		table = new Table();
	    		return false;
	    	}
	}
	
	/**
	 * Only if this.loadFromFile() == true, should this method be called
	 * @return
	 */
	public CollageConfiguration getCollageConfiguration() {
		TableRow firstRow = table.getRow(0);
		CollageActionEntry firstAction = CollageActionEntry.of(firstRow);
		CollageConfiguration config = firstAction.extractConfigurationData();
		return config;
	}
	
	/**
	 * 
	 * @return (key) index into target grid, (value) index into source grid
	 */
	public HashMap<Integer, Integer> getGridMap() {
		HashMap<Integer, Integer> gridMap = new HashMap<>();
		
		Iterable<TableRow> rowIter = table.rows();
		
		for (TableRow row : rowIter) {
			CollageActionEntry entry = CollageActionEntry.of(row);
			// int is Boxed for the map to Integer
			gridMap.put(entry.getTargetIdx(), entry.getSourceIdx());
		}
				
		return gridMap;
	}
	
	public void addActionGroup(List<CollageActionEntry> group) {
		
		if (group.size() == 0) { 
			log.info("Empty group passed to .addActionGroup");
			return;
		}
		
		log.info("group size: " + group.size());
		
		List<String> columnHeadings = group.get(0).getColumnHeadings();
		
		// If table is empty, then initialise it from collageActionEntry
		boolean isFirstRow = false;
		int groupId = 0;
		
		if (table.getRowCount() == 0) {
			isFirstRow = true;
			
			// CollageActionStore controls `id` and `group_id`
			table.addColumn("id");
			table.addColumn("group_id");
			
			log.info("Initialising column headings from CollageActionEntry");
			for (String columnName : columnHeadings) {
				table.addColumn(columnName);
			}
		} else {
			groupId = table.getInt(table.lastRowIndex(), "group_id") + 1;
		}
				
		for (CollageActionEntry actionEntry : group) {
			addAction(actionEntry, groupId);
		}
		
		// write to file		
		p.saveTable(table, path);
		
		DateTimeFormatter formatter =
			    DateTimeFormatter.ofLocalizedDateTime(FormatStyle.FULL)
			                     .withLocale(Locale.UK)
			                     .withZone(ZoneId.systemDefault());
		
		Instant instant = Instant.now();
		String output = formatter.format(instant);
		
		log.info("Table saved on: " + output);
	}
	
	/**
	 * 
	 * @param collageActionEntry row values (except for id, which CollageActionStore controls)
	 */
	private void addAction(CollageActionEntry collageActionEntry, int group_id) {
		
		List<String> columnHeadings = collageActionEntry.getColumnHeadings();
		Map<String, Integer> rowMap = collageActionEntry.getRowMap();
		
		TableRow row = table.addRow();
		
		// id
		int lastRowIdx = table.lastRowIndex();
		
		if (lastRowIdx < 0) {
			// Not sure what would cause this to happen. Remove?
			throw new RuntimeException("lastRowIdx < 0 in CollageActionStore.addAction()");
		}
		
		row.setInt("id", lastRowIdx);
		
		// group id
		
		row.setInt("group_id", group_id);
		
		// other fields
		
		for (String columnName : columnHeadings) {
			Integer value = rowMap.get(columnName);
			row.setInt(columnName, value.intValue());
		}
	}
}
