package ch.ethz.inf.dbproject;

import java.io.IOException;
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
 * Servlet implementation class Search
 */
@WebServlet(description = "The search page for cases", urlPatterns = { "/Search" })
public final class SearchServlet extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	private final DatastoreInterface dbInterface = new DatastoreInterface();
	
	public static String SEARCH_RESULTS = "searchResults";
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SearchServlet() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		final HttpSession session = request.getSession(true);
		//get the type of the search
		final String filter = request.getParameter("filter");

		if (filter != null) {
			//if the user entered something, search the database
			try{
				session.setAttribute(SEARCH_RESULTS, this.getResults(filter, request));
			} catch ( final Exception ex ){
				session.setAttribute(SEARCH_RESULTS, "System failed to search, sorry");
			}	
		} else {
			//if user didn't already asked something
			session.setAttribute(SEARCH_RESULTS, "No result to display");
		}
		// Finally, proceed to the Search.jsp page which will render the search results
        this.getServletContext().getRequestDispatcher("/Search.jsp").forward(request, response);	        
	}
	
    /**
     * provide the result of the search in database, regarding the filter
     * @return a table (either CaseDetail or Person) if filter is valid, else String
     */
    private Object getResults(String filter, HttpServletRequest request) throws Exception{
    	
    	if(filter.equals("namePerson") || filter.equals("convictionType") || filter.equals("convictionDate")) {
			//if the return type is a person, return a person table
    		return this.getTablePersons(filter, request);
		} else if(filter.equals("category") || filter.equals("caseDate")){
			//if the return type is a case, return a case table without description
			return this.getTableCases(filter, request);
		} else {
			return "Wrong filter. How did you get that one?";
		}
    }
    
    /**
     * provide a table of persons
     * @param request a specific request with a valid description, lastName and firstName fields
     * @return a table of Person
     */
    private BeanTableHelper<Person> getTablePersons(String filter, HttpServletRequest request) throws Exception{
    	
    	final BeanTableHelper<Person> table = new BeanTableHelper<Person>("persons",
				"contentTable", Person.class);
		
		table.addBeanColumn("Person ID", "personId");
		table.addBeanColumn("Last Name", "lastName");
		table.addBeanColumn("First Name", "firstName");
		table.addLinkColumn("", "View Person", "Person?id=", "id");
		final String lastName = request.getParameter("lastName");
		final String firstName = request.getParameter("firstName");
		final String description = request.getParameter("description");
		
		//depending on the filter, get and set right table
		if (filter.equals("namePerson")) {
			table.addObjects(this.dbInterface.getPersonsForName(firstName, lastName));
		} else if (filter.equals("convictionType")) {	
			table.addObjects(this.dbInterface.getPersonsForConvictionType(description));
		} else if (filter.equals("convictionDate")) {	
			table.addObjects(this.dbInterface.getPersonsForConvictionDate(description));
		}
		return table;
    }
    
    /**
     * provide a table of CaseDetail for a specific request
     * @param request a request with valid description field.
     * @return table of CaseDetail
     */
    private BeanTableHelper<CaseDetail> getTableCases(String filter, HttpServletRequest request) throws Exception{
    	
		final BeanTableHelper<CaseDetail> table = new BeanTableHelper<CaseDetail>("cases",
				"contentTable", CaseDetail.class);

		final String description = request.getParameter("description");
		
		table.addBeanColumn("Case ID", "caseId");
		table.addBeanColumn("Title", "title");
		table.addBeanColumn("Location", "location");
		table.addBeanColumn("Open", "isOpen");
		table.addBeanColumn("Date", "dateTimeFormated");
		//table.addBeanColumn("Description", "description");
		table.addBeanColumn("Author Name", "authorName");
		table.addLinkColumn("", "View Case", "Case?caseId=", "id");
		
		//depending on the filter, get and set right table
		if (filter.equals("category")) {
			table.addObjects(this.dbInterface.getCasesForCategory(description));
		} else if (filter.equals("caseDate")) {
			table.addObjects(this.dbInterface.getCasesForDateLike(description));
		}
		return table;
    }
}