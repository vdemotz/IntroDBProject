package ch.ethz.inf.dbproject;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import ch.ethz.inf.dbproject.database.DatastoreInterface;
import ch.ethz.inf.dbproject.model.CategorySummary;
import ch.ethz.inf.dbproject.model.StatsNode;
import ch.ethz.inf.dbproject.util.html.BeanTableHelper;


@WebServlet(description = "Displays Statistics", urlPatterns = { "/Statistics" })
public final class StatisticsServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private final DatastoreInterface dbInterface = new DatastoreInterface();
	
	public static final String SESSION_CATEGORY_SUMMARY_TABLE = "categorySummary";
	public static final String STATISTICS_ADD_CATEGORY = "statisticsAddCategory";
	public static final String STATISTICS_STATS_TABLE = "statisticsStatsTable";
	
	private final String NO_CONVICTIONS = "Number of Convictions";
	private final String NA_CITY = "Name of City";
	private final String NO_CASES = "Number of Cases";
	private final String DATE = "Date";
	private final String NA_CATEGORY = "Name of Category";
	private final String NA_USER = "Name of User";
	private final String NO_NOTES = "Number of Notes";
	
	private BeanTableHelper<CategorySummary> getCategorySummaryTable()
	{
		BeanTableHelper<CategorySummary> table = new BeanTableHelper<CategorySummary>("categorySummary", "contentTable", CategorySummary.class);
		table.addObjects(dbInterface.getCategorySummary());
		
		table.addBeanColumn("Category", "categoryName");
		table.addBeanColumn("Number of Cases", "numberOfCases");		
		table.addLinkColumn("Category", "View Cases", "Cases?category=", "categoryName");

		return table;
	}
	
	private Object getStatsTable(String filter) {
		BeanTableHelper<StatsNode> table = new BeanTableHelper<StatsNode>("statsSummary", "contentTable", StatsNode.class);
		
		if (filter == null){
			return "Choose one statistics to display";
		} else if (filter.equals("casesCity")){
			table.addBeanColumn(this.NA_CITY, "name");
			table.addBeanColumn(this.NO_CASES, "value");
			table.addObjects(dbInterface.casesPerCity());
		} else if (filter.equals("casesMonth")){
			table.addBeanColumn(this.DATE, "name");
			table.addBeanColumn(this.NO_CASES, "value");
			table.addObjects(dbInterface.casesPerMonth());
		} else if (filter.equals("convictionsCity")){
			table.addBeanColumn(this.NA_CITY, "name");
			table.addBeanColumn(this.NO_CONVICTIONS, "value");
			table.addObjects(dbInterface.convictionsPerCity());
		} else if (filter.equals("convictionsMonth")){
			table.addBeanColumn(this.NA_CITY, "name");
			table.addBeanColumn(this.NO_CONVICTIONS, "value");
			table.addObjects(dbInterface.convictionsPerMonth());
		} else if (filter.equals("convictionsCategory")){
			table.addBeanColumn(this.NA_CATEGORY, "name");
			table.addBeanColumn(this.NO_CONVICTIONS, "value");
			table.addObjects(dbInterface.convictionsPerCategory());
		}  else if (filter.equals("notesUser")){
			table.addBeanColumn(this.NA_USER, "name");
			table.addBeanColumn(this.NO_NOTES, "value");
			table.addObjects(dbInterface.numberNotesPerUser());
		} else {
			return "Sorry, these stats are unavaible";
		}
		return table;
	}
	
	private String addNewCategory(HttpServletRequest request){
		
		final String action = request.getParameter("action");
		final String description = request.getParameter("description");
		String ret;
		
		if (action != null && action.equals("categoryCreation")){
			try{
				if(this.dbInterface.insertIntoCategory(description)){
					ret = "Your category has been added";
				}
				else{
					ret = "Sorry, this category already exists.";
				}
			} catch(Exception ex) {
				ex.printStackTrace();
				ret = "Sorry, failed to add your category. Please contact us and we will do our best"+
				"to fix the problem.";
			}
		} else {
			ret = "";
		}
		return ret;
	}
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		final HttpSession session = request.getSession(true);
		final String filter = request.getParameter("filter");
		
		session.setAttribute(SESSION_CATEGORY_SUMMARY_TABLE, getCategorySummaryTable());
		session.setAttribute(STATISTICS_ADD_CATEGORY, this.addNewCategory(request));
		try {
			session.setAttribute(STATISTICS_STATS_TABLE, this.getStatsTable(filter));
		} catch (Exception ex){
			session.setAttribute(STATISTICS_STATS_TABLE, "A problem occured");
		}
		
		this.getServletContext().getRequestDispatcher("/Statistics.jsp").forward(request, response);
	}
	
}
