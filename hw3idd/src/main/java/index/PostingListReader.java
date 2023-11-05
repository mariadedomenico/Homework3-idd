package index;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.LeafReader;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;

public class PostingListReader {
	
    public static void readPostingList(Path indexPath) throws IOException {
    	
        // Apre l'indice
        Directory directory = FSDirectory.open(indexPath);
        IndexReader indexReader = DirectoryReader.open(directory);

        // Ottieni un LeafReader per accedere ai termini
        LeafReader leafReader = indexReader.leaves().get(0).reader();

        // Campo per cui desideri ottenere il term vector
        String campo = "cells";

        // Ottieni i termini per il campo specificato
        Terms terms = leafReader.terms(campo);

        // Itera sui termini
        if (terms != null) {
            TermsEnum termsEnum = terms.iterator();
            BytesRef term;
            while ((term = termsEnum.next()) != null) {
                String termText = term.utf8ToString();
                long termFrequency = termsEnum.totalTermFreq();
                System.out.println("Term: " + termText + ", Frequency: " + termFrequency);
            }
        }

        // Chiudi l'IndexReader alla fine
        indexReader.close();
        directory.close();
    }
}