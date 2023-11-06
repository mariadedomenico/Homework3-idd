package index;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;

import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.LeafReader;
import org.apache.lucene.index.PostingsEnum;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;

import table.Cella;

public class PostingListReader {
	
    @SuppressWarnings("deprecation")
	public void readPostingList(Path indexPath, String termToCheck, Map<Cella, Integer> set2count) throws IOException {
    	
        // Apre l'indice
        Directory directory = FSDirectory.open(indexPath);
        IndexReader indexReader = DirectoryReader.open(directory);

        // Ottieni un LeafReader per accedere ai termini
        LeafReader leafReader = indexReader.leaves().get(0).reader();

        // Ottieni i termini per il campo specificato
        Terms terms = leafReader.terms("cells");

        // Itera sui termini
        if (terms != null) {
            TermsEnum termsEnum = terms.iterator();
            BytesRef term = new BytesRef(termToCheck);
            if (termsEnum.seekExact(term)) {
            	termsEnum = terms.iterator();
	            while ((term = termsEnum.next()) != null) {
	            	String termValue = term.utf8ToString();
	                if (termValue.equals(termToCheck)) {
		                PostingsEnum postingsEnum = termsEnum.postings(null, PostingsEnum.POSITIONS);
		                int docID;
	                    while((docID = postingsEnum.nextDoc()) != PostingsEnum.NO_MORE_DOCS) {
	                        // docID contiene l'ID del documento associato al termine
	                    	String column = indexReader.document(docID).getField("column").stringValue();
	                    	String id = indexReader.document(docID).getField("id").stringValue();
	                    	Cella cella = new Cella(column, id);
	                    	if(set2count.containsKey(cella)) {
	                    		set2count.put(cella, set2count.get(cella)+1);
	                    	}
	                    	else {
	                    		set2count.put(cella, 1);
	                    	}
	                    }
	                }
	            }
            }
        }

        // Chiudi l'IndexReader alla fine
        indexReader.close();
        directory.close();
    }
}
