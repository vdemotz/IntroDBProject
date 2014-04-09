package ch.ethz.inf.dbproject;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.websocket.Session;

import ch.ethz.inf.dbproject.database.DatastoreInterface;
import ch.ethz.inf.dbproject.model.CategorySummary;
import ch.ethz.inf.dbproject.util.html.BeanTableHelper;


@WebServlet(description = "Displays Statistics", urlPatterns = { "/Statistics" })
public final class StatisticsServlet extends HttpServlet {

	private final DatastoreInterface dbInterface = new DatastoreInterface();
	
	public static final String SESSION_CATEGORY_SUMMARY_TABLE = "categorySummary";
	
	private BeanTableHelper<CategorySummary> getCategorySummaryTable()
	{
		BeanTableHelper<CategorySummary> table = new BeanTableHelper<CategorySummary>("categorySummary", "contentTable", CategorySummary.class);
		table.addObjects(dbInterface.getCategorySummary());
		
		table.addBeanColumn("Category", "categoryName");
		table.addBeanColumn("Number of Cases", "numberOfCases");		
		table.addLinkColumn("Category", "View Cases", "Cases?category=", "categoryName");

		return table;
	}
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		final HttpSession session = request.getSession(true);
		
		session.setAttribute(SESSION_CATEGORY_SUMMARY_TABLE, getCategorySummaryTable());
		
		this.getServletContext().getRequestDispatcher("/Statistics.jsp").forward(request, response);
	}
	
}
