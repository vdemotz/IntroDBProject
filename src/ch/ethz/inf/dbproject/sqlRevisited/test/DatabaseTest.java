package ch.ethz.inf.dbproject.sqlRevisited.test;

import org.junit.Test;
import ch.ethz.inf.dbproject.sqlRevisited.Database;
import ch.ethz.inf.dbproject.sqlRevisited.TableSchema;

public class DatabaseTest {

	@Test
	public void testDatabase() throws Exception{
		this.testMetaDataCreation();
	}
	
	@Test
	public void testMetaDataCreation() throws Exception{
		Database db = new Database();
		TableSchema tsUser = null;
		TableSchema tsCaseDetail = null;
		TableSchema tsCaseNote = null;
		TableSchema tsCategory = null;
		TableSchema tsConviction = null;
		TableSchema tsBla = null;
		try{
			tsUser = db.getTableSchema("User");
			tsCaseDetail = db.getTableSchema("CaseDetail");
			tsCaseNote = db.getTableSchema("CaseNote");
			tsCategory = db.getTableSchema("Category");
			tsConviction = db.getTableSchema("Conviction");
		} catch (Exception ex){
			ex.printStackTrace();
		}

		try{
			db.getTableSchema("Bla");
		} catch (Exception exFailed){
			exFailed.printStackTrace();
		}
		
		assert(tsCaseDetail != null && tsCaseNote != null && tsCategory != null && tsConviction != null && tsBla == null);
		assert(tsCaseDetail.getAttributesNames()[0].equals("caseId"));
		
		System.out.println("The table is called : "+tsUser.getTableName());
		System.out.println("It has attributes : ");
		for (int i = 0; i < tsUser.getAttributesNames().length; i++){
			System.out.print("The attribute " + tsUser.getAttributesNames()[i]+" has type : ");
			System.out.print(tsUser.getAttributesTypes()[i].type.toString()+"("+tsUser.getAttributesTypes()[i].size.toString()+")"+", ");
			System.out.print("primary key : " +tsUser.isPrimaryKey(i)+"\r\n");
		}
	}
}
