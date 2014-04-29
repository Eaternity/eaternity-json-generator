package ch.generator;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;


public class BatchGenerator {
	
	public static final int AMOUNT_RECIPES = 10;
	public static final int AMOUNT_INGREDIENTS = 10;
	public static final int AMOUNT_TRANSIENT = 25;
	
	// Here we can specify the probability of the different dimensional Ingredients
	private static final int PERCENTAGE_BASE_INGREDIENTS = 50;
	private static final int PERCENTAGE_TWO_DIM_INGREDIENTS = 30;
	private static final int PERCENTAGE_THREE_DIM_INGREDIENTS = 20;
	
	private static final int TWO_DIMENSIONAL_BASE_NUMBER = 10000;
	private static final int THREE_DIMENSIONAL_BASE_NUMBER = 20000;

	
	private  final int[] baseProductIds = getBaseProductIds(AMOUNT_RECIPES*AMOUNT_INGREDIENTS*PERCENTAGE_BASE_INGREDIENTS/100);
	private final int[] twoDimProductIds = getHigherDimProductIds(AMOUNT_RECIPES*AMOUNT_INGREDIENTS*PERCENTAGE_TWO_DIM_INGREDIENTS/100, TWO_DIMENSIONAL_BASE_NUMBER);
	private final int[] threeDimProductIds = getHigherDimProductIds(AMOUNT_RECIPES*AMOUNT_INGREDIENTS*PERCENTAGE_THREE_DIM_INGREDIENTS/100, THREE_DIMENSIONAL_BASE_NUMBER);
	
	private final ArrayList<Integer> productIds = getAllProductIds();
	
	
	private final String[] countries = getCountryNames();
	
	/**
	 * Generate a recipe batch json with AMOUNT_INGREDIENTS recipes and 10 ingredients each.
	 * @param args
	 */
	public void generateJSON() {
		int counter = AMOUNT_TRANSIENT;
		
		String batchRecipesJson = "[";
		
		for (int i = 0; i< AMOUNT_RECIPES; i++) {
			batchRecipesJson += "{	\"request-id\": " + i + ",";
			if (counter>0) {
				batchRecipesJson += "\"transient\": " + "true" + ",";
				counter--;
			}
			batchRecipesJson += generateRecipeJson(AMOUNT_INGREDIENTS) + "}";
			if (i < AMOUNT_RECIPES - 1)
				batchRecipesJson += ",\n"; 	
		}
		
		batchRecipesJson += "]";
		
		writeFile("batch_with_" + AMOUNT_RECIPES + "_recipes.json", batchRecipesJson);
	}

	private ArrayList<Integer> getAllProductIds() {
		ArrayList<Integer> returnList = new ArrayList<>();
		for (int i=0;i< baseProductIds.length;i++) {
			returnList.add(baseProductIds[i]);
		}
		for (int i=0;i< twoDimProductIds.length;i++) {
			returnList.add(twoDimProductIds[i]);
		}
		for (int i=0;i< threeDimProductIds.length;i++) {
			returnList.add(threeDimProductIds[i]);
		}
		return returnList;
	}

	private  String generateIngredientJSON() {
		String ingredientJSON = "{";
		ingredientJSON += "\"id\": \"" + productIds.get((int)(Math.random() * productIds.size())) + "\",";
		ingredientJSON += "\"origin\": \"" + countries[(int)(Math.random() * countries.length)] + "\",";
		ingredientJSON += getContentFromFile("ingredient.json");
		ingredientJSON += "}";
		
		return ingredientJSON;
	}
	
	private  String generateRecipeJson(Integer numberOfIngredients) {
		String recipeJSON = getContentFromFile("recipe.json");
		
		recipeJSON += "	\"ingredients\": [";
		for (int i = 0; i < numberOfIngredients; i++) {
			recipeJSON += generateIngredientJSON();
			if (i < numberOfIngredients - 1)
				recipeJSON += ",\n";
		}
		recipeJSON += "] }";
		return recipeJSON;
	}
	
	private void writeFile(String filename, String content) {
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
	
	private  String getContentFromFile(String filename){
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
				if (bufferedReader != null) bufferedReader.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		return content;
	}
	
	private  int[] getBaseProductIds() {
		int[] fPIds = new int[] { 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 121,
				122, 123, 124, 125, 126, 127, 128, 129, 130, 131, 132, 133, 134, 135, 136, 137, 138, 139, 140, 141, 142, 143, 144, 145, 146, 147, 148, 149, 150, 151, 152, 153,
				154, 155, 156, 157, 158, 159, 160, 161, 162, 163, 164, 165, 166, 167, 168, 169, 170, 171, 172, 173, 174, 175, 176, 177, 178, 179, 200, 201, 202, 203, 204, 205,
				206, 207, 208, 209, 210, 211, 212, 213, 214, 215, 216, 217, 218, 219, 220, 221, 222, 223, 224, 225, 226, 227, 228, 229, 230, 231, 232, 233, 234, 235, 236, 237,
				300, 301, 302, 303, 304, 305, 306, 307, 308, 309, 310, 311, 312, 313, 314, 315, 316, 317, 318, 319, 320, 321, 1330, 1331, 400, 401, 402, 403, 404, 405, 406, 409,
				410, 411, 412, 413, 414, 415, 416, 417, 418, 419, 420, 421, 422, 423, 424, 425, 426, 427, 428, 429, 430, 431, 432, 433, 434, 435, 436, 437, 500, 501, 502, 503,
				504, 505, 506, 507, 508, 509, 510, 511, 512, 513, 1332, 515, 516, 517, 518, 519, 600, 601, 602, 700, 701, 702, 703, 704, 705, 706, 707, 708, 709, 710, 711, 712,
				713, 714, 800, 801, 802, 803, 804, 805, 806, 807, 808, 809, 810, 811, 812, 813, 814, 815, 816, 818, 819, 820, 821, 822, 823, 824, 825, 826, 827, 828, 829, 830,
				831, 832, 833, 834, 835, 836, 837, 838, 839, 840, 841, 842, 843, 844, 845, 846, 847, 848, 850, 851, 852, 900, 901, 902, 1333, 904, 1334, 906, 907, 1335, 1336,
				1000, 1001, 1002, 1003, 1004, 1101, 1102, 1103, 1104, 1105, 1106, 1107, 1108, 1202, 1273, 1306, 1229, 1339, 1253, 1200, 1201, 1340, 1203, 1206, 1290, 1213, 1216,
				1271, 1274, 1219, 1221, 1228, 1282, 1234, 1289, 1236, 1239, 1240, 1241, 1244, 1310, 1272, 1275, 1248, 1251, 1283, 1311, 1260, 1263, 1307, 1321, 1300, 1309, 1246,
				1322, 1258, 1308, 1204, 1205, 1313, 1341, 1280, 1233, 1237, 1301, 1305, 1303, 1304, 1302, 1254, 1255, 1315, 1288, 1227, 1287, 1238, 1286, 1249, 1250, 1256, 1316,
				1284, 1294, 1267, 1235, 1242, 1298, 1252, 1299, 1262, 1208, 1209, 1210, 1211, 1212, 1207, 1214, 1297, 1215, 1342, 1285, 1295, 1218, 1292, 1270, 1276, 1277, 1220,
				1268, 1224, 1225, 1226, 1231, 1269, 1243, 1343, 1344, 1293, 1291, 1296, 1257, 1345, 1261, 1346, 1265, 1266, 1347, 1502, 1503, 1504, 1505, 1506, 1507, 1508, 1509,
				1510, 1511, 1512, 1513, 1514, 1515, 1516, 1517, 1518, 1519, 1520, 1521, 1522, 1523, 1524, 1525, 1526, 1528, 1529, 1530, 1531, 1532, 1533, 1535, 1536, 1537, 1538,
				1539, 1540, 1541, 1542, 1543, 1544, 1546, 1547, 1548, 1549, 1550, 1551, 1552, 1553, 1558, 1555, 1556, 1557, 1559, 1560, 1561, 1562, 1563, 1564, 1565, 1554, 1567,
				1568, 1569, 1570, 1571, 1572, 1574, 1576, 1578, 1580, 1581, 1582, 1583, 1584, 1585, 1586, 1590, 1591, 1592, 1593, 1597, 1598, 1599, 1600, 1601, 1602, 1604, 1606,
				1608, 1618, 1619 };
		return fPIds;
	}
	
	private int[] getBaseProductIds(int amountOfProducts) {
		int[] baseProducts = new int[amountOfProducts];
		
		for (int i=0;i<amountOfProducts;i++)
			baseProducts[i] = getBaseProductIds()[(int)(Math.random() * getBaseProductIds().length)]; 
		return baseProducts;
	}
	
	
	private int[] getHigherDimProductIds(int numberOfHigherDimMatchingItems, int idBaseNumber) {
		int[] returnList = new int[numberOfHigherDimMatchingItems];
		String higherDimStringIds = "";
		for (int i = 0;i < numberOfHigherDimMatchingItems-1; i++) {
			higherDimStringIds += Integer.toString(idBaseNumber) + Integer.toString(i) + ", ";
		}
		higherDimStringIds += Integer.toString(idBaseNumber) + Integer.toString(numberOfHigherDimMatchingItems);
		
		String namesSplitted[] = higherDimStringIds.split(",");
		int j = 0;
		for (String name : namesSplitted) {
			returnList[j] = Integer.parseInt(name.trim());
			j++;
		}
		return returnList;
	}
	
	private  String[] getCountryNames() {
		//String names = "United States of America, Afghanistan, Albania, Algeria, Andorra, Angola, Antigua & Deps, Argentina, Armenia, Australia, Austria, Azerbaijan, Bahamas, Bahrain, Bangladesh, Barbados, Belarus, Belgium, Belize, Benin, Bhutan, Bolivia, Bosnia Herzegovina, Botswana, Brazil, Brunei, Bulgaria, Burkina, Burma, Burundi, Cambodia, Cameroon, Canada, Cape Verde, Central African Rep, Chad, Chile, People's Republic of China, Republic of China, Colombia, Comoros, Democratic Republic of the Congo, Republic of the Congo, Costa Rica,, Croatia, Cuba, Cyprus, Czech Republic, Danzig, Denmark, Djibouti, Dominica, Dominican Republic, East Timor, Ecuador, Egypt, El Salvador, Equatorial Guinea, Eritrea, Estonia, Ethiopia, Fiji, Finland, France, Gabon, Gaza Strip, The Gambia, Georgia, Germany, Ghana, Greece, Grenada, Guatemala, Guinea, Guinea-Bissau, Guyana, Haiti, Holy Roman Empire, Honduras, Hungary, Iceland, India, Indonesia, Iran, Iraq, Republic of Ireland, Israel, Italy, Ivory Coast, Jamaica, Japan, Jonathanland, Jordan, Kazakhstan, Kenya, Kiribati, North Korea, South Korea, Kosovo, Kuwait, Kyrgyzstan, Laos, Latvia, Lebanon, Lesotho, Liberia, Libya, Liechtenstein, Lithuania, Luxembourg, Macedonia, Madagascar, Malawi, Malaysia, Maldives, Mali, Malta, Marshall Islands, Mauritania, Mauritius, Mexico, Micronesia, Moldova, Monaco, Mongolia, Montenegro, Morocco, Mount Athos, Mozambique, Namibia, Nauru, Nepal, Newfoundland, Netherlands, New Zealand, Nicaragua, Niger, Nigeria, Norway, Oman, Ottoman Empire, Pakistan, Palau, Panama, Papua New Guinea, Paraguay, Peru, Philippines, Poland, Portugal, Prussia, Qatar, Romania, Rome, Russian Federation, Rwanda, St Kitts & Nevis, St Lucia, Saint Vincent & the, Grenadines, Samoa, San Marino, Sao Tome & Principe, Saudi Arabia, Senegal, Serbia, Seychelles, Sierra Leone, Singapore, Slovakia, Slovenia, Solomon Islands, Somalia, South Africa, Spain, Sri Lanka, Sudan, Suriname, Swaziland, Sweden, Switzerland, Syria, Tajikistan, Tanzania, Thailand, Togo, Tonga, Trinidad & Tobago, Tunisia, Turkey, Turkmenistan, Tuvalu, Uganda, Ukraine, United Arab Emirates, United Kingdom, Uruguay, Uzbekistan, Vanuatu, Vatican City, Venezuela, Vietnam, Yemen, Zambia, Zimbabwe";
		String names = getContentFromFile("country_names.txt");
		String namesSplitted[] = names.split(",");
		for (String name : namesSplitted) {
			name.trim();
		}
		return namesSplitted;
	}

	public void generateMatchingItemIds(int numberOf2DimMatchingItems,int numberOf3DimMatchingItems) {
		
		String twoDimStringIds = "";
		for (int i = 0;i < numberOf2DimMatchingItems; i++) {
			twoDimStringIds += Integer.toString(TWO_DIMENSIONAL_BASE_NUMBER) + Integer.toString(i) + ", ";
		}
		writeFile(numberOf2DimMatchingItems + "_2dim_matching_item_ids.txt",twoDimStringIds);
		
		String threeDimStringIds = "";
		for (int j = 0;j < numberOf3DimMatchingItems; j++) {
			threeDimStringIds += Integer.toString(THREE_DIMENSIONAL_BASE_NUMBER) + Integer.toString(j) + ", ";
		}
		writeFile(numberOf3DimMatchingItems + "_3dim_matching_item_ids.txt",threeDimStringIds);
		
	}

				
}
