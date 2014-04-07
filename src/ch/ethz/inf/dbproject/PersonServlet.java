package ch.ethz.inf.dbproject;

import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import ch.ethz.inf.dbproject.database.DatastoreInterface;
import ch.ethz.inf.dbproject.model.*;
import ch.ethz.inf.dbproject.util.html.BeanTableHelper;

/**
 * Servlet implementation class of Case Details Page
 */
@WebServlet(description = "Displays a specific case.", urlPatterns = { "/Person" })
public final class PersonServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private final DatastoreInterface dbInterface = new DatastoreInterface();

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public PersonServlet() {
		super();
	}
	
	/**
	 * provide a table of details person given a specific id
	 * @param Id the id of the person
	 * @return a table of person details
	 */
	protected BeanTableHelper<Person> tablePersonDetail(int Id)
	{
		BeanTableHelper<Person> table = new BeanTableHelper<Person>("person", "contentTable", Person.class);
		
		table.addBeanColumn("Last Name", "lastName");
		table.addBeanColumn("First Name", "firstName");
		table.addBeanColumn("Birth Date", "birthdate");
		Person person = this.dbInterface.getPersonForId(Id);
		
		if (person == null) {
			System.err.println("Returned person null, caseId = "+Id);
		}
		table.addObject(person);
		
		return table;
	}
	
	/**
	 * provide a table of cases for which a specific person is convicted
	 * @param Id the id of the person
	 * @return a table of cases
	 */
	protected BeanTableHelper<CaseDetail> tableConvicted(int Id)
	{
		BeanTableHelper<CaseDetail> table = new BeanTableHelper<CaseDetail>("cases", "contentTable", CaseDetail.class);
		
		table.addBeanColumn("Cases", "title");
		table.addLinkColumn("", "View Case", "Case?caseId=", "id");
		List<CaseDetail> casesSuspected = this.dbInterface.getCasesForWhichPersonIsConvicted(Id);
		table.addObjects(casesSuspected);
		return table;
	}
	
	/**
	 * provide a table of cases for which a specific person is suspected
	 * @param Id the id of the person
	 * @return a table of cases
	 */
	protected BeanTableHelper<CaseDetail> tableSuspected(int Id)
	{
		BeanTableHelper<CaseDetail> table = new BeanTableHelper<CaseDetail>("cases", "contentTable", CaseDetail.class);
		
		table.addBeanColumn("Cases", "title");
		table.addLinkColumn("", "View Case", "Case?caseId=", "id");
		List<CaseDetail> casesSuspected = this.dbInterface.getCasesForWhichPersonIsSuspected(Id);
		table.addObjects(casesSuspected);
		return table;
	}
	
	/**
	 * provide a table of notes on a specific person
	 * @param Id the id of the person
	 * @return a table of notes on a person
	 */
	protected BeanTableHelper<PersonNote> tablePersonNotes(int Id)
	{
		BeanTableHelper<PersonNote> table = new BeanTableHelper<PersonNote>("personNotes", "contentTable", PersonNote.class);
		
		table.addBeanColumn("Author", "authorUsername");
		table.addBeanColumn("Date", "date");
		table.addBeanColumn("Comment", "text");

		List<PersonNote> personNotes = this.dbInterface.getPersonNotesForPerson(Id);
		table.addObjects(personNotes);
		return table;
	}
	

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected final void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {

		//get the entries of the user
		final HttpSession session = request.getSession(true);
		final String action = request.getParameter("action");
		final String userId = request.getParameter("user_id");
		final String comment = request.getParameter("comment");
		final String idString = request.getParameter("id");
		
		if (action != null){
			//if the user wants to add a personNote, add it to DB
			PersonNote pn = dbInterface.addPersonNote(Integer.parseInt(idString), comment, userId);
			if (pn == null){
				//it the query return null, it means the person note failed to be added
				System.err.println("Failed to add PersonNote");
			}
		}
		
		try {
			//try to display the person asked
			final Integer id = Integer.parseInt(idString);
			
			
			if (this.dbInterface.getPersonForId(id) == null){
				session.setAttribute(HomeServlet.SESSION_ERROR_MESSAGE, "Person doesn't exist");
			} else {
				//set the caseId
				session.setAttribute("personId", id);
				//set the PersonDetail (the header) wanted by the user
				session.setAttribute("personDetailsTable", tablePersonDetail(id));
				//set the Cases where the person is convicted
				session.setAttribute("casesPersonConvicted", tableConvicted(id));
				//set the Cases where the person is suspected
				session.setAttribute("casesPersonSuspected", tableSuspected(id));
				//set the Person Notes
				session.setAttribute("personNotesTable", tablePersonNotes(id));
				//if we reaches this point, we are able to display the PersonDetails	
				session.setAttribute(HomeServlet.SESSION_ERROR_MESSAGE, null);
			}				
		} catch (final Exception ex) {
			session.setAttribute(HomeServlet.SESSION_ERROR_MESSAGE, "Invalid Person id or Person is unreachable");
		}
		//proceed and display the page
		this.getServletContext().getRequestDispatcher("/Person.jsp").forward(request, response);
	}
}