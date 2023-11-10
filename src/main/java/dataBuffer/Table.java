package dataBuffer;

public class Table {
	
	
	public String tableName;
	public int numOfBlocks;
	public int numOfRecords;
	public int bfr;
	
	public Table(String tableName, int numOfBlocks, int numOfRecords, int bfr) {
		this.tableName = tableName;
		this.numOfBlocks = numOfBlocks;
		this.numOfRecords = numOfRecords;
		this.bfr = bfr;
	}

	@Override
	public String toString() {
		return "Table [tableName=" + tableName + ", numOfBlocks=" + numOfBlocks + ", numOfRecords=" + numOfRecords
				+ ", bfr=" + bfr + "]";
	}

	
}