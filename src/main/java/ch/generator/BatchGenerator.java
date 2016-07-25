package ch.generator;

import com.google.common.collect.Iterables;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

/**
 * Batch generates json files with the settings in this class.
 */
public class BatchGenerator {

	//------------------------------------- PROPERTIES - CHANGE THAT -------------------------------------

	// ATTENTION These whole setting work just until 500 baseproducts!
	// Max AMOUNT_INGREDIENTS_PER_RECIPE = PERCENTAGE_BASE_INGREDIENTS * 500 / 100

	public static final int YEAR = 2014;
	public static final int MONTH = 6;

	public static final int AMOUNT_SUPPLIES = 6;
	public static final int AMOUNT_RECIPES = 125;
	public static final int AMOUNT_TRANSIENT_RECIPES = 0;

	// if you change this, also change INGREDIENT_WEIGHT_RANGE_RECIPES to get a couple of climate friendly recipe
	public static final int AMOUNT_INGREDIENTS_PER_RECIPE = 10;
	public static final int AMOUNT_INGREDIENTS_PER_SUPPLY = 35;
	public static final int AMOUNT_INGREDIENTS_PER_TRANSIENT_RECIPES = 0;
	public static final int PERCENTAGE_DIFFERENT_ORIGINS = 100;
	public static final int PERCENTAGE_DIFFERENT_MITEMS = 100; // that means the percentage value are all different mItems, then it repeats

	private static final int INGREDIENT_WEIGHT_RANGE_RECIPES = 80;
	private static final int INGREDIENT_WEIGHT_RANGE_SUPPLIES = 6000;

	private static final boolean REAL_MATCHING_ITEMS = false;

	//------------------------------------- CONSTANTS - DONT CHANGE -------------------------------------

	private static final int TOTAL_AMOUNT_INGREDIENTS_RECIPES =
			AMOUNT_TRANSIENT_RECIPES * AMOUNT_INGREDIENTS_PER_TRANSIENT_RECIPES
					+ (AMOUNT_RECIPES - AMOUNT_TRANSIENT_RECIPES) * AMOUNT_INGREDIENTS_PER_RECIPE;
	private static final int TOTAL_AMOUNT_INGREDIENTS_SUPPLIES = AMOUNT_SUPPLIES * AMOUNT_INGREDIENTS_PER_SUPPLY;

	private final List<Integer> baseProductIds = getBaseProductIds();

	private List<Integer> productIds = new ArrayList<>();
	private List<String> countries = new ArrayList<>();
	private List<Map<Locale, String>> menuNames = getMenuNames();
	private List<Map<Locale, String>> ingredientNames = getIngredientNames();

	private Iterator<Map<Locale, String>> menuNamesIterator = Iterables.cycle(menuNames).iterator();
	private Iterator<Map<Locale, String>> ingredientNamesIterator = Iterables.cycle(ingredientNames).iterator();

	private static final String[] TRANSPORATION_MODES = new String[] {"air", "ground", "sea", "train"};
	private static final String[] PRODUCTION_MODES = new String[] {"standard", " organic", "fair-trade", "greenhouse", " farm", "wild-caught"};
	private static final String[] PROCESSING_MODES = new String[] {"raw", "unboned", "boned", "skinned", "beheaded", "fillet", "cut", "boiled", "peeled"};
	private static final String[] CONSERVATION_MODES = new String[] {"fresh", "frozen", "dried", "conserved", "canned", "boiled-down"};
	private static final String[] PACKAGING_MODES = new String[] {"plastic", "paper", "pet", "tin", "alu", "glas", "cardboard", "tetra"};

	private Random rand = new Random();
	private Map<Integer, String> matchingItemIdsAndNames;


	public BatchGenerator() throws IOException {
		if (AMOUNT_TRANSIENT_RECIPES > AMOUNT_RECIPES) {
			throw new IllegalArgumentException("Amount transient recipes can not be bigger than total amount recipes");
		}
		ArrayList<Integer> allProductIds = getAllProductIds();

		// add 2 because 1 for rounding and 1 for exclusion of end index.
		countries = getCountryNames().subList(0, Math.min((PERCENTAGE_DIFFERENT_ORIGINS * TOTAL_AMOUNT_INGREDIENTS_RECIPES / 100 + 2), getCountryNames().size()));
		productIds = new ArrayList<>(allProductIds.subList(0, Math.min(PERCENTAGE_DIFFERENT_MITEMS * TOTAL_AMOUNT_INGREDIENTS_RECIPES / 100 + 2, allProductIds.size())));

		// repeat the products in the list so that PERCENTAGE_DIFFERENT_MITEMS
		// is correct
		while (productIds.size() < TOTAL_AMOUNT_INGREDIENTS_RECIPES) {
			productIds.addAll(productIds);
		}

		productIds = productIds.subList(0, TOTAL_AMOUNT_INGREDIENTS_RECIPES + 2);
	}

	/**
	 * Generate a recipe batch json with AMOUNT_INGREDIENTS_PER_RECIPE recipes and
	 * AMOUNT_INGREDIENTS_PER_RECIPE or AMOUNT_INGREDIENTS_PER_TRANSIENT_RECIPES ingredients each.
	 */
	public void generateRecipeJSON() {
		System.out.println("Amount of recipes: " + AMOUNT_RECIPES);
		System.out.println("Amount of ingredients: " + TOTAL_AMOUNT_INGREDIENTS_RECIPES);

		int counter = AMOUNT_TRANSIENT_RECIPES;

		String batchRecipesJson = "[";

		for (int i = 0; i < AMOUNT_RECIPES; i++) {
			batchRecipesJson += "{	\"request-id\": " + i + ",";
			if (counter > 0) {
				batchRecipesJson += "\"transient\": " + "true" + ",";
				batchRecipesJson += generateCompositeRootJson(AMOUNT_INGREDIENTS_PER_TRANSIENT_RECIPES, "recipe") + "}";
				counter--;
			} else {
				batchRecipesJson += generateCompositeRootJson(AMOUNT_INGREDIENTS_PER_RECIPE, "recipe") + "}";
			}

			if (i < AMOUNT_RECIPES - 1) {
				batchRecipesJson += ",\n";
			}
		}

		batchRecipesJson += "]";

		writeFile("batch_" + AMOUNT_RECIPES + "_recipes_" + TOTAL_AMOUNT_INGREDIENTS_RECIPES + "_ingredient.json", batchRecipesJson);
	}

	public void generateSupplyJSON() {
		System.out.println("Amount of supplies: " + AMOUNT_SUPPLIES);
		System.out.println("Amount of ingredients: " + TOTAL_AMOUNT_INGREDIENTS_SUPPLIES);

		String batchRecipesJson = "[";

		for (int i = 0; i < AMOUNT_SUPPLIES; i++) {
			batchRecipesJson += "{	\"request-id\": " + i + ",";
			batchRecipesJson += generateCompositeRootJson(AMOUNT_INGREDIENTS_PER_SUPPLY, "supply") + "}";

			if (i < AMOUNT_SUPPLIES - 1) {
				batchRecipesJson += ",\n";
			}
		}

		batchRecipesJson += "]";

		writeFile("batch_" + AMOUNT_SUPPLIES + "_supplies_" + TOTAL_AMOUNT_INGREDIENTS_SUPPLIES + "_ingredient.json", batchRecipesJson);
	}

	// **************************************************

	private String generateCompositeRootJson(final Integer numberOfIngredients, final String kindOfcompositeRoot) {
		List<Integer> productIdsCopy = new ArrayList<>(productIds);

		String compositeRootJSON = getContentFromFile(kindOfcompositeRoot + ".json");
		if (kindOfcompositeRoot.equals("supply")) {
			compositeRootJSON += "\"supply-date\": ";
		} else {
			compositeRootJSON += "\"date\": ";
		}

		compositeRootJSON += "\"" + YEAR + "-" + MONTH + "-" + generateRandomDay() + "\",";

		if (kindOfcompositeRoot.equals("recipe")) {
			compositeRootJSON += generateLocalizedJSONField("titles", menuNamesIterator.next()) + ",";
		}

		compositeRootJSON += "	\"ingredients\": [\n";
		for (int i = 0; i < numberOfIngredients; i++) {
			compositeRootJSON += generateIngredientJSON(kindOfcompositeRoot, productIdsCopy);
			if (i < numberOfIngredients - 1) {
				compositeRootJSON += ",\n";
			}
		}
		compositeRootJSON += "] }";
		return compositeRootJSON;
	}

	private String generateLocalizedJSONField(final String fieldName, final Map<Locale, String> localizedValues) {
		String json = "\"" + fieldName + "\": [";
		int i = 0;
		for (Locale locale : localizedValues.keySet()) {
			json += "{ \"language\": \"" + locale.getLanguage() + "\", \"value\": \"" + localizedValues.get(locale) + "\" }";
			if (i < localizedValues.size() - 1) {
				json += ",\n";
			}
			i++;
		}
		json += "]";
		return json;
	}

	private String generateIngredientJSON(final String kindOfcompositeRoot, final List<Integer> productIdsCopy) {
		int weightRange = INGREDIENT_WEIGHT_RANGE_RECIPES;
		if (kindOfcompositeRoot.equals("supply")) {
			weightRange = INGREDIENT_WEIGHT_RANGE_SUPPLIES;
		}

		String ingredientJSON = "{";
		int index = rand.nextInt(productIdsCopy.size());
		ingredientJSON += "\"id\": \"" + productIdsCopy.get(index) + "\",";
		Map<Locale, String> localizedIngredientNames;
		if (REAL_MATCHING_ITEMS) {
			localizedIngredientNames = new HashMap<>();
			localizedIngredientNames.put(Locale.GERMAN, matchingItemIdsAndNames.get(productIdsCopy.get(index)));
			//TODO put here the french name localizedIngredientNames.put(Locale.FRENCH, ...)
		} else {
			localizedIngredientNames = ingredientNamesIterator.next();
		}
		ingredientJSON += generateLocalizedJSONField("names", localizedIngredientNames) + ",";
		ingredientJSON += "\"origin\": \"" + countries.get(rand.nextInt(countries.size())) + "\",";
		ingredientJSON += "\"amount\": " + rand.nextInt(weightRange) + ",";
		ingredientJSON += "\"transport\": \"" + TRANSPORATION_MODES[rand.nextInt(TRANSPORATION_MODES.length)] + "\",";
		ingredientJSON += "\"production\": \"" + PRODUCTION_MODES[rand.nextInt(PRODUCTION_MODES.length)] + "\",";
		ingredientJSON += "\"processing\": \"" + PROCESSING_MODES[rand.nextInt(PROCESSING_MODES.length)] + "\",";
		ingredientJSON += "\"conservation\": \"" + CONSERVATION_MODES[rand.nextInt(CONSERVATION_MODES.length)] + "\",";
		ingredientJSON += "\"packaging\": \"" + PACKAGING_MODES[rand.nextInt(PACKAGING_MODES.length)] + "\",";
		ingredientJSON += getContentFromFile("ingredient.json");
		ingredientJSON += "}";

		// remove the product from the list so that its not used twice...
		productIdsCopy.remove(index);

		return ingredientJSON;
	}

	private void writeFile(final String filename, final String content) {
		try {
			File file = new File(filename);

			// if file doesnt exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			}

			OutputStreamWriter fw = new OutputStreamWriter(new FileOutputStream(file.getAbsoluteFile()), "UTF-8");
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(content);
			bw.close();

			System.out.println("File successfull Written: " + filename);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private String getContentFromFile(final String filename) {
		String content = "";
		BufferedReader bufferedReader = null;
		try {
			String sCurrentLine;
			bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(filename), "UTF-8"));

			while ((sCurrentLine = bufferedReader.readLine()) != null) {
				content = content + sCurrentLine;
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (bufferedReader != null) {
					bufferedReader.close();
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		return content;
	}

	private ArrayList<Integer> getAllProductIds() throws IOException {
		ArrayList<Integer> localProductIds = new ArrayList<>();
		if (REAL_MATCHING_ITEMS) {
			localProductIds.addAll(new ArrayList<>(getRealMatchingItemIdsAndNames().keySet()));
		} else {
			localProductIds.addAll(baseProductIds);
		}

		// shuffle the ids
		ArrayList<Integer> randomProductIds = new ArrayList<>();
		int index = 0;
		for (int i = 0; i < localProductIds.size(); i++) {
			index = rand.nextInt(localProductIds.size());
			randomProductIds.add(localProductIds.get(index));
			localProductIds.remove(index);
		}

		return randomProductIds;
	}

	private List<String> getCountryNames() {
		String names = getContentFromFile("country_names.txt");
		String[] namesSplitted = names.split(",");
		List<String> countryNames = new ArrayList<>();
		for (String name : namesSplitted) {
			countryNames.add(name.trim());
		}
		return countryNames;
	}

	private List<Map<Locale, String>> getMenuNames() throws IOException {
		CSVParser csvParser = new CSVParser();
		StringBuilder errorMessage = new StringBuilder();
		return csvParser.parseLocaleNames("menu-names-DE-FR.csv", errorMessage);
	}

	private List<Map<Locale, String>> getIngredientNames() throws IOException {
		CSVParser csvParser = new CSVParser();
		StringBuilder errorMessage = new StringBuilder();
		return csvParser.parseLocaleNames("ingredient-names-DE-FR.csv", errorMessage);
	}

	private Map<Integer, String> getRealMatchingItemIdsAndNames() throws IOException {
		CSVParser csvParser = new CSVParser();
		StringBuilder errorMessage = new StringBuilder();
		return csvParser.parseMatchingItems("2015-01-07 Matching Items.txt", errorMessage);
	}

	private String generateRandomDay() {
		int randomInt = rand.nextInt(30);
		randomInt++;
		String returnString = "";
		if (randomInt < 10) {
			returnString += 0;
		}
		returnString += randomInt;
		return returnString;
	}

	/**
	 * Take care to import here only the NON-deleted baseproducts (in the products json: deleted=false).
	 */
	private List<Integer> getBaseProductIds() {
		Integer[] fPIds = new Integer[] {2,3,4,5,7,8,9,10,11,12,100,101,102,103,104,105,106,107,108,109,110,111,112,113,114,115,116,117,118,119,120,121,122,123,124,125,126,127,128,129,130,131,132,133,134,135,136,137,138,139,140,141,142,143,144,145,146,147,148,149,150,151,153,154,155,156,157,158,159,160,161,162,164,165,166,167,168,169,170,171,172,173,174,175,176,177,178,179,200,201,202,203,204,205,206,207,208,209,210,211,212,213,214,216,217,218,219,220,221,222,223,224,225,226,227,228,229,230,231,232,233,234,235,236,237,300,303,304,305,306,307,308,309,310,311,312,313,315,316,317,318,319,320,321,401,402,403,404,406,409,410,411,414,415,416,418,419,420,421,422,423,424,425,426,427,428,429,430,431,432,434,435,437,500,501,502,503,505,512,515,516,517,518,519,600,601,602,700,701,703,704,705,706,707,708,709,710,711,712,713,714,800,801,802,803,804,805,806,807,808,809,810,811,812,813,814,815,816,818,820,821,822,825,826,827,828,829,830,831,832,833,834,835,836,837,839,840,841,842,843,844,845,846,847,848,851,852,900,901,902,906,1000,1002,1003,1004,1101,1102,1103,1104,1105,1106,1107,1108,1200,1201,1202,1203,1204,1205,1206,1207,1208,1209,1210,1213,1214,1215,1216,1218,1219,1220,1221,1224,1225,1226,1228,1229,1231,1233,1234,1236,1238,1239,1240,1241,1242,1243,1244,1246,1249,1250,1251,1252,1253,1255,1256,1257,1258,1260,1261,1262,1263,1265,1266,1267,1268,1269,1270,1271,1272,1273,1274,1275,1276,1277,1283,1285,1286,1287,1288,1289,1290,1292,1293,1294,1295,1296,1297,1298,1299,1301,1306,1307,1308,1309,1310,1311,1315,1316,1321,1322,1332,1334,1335,1339,1343,1502,1503,1504,1505,1506,1521,1522,1532,1541,1546,1547,1554,1555,1557,1558,1559,1560,1561,1563,1564,1565,1567,1568,1569,1570,1571,1572,1580,1581,1582,1583,1591,1593,1597,1598,1599,1601,1602,1608,1618,1619,1623,1624,1627,1628,1629,1630,1631,1632,1633,1634,1635,1636,1637,1638,1639,1640,1641,1642,1643,1644,1645,1646,1647,1648,1649,1650,1651,1652,1653,1654,1655,1656,1657,1658,1659,1660,1661,1662,1663,1664,1665,1666,1667,1668,1669,1670,1671,1672,1673,1674,1675,1676,1677,1678,1678,1679,1680,1681,1682,1685,1686,1687,1688,1689,1691,1692,1693,1694,1695,1697,1698,1699,1700,1701,1702,1703,1704,1705,1706,1707};

		return new ArrayList<>(Arrays.asList(fPIds));
	}
}
