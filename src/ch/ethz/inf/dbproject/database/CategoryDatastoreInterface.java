package ch.ethz.inf.dbproject.database;

import java.util.List;

import ch.ethz.inf.dbproject.model.Category;

public interface CategoryDatastoreInterface {

	/**
	 * @param caseId
	 * @return a list of categories if the caseId is valid
	 * 		   otherwise null
	 */
	List<Category>getCategoriesForCase(int caseId);
	
	/**
	 * insert into table 'Category' a new entry
	 * @param name the name of the new category
	 * @return true if no problem, else false
	 */
	boolean insertIntoCategory(String name);

	/**
	 * insert into table 'CategoryForCase' a new entry
	 * @param name the name of the category
	 * @param caseId the id of the case
	 * @return true if no problem, else false
	 */
	boolean insertIntoCategoryForCase(String name, int caseId);
	
	/**
	 * return a Category for a specific name if its exists
	 * @param name the name of the category
	 * @return Category of that name, null if doesn't exists
	 */
	Category getCategoryForName(String name);

	/**
	 * return a list of all categories
	 * @return all categories in the database
	 */
	List<Category> getAllCategories();

	/**
	 * delete the entry corresponding to the category for a specific case
	 * @param caseId the case to update
	 * @param categoryName the category to delete from the case
	 * @return true if successful and the database was modified, false otherwise
	 */
	boolean deleteCategoryForCaseIdAndCategory(int caseId, String categoryName);
	
}
