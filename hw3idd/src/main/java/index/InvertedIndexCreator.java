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
			if(cells.get(i).get("cleanedText").isNull() || cells.get(i).get("isHeader").booleanValue()) {
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

	public void createIndex(String tablePath, Path indexPath) throws Exception {

		Directory directory = null;

		Codec codec = new SimpleTextCodec();
		Analyzer defaultAnalyzer = new StandardAnalyzer();
		Map<String, Analyzer> perFieldAnalyzers = new HashMap<>();

		perFieldAnalyzers.put("id", new StandardAnalyzer());
		perFieldAnalyzers.put("column", new StandardAnalyzer());
		perFieldAnalyzers.put("cells", new StandardAnalyzer());

		Analyzer analyzer = new PerFieldAnalyzerWrapper(defaultAnalyzer, perFieldAnalyzers);
		IndexWriterConfig config = new IndexWriterConfig(analyzer);
		if (codec != null) {
			config.setCodec(codec);
		}

		 // Registra il momento iniziale
        long startTime = System.currentTimeMillis();
        
		try {
			directory = FSDirectory.open(indexPath);
			IndexWriter writer = new IndexWriter(directory, config);
			writer.deleteAll(); 

			this.readTables(tablePath, indexPath, writer);
			writer.commit();
			writer.close();
			
			// Registra il momento finale
            long endTime = System.currentTimeMillis();

            // Calcola il tempo trascorso
            long indexingTime = endTime - startTime;
            System.out.println("Tempo di indicizzazione: " + indexingTime + " ms\n");
		}
		catch(Exception e) {
			e.printStackTrace();
		}

	}

	public void readTables(String tablePath, Path indexPath, IndexWriter writer) throws Exception {

		Map<Integer, List<String>> cells2column = new HashMap<>();
		BufferedReader br = null;
		Integer tableId = 0;

		try {
			String currentLine;

			br = new BufferedReader(new FileReader(tablePath));

			while ((currentLine = br.readLine()) != null) {
				//System.out.println("Record:\t" + currentLine);

				ObjectMapper objectMapper = new ObjectMapper();

				try {
					JsonNode rootNode = objectMapper.readTree(currentLine);

					// Estrai l'oggetto "cells"
					JsonNode cellsNode = rootNode.get("cells");
					tableId++;
					//dobbiamo creare una mappa con id della tabella, contenuto della cella e coordinate della cella
					this.updateCells2column(cellsNode, cells2column);
					this.addDocumentToIndex(cells2column, tableId.toString(), writer);

					cells2column.clear();

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			System.out.println("\nFINE LETTURA FILE\n");

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
			String col = cells2column.get(column).toString();
			Document document = new Document();
			document.add(new TextField("id", tableId, Field.Store.YES));
			document.add(new TextField("cells", col.substring(1, col.length()-1), Field.Store.YES));
			document.add(new TextField("column", column.toString(), Field.Store.YES));
			writer.addDocument(document);
		}
	}

}

