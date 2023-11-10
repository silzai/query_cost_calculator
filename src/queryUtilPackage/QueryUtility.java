package queryUtilPackage;

import java.sql.SQLException;

import dataBuffer.*;

public class QueryUtility {
	
	private static DataBuffer dataBuffer = new DataBuffer();
	
	//parseQuery hardcoded with relevant input only
	public static void parseQuery(String inputQuery) throws SQLException {
		
		String[] tokenizedInput = inputQuery.split(" ");
		//doing this because the selection attribute is usually the 3rd token from the end of a query
		//ASSUMING ALL TOKENS ARE SPACE SEPARATED and LOWER CASE
		String selectionAttribute = tokenizedInput[tokenizedInput.length-3];
		//checking if there are two tables present, so then will do JOIN
		String selectionTableCheck = tokenizedInput[3];
		if (selectionTableCheck.contains(",")) {
			String selectionTable = selectionTableCheck.split(",")[0];
			String selectionTable2 = selectionTableCheck.split(",")[1];
			//getting the proper selection Attribute in case of JOIN, as the
			//selection attribute will be: tableName.selectionAttibute
			selectionAttribute = selectionAttribute.split(".")[0];
		}
		//else get the only selection table there is
		else {String selectionTable = selectionTableCheck;}
		
		inputQuery = inputQuery.toLowerCase();
		
		//EQUIJOIN
		if (inputQuery.contains("employee") && inputQuery.contains("engineer")) {
			calculateCost_equiJoin_nestedLoop();
			calculateCost_equiJoin_mergeJoin(selectionTable);
		}
		//if SELECT and equality operator
		else if (inputQuery.contains("=")) {
			
			if (dataBuffer.getStatsOfAttributes().get(selectionAttribute).indexType == null) {
				
			}
			
			//is it selection on primary key?
			if (dataBuffer.getStatsOfAttributes().get(selectionAttribute).indexType.equalsIgnoreCase("primary")) {
				System.out.println( calculateCost_PrimaryKey_EqualityOperator_UsingFileScan(selectionTable) );
				System.out.println(calculateCost_PrimaryKey_EqualityOperator_UsingIndex(selectionAttribute)); 
				System.out.println(calculateCost_equalityHashing());
			}
			else {
				calculateCost_nonPrimaryKey_equalityOperator_UsingFileScan(selectionTable);
				calculateCost_nonPrimaryKey_EqualityOperator_UsingIndex();
			}
				
		}
		//if SELECT and range operator
		else if (inputQuery.contains(">") || inputQuery.contains("<")) {
			//is it selection on primary key?
			if (dataBuffer.getStatsOfAttributes().get(selectionAttribute).indexType.equalsIgnoreCase("primary")) {
				System.out.println(calculateCost_PrimaryKey_RangeOperator_UsingFileScan()); 
				calculateCost_PrimaryKey_RangeOperator_UsingIndex(selectionTable);//?????
			}
			else {
				calculateCost_nonPrimaryKey_rangeOperator_UsingFileScan();
				calculateCost_nonPrimaryKey_rangeOperator_UsingIndex();
			}
		}
	}
	
	//a1 completed
	public static String calculateCost_PrimaryKey_EqualityOperator_UsingIndex(String selectionAttribute) throws SQLException {
		// Primary index: height + 1
		int height = dataBuffer.getStatsOfAttributes().get(selectionAttribute).treeHeight;
		int cost = height + 1;
		String result = "Primary index: " + cost;
		return result;
	}

	//a2 completed 
	public static String calculateCost_PrimaryKey_EqualityOperator_UsingFileScan(String tableName) throws SQLException {
//			Linear search: b/2,
//			Binary search: log2b
//			Using both algorithms in this method
		double blocks = dataBuffer.getStatsOfTables().get(tableName).numOfBlocks;
		
		int linearSearch = (int) Math.ceil(blocks/2);
		int binarySearch = (int) Math.ceil(Math.log(blocks) / Math.log(2));
		
		String result = "Linear Search: " + linearSearch + "\n" + "Binary Search: " + binarySearch + "\n";
		
		return result;
	}
	
	//b1
	public static String calculateCost_PrimaryKey_RangeOperator_UsingIndex(String selectionTable, String selectionAttribute) throws SQLException {
		
		// B-tree: Height + numOfBlocksOfRecords in the range
		int height = dataBuffer.getStatsOfAttributes().get(selectionAttribute).treeHeight;
		int blocking_Factor = dataBuffer.getStatsOfTables().get(selectionTable).bfr;
		int cost = height + blocking_Factor;
		String result = "B+-Tree: " + cost;
		return result;
		 
	}
	//b2
	public static String calculateCost_PrimaryKey_RangeOperator_UsingFileScan(String selectionTable) throws SQLException {
		int blocks = dataBuffer.getStatsOfTables().get(selectionTable).numOfBlocks/2;
		String result = "Linear search: " + blocks;
		return result;
	}
	//c1
	public static String calculateCost_nonPrimaryKey_EqualityOperator_UsingIndex(String selectionTable) throws SQLException {
		int blocks = dataBuffer.getStatsOfTables().get(selectionTable).numOfBlocks/2;
		String result = "Linear search: " + blocks;
		return result;
	}
	//c2
	public static String calculateCost_nonPrimaryKey_equalityOperator_UsingFileScan(String selectionTable) {
		
		// Linear Search: b
		// Binary Search: log2b + (s/bfr) - 1
		int blocks = dataBuffer.getStatsOfTables().get(selectionTable).numOfBlocks;
		int linearSearch = (int) Math.ceil(blocks);
		int blocking_factor = dataBuffer.getStatsOfTables().get(selectionTable).bfr;
		//need to find "s" in the next line
		int binarySearch = (int) ((Math.ceil(Math.log(blocks) / Math.log(2))) + (s / blocking_factor) - 1);

		String result = "Linear Search: " + linearSearch + "\n" + "Binary Search: " + binarySearch + "\n";
		return result;
		
	}
	//d1 TODO apply the correct cost formula
	public static String calculateCost_nonPrimaryKey_rangeOperator_UsingIndex(String selectionTable, String selectionAttribute) throws SQLException {
		//1.	B+ tree: x + (bi1/2) + (r/2)
		int height = dataBuffer.getStatsOfAttributes().get(selectionAttribute).treeHeight;
		int blocking_Factor = dataBuffer.getStatsOfTables().get(selectionTable).bfr;
		int cost = height + blocking_Factor;
		String result = "B+-Tree: " + cost;
		return result;
	}
	//d2
	public static String calculateCost_nonPrimaryKey_rangeOperator_UsingFileScan(String selectionTable) throws SQLException {
		int blocks = dataBuffer.getStatsOfTables().get(selectionTable).numOfBlocks/2;
		String result = "Linear search: " + blocks;
		return result;
	}
	//e1 completed
	public static String calculateCost_equiJoin_nestedLoop(String selectionTable, String selectionTable2) throws SQLException {
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
		String result = "Nested-Loop: " + cost;
		return result;
	}
	//e2 completed
	public static String calculateCost_equiJoin_mergeJoin(String selectionTable, String selectionTable2) throws SQLException {
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
		String result = "Sort-merge Join: " + cost;
		return result;
	
	}
	//e3
	public static String calculateCost_equiJoin_nestedLoopIndex(String selectionTable, String selectionTable2, String selectionAttribute) throws SQLException {
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
		int cost = B + (B2NumRecords * (x + 1 + s)) + ((js * BNumRecords * B2NumRecords)/bfrOfResultantTable);
		String result = "Sort-merge Join: " + cost;
		return result;
	
	}
	//for equality hash
	public static int calculateCost_equalityHashing() {
		return 1;
	}


}
