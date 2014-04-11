package ch.ethz.inf.dbproject;

import java.io.IOException;
import java.sql.SQLException;

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

	public final static String USERCREATION_FORM_MESSAGE = "userCreationMessage";

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
		session.setAttribute(USERCREATION_FORM_MESSAGE, "");
		final User loggedUser = UserManagement.getCurrentlyLoggedInUser(session);

		final String action = request.getParameter("action");
		if (action != null && action.trim().equals("creation") 	&& loggedUser == null) {

			try {
				//get the entries of the new user
				final String username = request.getParameter("username");
				final String password1 = request.getParameter("password1");
				final String password2 = request.getParameter("password2");
				final String lastName = request.getParameter("lastName");
				final String firstName = request.getParameter("firstName");
				boolean errorInForm = false;
				
				
				//check if all entries are non empty and both passwords are equivalents
				if(username.isEmpty()){
					session.setAttribute(USERCREATION_FORM_MESSAGE, "Please enter an username");
					errorInForm = true;
				} else if (this.dbInterface.isUsernameAvailable(username)) {
					session.setAttribute(USERCREATION_FORM_MESSAGE, "Sorry, username already in use");
					errorInForm = true;
				}
				if(password1.isEmpty()){
					session.setAttribute(USERCREATION_FORM_MESSAGE, "Please enter a password");
					errorInForm = true;
				}
				if(!password1.equals(password2)){
					session.setAttribute(USERCREATION_FORM_MESSAGE, "Sorry, both passwords aren't the same");
					errorInForm = true;
				}
				
				//if all is correct and user name available, create user
				if(!errorInForm){
					this.dbInterface.addUser(username, password1, (lastName.isEmpty())?null:lastName, (firstName.isEmpty())?null:firstName);
					session.setAttribute(USERCREATION_FORM_MESSAGE, "Your account has been created");
				}
			} catch (Exception e){
				session.setAttribute(USERCREATION_FORM_MESSAGE, "Sorry, something went wrong. Web manager has been informed!");
				e.printStackTrace();
			}
		}
		//proceed and display the user creation page
		this.getServletContext().getRequestDispatcher("/UserCreation.jsp").forward(request, response);
	}
}