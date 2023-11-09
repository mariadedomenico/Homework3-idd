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
	
	private Double retrievedDoc = 0.0;
	private Double relevantDoc = 0.0;
	
    public Double getRetrievedDoc() {
		return retrievedDoc;
	}

	public void setRetrievedDoc(Double retrievedDoc) {
		this.retrievedDoc = retrievedDoc;
	}

	public Double getRelevantDoc() {
		return relevantDoc;
	}

	public void setRelevantDoc(Double relevantDoc) {
		this.relevantDoc = relevantDoc;
	}

	@SuppressWarnings("deprecation")
	public void readPostingList(IndexReader indexReader, IndexSearcher searcher, String termToCheck, Map<Cella, Integer> set2count) throws IOException, ParseException {
    	QueryParser queryParser = new QueryParser("cells", new StandardAnalyzer());
    	Query query = queryParser.parse(termToCheck);
        TopDocs hits = searcher.search(query, indexReader.numDocs());
        System.out.println("Sono stati trovati " + hits.scoreDocs.length + " " + "documenti");
        Integer cont = hits.scoreDocs.length;
        this.setRetrievedDoc(this.getRetrievedDoc()+cont.doubleValue());
        for (int i = 0; i < cont; i++) {
            ScoreDoc scoreDoc = hits.scoreDocs[i];
			Document doc = searcher.doc(scoreDoc.doc);
            if(doc.get("cells").startsWith(termToCheck + ",") || doc.get("cells").contains(", " + termToCheck + ',') || doc.get("cells").endsWith(", " + termToCheck)) {
            	Cella cella = new Cella(doc.get("column"), doc.get("id"));
            	if(set2count.containsKey(cella)) {
            		set2count.put(cella, set2count.get(cella)+1);
            	}
            	else {
            		set2count.put(cella, 1);
            	}
            	this.setRelevantDoc(this.getRelevantDoc()+1);
        	}
        }
        System.out.print("\n");
    }
    
}
