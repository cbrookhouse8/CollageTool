package persistence;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import processing.core.PApplet;
import processing.data.Table;
import processing.data.TableRow;

/**
 * Sugar over the Processing Table API
 * @author charliebrookhouse
 *
 */
public class CollageActionStore {
	protected PApplet p;
	protected Table table;
	protected String path;

	public CollageActionStore(PApplet p, String path) {
		super();
		this.p = p;
		this.path = path;
		table = new Table();
	}
	
	public void addActionGroup(List<CollageActionEntry> group) {
		
		if (group.size() == 0) { 
			System.out.println("Empty group passed to CollageActionStore.addActionGroup");
			return;
		}
		
		System.out.println("group size: " + group.size());
		
		List<String> columnHeadings = group.get(0).getColumnHeadings();
		
		// If table is empty, then initialise it from collageActionEntry
		boolean isFirstRow = false;
		int groupId = 0;
		
		if (table.getRowCount() == 0) {
			isFirstRow = true;
			
			// CollageActionStore controls `id` and `group_id`
			table.addColumn("id");
			table.addColumn("group_id");
			
			System.out.println("Initialising column headings from CollageActionEntry");
			for (String columnName : columnHeadings) {
				table.addColumn(columnName);
			}
		} else {
			groupId = table.getInt(table.lastRowIndex(), "group_id") + 1;
		}
				
		for (CollageActionEntry actionEntry : group) {
			addAction(actionEntry, groupId, isFirstRow);
		}
		
		// write to file		
		p.saveTable(table, path);
		
		DateTimeFormatter formatter =
			    DateTimeFormatter.ofLocalizedDateTime(FormatStyle.FULL)
			                     .withLocale(Locale.UK)
			                     .withZone(ZoneId.systemDefault());
		
		Instant instant = Instant.now();
		String output = formatter.format(instant);
		
		System.out.println("Table saved on: " + output);
	}
	
	/**
	 * 
	 * @param collageActionEntry row values (except for id, which CollageActionStore controls)
	 */
	private void addAction(CollageActionEntry collageActionEntry, int group_id, boolean isFirstRow) {
		
		List<String> columnHeadings = collageActionEntry.getColumnHeadings();
		Map<String, Integer> rowMap = collageActionEntry.getRowMap();
		
		TableRow row = table.addRow();
		
		// id
		
		if (isFirstRow) { 
			row.setInt("id", 0);
		} else {
			row.setInt("id", table.lastRowIndex());
		}
		
		// group id
		
		row.setInt("group_id", group_id);
		
		// other fields
		
		for (String columnName : columnHeadings) {
			Integer value = rowMap.get(columnName);
			row.setInt(columnName, value.intValue());
		}
	}
}
