package Query;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.lucene.document.Document;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;

import table.Cella;

public class QueryRunner {

	
    @SuppressWarnings("deprecation")
	public static void runQuery(IndexSearcher searcher, Query query, Map<String, List<Cella>> term2cell, String input) throws IOException {
        TopDocs hits = searcher.search(query, 8);
        System.out.println("Sono stati trovati " + hits.scoreDocs.length + " " + "documenti");
        for (int i = 0; i < hits.scoreDocs.length; i++) {
            ScoreDoc scoreDoc = hits.scoreDocs[i];
            Document doc = searcher.doc(scoreDoc.doc);
            Cella cella = new Cella(doc.get("column"), doc.get("id"));
            term2cell.get(input).add(cella);
            System.out.println("doc"+scoreDoc.doc + ": "+ doc.get("nome") + " (" + scoreDoc.score +")");
        }
        System.out.print("\n");
    }
}
