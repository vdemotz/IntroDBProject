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
		//get the type of the search and parameter
		final String filter = request.getParameter("filter");
		final String description = request.getParameter("description");
		
		if (filter != null) {
			if(filter.equals("namePerson") || filter.equals("convictionType") || filter.equals("convictionDate")) {
				//if the return type is a person, create a person table
				final BeanTableHelper<Person> table = new BeanTableHelper<Person>("persons",
						"contentTable", Person.class);
				
				table.addBeanColumn("Person ID", "personId");
				table.addBeanColumn("Last Name", "lastName");
				table.addBeanColumn("First Name", "firstName");
				table.addLinkColumn("", "View Person", "Person?id=", "id");
				
				//depending on the filter, get and set right table
				if (filter.equals("namePerson")) {
					final String lastName = request.getParameter("lastName");
					final String firstName = request.getParameter("firstName");
					table.addObjects(this.dbInterface.getPersonsForName(firstName+"%", lastName+"%"));
				} else if (filter.equals("convictionType")) {	
					table.addObjects(this.dbInterface.getPersonsForConvictionType(description));
				} else if (filter.equals("convictionDate")) {	
					table.addObjects(this.dbInterface.getPersonsForConvictionDate(description));
				}
				session.setAttribute("results", table);

			} else if(filter.equals("category") || filter.equals("caseDate")){
				//if the return type is a case, create a case table without description
				final BeanTableHelper<CaseDetail> table = new BeanTableHelper<CaseDetail>("cases",
						"contentTable", CaseDetail.class);

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
				
				session.setAttribute("results", table);
				
			} else {
				System.err.println("Error :: Code should not be reachable. Filter equals to :"+filter);
				final BeanTableHelper<CaseDetail> table = null;
				session.setAttribute("results", table);
			}	
		}

		// Finally, proceed to the Search.jsp page which will render the search results
        this.getServletContext().getRequestDispatcher("/Search.jsp").forward(request, response);	        
	}
}