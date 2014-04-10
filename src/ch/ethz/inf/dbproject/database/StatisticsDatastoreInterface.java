package ch.ethz.inf.dbproject.database;

import java.util.List;

import com.sun.tools.javac.util.Pair;

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
	public List<StatsNode> casesPerCity();

	/**
	 * list all month where there are cases and respective number of cases
	 * @return a list of StatsNode , with StatsNode.name is the month and
	 * StatsNode.value is the number of cases
	 */
	public List<StatsNode> casesPerMonth();
	
	/**
	 * list all month where there are convictions and respective number of convictions
	 * @return a list of StatsNode , with StatsNode.name is the month and
	 * StatsNode.value is the number of convictions
	 */
	public List<StatsNode> convictionsPerMonth();
	
	/**
	 * list all cities and respective number of convictions
	 * @return a list of StatsNode, with StatsNode.name is the name of the city and
	 * StatsNode.value is the number of convictions
	 */
	public List<StatsNode> convictionsPerCity();
	
	/**
	 * list all categories and respective number of convictions
	 * @return a list of StatsNode , with StatsNode.name is the name of the category and
	 * StatsNode.value is the number of convictions
	 */
	public List<StatsNode> convictionsPerCategory();
	
	/**
	 * list all users and respective number of notes (PersonNote and CaseNote)
	 * @return a list of StatsNode, with StatsNode.name is the user name and 
	 * StatsNode.value is the number of notes
	 */
	public List<StatsNode> numberNotesPerUser();
}
