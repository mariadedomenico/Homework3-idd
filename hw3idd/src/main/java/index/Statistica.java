package index;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import table.Cella;

public class Statistica {
	
	public <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
		List<Entry<K, V>> list = new ArrayList<>(map.entrySet());
		list.sort(Collections.reverseOrder(Entry.comparingByValue()));

		Map<K, V> result = new LinkedHashMap<>();
		for (Entry<K, V> entry : list) {
			result.put(entry.getKey(), entry.getValue());
		}

		return result;
	}
	
	public void findTopK(Map<Cella, Integer> map) {
		map = this.sortByValue(map);
		for(Cella k : map.keySet()) {
			System.out.println("colonna " + k.getColonna() + ", tableID " + k.getTableId() + "-> " + map.get(k));
		}
		System.out.println("\n");
		
		int i = map.entrySet().iterator().next().getValue();
		
		for(Cella k : map.keySet()) {
			if(map.get(k) == i-3) break;
			System.out.println("colonna " + k.getColonna() + ", tableID " + k.getTableId() + "-> " + map.get(k));
		}
	
	}

}
