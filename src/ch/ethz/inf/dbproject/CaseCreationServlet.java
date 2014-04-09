package ch.ethz.inf.dbproject;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.Date;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import ch.ethz.inf.dbproject.database.DatastoreInterface;
import ch.ethz.inf.dbproject.model.CategorySummary;
import ch.ethz.inf.dbproject.model.User;
import ch.ethz.inf.dbproject.util.UserManagement;
import ch.ethz.inf.dbproject.util.html.BeanTableHelper;

@WebServlet(description = "Page where user can create new cases", urlPatterns = { "/CaseCreation" })
public final class CaseCreationServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private final DatastoreInterface dbInterface = new DatastoreInterface();

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public CaseCreationServlet() {
		super();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected final void doGet(final HttpServletRequest request,
			final HttpServletResponse response) throws ServletException,
			IOException {

		final HttpSession session = request.getSession(true);
		final User loggedUser = UserManagement.getCurrentlyLoggedInUser(session);

		final String action = request.getParameter("action");
		if (action != null && action.trim().equals("creation") 	&& loggedUser != null) {
			//create a new case with the description given by the user. The date is set to today
			//the case is by default open

			final String title = request.getParameter("title");
			final String city = request.getParameter("city");
			final String street = request.getParameter("street");
			final String zipCode = request.getParameter("zipCode");
			final String description = request.getParameter("description");
			String authorUsername = loggedUser.getUsername();
			Date date = new Date();
			Timestamp timestamp = new Timestamp(date.getTime());
			dbInterface.insertIntoCaseDetail(title, city, zipCode, street, timestamp, description, authorUsername);
			
			//TODO display 'case created', goto Cases.jsp
		}

		this.getServletContext().getRequestDispatcher("/CaseCreation.jsp").forward(request, response);
	}
	
	private BeanTableHelper<CategorySummary> getCategoryNamesTable()
	{
		BeanTableHelper<CategorySummary> table = new BeanTableHelper<CategorySummary>("categorySummary", "contentTable", CategorySummary.class);
		table.addObjects(dbInterface.getCategorySummary());		
		table.addLinkColumn("Category", "View Cases", "Cases?category=", "categoryName");

		return table;
	}
}