package ch.generator;

public class Main {

	public static void main(String[] args) {
		BatchGenerator batchGenerator = new BatchGenerator();
		batchGenerator.generateRecipeJSON();
		
		BatchGenerator batchGenerator2 = new BatchGenerator();
		batchGenerator2.generateSupplyJSON();
		//batchGenerator.generateMatchingItemIds(1000,1000);
	}
}
