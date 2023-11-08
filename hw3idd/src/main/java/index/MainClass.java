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

import table.Cella;

public class MainClass {

	public static void main(String args[]) throws Exception {
		
		Path indexPath = Paths.get("/Users/elisacatena/Desktop/index");   //path per memorizzare l'indice
		String tablePath = System.getProperty("user.dir") + "/src/main/resources/tables/tables.json";
		Scanner scanner = new Scanner(System.in);
		
		try {
			
			InvertedIndexCreator indexCreator = new InvertedIndexCreator();
			
			indexCreator.createIndex(tablePath, indexPath);


			Map<Cella, Integer> set2count = new HashMap<>();
			PostingListReader postingListReader = new PostingListReader();

			System.out.print("Inserisci una query: ");
			String queryReader = scanner.nextLine();
			String[] input = queryReader.split(",");
			//elimina le query duplicate dall'input
			Set<String> setWithoutDuplicates = new HashSet<>(Arrays.asList(input));
			List<String> inputWithoutDuplicates = new ArrayList<>(setWithoutDuplicates);
			
			for(int i = 0; i < inputWithoutDuplicates.size(); i++) {
				postingListReader.readPostingList(indexPath, inputWithoutDuplicates.get(i), set2count);
			}

			scanner.close();
			Statistica stat = new Statistica(inputWithoutDuplicates);
			stat.createStats(tablePath, indexPath);
			System.out.println("\nNumero tabelle: " + stat.getNTables().intValue());
			System.out.println("\nNumero medio righe: " + stat.getNRows());
			System.out.println("\nNumero medio colonne: " + stat.getNColumns());
			System.out.println("\nNumero medio valori nulli per tabella: " + stat.getNumNullValues());
			System.out.println("\nDistribuzione numero di righe: ");
			for(Integer i : stat.getRow2count().keySet()) {
				System.out.println(stat.getRow2count().get(i) + " tabelle hanno " + i + " righe");
			}
			System.out.println("\nDistribuzione numero di colonne: ");
			for(Integer i : stat.getCol2count().keySet()) {
				System.out.println(stat.getCol2count().get(i) + " tabelle hanno " + i + " colonne");
			}
			System.out.println("\nDistribuzione valori distinti per colonna: ");
			for(Integer i : stat.getDistinct2col().keySet()) {
				System.out.println(stat.getDistinct2col().get(i) + " colonne hanno " + i + " valori distinti");
			}
			System.out.println("\nDistribuzione valori distinti per riga: ");
			for(Integer i : stat.getDistinct2row().keySet()) {
				System.out.println(stat.getDistinct2row().get(i) + " righe hanno " + i + " valori distinti");
			}
			System.out.println("\nPrecision totale:" + stat.getPrecision());
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}


}
