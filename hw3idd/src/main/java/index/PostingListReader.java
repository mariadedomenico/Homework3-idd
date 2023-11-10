package index;

import java.io.IOException;
import java.util.Map;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;

import table.Cella;

public class PostingListReader {
	
	private Double m1 = 0.0;
	private Double m2 = 0.0;

	public Double getM1() {
		return m1;
	}

	public void setM1(Double m1) {
		this.m1 = m1;
	}
	
	public Double getM2() {
		return m2;
	}

	public void setM2(Double m2) {
		this.m2 = m2;
	}

	@SuppressWarnings("deprecation")
	public void readPostingList(IndexReader indexReader, IndexSearcher searcher, String termToCheck, Map<Cella, Integer> set2count) throws IOException, ParseException {
    	QueryParser queryParser = new QueryParser("cells", new StandardAnalyzer());
    	Query query = queryParser.parse(termToCheck);
        TopDocs hits = searcher.search(query, indexReader.numDocs());
        System.out.println("Sono state trovate " + hits.scoreDocs.length + " " + "colonne");
        Integer cont = hits.scoreDocs.length;
        Double relevantDocCount = 0.0;
        for (int i = 0; i < cont; i++) {
            ScoreDoc scoreDoc = hits.scoreDocs[i];
			Document doc = searcher.doc(scoreDoc.doc);
			String cells = doc.get("cells").toLowerCase();
            if(cells.startsWith(termToCheck + ",") || cells.contains(", " + termToCheck + ',') || cells.endsWith(", " + termToCheck)) {
            	Cella cella = new Cella(doc.get("column"), doc.get("id"));
            	if(set2count.containsKey(cella)) {
            		set2count.put(cella, set2count.get(cella)+1);
            	}
            	else {
            		set2count.put(cella, 1);
            	}
            	relevantDocCount++;
        	}
        }
        
        this.setM1(this.getM1()+(relevantDocCount/cont.doubleValue()));
        System.out.print("\n");
    }
    
}
