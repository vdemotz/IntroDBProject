package ch.ethz.inf.dbproject.sqlRevisited;

public class DataStructure {

	private String name = "To be updated";
	private int BLOCK_SIZE = -1;
	private int ELEMENT_SIZE = -1;
	static private DataStructure instance;
	
	static public DataStructure getDataStructure(){
		if (instance != null)
			return instance;
		instance = new DataStructure();
		return instance;
	}	
	private DataStructure(){
		
	}
}
