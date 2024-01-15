package index;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import table.Colonna;

public class MainClass {
	
	private final static Path indexPath = Paths.get("/Users/elisacatena/Desktop/index"); 
	//private final static String tablePath = System.getProperty("user.dir") + "/src/main/resources/tables/myTables.json";
	private final static String tablePath = "/Users/elisacatena/Desktop/tables.json";


	public static void main(String args[]) throws Exception {
		
		Scanner scanner = new Scanner(System.in);
		
		try {
			
//			InvertedIndexCreator indexCreator = new InvertedIndexCreator();
//			
//			indexCreator.createIndex(tablePath, indexPath);
			
			Directory directory = FSDirectory.open(indexPath);
			IndexReader indexReader = DirectoryReader.open(directory);
			IndexSearcher searcher = new IndexSearcher(indexReader);
			
			Map<Colonna, Integer> set2count = new HashMap<>();
			PostingListReader postingListReader = new PostingListReader();

			System.out.print("Inserisci una query: ");
			String queryReader = scanner.nextLine();
			String[] input = queryReader.split(",");
			//elimina le query duplicate dall'input
			Set<String> setWithoutDuplicates = new HashSet<>(Arrays.asList(input));
			List<String> inputWithoutDuplicates = new ArrayList<>(setWithoutDuplicates);
			int dimInput = inputWithoutDuplicates.size();
			for(int i = 0; i < inputWithoutDuplicates.size(); i++) {
				System.out.println("input["+i+"]: "+inputWithoutDuplicates.get(i));
				postingListReader.readPostingList(indexReader, searcher, inputWithoutDuplicates.get(i).toLowerCase(), set2count);
			}

			scanner.close();
			indexReader.close();
			Statistica stat = new Statistica(inputWithoutDuplicates);
			//stat.createStats(tablePath, indexPath);
//			System.out.println("size di set2count: " + set2count.size());
//			for(Colonna c : set2count.keySet()) {
//				System.out.println(c.getColonna() + ", " + c.getTableId() + "->" + set2count.get(c) + " [" + c.getContenuto() + "]");
//			}
			
			stat.findTopK(set2count);
			Double occ = 0.0;
	        Double occTot = 0.0;
	        for(Colonna c: set2count.keySet()) {
	        	occTot += set2count.get(c);
	        	if(set2count.get(c) == dimInput) occ++;
	        }
	        System.out.println("occTot: " + occTot);
	        System.out.println("occ: " + occ);
			System.out.println("M1: " + postingListReader.getM1()/inputWithoutDuplicates.size());
			
			System.out.println("M2: " + occ/occTot);
			System.out.println("FINE RICERCA");
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}


}
