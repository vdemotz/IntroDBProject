package ch.ethz.inf.dbproject.sqlRevisited;

import ch.ethz.inf.dbproject.sqlRevisited.SQLType.BaseType;

	/*
	 * Content of the database.
	 * Its constructed as a bag, with a pointer to indicate which
	 * element the bag currently point
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
		private String[] tablesNames = new String[]{"User", "CaseDetail", "CaseNote", "Person", "PersonNote", "Category", "CategoryForCase", "Suspected", "Conviction"};
		//attributes names
		private String[][] attributesNames = new String[][] {
				{"username", "firstName", "lastName", "password"}, //User
				{"caseId", "title", "street", "city", "zipCode", "isOpen", "date", "description", "authorName"}, //CaseDetail
				{"caseId", "caseNoteId", "text", "date", "authorUsername"}, //CaseNote
				{"personId", "firstName", "lastName", "birthdate"}, //Person
				{"personId", "personNoteId", "text", "date", "authorUsername"}, //PersonNote
				{"name"}, //Category
				{"caseId", "categoryName"}, //CategoryForCase
				{"personId", "caseId"}, //Suspected
				{"convictionId", "personId", "caseId", "startDate", "endDate"} //Conviction
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
				{true, false, false, false, false, false, false, false}, //CaseDetail
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
			return new TableSchema(tablesNames[position], attributesNames[position], attributesTypes[position], isPrimaryKey[position]);
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
	}
