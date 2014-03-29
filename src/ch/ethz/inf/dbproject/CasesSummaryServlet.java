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


@WebServlet(description = "Displays Groups of Cases", urlPatterns = { "/CasesSummary" })
public class CasesSummaryServlet extends HttpServlet {

	private final DatastoreInterface dbInterface = new DatastoreInterface();
	
	private BeanTableHelper<CategorySummary> getCategorySummaryTable()
	{
		BeanTableHelper<CategorySummary> table = new BeanTableHelper<CategorySummary>("categorySummary", "casesTable", CategorySummary.class);
		table.addObjects(dbInterface.getCategorySummary());
		
		table.addBeanColumn("Category", "categoryName");
		table.addBeanColumn("Number of Cases", "numberOfCases");		
		table.addLinkColumn("Category", "View Cases", "Cases?category=", "categoryName");

		return table;
	}
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		final HttpSession session = request.getSession(true);
		
		session.setAttribute("casesSummary", getCategorySummaryTable());
		
		this.getServletContext().getRequestDispatcher("/CasesSummary.jsp").forward(request, response);
	}
	
}
