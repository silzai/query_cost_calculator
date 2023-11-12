package queryUtilPackage;

import java.sql.SQLException;

import dataBuffer.*;

public class QueryUtility {
	
	private static DataBuffer dataBuffer = new DataBuffer();
	
	//hardcoded with relevant input only
	public static void parseQuery(String inputQuery) throws SQLException {
		
		String[] tokenizedInput = inputQuery.split(" ");
		//doing this because the selection attribute is usually the 3rd token from the end of a query
		//ASSUMING ALL TOKENS ARE SPACE SEPARATED and LOWER CASE
		String selectionAttribute = tokenizedInput[tokenizedInput.length-3];
		//checking if there are two tables present, so then will do JOIN
		String selectionTableCheck = tokenizedInput[3];
		String selectionTable;
		String selectionTable2 = "";
		if (selectionTableCheck.contains(",")) {
			selectionTable = selectionTableCheck.split(",")[0];
			selectionTable2 = selectionTableCheck.split(",")[1];
			//getting the proper selection Attribute in case of JOIN, as the
			//selection attribute will be: tableName.selectionAttibute
			selectionAttribute = selectionAttribute.split("\\.")[1];
		}
		//else get the only selection table there is
		else {selectionTable = selectionTableCheck;}
		
		inputQuery = inputQuery.toLowerCase();
		
		chooseCalculationMethod(inputQuery, selectionAttribute, selectionTable, selectionTable2);
	}
	
	private static void chooseCalculationMethod(String inputQuery, String selectionAttribute, String selectionTable, String selectionTable2) throws SQLException {
		
		//EQUIJOIN
		if (inputQuery.contains("employee") && inputQuery.contains("engineer")) {
			calculateCost_equiJoin_nestedLoop(selectionTable, selectionTable2);
			calculateCost_equiJoin_mergeJoin(selectionTable, selectionTable2);
			calculateCost_equiJoin_nestedLoopIndex(selectionTable, selectionTable2, selectionAttribute);
		}
		//if SELECT and equality operator
		else if (inputQuery.contains("=")) {
			
			if (dataBuffer.getStatsOfAttributes().get(selectionAttribute).indexType == null) {
				calculateCost_nonPrimaryKey_equalityOperator_UsingFileScan(selectionTable);
			}
			//is it selection on primary key?
			else if (dataBuffer.getStatsOfAttributes().get(selectionAttribute).indexType.equalsIgnoreCase("primary")) {
				calculateCost_PrimaryKey_EqualityOperator_UsingFileScan(selectionTable);
				calculateCost_PrimaryKey_EqualityOperator_UsingIndex(selectionAttribute); 
				calculateCost_equalityHashing();
			}
			else if (dataBuffer.getStatsOfAttributes().get(selectionAttribute).indexType.equals("secondary")){
				calculateCost_nonPrimaryKey_equalityOperator_UsingFileScan(selectionTable);
				calculateCost_nonPrimaryKey_EqualityOperator_UsingIndex(selectionAttribute);
			}
				
		}
		//if SELECT and range operator
		else if (inputQuery.contains(">") || inputQuery.contains("<")) {
			//if no index
			if (dataBuffer.getStatsOfAttributes().get(selectionAttribute).indexType == null) {
				calculateCost_nonPrimaryKey_rangeOperator_UsingFileScan(selectionTable);
			}
			//is it selection on primary key?
			else if (dataBuffer.getStatsOfAttributes().get(selectionAttribute).indexType.equalsIgnoreCase("primary")) {
				calculateCost_PrimaryKey_RangeOperator_UsingFileScan(selectionTable); 
				calculateCost_PrimaryKey_RangeOperator_UsingIndex(selectionTable, selectionAttribute);//?????
			}
			else {
				calculateCost_nonPrimaryKey_rangeOperator_UsingFileScan(selectionTable);
				calculateCost_nonPrimaryKey_rangeOperator_UsingIndex(selectionTable, selectionAttribute);
			}
		}
		
	}
	
	//a1 completed
	public static void calculateCost_PrimaryKey_EqualityOperator_UsingIndex(String selectionAttribute) throws SQLException {
		// Primary index: height + 1
		int height = dataBuffer.getStatsOfAttributes().get(selectionAttribute).treeHeight;
		int cost = height + 1;
		System.out.println("cost of using Primary index (b+ tree): " + cost);

	}

	//a2 completed 
	public static void calculateCost_PrimaryKey_EqualityOperator_UsingFileScan(String tableName) throws SQLException {
//			Linear search: b/2,
//			Binary search: log2b
//			Using both algorithms in this method
		double blocks = dataBuffer.getStatsOfTables().get(tableName).numOfBlocks;
		int linearSearchWorstCase = (int) Math.ceil(blocks);
		int linearSearchAvgCase = (int) Math.ceil(blocks/2);
		int binarySearch = (int) Math.ceil(Math.log(blocks) / Math.log(2));
		
		System.out.println("cost of using Linear Search avg case: " + 
		linearSearchAvgCase + "\n" + "\n" + "cost of using Linear Search worst case: " + 
				linearSearchWorstCase + "\n" + "cost of using Binary Search: " + binarySearch);

	}
	
	//b1 completed
	public static void calculateCost_PrimaryKey_RangeOperator_UsingIndex(String selectionTable, String selectionAttribute) throws SQLException {

		// B-tree: Height + numOfBlocksOfRecords in the range
		int height = dataBuffer.getStatsOfAttributes().get(selectionAttribute).treeHeight;
		int numBlocks = dataBuffer.getStatsOfTables().get(selectionTable).numOfBlocks / 2;
		int cost = height + numBlocks;
		System.out.println("cost of using B+-Tree: " + cost);
		 
	}
	//b2 completed
	public static void calculateCost_PrimaryKey_RangeOperator_UsingFileScan(String selectionTable) throws SQLException {
		double blocks = dataBuffer.getStatsOfTables().get(selectionTable).numOfBlocks;
		int linearSearchWorstCase = (int) Math.ceil(blocks);
		int linearSearchAvgCase = (int) Math.ceil(blocks/2);
		System.out.println("cost of using Linear search avg case: " + linearSearchAvgCase + "\n" + "\n" + 
		"cost of using Linear Search worst case: " + linearSearchWorstCase);
	}
	//c1
	public static void calculateCost_nonPrimaryKey_EqualityOperator_UsingIndex(String selectionAttribute) throws SQLException {
		//using b+ tree: x + 1 + s
		int x = dataBuffer.getStatsOfAttributes().get(selectionAttribute).treeHeight;
		//s = selection cardinality
		int s = 0;
		int cost = x + 1 + s;
		System.out.println("cost of using index (b+ tree): " + cost);
	}
	//c2 completed
	public static void calculateCost_nonPrimaryKey_equalityOperator_UsingFileScan(String selectionTable) throws SQLException {
		
		// Linear Search: b
		double blocks = dataBuffer.getStatsOfTables().get(selectionTable).numOfBlocks;
		int linearSearchWorstCase = (int) Math.ceil(blocks);
		int linearSearchAvgCase = (int) Math.ceil(blocks/2);

		System.out.println("cost of using Linear Search avg case: " + linearSearchAvgCase + "\n" +
				"cost of using Linear Search worst case: " + linearSearchWorstCase);
		
	}
	//d1 completed
	public static void calculateCost_nonPrimaryKey_rangeOperator_UsingIndex(String selectionTable, String selectionAttribute) throws SQLException {
		//B+ tree cost: x + (bi1/2) + (r/2)
		//bi1 (first level index blocks): The method to find this is given no where in the book, so we assumed a value
		int bi1 = 4;
		int height = dataBuffer.getStatsOfAttributes().get(selectionAttribute).treeHeight;
		int r = dataBuffer.getStatsOfTables().get(selectionTable).numOfRecords;
		int cost = height + (bi1/2) + (r/2);
		System.out.println("cost of using B+-Tree: " + cost);
	}
	//d2 completed
	public static void calculateCost_nonPrimaryKey_rangeOperator_UsingFileScan(String selectionTable) throws SQLException {
		double blocks = dataBuffer.getStatsOfTables().get(selectionTable).numOfBlocks;
		int linearSearchWorstCase = (int) Math.ceil(blocks);
		int linearSearchAvgCase = (int) Math.ceil(blocks/2);
		System.out.println("cost of using Linear Search avg case: " + linearSearchAvgCase + "\n" +
				"cost of using Linear Search worst case: " + linearSearchWorstCase);
	}
	//e1 completed
	public static void calculateCost_equiJoin_nestedLoop(String selectionTable, String selectionTable2) throws SQLException {
		// Assuming Buffer (nB) size to 32;
		int nB = 32;
		//getting total number of blocks of tables that are JOINED
		int B = dataBuffer.getStatsOfTables().get(selectionTable).numOfBlocks;
		int B2 = dataBuffer.getStatsOfTables().get(selectionTable2).numOfBlocks;
		//getting total number of records of the tables
		int BNumRecords = dataBuffer.getStatsOfTables().get(selectionTable).numOfRecords;
		int B2NumRecords = dataBuffer.getStatsOfTables().get(selectionTable2).numOfRecords;
		//js = 1, since the NDV(primaryKey,selectionTable) on both tables is 1, so 1/1 = 1
		int js = 1;
		//assuming bfr Of Resultant Table as 3
		int bfrOfResultantTable = 3;
		int cost = (int) (B + ((B/(nB-2))*B2) + ((js * BNumRecords * B2NumRecords)/bfrOfResultantTable));
		System.out.println("Cost of using nested-loop: " + cost);

	}
	//e2 completed
	public static void calculateCost_equiJoin_mergeJoin(String selectionTable, String selectionTable2) throws SQLException {
		//getting total number of blocks of tables that are JOINED
		int B = dataBuffer.getStatsOfTables().get(selectionTable).numOfBlocks;
		int B2 = dataBuffer.getStatsOfTables().get(selectionTable2).numOfBlocks;
		//getting total number of records of the tables
		int BNumRecords = dataBuffer.getStatsOfTables().get(selectionTable).numOfRecords;
		int B2NumRecords = dataBuffer.getStatsOfTables().get(selectionTable2).numOfRecords;
		//assuming bfr Of Resultant Table as 3
		int bfrOfResultantTable = 3;
		//js = 1, since the NDV(primaryKey,selectionTable) on both tables is 1, so 1/1 = 1
		int js = 1;
		int cost = B + B2 + ((js * BNumRecords * B2NumRecords)/bfrOfResultantTable);
		System.out.println("Cost of using Sort-merge Join: " + cost);

	
	}
	//e3
	public static void calculateCost_equiJoin_nestedLoopIndex(String selectionTable, String selectionTable2, String selectionAttribute) throws SQLException {
		//getting total number of blocks of tables that are JOINED
		int B = dataBuffer.getStatsOfTables().get(selectionTable).numOfBlocks;
		int B2 = dataBuffer.getStatsOfTables().get(selectionTable2).numOfBlocks;
		//getting total number of records of the tables
		int BNumRecords = dataBuffer.getStatsOfTables().get(selectionTable).numOfRecords;
		int B2NumRecords = dataBuffer.getStatsOfTables().get(selectionTable2).numOfRecords;
		//assuming bfr Of Resultant Table as 3
		int bfrOfResultantTable = 3;
		//js = 1, since the NDV(primaryKey,selectionTable) on both tables is 1, so 1/1 = 1
		int js = 1;
		//getting tree height of index on B
		int x = dataBuffer.getStatsOfAttributes().get(selectionAttribute).treeHeight;
		//need to add s to the cost function
		int s = 2;
		int cost = B + (B2NumRecords * (x + 1 + s)) + ((js * BNumRecords * B2NumRecords)/bfrOfResultantTable);
		System.out.println("cost of using nested-loop with index: " + cost);
	
	}
	//for equality hash: completed
	public static void calculateCost_equalityHashing() {
		System.out.println("cost of using hashing: " + 1);
	}


}
