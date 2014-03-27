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

@WebServlet(description = "Page where user can register.", urlPatterns = { "/UserCreation" })
public final class UserCreationServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private final DatastoreInterface dbInterface = new DatastoreInterface();

	public final static String SESSION_USER_LOGGED_IN = "userLoggedIn";
	public final static String SESSION_USER_DETAILS = "userDetails";
	public final static String SESSION_FORM_UN = "usernameEmpty";
	public final static String SESSION_FORM_LN = "lastNameEmpty";
	public final static String SESSION_FORM_FN = "firstNameEmpty";
	public final static String SESSION_FORM_EP = "passwordEmpty";
	public final static String SESSION_FORM_PNE = "notSamePassword";

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public UserCreationServlet() {
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

		if (loggedUser == null) {
			// Not logged in!
			session.setAttribute(SESSION_USER_LOGGED_IN, false);
			session.setAttribute(SESSION_FORM_UN, false);
			session.setAttribute(SESSION_FORM_LN, false);
			session.setAttribute(SESSION_FORM_FN, false);
			session.setAttribute(SESSION_FORM_EP, false);
			session.setAttribute(SESSION_FORM_PNE, false);
		} else {
			// Logged in
			session.setAttribute(SESSION_USER_LOGGED_IN, true);
		}

		final String action = request.getParameter("action");
		if (action != null && action.trim().equals("creation") 	&& loggedUser == null) {

			final String username = request.getParameter("username");
			final String password1 = request.getParameter("password1");
			final String password2 = request.getParameter("password2");
			final String lastName = request.getParameter("lastName");
			final String firstName = request.getParameter("firstName");
			boolean errorInForm = false;
			
			
			//check if all entries are non empty and both passwords are equivalents
			if(username.isEmpty()){
				session.setAttribute(SESSION_FORM_UN, true);
				errorInForm = true;
			}
			else{
				session.setAttribute(SESSION_FORM_UN, false);
			}
			if(lastName.isEmpty()){
				session.setAttribute(SESSION_FORM_LN, true);
				errorInForm = true;
			}
			else{
				session.setAttribute(SESSION_FORM_LN, false);
			}
			if(firstName.isEmpty()){
				session.setAttribute(SESSION_FORM_FN, true);
				errorInForm = true;
			}
			else{
				session.setAttribute(SESSION_FORM_FN, false);
			}
			if(password1.isEmpty()){
				session.setAttribute(SESSION_FORM_EP, true);
				errorInForm = true;
			}
			else{
				session.setAttribute(SESSION_FORM_EP, false);
			}
			if(!password1.equals(password2)){
				session.setAttribute(SESSION_FORM_PNE, true);
				errorInForm = true;
			}
			else{
				session.setAttribute(SESSION_FORM_PNE, false);
			}
			
			//if all is correct and user name available, create user
			if(!errorInForm && !this.dbInterface.isUsernameAvailable(username)){
				this.dbInterface.addUser(username, password1, lastName, firstName);
			}
		}

		this.getServletContext().getRequestDispatcher("/UserCreation.jsp").forward(request, response);
	}
}