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
import ch.ethz.inf.dbproject.util.html.BeanTableHelper;


@WebServlet(description = "Displays Statistics", urlPatterns = { "/Statistics" })
public final class StatisticsServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private final DatastoreInterface dbInterface = new DatastoreInterface();
	
	public static final String SESSION_CATEGORY_SUMMARY_TABLE = "categorySummary";
	public static final String STATISTICS_ADD_CATEGORY = "statisticsAddCategory";
	
	private BeanTableHelper<CategorySummary> getCategorySummaryTable()
	{
		BeanTableHelper<CategorySummary> table = new BeanTableHelper<CategorySummary>("categorySummary", "contentTable", CategorySummary.class);
		table.addObjects(dbInterface.getCategorySummary());
		
		table.addBeanColumn("Category", "categoryName");
		table.addBeanColumn("Number of Cases", "numberOfCases");		
		table.addLinkColumn("Category", "View Cases", "Cases?category=", "categoryName");

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
		
		session.setAttribute(SESSION_CATEGORY_SUMMARY_TABLE, getCategorySummaryTable());
		session.setAttribute(STATISTICS_ADD_CATEGORY, this.addNewCategory(request));
		
		this.getServletContext().getRequestDispatcher("/Statistics.jsp").forward(request, response);
	}
	
}
