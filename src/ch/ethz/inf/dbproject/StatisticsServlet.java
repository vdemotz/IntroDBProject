package ch.ethz.inf.dbproject;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import ch.ethz.inf.dbproject.database.DatastoreInterface;
import ch.ethz.inf.dbproject.model.CategorySummary;
import ch.ethz.inf.dbproject.model.StatsNode;
import ch.ethz.inf.dbproject.util.html.BeanTableHelper;


@WebServlet(description = "Displays Statistics", urlPatterns = { "/Statistics" })
public final class StatisticsServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private final DatastoreInterface dbInterface = new DatastoreInterface();
	
	public static final String REQUEST_CATEGORY_SUMMARY_TABLE = "categorySummary";
	public static final String REQUEST_STATISTICS_STATS_TABLE = "statisticsStatsTable";
	
	private static final String NUMBER_OF_CONVICTIONS = "Number of Convictions";
	private static final String NAME_OF_CITY = "Name of City";
	private static final String NUMBER_OF_CASES = "Number of Cases";
	private static final String DATE = "Date";
	private static final String NAME_OF_CATEGORY = "Name of Category";
	//private static final String NAME_OF_USER = "Name of User";
	//private static final String NUMBER_OF_NOTES = "Number of Notes";
	
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
			return "Please choose which statistics to display";
		} else if (filter.equals("casesCity")){
			table.addBeanColumn(NAME_OF_CITY, "name");
			table.addBeanColumn(NUMBER_OF_CASES, "value");
			table.addObjects(dbInterface.getCasesPerCity());
		} else if (filter.equals("casesMonth")){
			table.addBeanColumn(DATE, "month");
			table.addBeanColumn(NUMBER_OF_CASES, "value");
			table.addObjects(dbInterface.getCasesPerMonth());
		} else if (filter.equals("convictionsCity")){
			table.addBeanColumn(NAME_OF_CITY, "name");
			table.addBeanColumn(NUMBER_OF_CONVICTIONS, "value");
			table.addObjects(dbInterface.getConvictionsPerCity());
		} else if (filter.equals("convictionsMonth")){
			table.addBeanColumn(NAME_OF_CITY, "month");
			table.addBeanColumn(NUMBER_OF_CONVICTIONS, "value");
			table.addObjects(dbInterface.getConvictionsPerMonth());
		} else if (filter.equals("convictionsCategory")){
			table.addBeanColumn(NAME_OF_CATEGORY, "name");
			table.addBeanColumn(NUMBER_OF_CONVICTIONS, "value");
			table.addObjects(dbInterface.getConvictionsPerCategory());
		} else {
			return "Sorry, these stats are unavaible";
		}
		return table;
	}
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		final String filter = request.getParameter("filter");
		
		request.setAttribute(REQUEST_CATEGORY_SUMMARY_TABLE, getCategorySummaryTable());
		try {
			request.setAttribute(REQUEST_STATISTICS_STATS_TABLE, this.getStatsTable(filter));
		} catch (Exception ex){
			request.setAttribute(REQUEST_STATISTICS_STATS_TABLE, "A problem occured");
		}
		
		this.getServletContext().getRequestDispatcher("/Statistics.jsp").forward(request, response);
	}
	
}
