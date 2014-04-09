package ch.ethz.inf.dbproject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import ch.ethz.inf.dbproject.database.DatastoreInterface;
import ch.ethz.inf.dbproject.model.*;

import ch.ethz.inf.dbproject.model.CaseDetail;
import ch.ethz.inf.dbproject.util.html.BeanTableHelper;

@WebServlet(description = "Selects an existing person or a new person and returns its id", urlPatterns = { "/PersonSelection" })
public final class PersonSelectionServlet extends HttpServlet {

	public static final String EXTERNAL_TITLE_PARAMETER = "PStitle";
	public static final String EXTERNAL_RETURN_ADDRESS_PARAMETER = "PSreturn";
	public static final String EXTERNAL_RESULT_PERSON_ID_PARAMETER = "PSpid";
	
	public static final String BASE_ADDRESS = "PersonSelection";
	public static final String INTERNAL_SEARCH_ACTION = "search";
	public static final String INTERNAL_SEARCH_PERSON_ID = "sepid";
	public static final String INTERNAL_SEARCH_FIRSTNAME = "fnm";
	public static final String INTERNAL_SEARCH_LASTNAME = "lnm";
	public static final String INTERNAL_SEARCH_RESULT_TABLE = "rst";
	
	private final DatastoreInterface dbInterface = new DatastoreInterface();
	
	protected final void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
		
		final HttpSession session = request.getSession(true);
		final String action = request.getParameter("action");
		
		if (INTERNAL_SEARCH_ACTION.equals(action)) {
			
			BeanTableHelper<Person> resultTable = new BeanTableHelper<Person>("persons", "contentTable", Person.class);
			
			resultTable.addBeanColumn("Id", "personId");
			resultTable.addBeanColumn("First Name", "firstName");
			resultTable.addBeanColumn("Last Name", "lastName");
			resultTable.addLinkColumn("", "Select", request.getParameter(EXTERNAL_RETURN_ADDRESS_PARAMETER) + "&" + EXTERNAL_RESULT_PERSON_ID_PARAMETER + "=", "personId");
			
			String idString = request.getParameter(INTERNAL_SEARCH_PERSON_ID);
			try {
				int id = Integer.parseInt(idString);//try to search by id
				Person result = dbInterface.getPersonForId(id);
				if (result == null) {
					resultTable = null; 
				} else {
					resultTable.addObject(result);
				}
			} catch (Exception ex) {//search by name 
				List<Person> result = dbInterface.getPersonsForName(request.getParameter(INTERNAL_SEARCH_FIRSTNAME),  request.getParameter(INTERNAL_SEARCH_LASTNAME));
				
				if (result == null) {
					resultTable = null;
				} else {
					resultTable.addObjects(result);
				}
			}
			
			session.setAttribute(INTERNAL_SEARCH_RESULT_TABLE, resultTable);
			
			
		}
		
		this.getServletContext().getRequestDispatcher("/PersonSelection.jsp").forward(request, response);
	
	}
	

}
