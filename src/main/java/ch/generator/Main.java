package ch.generator;

import java.io.IOException;

/**
 * Comment or uncomment below what you want this Module to do.
 * Best is to run this Main class from you IDE.
 */
public final class Main {

	private Main() { }

	public static void main(final String[] args) throws IOException {
//		BatchGenerator batchGenerator = new BatchGenerator();
//		batchGenerator.generateRecipeJSON();

//		BatchGenerator batchGenerator2 = new BatchGenerator();
//		batchGenerator2.generateSupplyJSON();
		//batchGenerator.generateMatchingItemIds(1000,1000);

		new JacksonCSVParser().parse();
	}
}
