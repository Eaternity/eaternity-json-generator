package ch.generator;

public class Main {

	public static void main(String[] args) {
		BatchGenerator batchGenerator = new BatchGenerator();
		batchGenerator.generateJSON();
		batchGenerator.generateMatchingItemIds(100,100);
	}
}
