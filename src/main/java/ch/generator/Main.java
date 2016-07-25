package ch.generator;

import java.io.IOException;

public class Main {

	public static void main(String[] args) throws IOException {
//		BatchGenerator batchGenerator = new BatchGenerator();
//		batchGenerator.generateRecipeJSON();
		
//		BatchGenerator batchGenerator2 = new BatchGenerator();
//		batchGenerator2.generateSupplyJSON();
		//batchGenerator.generateMatchingItemIds(1000,1000);

		new JacksonCSVParser().parse();
	}
}
