package ch.ethz.inf.dbproject;

import java.io.IOException;
import java.sql.Timestamp;
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
import ch.ethz.inf.dbproject.model.Conviction;
import ch.ethz.inf.dbproject.model.Person;
import ch.ethz.inf.dbproject.model.User;
import ch.ethz.inf.dbproject.util.UserManagement;
import ch.ethz.inf.dbproject.util.html.BeanTableHelper;
import ch.ethz.inf.dbproject.*;

@WebServlet(description = "Create a new Conviction", urlPatterns = { "/"+ConvictionCreationServlet.BASE_ADDRESS })
public class ConvictionCreationServlet extends HttpServlet {

	public static final String BASE_ADDRESS = "ConvictionCreation";
	
	public static final String INTERNAL_ACTION_PARAMETER = "action";
	public static final String INTERNAL_ACTION_CREATE_PARAMETER_VALUE = "create";
	public static final String INTERNAL_ACTION_SELECT_PERSON_PARAMETER_VALUE = "selPerson";
	
	public static final String INTERNAL_START_DATE_PARAMETER = "startDate";
	public static final String INTERNAL_END_DATE_PARAMETER = "endDate";
	public static final String INTERNAL_CASE_ID_PARAMETER = "caseId";
	
	public static final String SESSION_ERROR_MESSAGE = "error";
	public static final String SESSION_ERROR_MESSAGE_DATE_INVALID_FORMAT = "Invalid or incomplete dates.";
	public static final String SESSION_ERROR_MESSAGE_DATE_WRONG_ORDER = "The start date must be before the end date.";
	public static final String SESSION_ERROR_MESSAGE_INVALID_ID_FORMAT = "Invalid case or person id Format.";
	public static final String SESSION_ERROR_MESSAGE_INVALID_PERSON_ID_FORMAT = "Invalid person id Format.";
	public static final String SESSION_ERROR_MESSAGE_INVALID_ID_VALUE = "There is no case with such an id.";
	public static final String SESSION_ERROR_MESSAGE_PROCESSING_ERROR = "An internal error occurred. Please try again later.";
	
	public static final String SESSION_MESSAGE = "msg";
	public static final String SESSION_MESSAGE_CREATED = "Conviction successfully created";
	
	public static final String SESSION_SELECTED_PERSON = "CCSelP";
	
	private final DatastoreInterface dbInterface = new DatastoreInterface();
	private final SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
	
	/**
	 * provide a table of details person given a specific id
	 * @param Id the id of the person
	 * @return a table of person details, if no error occurs, null otherwise
	 */
	protected BeanTableHelper<Person> getPersonDetail(int Id)
	{
		BeanTableHelper<Person> table = new BeanTableHelper<Person>("person", "contentTable", Person.class);
		
		table.addBeanColumn("Last Name", "lastName");
		table.addBeanColumn("First Name", "firstName");
		table.addBeanColumn("Birth Date", "birthdate");
		Person person = this.dbInterface.getPersonForId(Id);
		
		if (person == null) {
			return null;
		}
		table.addObject(person);
		return table;
	}
	
	
	private void handleActionCreate(final HttpServletRequest request, final HttpServletResponse response)
	{
		final HttpSession session = request.getSession(true);
		
		String personId = request.getParameter(PersonSelectionServlet.EXTERNAL_RESULT_PERSON_ID_PARAMETER);
		String caseId = request.getParameter(INTERNAL_CASE_ID_PARAMETER);
		String startDate = request.getParameter(INTERNAL_START_DATE_PARAMETER);
		String endDate = request.getParameter(INTERNAL_END_DATE_PARAMETER);
		
		try {
			java.util.Date startDateParsed = dateFormatter.parse(startDate);
			java.util.Date endDateParsed = dateFormatter.parse(endDate);
			if (startDateParsed.after(endDateParsed)) {
				session.setAttribute(SESSION_ERROR_MESSAGE, SESSION_ERROR_MESSAGE_DATE_WRONG_ORDER);
				return;
			}
			int personIdParsed = Integer.parseInt(personId);
			Integer caseIdParsed = null;
			if (caseId != null && caseId.length() > 0) {//caseId is optional
				caseIdParsed = Integer.parseInt(caseId);
			}
			if (caseIdParsed == null || dbInterface.getCaseForId(caseIdParsed) != null) {
				Conviction conviction = dbInterface.insertIntoConviction(personIdParsed, caseIdParsed, startDateParsed, endDateParsed);
				if (conviction != null) {
					session.setAttribute(SESSION_MESSAGE, SESSION_MESSAGE_CREATED);
				} else {
					session.setAttribute(SESSION_ERROR_MESSAGE, SESSION_ERROR_MESSAGE_PROCESSING_ERROR);
				}
			} else {
				session.setAttribute(SESSION_ERROR_MESSAGE, SESSION_ERROR_MESSAGE_INVALID_ID_VALUE);
			}
			
		} catch (ParseException e) {
			session.setAttribute(SESSION_ERROR_MESSAGE, SESSION_ERROR_MESSAGE_DATE_INVALID_FORMAT);
		} catch (NumberFormatException e) {
			session.setAttribute(SESSION_ERROR_MESSAGE, SESSION_ERROR_MESSAGE_INVALID_ID_FORMAT);
		} catch (Exception e) {
			e.printStackTrace();
			session.setAttribute(SESSION_ERROR_MESSAGE, SESSION_ERROR_MESSAGE_PROCESSING_ERROR);
		}
	}
	
	private void handleActionSelectPerson(final HttpServletRequest request, final HttpServletResponse response)
	{
		final HttpSession session = request.getSession(true);
		
		String personString = request.getParameter(PersonSelectionServlet.EXTERNAL_RESULT_PERSON_ID_PARAMETER);
		try {
			int personId = Integer.parseInt(personString);
			session.setAttribute(SESSION_SELECTED_PERSON, getPersonDetail(personId));
		} catch (Exception e) {
			session.setAttribute(SESSION_ERROR_MESSAGE, SESSION_ERROR_MESSAGE_INVALID_PERSON_ID_FORMAT);
			session.setAttribute(SESSION_SELECTED_PERSON, null);
		}
	}
	
	protected final void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {

		final User loggedUser = UserManagement.getCurrentlyLoggedInUser(request.getSession());

		final String action = request.getParameter(INTERNAL_ACTION_PARAMETER);
		
		if (loggedUser != null) {
			if (INTERNAL_ACTION_CREATE_PARAMETER_VALUE.equals(action)) {//The user has already selected a person and dates: create the conviction and add to DB
				handleActionCreate(request, response);
				
			} else if (INTERNAL_ACTION_SELECT_PERSON_PARAMETER_VALUE.equals(action)){//The user has selected just the person. Construct the object displaying the person details
				handleActionSelectPerson(request, response);
			}
		}

		this.getServletContext().getRequestDispatcher("/ConvictionCreation.jsp").forward(request, response);
	}
	
}
