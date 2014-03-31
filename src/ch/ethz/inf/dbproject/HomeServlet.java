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

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		final HttpSession session = request.getSession(true);
		final User loggedUser = UserManagement.getCurrentlyLoggedInUser(session);

		if (loggedUser == null) {
			// Not logged in!
			session.setAttribute(SESSION_USER_LOGGED_IN, false);
		} else {
			// Logged in
			session.setAttribute(SESSION_USER_LOGGED_IN, true);
		}

		final String action = request.getParameter("action");
		if (action != null && action.trim().equals("login") 	&& loggedUser == null) {

			final String username = request.getParameter("username");
			// Note: It is really not safe to use HTML get method to send passwords.
			// However for this project, security is not a requirement.
			final String password = request.getParameter("password");
			
			User user = dbInterface.getUserForUsernameAndPassword(username, password);
				//get the user if one exits with this username and password 
			
			if (user == null){
				// TODO display "wrong password" or something like that
			}
			else{
				// TODO Add cases created by the User.
				final BeanTableHelper<User> userDetails = new BeanTableHelper<User>("userDetails", "userDetails", User.class);
				userDetails.addBeanColumn("Username", "username");
				userDetails.addBeanColumn("First Name", "firstName");
				userDetails.addBeanColumn("Last Name", "lastName");
					
				userDetails.addObject(user);
				
				session.setAttribute(SESSION_USER_LOGGED_IN, true);
				session.setAttribute(SESSION_USER_DETAILS, userDetails);
				session.setAttribute(UserManagement.SESSION_USER, user);
					//add the user to the session, with the details
				
				final BeanTableHelper<CaseDetail> casesUserModified = new BeanTableHelper<CaseDetail>("cases", "casesTable", CaseDetail.class);
				// Add columns to the new table
				casesUserModified.addBeanColumn("Case ID", "caseId");
				casesUserModified.addBeanColumn("Title", "title");
				//table.addBeanColumn("Location", "location");
				casesUserModified.addBeanColumn("Open", "isOpen");
				casesUserModified.addBeanColumn("Date", "date");
				casesUserModified.addBeanColumn("Author Name", "authorName");
				
				casesUserModified.addLinkColumn("", "View Case", "Case?id=", "id");
				
				casesUserModified.addObjects(this.dbInterface.getCurrentCasesForUser(username));
				session.setAttribute(SESSION_USER_CASES, casesUserModified);
			}
		}

		// Finally, proceed to the User.jsp page which will render the profile
		this.getServletContext().getRequestDispatcher("/Home.jsp").forward(request, response);
	}        
}
