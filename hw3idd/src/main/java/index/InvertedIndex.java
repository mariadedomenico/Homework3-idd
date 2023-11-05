package index;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.miscellaneous.PerFieldAnalyzerWrapper;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.codecs.Codec;
import org.apache.lucene.codecs.simpletext.SimpleTextCodec;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.json.simple.parser.ParseException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import Query.QueryRunner;
import table.Cella;

public class InvertedIndex {

	public static void main(String args[]) throws Exception {
		
		Path indexPath = Paths.get("/Users/elisacatena/Desktop");   //path per memorizzare l'indice
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
	            
	            IndexReader reader = DirectoryReader.open(directory);
	            IndexSearcher searcher = new IndexSearcher(reader);
	            
	            while (true) {
	                System.out.print("Inserisci una query oppure 'exit' per uscire: ");
	                String queryReader = scanner.nextLine();
	                if(queryReader.equals("exit")) break;
	                
	                String[] input = queryReader.split(",");
	          
	                QueryParser queryParser = new QueryParser("cells", new StandardAnalyzer());
	                Query query;
	                Map<String, List<Cella>> term2cell = new HashMap<>();
	                for(int i = 0; i < input.length; i++) {
	                	query = queryParser.parse(input[i]);
	                	term2cell.put(input[i], new ArrayList<>());
	                	QueryRunner.runQuery(searcher, query, term2cell);
	                }
	                
	            }
	            
	            
	            PostingListReader.readPostingList(indexPath);

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
		}
		catch(Exception e) {
			e.printStackTrace();
		}
        

	}
}
