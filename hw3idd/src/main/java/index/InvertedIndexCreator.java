package index;

import java.util.List;
import java.util.Map;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;


public class InvertedIndexCreator {

	public static void createIndex(Map<Integer, List<String>> cells2column, String tableId, IndexWriter writer) throws Exception {

		try {
			
			// iteriamo sulle colonne
			for(Integer column : cells2column.keySet()) {
				// iteriamo sulle celle
				for(String c : cells2column.get(column)) {
					Document document = new Document();
					document.add(new TextField("id", tableId, Field.Store.YES));
					document.add(new TextField("cells", c, Field.Store.YES));
					document.add(new TextField("column", column.toString(), Field.Store.YES));
					writer.addDocument(document);
				}
			}
		}

		catch(Exception e) {
			e.printStackTrace();
		}

	}
}

