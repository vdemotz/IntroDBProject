package ch.ethz.inf.dbproject;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import ch.ethz.inf.dbproject.model.*;
import ch.ethz.inf.dbproject.util.html.BeanTableHelper;

/**
 * Servlet implementation class Search
 */
@WebServlet(description = "The search page for cases", urlPatterns = { "/Search" })
public final class SearchServlet extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
		
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
		
		final String filter = request.getParameter("filter");

		if (filter != null) {
		
			if(filter.equals("firstName") || filter.equals("lastName") || filter.equals("convictionType") || filter.equals("convictionDate")) {
				
				final BeanTableHelper<Person> table = new BeanTableHelper<Person>("persons",
						"personsTable", Person.class);
				
				table.addBeanColumn("Person ID", "personId");
				table.addBeanColumn("Last Name", "lastName");
				table.addBeanColumn("First Name", "firstName");
				table.addLinkColumn("", "View Person", "Person?id=", "id");

			} else{
				final BeanTableHelper<CaseDetail> table = new BeanTableHelper<CaseDetail>("cases",
						"casesTable", CaseDetail.class);

				table.addBeanColumn("Case ID", "caseId");
				table.addBeanColumn("Title", "title");
				table.addBeanColumn("Location", "location");
				table.addBeanColumn("Open", "isOpen");
				table.addBeanColumn("Date", "date");
				table.addBeanColumn("Description", "description");
				table.addBeanColumn("Author Name", "authorName");

				table.addLinkColumn("", "View Case", "Case?id=", "id");
			}
			
			
			if(filter.equals("firstName")){
				
			} else if (filter.equals("lastName")) {

			} else if (filter.equals("convictionType")) {	
	
			} else if (filter.equals("convictionDate")) {	

			} else if (filter.equals("category")) {	

			} else if (filter.equals("caseDate")) {	

			} else {
				
			}
				
		}

		// Finally, proceed to the Search.jsp page which will render the search results
        this.getServletContext().getRequestDispatcher("/Search.jsp").forward(request, response);	        
	}
}