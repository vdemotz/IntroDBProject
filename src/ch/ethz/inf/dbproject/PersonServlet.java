package ch.ethz.inf.dbproject;

import java.io.IOException;
import java.util.List;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import ch.ethz.inf.dbproject.database.DatastoreInterface;
import ch.ethz.inf.dbproject.model.*;
import ch.ethz.inf.dbproject.util.UserManagement;
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
	
	
	protected BeanTableHelper<Person> tablePersonDetail(int Id)
	{
		BeanTableHelper<Person> table = new BeanTableHelper<Person>("person", "personTable", Person.class);
		
		table.addBeanColumn("Last Name", "lastName");
		table.addBeanColumn("First Name", "firstName");
		table.addBeanColumn("Birth Date", "birthdate");
		Person person = this.dbInterface.getPersonForId(Id);
		
		if (person == null) {
			System.err.println("Returned person null, id = "+Id);
		}
		table.addObject(person);
		
		return table;
	}
	
	protected BeanTableHelper<CaseDetail> tableConvicted(int Id)
	{
		BeanTableHelper<CaseDetail> table = new BeanTableHelper<CaseDetail>("cases", "casesTable", CaseDetail.class);
		
		table.addBeanColumn("Cases", "title");
		table.addLinkColumn("", "View Case", "Case?id=", "id");
		List<CaseDetail> casesSuspected = this.dbInterface.getCasesForWhichPersonIsConvicted(Id);
		table.addObjects(casesSuspected);
		return table;
	}
	
	protected BeanTableHelper<CaseDetail> tableSuspected(int Id)
	{
		BeanTableHelper<CaseDetail> table = new BeanTableHelper<CaseDetail>("cases", "casesTable", CaseDetail.class);
		
		table.addBeanColumn("Cases", "title");
		table.addLinkColumn("", "View Case", "Case?id=", "id");
		List<CaseDetail> casesSuspected = this.dbInterface.getCasesForWhichPersonIsSuspected(Id);
		table.addObjects(casesSuspected);
		return table;
	}
	
	protected BeanTableHelper<PersonNote> tablePersonNotes(int Id)
	{
		BeanTableHelper<PersonNote> table = new BeanTableHelper<PersonNote>("personNotes", "personNotesTable", PersonNote.class);
		
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

		final HttpSession session = request.getSession(true);
		final User loggedUser = UserManagement.getCurrentlyLoggedInUser(session);

		final String action = request.getParameter("action");
		final String userId = request.getParameter("user_id");
		final String comment = request.getParameter("comment");
		final String idString = request.getParameter("id");
		
		if (action != null){
			PersonNote pn = dbInterface.addPersonNote(Integer.parseInt(idString), comment, userId);
		}
		
		if (idString == null) {
			this.getServletContext().getRequestDispatcher("/PersonsOfInterest.jsp").forward(request, response);
		} else {
			try {
				final Integer id = Integer.parseInt(idString);
				
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
				
				
								
			} catch (final Exception ex) {
				System.err.println("not able to display the person wanted");
				ex.printStackTrace();
				this.getServletContext().getRequestDispatcher("/PersonsOfInterest.jsp").forward(request, response);
			}
			
			this.getServletContext().getRequestDispatcher("/Person.jsp").forward(request, response);
		}
	}
}