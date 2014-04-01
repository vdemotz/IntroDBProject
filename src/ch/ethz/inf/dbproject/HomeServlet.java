package ch.ethz.inf.dbproject;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import ch.ethz.inf.dbproject.database.DatastoreInterface;
import ch.ethz.inf.dbproject.model.User;
import ch.ethz.inf.dbproject.util.UserManagement;
import ch.ethz.inf.dbproject.util.html.BeanTableHelper;
import ch.ethz.inf.dbproject.model.*;
import java.util.List;

/**
 * Servlet implementation class HomePage
 */
@WebServlet(description = "The home page of the project", urlPatterns = { "/Home" })
public final class HomeServlet extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	private final DatastoreInterface dbInterface = new DatastoreInterface();
	public final static String SESSION_USER_LOGGED_IN = "userLoggedIn";
	public final static String SESSION_USER_DETAILS = "userDetails";
	public final static String SESSION_USER_CASES = "casesByUser";
		
    /**
     * @see HttpServlet#HttpServlet()
     */
    public HomeServlet() {
        super();
    }
    
	protected BeanTableHelper<User> tableUserDetails(User usr) {
		final BeanTableHelper<User> userDetails = new BeanTableHelper<User>("userDetails", "userDetails", User.class);
		userDetails.addBeanColumn("Username", "username");
		userDetails.addBeanColumn("First Name", "firstName");
		userDetails.addBeanColumn("Last Name", "lastName");
			
		userDetails.addObject(usr);
		return userDetails;
	}
	
	protected BeanTableHelper<CaseDetail> tableCasesUserModified(String username) {
		final BeanTableHelper<CaseDetail> casesUserModified = new BeanTableHelper<CaseDetail>("cases", "casesTable", CaseDetail.class);
		// Add columns to the new table
		casesUserModified.addBeanColumn("Case ID", "caseId");
		casesUserModified.addBeanColumn("Title", "title");
		//table.addBeanColumn("Location", "location");
		casesUserModified.addBeanColumn("Open", "isOpen");
		casesUserModified.addBeanColumn("Date", "date");
		casesUserModified.addBeanColumn("Author Name", "authorName");
			
		casesUserModified.addLinkColumn("", "View Case", "Case?caseId=", "id");

		casesUserModified.addObjects(this.dbInterface.getCurrentCasesForUser(username));
		return casesUserModified;
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		final HttpSession session = request.getSession(true);
		final User loggedUser = UserManagement.getCurrentlyLoggedInUser(session);
		final String action = request.getParameter("action");

		if (loggedUser == null) {
			// Not logged in!
			session.setAttribute(SESSION_USER_LOGGED_IN, false);
		} else {
			// Logged in
			session.setAttribute(SESSION_USER_LOGGED_IN, true);
			session.setAttribute(SESSION_USER_DETAILS, tableUserDetails(loggedUser));
			session.setAttribute(SESSION_USER_CASES, tableCasesUserModified(loggedUser.getUsername()));
		}

		if (action != null && action.trim().equals("login") 	&& loggedUser == null) {

			final String username = request.getParameter("username");
			final String password = request.getParameter("password");
			
			User user = dbInterface.getUserForUsernameAndPassword(username, password);
			session.setAttribute(UserManagement.SESSION_USER, user);
			
			session.setAttribute(SESSION_USER_DETAILS, tableUserDetails(user));
			session.setAttribute(SESSION_USER_CASES, tableCasesUserModified(user.getUsername()));
			
		} else if( action != null && action.trim().equals("logout")){
			session.setAttribute(SESSION_USER_LOGGED_IN, false);
			session.setAttribute(UserManagement.SESSION_USER, null);
		}

		// Finally, proceed to the Home.jsp page which will render the profile
		this.getServletContext().getRequestDispatcher("/Home.jsp").forward(request, response);
	}
}

