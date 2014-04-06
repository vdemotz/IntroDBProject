package ch.ethz.inf.dbproject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import ch.ethz.inf.dbproject.database.DatastoreInterface;
import ch.ethz.inf.dbproject.model.CaseDetail;
import ch.ethz.inf.dbproject.model.Person;
import ch.ethz.inf.dbproject.model.User;
import ch.ethz.inf.dbproject.util.UserManagement;

@WebServlet(description = "Page where user can create new persons", urlPatterns = { "/PersonCreation" })
public final class PersonCreationServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private final DatastoreInterface dbInterface = new DatastoreInterface();

	public final static String SESSION_USER_LOGGED_IN = "userLoggedIn";
	public final static String SESSION_USER_DETAILS = "userDetails";
	public final static String SESSION_FORM_CI = "caseIdEmpty";
	public final static String SESSION_FORM_LN = "lastNameEmpty";
	public final static String SESSION_FORM_FN = "firstNameEmpty";

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public PersonCreationServlet() {
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
			session.setAttribute(SESSION_FORM_LN, false);
			session.setAttribute(SESSION_FORM_FN, false);
		} else {
			// Logged in
			session.setAttribute(SESSION_USER_LOGGED_IN, true);
		}

		final String action = request.getParameter("action");
		if (action != null && action.trim().equals("creation") 	&& loggedUser != null) {

			final String caseId = request.getParameter("caseId");
			final String lastName = request.getParameter("lastName");
			final String firstName = request.getParameter("firstName");
			final String birthdate = request.getParameter("birthdate");
			final String typeOfPerson = request.getParameter("typeOfPerson");
			final String dateCrime = request.getParameter("dateCrime");
			boolean errorInForm = false;
			
			//check if all entries are valid
			if (lastName.isEmpty() || firstName.isEmpty()){
				errorInForm = true;
			} else if (!this.isValidDate(birthdate)){
				errorInForm = true;
			}
			
			if (!errorInForm){
				Person person = dbInterface.addPerson(firstName, lastName, birthdate);
				if (!caseId.isEmpty()){
					int ci = Integer.parseInt(caseId);
					CaseDetail aCase = dbInterface.getCaseForId(ci);
					if (aCase.getIsOpen()){
						dbInterface.setPersonSuspected(ci, person.getPersonId());
					}
				}
				
			}
		}

		this.getServletContext().getRequestDispatcher("/PersonCreation.jsp").forward(request, response);
	}
	
	private boolean isValidDate(String date){
	    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	    Date testDate = null;

	    try { testDate = sdf.parse(date); }
	    catch (ParseException e) { return false; }
	    
	    if (!sdf.format(testDate).equals(date))
	    { return false; }
	    return true;
	}
}