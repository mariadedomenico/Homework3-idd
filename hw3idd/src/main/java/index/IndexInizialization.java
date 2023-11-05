package index;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class IndexInizialization {

	public static void updateCells2column(int column, String cellContent, Map<Integer, List<String>> cells2column) {
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
