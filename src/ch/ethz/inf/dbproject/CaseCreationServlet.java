package ch.ethz.inf.dbproject;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import ch.ethz.inf.dbproject.database.DatastoreInterface;
import ch.ethz.inf.dbproject.model.CaseDetail;
import ch.ethz.inf.dbproject.model.Category;
import ch.ethz.inf.dbproject.model.User;
import ch.ethz.inf.dbproject.util.UserManagement;

@WebServlet(description = "Page where user can create new cases", urlPatterns = { "/CaseCreation" })
public final class CaseCreationServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private final DatastoreInterface dbInterface = new DatastoreInterface();
	
	public static final String CASECREATION_LIST_CAT = "caseCreationListCategories";
	public static final String CASECREATION_MESSAGE = "caseCreationMessage";
	
	private static final String UI_CASE_CREATION_MESSAGE = "Case created";
	
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public CaseCreationServlet() {
		super();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected final void doGet(final HttpServletRequest request,
			final HttpServletResponse response) throws ServletException,
			IOException {

		final HttpSession session = request.getSession(true);
		final User loggedUser = UserManagement.getCurrentlyLoggedInUser(session);

		final String action = request.getParameter("action");
		
		List<Category> catSum = dbInterface.getAllCategories();
		if (catSum == null){
			catSum = new ArrayList<Category>();
			catSum.add(new Category("Other"));
		}
		request.setAttribute(CASECREATION_LIST_CAT, catSum);
		request.setAttribute(CASECREATION_MESSAGE, "");
		
		if (action != null && action.trim().equals("creation") 	&& loggedUser != null) {
			//create a new case with the description given by the user. The date is set to today
			//the case is by default open
			try{
				CaseDetail caseDetail = null;
				//get all parameters
				final String title = request.getParameter("title");
				final String city = request.getParameter("city");
				final String street = request.getParameter("street");
				final String zipCode = request.getParameter("zipCode");
				final String description = request.getParameter("description");
				final String dateString = request.getParameter("date");
				//get the list of categories the case belongs
				final String[] categories = request.getParameterValues("categories");
				//date and username
				String authorUsername = loggedUser.getUsername();
				Timestamp timestamp = this.getValidDate(dateString);
				
				if (title != null && !title.isEmpty()){
					//try to create the case
					caseDetail = dbInterface.insertIntoCaseDetail(title, (city.isEmpty()) ? null :city, (zipCode.isEmpty()) ? null : zipCode, (street.isEmpty()) ? null : street, timestamp, description, authorUsername);
				} 
				if (caseDetail == null){
					//if caseDetail is null, then there's a failure in the procedure
					request.setAttribute(CASECREATION_MESSAGE, "Please verifiy your entries. Is there anything missing?");
				} else {
					if (categories != null){
						System.err.println("Categories not null");
						for (int i = 0; i < categories.length; i++){
							dbInterface.insertIntoCategoryForCase(categories[i], caseDetail.getCaseId());
						}
					}
					addCreationNoteToCase(caseDetail, loggedUser);
					request.setAttribute(CASECREATION_MESSAGE, "Your case has been added.");
				}

			} catch (Exception ex){
				request.setAttribute(CASECREATION_MESSAGE, "Sorry, failed to add your case");
				ex.printStackTrace();
			}
		}

		this.getServletContext().getRequestDispatcher("/CaseCreation.jsp").forward(request, response);
	}
	
	private void addCreationNoteToCase(CaseDetail caseDetail, User user)
	{
		dbInterface.insertIntoCaseNote(caseDetail.getCaseId(), UI_CASE_CREATION_MESSAGE, user.getUsername());
	}
	
	/**
	 * test if the date is of the format yyyy-mm-dd
	 * @param date a date to test
	 * @return true if the date has format yyyy-mm-dd, else false
	 */
	private Timestamp getValidDate(String date){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		try{
			java.util.Date DateParsed = sdf.parse(date);
			return new Timestamp(DateParsed.getTime());
		} catch (Exception ex){
			return new Timestamp(new Date().getTime());
		}
	}
}