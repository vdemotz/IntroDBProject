package ch.ethz.inf.dbproject.database;

import java.util.List;

import ch.ethz.inf.dbproject.model.CaseDetail;
import ch.ethz.inf.dbproject.model.CaseNote;
import ch.ethz.inf.dbproject.model.Category;
import ch.ethz.inf.dbproject.model.CategorySummary;
import ch.ethz.inf.dbproject.model.Person;
import ch.ethz.inf.dbproject.model.StatsNode;


public interface StatisticsDatastoreInterface {
	
	/**
	 * list all cities and respective number of cases
	 * @return a list of StatsNode , with StatsNode.name is the name of the city and
	 * StatsNode.value is the number of cases
	 */
	public List<StatsNode> getCasesPerCity();

	/**
	 * list all month where there are cases and respective number of cases
	 * @return a list of StatsNode , with StatsNode.name is the month and
	 * StatsNode.value is the number of cases
	 */
	public List<StatsNode> getCasesPerMonth();
	
	/**
	 * list all month where there are convictions and respective number of convictions
	 * @return a list of StatsNode , with StatsNode.name is the month and
	 * StatsNode.value is the number of convictions
	 */
	public List<StatsNode> getConvictionsPerMonth();
	
	/**
	 * list all cities and respective number of convictions
	 * @return a list of StatsNode, with StatsNode.name is the name of the city and
	 * StatsNode.value is the number of convictions
	 */
	public List<StatsNode> getConvictionsPerCity();
	
	/**
	 * list all categories and respective number of convictions
	 * @return a list of StatsNode , with StatsNode.name is the name of the category and
	 * StatsNode.value is the number of convictions
	 */
	public List<StatsNode> getConvictionsPerCategory();
	
	/**
	 * list all users and respective number of notes (PersonNote and CaseNote)
	 * @return a list of StatsNode, with StatsNode.name is the user name and 
	 * StatsNode.value is the number of notes
	 */
	public List<StatsNode> getNumberNotesPerUser();


	/**
	 * get the three more active categories for an user
	 * @param username the name (thus, the id) of the user
	 * @return a list of StatsNode, with StatsNode.name is the name of the category
	 * and StatsNode.value is the number of changes made on this particular category
	 */
	public List<StatsNode> getMostActiveCategoriesForUser(String username);
}

