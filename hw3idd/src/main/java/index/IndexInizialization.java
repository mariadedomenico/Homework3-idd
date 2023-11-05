package index;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;

public class IndexInizialization {

	public static void updateCells2column(JsonNode cells, Map<Integer, List<String>> cells2column) {
		int column;
		String cellContent = null;
		
		for(int i = 0; i < cells.size(); i++) {
			column = cells.get(i).get("Coordinates").get("column").asInt();
			cellContent = cells.get(i).get("cleanedText").asText();
			
			if(cells2column.containsKey(column)) {
				cells2column.get(column).add(cellContent);
			}
			else {
				List<String> temp = new ArrayList<String>();
				temp.add(cellContent);
				cells2column.put(column, temp);
			}
		}
		
	}
}
