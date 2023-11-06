package index;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.miscellaneous.PerFieldAnalyzerWrapper;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.codecs.Codec;
import org.apache.lucene.codecs.simpletext.SimpleTextCodec;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;


public class InvertedIndexCreator {

	public void updateCells2column(JsonNode cells, Map<Integer, List<String>> cells2column) {
		int column;
		String cellContent = null;

		for(int i = 0; i < cells.size(); i++) {
			column = cells.get(i).get("Coordinates").get("column").asInt();
			if(cells.get(i).get("cleanedText").isNull()) {
				continue;
			}
			
			cellContent = cells.get(i).get("cleanedText").asText();
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

	public void createIndex(String tablePath, Path indexPath, Statistica stat) throws Exception {

		Directory directory = null;

		Codec codec = new SimpleTextCodec();
		Analyzer defaultAnalyzer = new StandardAnalyzer();
		Map<String, Analyzer> perFieldAnalyzers = new HashMap<>();

		perFieldAnalyzers.put("id", new StandardAnalyzer());
		perFieldAnalyzers.put("cells", new StandardAnalyzer());
		perFieldAnalyzers.put("column", new StandardAnalyzer());

		Analyzer analyzer = new PerFieldAnalyzerWrapper(defaultAnalyzer, perFieldAnalyzers);
		IndexWriterConfig config = new IndexWriterConfig(analyzer);
		if (codec != null) {
			config.setCodec(codec);
		}

		try {
			directory = FSDirectory.open(indexPath);
			IndexWriter writer = new IndexWriter(directory, config);
			writer.deleteAll(); 

			this.readTables(tablePath, indexPath, writer, stat);
			writer.commit();
			writer.close();
		}
		catch(Exception e) {
			e.printStackTrace();
		}

	}

	public void readTables(String tablePath, Path indexPath, IndexWriter writer, Statistica stat) throws Exception {
		
		Map<Integer, List<String>> cells2column = new HashMap<>();
		BufferedReader br = null;
		
		try {
			String currentLine;

			br = new BufferedReader(new FileReader(tablePath));

			while ((currentLine = br.readLine()) != null) {
				System.out.println("Record:\t" + currentLine);

				ObjectMapper objectMapper = new ObjectMapper();

				try {
					JsonNode rootNode = objectMapper.readTree(currentLine);
					
					
					// Estrai l'oggetto "cells"
					JsonNode cellsNode = rootNode.get("cells");
					String tableId = rootNode.get("_id").asText();
					//dobbiamo creare una mappa con id della tabella, contenuto della cella e coordinate della cella
					this.updateCells2column(cellsNode, cells2column);
					this.addDocumentToIndex(cells2column, tableId, writer);
					
					cells2column.clear();

				} catch (Exception e) {
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
	}

	public void addDocumentToIndex(Map<Integer, List<String>> cells2column, String tableId, IndexWriter writer) throws IOException {
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

}

