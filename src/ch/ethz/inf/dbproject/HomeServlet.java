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
	public final static String SESSION_WRONG_PASSWORD = "wrongUserPassword";
		
    /**
     * @see HttpServlet#HttpServlet()
     */
    public HomeServlet() {
        super();
    }
    
    /**
     * Provide a table of user details
     * @param usr a user to display details
     * @return a table of user details
     */
	protected BeanTableHelper<User> tableUserDetails(User usr) {
		final BeanTableHelper<User> userDetails = new BeanTableHelper<User>("userDetails", "contentTable", User.class);
		userDetails.addBeanColumn("Username", "username");
		userDetails.addBeanColumn("First Name", "firstName");
		userDetails.addBeanColumn("Last Name", "lastName");
			
		userDetails.addObject(usr);
		return userDetails;
	}
	
	/**
	 * Provide a table of all cases open by a user
	 * @param username
	 * @return a table of cases
	 */
	protected BeanTableHelper<CaseDetail> tableCasesUserModified(String username) {
		final BeanTableHelper<CaseDetail> casesUserModified = new BeanTableHelper<CaseDetail>("cases", "contentTable", CaseDetail.class);
		// Add columns to the new table
		casesUserModified.addBeanColumn("Case ID", "caseId");
		casesUserModified.addBeanColumn("Title", "title");
		casesUserModified.addBeanColumn("Location", "location");
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
			session.setAttribute(SESSION_WRONG_PASSWORD, false);
		} else {
			// Logged in
			session.setAttribute(SESSION_USER_LOGGED_IN, true);
			session.setAttribute(SESSION_USER_DETAILS, tableUserDetails(loggedUser));
			session.setAttribute(SESSION_USER_CASES, tableCasesUserModified(loggedUser.getUsername()));
		}

		if (action != null && action.trim().equals("login") 	&& loggedUser == null) {
			
			//if the user try to login, ask the database if user is registered
			final String username = request.getParameter("username");
			final String password = request.getParameter("password");
			User user = dbInterface.getUserForUsernameAndPassword(username, password);
			
			if (user != null) {
				//if the database return an user, it means the user is registered
				//then, set session attributes to display all details of the user
				session.setAttribute(UserManagement.SESSION_USER, user);
				session.setAttribute(SESSION_USER_DETAILS, tableUserDetails(user));
				session.setAttribute(SESSION_USER_CASES, tableCasesUserModified(user.getUsername()));
			} else {
				//if the database return null, then set session to display 'wrong password'
				session.setAttribute(SESSION_WRONG_PASSWORD, true);
			}
			
		} else if( action != null && action.trim().equals("logout")){
			//if user want to logout, retrieve user from session
			session.setAttribute(SESSION_USER_LOGGED_IN, false);
			session.setAttribute(UserManagement.SESSION_USER, null);
		}

		// Finally, proceed to the Home.jsp page which will render the profile
		this.getServletContext().getRequestDispatcher("/Home.jsp").forward(request, response);
	}
}

