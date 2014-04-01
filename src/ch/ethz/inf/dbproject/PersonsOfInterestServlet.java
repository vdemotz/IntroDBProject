package ch.ethz.inf.dbproject;

import java.io.IOException;

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
import ch.ethz.inf.dbproject.util.html.BeanTableHelper;

@WebServlet(description = "Page that displays the details regarding a person", urlPatterns = { "/PersonsOfInterest" })
public final class PersonsOfInterestServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private final DatastoreInterface dbInterface = new DatastoreInterface();

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public PersonsOfInterestServlet() {
		super();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected final void doGet(final HttpServletRequest request,
			final HttpServletResponse response) throws ServletException,
			IOException {
		
		final HttpSession session = request.getSession(true);
		
		final String filter = request.getParameter("filter");
		
		final BeanTableHelper<Person> table = new BeanTableHelper<Person>("persons",
				"contentTable", Person.class);
		
		table.addBeanColumn("Person ID", "personId");
		table.addBeanColumn("Last Name", "lastName");
		table.addBeanColumn("First Name", "firstName");
		table.addLinkColumn("", "View Person", "Person?id=", "id");
		
		if (filter == null){
			
		} else if(filter.equals("convicted")){
			table.addObjects(this.dbInterface.getAllConvictedPersons());
		} else if(filter.equals("suspected")){
			table.addObjects(this.dbInterface.getAllSuspectedPersons());
		} else {
			System.err.println("Error :: Code should not be reachable. Filter equals to : "+filter);
		}
		
		session.setAttribute("resultsPersons", table);
		
		this.getServletContext().getRequestDispatcher("/PersonsOfInterest.jsp").forward(request, response);
	}
}
