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
		
		try {
			
			InvertedIndexCreator indexCreator = new InvertedIndexCreator();
			
			indexCreator.createIndex(tablePath, indexPath);


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

			Statistica stat = new Statistica();
			stat.findTopK(set2count);

		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}


}
