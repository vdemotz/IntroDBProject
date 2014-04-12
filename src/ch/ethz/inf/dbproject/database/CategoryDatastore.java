package ch.ethz.inf.dbproject.database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import ch.ethz.inf.dbproject.model.Category;

public class CategoryDatastore extends Datastore implements CategoryDatastoreInterface {

	//template: all categories for a specific case
	private static final String categoriesForCaseQuery = "select distinct Category.* " +
									"from CaseDetail caseDetail, CategoryForCase categoryForCase, Category category " +
								    "where caseDetail.caseId = ? and categoryForCase.caseId = caseDetail.caseId and categoryForCase.categoryName = category.Name";
	//template add a new category for a case
	private static final String insertIntoCategoryForCaseQuery = "insert into CategoryForCase(caseId, categoryName) values(?, ?)";
	//template add a new category
	private static final String insertIntoCategoryQuery = "insert into Category(name) values(?)";
	//template get a category for a name
	private static final String getCategoryForNameQuery = "select * from Category where name = ?";
	//template get all categories
	private static final String getAllCategoriesQuery = "select * from Category";
	//template remove a categoryForCase for a specific case and category
	private static final String deleteCategoryForCaseIdAndCategoryQuery = "delete from CategoryForCase where caseId = ? and categoryName = ?";
	
	PreparedStatement categoriesForCaseStatement;
	PreparedStatement insertIntoCategoryForCaseStatement;
	PreparedStatement insertIntoCategoryStatement;
	PreparedStatement getCategoryForNameStatement;
	PreparedStatement getAllCategoriesStatement;
	PreparedStatement deleteCategoryForCaseIdAndCategoryStatement;
	
	@Override
	protected void prepareStatements() throws SQLException
	{
		categoriesForCaseStatement = sqlConnection.prepareStatement(categoriesForCaseQuery);
		insertIntoCategoryForCaseStatement = sqlConnection.prepareStatement(insertIntoCategoryForCaseQuery);
		insertIntoCategoryStatement = sqlConnection.prepareStatement(insertIntoCategoryQuery);
		getCategoryForNameStatement = sqlConnection.prepareStatement(getCategoryForNameQuery);
		getAllCategoriesStatement = sqlConnection.prepareStatement(getAllCategoriesQuery);
		deleteCategoryForCaseIdAndCategoryStatement = sqlConnection.prepareStatement(deleteCategoryForCaseIdAndCategoryQuery);
	}
	
	/////
	//Result of type List<Category>
	/////
	
	@Override
	public List<Category> getCategoriesForCase(int caseId) {
		try {
			categoriesForCaseStatement.setInt(1, caseId);
			return getResults(Category.class, categoriesForCaseStatement);
		} catch (SQLException e) {
			return null;
		}
	}
	
	@Override
	public List<Category> getAllCategories() {
		return getResults(Category.class, getAllCategoriesStatement);
	}

	/////
	//Result of type Category
	/////
	
	@Override
	public Category getCategoryForName(String name) {
		try {
			getCategoryForNameStatement.setString(1, name);
			ResultSet rs = getCategoryForNameStatement.executeQuery();
			if (rs.first()){
				return new Category(rs);
			} else {
				return null;
			}
		} catch (SQLException e){
			e.printStackTrace();
			return null;
		}
	}
	

	/////
	//MODIFY
	////
	
	@Override
	public boolean deleteCategoryForCaseIdAndCategory(int caseId, String categoryName) {
		try {
			deleteCategoryForCaseIdAndCategoryStatement.setInt(1, caseId);
			deleteCategoryForCaseIdAndCategoryStatement.setString(2, categoryName);
			deleteCategoryForCaseIdAndCategoryStatement.execute();
			return deleteCategoryForCaseIdAndCategoryStatement.getUpdateCount() > 0;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	
	@Override
	public boolean insertIntoCategory(String name){
		try {
			insertIntoCategoryStatement.setString(1, name);
			insertIntoCategoryStatement.execute();
			return insertIntoCategoryStatement.getUpdateCount() > 0;
		} catch (SQLException e) {
			return false;
		}
	}
	
	@Override
	public boolean insertIntoCategoryForCase(String name, int caseId){
		try {
			insertIntoCategoryForCaseStatement.setInt(1, caseId);
			insertIntoCategoryForCaseStatement.setString(2, name);
			insertIntoCategoryForCaseStatement.execute();
			return insertIntoCategoryForCaseStatement.getUpdateCount() > 0;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	
}
