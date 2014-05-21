package ch.ethz.inf.dbproject.sqlRevisited;

import java.util.Arrays;

import ch.ethz.inf.dbproject.sqlRevisited.SQLType.BaseType;

	/*
	 * Content of the database.
	 * Its constructed as a bag, with a pointer to indicate which
	 * element the bag currently points to
	 * Changes to the array will directly influence the shape of the database
	 * Note that if any change is made, you have to update (any change) SERIAL_NUMBER in Database.java
	 * for the changes to be visible at next session
	 */
	public class TableSet{
		
		//Some often used SQLType's :
		private SQLType vc40 = new SQLType(BaseType.Varchar, 40);
		private SQLType vct = new SQLType(BaseType.Varchar, 255);
		private SQLType i = new SQLType(BaseType.Integer);
		private SQLType b = new SQLType(BaseType.Boolean);
		private SQLType d = new SQLType(BaseType.Date);
		private SQLType dt = new SQLType(BaseType.Datetime);
		
		/**
		 * Construct a new TableSet and set position pointer before first element
		 */
		public TableSet(){
			position = -1;
		}
		
		//position of the iterator
		private int position;
		//tables names
		private String[] tablesNames = new String[]{"user", "casedetail", "casenote", "person", "personnote", "category", "categoryforcase", "suspected", "conviction"};
		//attributes names
		private String[][] attributesNames = new String[][] {
				{"username", "firstname", "lastname", "password"}, //User
				{"caseid", "title", "street", "city", "zipCode", "isopen", "date", "description", "authorname"}, //CaseDetail
				{"caseid", "casenoteid", "text", "date", "authorusername"}, //CaseNote
				{"personid", "firstname", "lastname", "birthdate"}, //Person
				{"personid", "personnoteid", "text", "date", "authorusername"}, //PersonNote
				{"name"}, //Category
				{"caseId", "categoryname"}, //CategoryForCase
				{"personid", "caseid"}, //Suspected
				{"convictionid", "personid", "caseid", "startdate", "enddate"} //Conviction
		};
		//attributes types
		private SQLType[][] attributesTypes = new SQLType[][] {
				{this.vc40, this.vc40, this.vc40, this.vc40}, //User
				{this.i, this.vc40, this.vc40, this.vc40, this.vc40, this.b, this.dt, this.vct, this.vc40}, //CaseDetail
				{this.i, this.i, this.vct, this.dt, this.vct}, //CaseNote
				{this.i, this.vc40, this.vc40, this.d}, //Person
				{this.i, this.i, this.vct, this.dt, this.vct}, //PersonNote
				{this.vc40}, //Category
				{this.i, this.vc40}, //CategoryForCase
				{this.i, this.i}, //Suspected
				{this.i, this.i, this.i, this.d, this.d}, //Conviction
				
		};
		//attributes is primary key
		private boolean[][] isPrimaryKey = new boolean[][] {
				{true, false, false, false}, //User
				{true, false, false, false, false, false, false, false, false}, //CaseDetail
				{true, true, false, false, false}, //CaseNote
				{true, false, false, false}, //Person
				{true, true, false, false, false}, //PersonNote
				{true}, //Category
				{true, true}, //CategoryForCase
				{true, true}, //Suspected
				{true, false, false, false, false} //Conviction
		};
		
		/**
		 * Move the pointer of TableSet to the next element and return if there is
		 * one or not
		 * @return true if there is an other element, else false
		 */
		public boolean next(){
			position++;
			if (position >= tablesNames.length)
				return false;
			return true;
		}
		
		/**
		 * Get currently pointed TableSchema
		 * @return the TableSchema pointed by TableSet
		 */
		public TableSchema getCurrent(){
			return getSchemaForIndex(position);
		}	
		
		/**
		 * Get number of tables
		 * @return number of tables
		 */
		public int getNumberTables(){
			return this.tablesNames.length;
		}
		
		/**
		 * Get all names of the tables
		 * @return an array of string representing all tables names.
		 */
		public String[] getTablesNames(){
			return this.tablesNames;
		}
		
		public TableSchema getSchemaForName(String name) {
			return getSchemaForIndex(Arrays.asList(getTablesNames()).indexOf(name));
		}
		
		public TableSchema getSchemaForIndex(int index)
		{
			return new TableSchema(tablesNames[index], attributesNames[index], attributesTypes[index], isPrimaryKey[index]);
		}
	}
