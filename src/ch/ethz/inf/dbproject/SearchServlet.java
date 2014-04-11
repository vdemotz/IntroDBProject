package ch.ethz.inf.dbproject;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.sun.tools.javac.util.Pair;

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
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	
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
    private Object getTablePersons(String filter, HttpServletRequest request) throws Exception{
    	
    	final BeanTableHelper<Person> table = new BeanTableHelper<Person>("persons",
				"contentTable", Person.class);
		
		table.addBeanColumn("Person ID", "personId");
		table.addBeanColumn("Last Name", "lastName");
		table.addBeanColumn("First Name", "firstName");
		table.addLinkColumn("", "View Person", "Person?id=", "id");
		final String lastName = request.getParameter("lastName");
		final String firstName = request.getParameter("firstName");
		final String description = request.getParameter("description");
		final Pair<String, String> date = this.getValidDate(request.getParameter("startDate"), request.getParameter("endDate"));
		
		//depending on the filter, get and set right table
		if (filter.equals("namePerson")) {
			table.addObjects(this.dbInterface.getPersonsForName(firstName, lastName));
		} else if (filter.equals("convictionType")) {	
			table.addObjects(this.dbInterface.getPersonsForConvictionType(description));
		} else if (filter.equals("convictionDate")) {
			if (date.snd != null){
				table.addObjects(this.dbInterface.getPersonsForConvictionDates(date.fst, date.snd));
			} else if (date.fst != null){
				table.addObjects(this.dbInterface.getPersonsForConvictionDate(date.fst+"%"));
			} else {
				return "Sorry, not valid dates. Please make sur that you entered the date on the form : yyyy-mm-dd and the second date is after first one";
			}
		} else if (filter.equals("birthdate")) {	
			if (date.snd != null){
				table.addObjects(this.dbInterface.getPersonsForBirthdates(date.fst, date.snd));
			} else if (date.fst != null){
				table.addObjects(this.dbInterface.getPersonsForBirthdate(date.fst+"%"));
			} else {
				return "Sorry, not valid dates. Please make sur that you entered the date on the form : yyyy-mm-dd and the second date is after first one";
			}
		}
		return table;
    }
    
    /**
     * provide a table of CaseDetail for a specific request
     * @param request a request with valid description field.
     * @return table of CaseDetail
     */
    private Object getTableCases(String filter, HttpServletRequest request) throws Exception{
    	
		final BeanTableHelper<CaseDetail> table = new BeanTableHelper<CaseDetail>("cases",
				"contentTable", CaseDetail.class);

		final String description = request.getParameter("description");
		final Pair<String, String> date = this.getValidDate(request.getParameter("startDate"), request.getParameter("endDate"));

		
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
			if (date.snd != null){
				table.addObjects(this.dbInterface.getCasesForDates(date.fst, date.snd));
			} else if (date.fst != null){
				table.addObjects(this.dbInterface.getCasesForDateLike(date.fst+"%"));
			} else {
				return "Sorry, not valid dates. Please make sur that you entered the date on the form : yyyy-mm-dd and the second date is after first one";
			}
		}
		return table;
    }
    
	/**
	 * this method provide a Pair<String, String> useful to search in DB with different case:
	 * case 1 : startDate empty, return Pair<null, null>
	 * case 2 : startDate empty, return Pair<startDate, null>
	 * case 3 : neither are empty, return Pair<startDate, endDate> if these are valid dates, else Pair<null, null>
	 */
	private Pair<String, String> getValidDate(String startDate, String endDate){
		String sd;
		String ed;
		if (startDate == null || endDate == null) {
			return null;
		}
		if (startDate.isEmpty()){
			ed = null; sd = null;
		} else if (endDate.isEmpty()){
			ed = null;
			sd = startDate;
		} else {
			try {
				java.util.Date startDateParsed = sdf.parse(startDate);
				java.util.Date endDateParsed = sdf.parse(endDate);
				if (startDateParsed.after(endDateParsed)) {
					return new Pair<String, String>(null, null);
				}
				sd = new Timestamp(startDateParsed.getTime()).toString().substring(0,10);
				ed = new Timestamp(endDateParsed.getTime()).toString().substring(0,10);
			} catch (Exception ex){
				sd = null; ed = null;
			}
		}
		return new Pair<String, String>(sd, ed);
	}
}