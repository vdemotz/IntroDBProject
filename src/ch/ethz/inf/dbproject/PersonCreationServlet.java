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

	public final static String PERSON_FORM_CI = "personCaseIdEmpty";
	public final static String PERSON_FORM_LN = "personLastNameEmpty";
	public final static String PERSON_FORM_FN = "personFirstNameEmpty";
	public final static String PERSON_CREATION_MESSAGE = "personCreationMessage";

	private final static String UI_PERSON_CREATED = "Person added";
	
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
			session.setAttribute(PERSON_FORM_LN, false);
			session.setAttribute(PERSON_FORM_FN, false);
		} else {
			session.setAttribute(PERSON_CREATION_MESSAGE, "");
		}

		final String action = request.getParameter("action");
		if (action != null && action.trim().equals("creation") 	&& loggedUser != null) {

			//get all entries the user entered
			final String caseId = request.getParameter("caseId");
			final String lastName = request.getParameter("lastName");
			final String firstName = request.getParameter("firstName");
			final String birthdate = request.getParameter("birthdate");
			boolean errorInForm = false;
			
			try{
				//check if all entries are valid
				if (lastName.isEmpty() || firstName.isEmpty()){
					errorInForm = true;
				} else if (!this.isValidDate(birthdate)){
					errorInForm = true;
				}
				
				if (!errorInForm){
					//if mandatory entries are filled, create a new person
					Person person = dbInterface.addPerson(firstName, lastName, birthdate);
					if (person != null) {
						if (!caseId.isEmpty()){
							trySetPersonSuspectedOnCaseWithId(person, caseId);
						}
						addCreationNoteToPerson(person, loggedUser);
						session.setAttribute(PERSON_CREATION_MESSAGE, "The person has been created with : " +
								"first name : "+firstName+
								" and last name : "+lastName);
					} else {
						//TODO
					}
				} else {
					session.setAttribute(PERSON_CREATION_MESSAGE, "Error in one of the entries, please enter " +
							"a valid first name, last name and birthdate");
				}
			} catch (final Exception ex){
				ex.printStackTrace();
			}
		}

		//proceed the page
		this.getServletContext().getRequestDispatcher("/PersonCreation.jsp").forward(request, response);
	}
	
	private void addCreationNoteToPerson(Person person, User user)
	{
		dbInterface.addPersonNote(person.getId(), UI_PERSON_CREATED, user.getUsername());
	}
	
	private void trySetPersonSuspectedOnCaseWithId(Person person, String caseId) throws NumberFormatException
	{
		//if optional (caseId) entry is filled, get the case
		int ci = Integer.parseInt(caseId);
		CaseDetail aCase = dbInterface.getCaseForId(ci);
		if (aCase != null && aCase.getIsOpen()){
			//if user entered a valid, opened case, set the person as suspected person for this case
			dbInterface.setPersonSuspected(ci, person.getPersonId());
		}
	}
	
	/**
	 * test if the date is of the format yyyy-mm-dd
	 * @param date a date to test
	 * @return true if the date has format yyyy-mm-dd, else false
	 */
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