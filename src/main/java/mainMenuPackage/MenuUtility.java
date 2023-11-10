package mainMenuPackage;

import java.sql.SQLException;
import java.util.Map;
import java.util.Scanner;
import dataBuffer.*;

public class MenuUtility {
	
	private static Scanner scanGlobal = new Scanner(System.in);
	
	public static void startMenu() throws SQLException {
		
		System.out.println("welcome to Query Cost Calculator");
		String horizontalLineForFormatting = new String(new char[30]).replace("\0", "-");
		System.out.println(horizontalLineForFormatting);
		displayMenuOptions();
		
	}
	
	private static void displayMenuOptions() throws SQLException {
		
		System.out.println("To choose an option, enter its number:");
		System.out.println("1: Calculate cost of a SQL query (input should be provided strictly in line with the required format)");
		System.out.println("2: Look at stats");
		
		String selectedOption = scanGlobal.nextLine();
		
		switch (selectedOption) {
				
		case "1":
			queryProcessor();
			startMenu();
			break;
		case "2":
			chooseStats();
			startMenu();
			break;
		
		}
		
		
	}

	private static void queryProcessor() throws SQLException {
		System.out.println("----Please Write a Query----");
		String inputQuery = scanGlobal.nextLine();
		queryUtilPackage.QueryUtility.parseQuery(inputQuery);
	}
	
	private static void chooseStats() throws SQLException {
		
		DataBuffer dataBuffer = new DataBuffer();
		System.out.println("\n" + "----Attributes Statistics----");
		for (Map.Entry<String, Attribute> entry : dataBuffer.getStatsOfAttributes().entrySet()) {
		     System.out.println(entry.getValue().toString() + "\n");
		 
		}
		System.out.println("----Table Statistics----");
		for (Map.Entry<String, Table> entry : dataBuffer.getStatsOfTables().entrySet()) {
		     System.out.println(entry.getValue().toString() + "\n");
		 
		}
		
	}

}
