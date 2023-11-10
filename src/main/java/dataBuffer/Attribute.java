package dataBuffer;

public class Attribute {

	public String attributeName;
	public String tableName;
	public String indexType;
	public int treeHeight;
	
	public Attribute(String attributeName, String tableName, String indexType, int treeHeight) {
		this.attributeName = attributeName;
		this.tableName = tableName;
		this.indexType = indexType;
		this.treeHeight = treeHeight;
	}
	
	@Override
	public String toString() {
		return "attributeName=" + attributeName + ", tableName=" + tableName + ", indexType=" + indexType
				+ ", treeHeight=" + treeHeight;
	}

}