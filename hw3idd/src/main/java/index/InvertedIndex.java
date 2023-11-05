package index;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.miscellaneous.PerFieldAnalyzerWrapper;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.codecs.Codec;
import org.apache.lucene.codecs.simpletext.SimpleTextCodec;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.json.simple.parser.ParseException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import table.Cella;

public class InvertedIndex {

	public static void main(String args[]) throws Exception {
		
		Path indexPath = Paths.get("/Users/elisacatena/Desktop/index");   //path per memorizzare l'indice
		Directory directory = null;
		Scanner scanner = new Scanner(System.in);
		
		Codec codec = new SimpleTextCodec();
		Analyzer defaultAnalyzer = new StandardAnalyzer();
		Map<String, Analyzer> perFieldAnalyzers = new HashMap<>();

		perFieldAnalyzers.put("id", new StandardAnalyzer());
		perFieldAnalyzers.put("cells", new StandardAnalyzer());

		Analyzer analyzer = new PerFieldAnalyzerWrapper(defaultAnalyzer, perFieldAnalyzers);
		IndexWriterConfig config = new IndexWriterConfig(analyzer);
		if (codec != null) {
			config.setCodec(codec);
		}
		
		try {
			directory = FSDirectory.open(indexPath);
			IndexWriter writer = new IndexWriter(directory, config);
			writer.deleteAll(); 
			BufferedReader br = null;
			Map<Integer, List<String>> cells2column = new HashMap<>();
			
			try {

	            String currentLine;

	            br = new BufferedReader(new FileReader("/Users/elisacatena/Desktop/tables.json"));

	            while ((currentLine = br.readLine()) != null) {
	                System.out.println("Record:\t" + currentLine);

	                ObjectMapper objectMapper = new ObjectMapper();
	                
	                try {
	                	JsonNode rootNode = objectMapper.readTree(currentLine);
	                	
	                	// Estrai l'oggetto "cells"
	                	JsonNode cellsNode = rootNode.get("cells");
	                    String tableId = rootNode.get("_id").asText();
	                    
	                    //dobbiamo creare una mappa con id della tabella, contenuto della cella e coordinate della cella
	                    IndexInizialization.updateCells2column(cellsNode, cells2column);
	                    
	                   	InvertedIndexCreator.createIndex(cells2column, tableId, writer);
	                   	cells2column.clear();

	                } catch (ParseException e) {
	                    e.printStackTrace();
	                }
	            }

	        } catch (IOException e) {
	            e.printStackTrace();
	        } finally {
	            try {
	                if (br != null) br.close();
	            } catch (IOException ex) {
	                ex.printStackTrace();
	            }
	        }
			writer.commit();
			writer.close();
            
            Map<Cella, Integer> set2count = new HashMap<>();
            
            while (true) {
                System.out.print("Inserisci una query oppure 'exit' per uscire: ");
                String queryReader = scanner.nextLine();
                if(queryReader.equals("exit")) break;
                String[] input = queryReader.split(",");
                for(int i = 0; i < input.length; i++) {
                	PostingListReader.readPostingList(indexPath, input[i], set2count);
                }
                
            }
            scanner.close();
                    
            for(Cella k : set2count.keySet()) {
            	System.out.println("colonna " + k.getColonna() + ", tableID " + k.getTableId() + "-> " + set2count.get(k));
            }
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
}
