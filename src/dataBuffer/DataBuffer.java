package dataBuffer;

import databaseConnectionPackage.ConnectionAccount;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.TreeMap;

public class DataBuffer {
	
	private TreeMap<String, Attribute> attributeMetadata = new TreeMap<>();
	private TreeMap<String, Table> tableMetadata = new TreeMap<>();
	private ConnectionAccount account = new ConnectionAccount();

	
	public TreeMap<String, Attribute> getStatsOfAttributes() throws SQLException {
		
		if (attributeMetadata.isEmpty()) {
			
			Connection connection = DriverManager.
					getConnection(account.url, account.username, account.password);
			Statement statement = connection.createStatement();
			String sql = "SELECT * FROM attributeMetadata";
			ResultSet resultSet = statement.executeQuery(sql);
			//need to change this to add fields of the called table to attributeMetaData treemap
			// to set the attributes, will first need to know what attributes are needed in the metadata tables
			while (resultSet.next()) {
				String attributeName = resultSet.getString(1);
				String tableName = resultSet.getString(2);
				String indexType = resultSet.getString(3);
				int treeHeight = resultSet.getInt(4);
				attributeMetadata.put(attributeName, new Attribute(attributeName, tableName, indexType, treeHeight));
			}
			
		}
		return this.attributeMetadata;
		
	}
	
	// getting stats of employee and engineer table
	
	public TreeMap<String, Table> getStatsOfTables() throws SQLException {
		
		if (tableMetadata.isEmpty()) {
			
			Connection connection = DriverManager.
					getConnection(account.url, account.username, account.password);
			Statement statement = connection.createStatement();
			String sql = "SELECT * FROM tableMetadata";
			ResultSet resultSet = statement.executeQuery(sql);
			//need to change this to add fields of the called table to tableMetaData treemap
			while (resultSet.next()) {
				String tableName = resultSet.getString(1);
				int numOfBlocks = resultSet.getInt(2);
				int numOfRecords = resultSet.getInt(3);
				int bfr = resultSet.getInt(4);
				tableMetadata.put(tableName, new Table(tableName, numOfBlocks, numOfRecords, bfr));
			}
			
		}
		return this.tableMetadata;
		
	}

}
