package ch.ethz.inf.dbproject;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import ch.ethz.inf.dbproject.database.DatastoreInterface;
import ch.ethz.inf.dbproject.model.Person;
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
	 * provide a table of all persons convicted or suspected
	 * @param filter either convicted or suspected
	 * @return table of persons convicted or suspected
	 */
	protected BeanTableHelper<Person> getTablePersons(String filter){
		final BeanTableHelper<Person> table = new BeanTableHelper<Person>("persons",
				"contentTable", Person.class);
		table.addBeanColumn("Person ID", "personId");
		table.addBeanColumn("Last Name", "lastName");
		table.addBeanColumn("First Name", "firstName");
		table.addLinkColumn("", "View Person", "Person?id=", "id");
		
		if(filter.equals("convicted")){
			table.addObjects(this.dbInterface.getAllConvictedPersons());
		} else if (filter.equals("suspected")){
			table.addObjects(this.dbInterface.getAllSuspectedPersons());
		}	
		return table;
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected final void doGet(final HttpServletRequest request,
			final HttpServletResponse response) throws ServletException,
			IOException {
		
		//get if the user want suspected or convicted persons
		final String filter = request.getParameter("filter");
		if (filter != null){
			//set the attribute to the session if the filter is not null
			request.setAttribute("resultsPersons", this.getTablePersons(filter));
		} else {
			request.setAttribute("resultsPersons", "Press a filter");
		}
		//proceed and display the page
		this.getServletContext().getRequestDispatcher("/PersonsOfInterest.jsp").forward(request, response);
	}
}
