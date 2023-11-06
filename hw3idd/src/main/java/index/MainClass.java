package index;

import java.nio.file.Path;
import java.nio.file.Paths;
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
		Statistica stat = new Statistica();
		
		try {
			
			InvertedIndexCreator indexCreator = new InvertedIndexCreator();
			
			indexCreator.createIndex(tablePath, indexPath, stat);


			Map<Cella, Integer> set2count = new HashMap<>();
			PostingListReader postingListReader = new PostingListReader();

			System.out.print("Inserisci una query: ");
			String queryReader = scanner.nextLine();
			String[] input = queryReader.split(",");
			//elimina le query duplicate dall'input
			List<String> listWithDuplicates = Arrays.asList(input);
			Set<String> setWithoutDuplicates = new HashSet<>(listWithDuplicates);
			String[] newInput = setWithoutDuplicates.toArray(new String[0]);
			for(int i = 0; i < newInput.length; i++) {
				postingListReader.readPostingList(indexPath, newInput[i], set2count);
			}

			scanner.close();
			stat.createStats(tablePath);
			System.out.println("\nNumero tabelle: " + stat.getNTables().intValue());
			System.out.println("\nNumero medio righe: " + stat.getNRows());
			System.out.println("\nNumero medio colonne: " + stat.getNColumns());
			System.out.println("\nNumero medio valori nulli per tabella: " + stat.getNumNullValues());
			System.out.println("\nDistribuzione numero di righe : ");
			for(Integer i : stat.getRow2count().keySet()) {
				System.out.println(i + "->" + stat.getRow2count().get(i));
			}
			System.out.println("\nDistribuzione numero di colonne : ");
			for(Integer i : stat.getCol2count().keySet()) {
				System.out.println(i + "->" + stat.getCol2count().get(i));
			}
			System.out.println("\nDistribuzione valori distinti : ");
			for(Integer i : stat.getDistinct2col().keySet()) {
				System.out.println(i + "->" + stat.getDistinct2col().get(i));
			}
			
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}


}
