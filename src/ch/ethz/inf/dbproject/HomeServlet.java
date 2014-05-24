package ch.ethz.inf.dbproject;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import ch.ethz.inf.dbproject.database.DatastoreInterface;
import ch.ethz.inf.dbproject.model.User;
import ch.ethz.inf.dbproject.util.UserManagement;
import ch.ethz.inf.dbproject.util.html.BeanTableHelper;
import ch.ethz.inf.dbproject.model.*;

/**
 * Servlet implementation class HomePage
 */
@WebServlet(description = "The home page of the project", urlPatterns = { "/Home" })
public final class HomeServlet extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	private final DatastoreInterface dbInterface = new DatastoreInterface();
	
	//attributes specific to Session
	public final static String REQUEST_USER_DETAILS = "userDetails";
	public final static String REQUEST_USER_CASES = "casesByUser";
	public final static String REQUEST_ERROR_MESSAGE = "error";
	
	//attributes specific to HomeServlet
	public final static String HOME_MESSAGE = "homeMessage";
	public static final String HOME_ADD_CATEGORY_MESSAGE = "homeAddCategory";
	public static final String HOME_SUMMARY_CAT_TABLE = "homeSummaryCatTable";
	public static final String HOME_MOST_ACTIVE_USER = "homeMostActiveUser";
	public static final String HOME_MOST_ACTIVE_CAT_FOR_USER = "homeMostActiveCatForUser";
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public HomeServlet() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		//get session and action
		final HttpSession session = request.getSession(true);
		final String action = request.getParameter("action");
		
		//default messages
		request.setAttribute(HOME_MESSAGE, "");
		request.setAttribute(HOME_ADD_CATEGORY_MESSAGE, "");
		
		//handle action if any
		if (action != null){
			this.handleAction(request, session, action);
		}
		
		//get yet the user (if logged in, he's added after ... Well... login)
		final User loggedUser = UserManagement.getCurrentlyLoggedInUser(session);
		
		//set categories
		request.setAttribute(HOME_SUMMARY_CAT_TABLE, this.getCategorySummaryTable());
		
		//set other attributes depending on if the user is logged in or not
		if (loggedUser == null) {
			// Not logged in!
			//set the most active users
			//TODO DISABLED
			//request.setAttribute(HOME_MOST_ACTIVE_USER, this.getMostActiveUser());
		} else {
			// Logged in
			//set the details of the user
			request.setAttribute(REQUEST_USER_DETAILS, getTableUserDetails(loggedUser));
			//set the table of cases modified by user
			//TODO DISABLED
			//request.setAttribute(REQUEST_USER_CASES, getTableCasesUserModified(loggedUser.getUsername()));
			//set table most active cases by user
			//TODO DISABLED
			//request.setAttribute(HOME_MOST_ACTIVE_CAT_FOR_USER, this.getMostActiveCategoriesForUser(loggedUser.getUsername()));
		}
		
		// Finally, proceed to the Home.jsp page which will render the profile
		this.getServletContext().getRequestDispatcher("/Home.jsp").forward(request, response);
	}
	
	/**
	 * Handle any type of action
	 * @param request of the DoGet
	 * @param session of the DoGet
	 * @param action of the DoGet
	 */
	private void handleAction(HttpServletRequest request, HttpSession session, String action){
		try {
			if (action.trim().equals("login")) {
				//if the user try to login, ask the database if user is registered
				
				final String username = request.getParameter("username");
				final String password = request.getParameter("password");
				User user = dbInterface.getUserForUsernameAndPassword(username, password);
				
				if (user != null) {
					//if the database return an user, it means the user is registered
					//then, set session attributes to remember the user between requests
					session.setAttribute(UserManagement.SHARED_SESSION_USER, user);
				} else {
					//if the database return null, then set request to display 'wrong password'
					request.setAttribute(HOME_MESSAGE, "Sorry, wrong password / username");
				}
				
			} else if(action.trim().equals("logout")){
				//if user wants to logout, remove user from session
				session.setAttribute(UserManagement.SHARED_SESSION_USER, null);
			} else if (action.trim().equals("categoryCreation") && UserManagement.getCurrentlyLoggedInUser(session) != null){
				//if the user wants to add a category (and he's logged in!), add a category
				request.setAttribute(HOME_ADD_CATEGORY_MESSAGE, this.addNewCategory(request));
			} else {
				request.setAttribute(HOME_MESSAGE, "Action not handled, sorry");
			}
		} catch (Exception ex) {
			request.setAttribute(HOME_MESSAGE, "Sorry, something went wrong. Administrators have been alerted");
			ex.printStackTrace();
		}
	}
	
    /**
     * Provide a table of user details
     * @param usr a user to display details
     * @return a table of user details
     */
	protected BeanTableHelper<User> getTableUserDetails(User usr) {
		final BeanTableHelper<User> userDetails = new BeanTableHelper<User>("userDetails", "contentTable", User.class);
		userDetails.addBeanColumn("Username", "username");
		userDetails.addBeanColumn("First Name", "firstName");
		userDetails.addBeanColumn("Last Name", "lastName");
			
		userDetails.addObject(usr);
		return userDetails;
	}
	
	/**
	 * Provide a table of all cases open by a user
	 * @param username
	 * @return a table of cases
	 */
	protected BeanTableHelper<CaseDetail> getTableCasesUserModified(String username) {
		final BeanTableHelper<CaseDetail> casesUserModified = new BeanTableHelper<CaseDetail>("cases", "contentTable", CaseDetail.class);
		// Add columns to the new table
		casesUserModified.addBeanColumn("Case ID", "caseId");
		casesUserModified.addBeanColumn("Title", "title");
		casesUserModified.addBeanColumn("Location", "location");
		casesUserModified.addBeanColumn("Open", "isOpen");
		casesUserModified.addBeanColumn("Date", "dateTimeFormated");
		casesUserModified.addBeanColumn("Author Name", "authorName");
			
		casesUserModified.addLinkColumn("", "View Case", "Case?caseId=", "id");

		casesUserModified.addObjects(this.dbInterface.getCurrentCasesForUser(username));
		return casesUserModified;
	}
	
	/**
	 * Handle the action 'categoryCreation' and add, if not already exists a new category in DB
	 * @param request the action
	 * @return a message which says if category has been added or not
	 */
	private String addNewCategory(HttpServletRequest request){
		
		final String action = request.getParameter("action");
		final String description = request.getParameter("description");
		String ret;
		
		if (action != null && action.equals("categoryCreation")){
			try{
				if(this.dbInterface.insertIntoCategory(description)){
					ret = "Your category has been added";
				}
				else{
					ret = "Sorry, this category already exists.";
				}
			} catch(Exception ex) {
				ex.printStackTrace();
				ret = "Sorry, failed to add your category. Please contact us and we will do our best"+
				"to fix the problem.";
			}
		} else {
			ret = "";
		}
		return ret;
	}
	
	private BeanTableHelper<Category> getCategorySummaryTable()
	{
		BeanTableHelper<Category> table = new BeanTableHelper<Category>("categoriesTable", "contentTable", Category.class);
		table.addBeanColumn("Categories", "name");	
		try{
			table.addObjects(dbInterface.getAllCategories());
		} catch (Exception ex){
			ex.printStackTrace();
		}
		return table;
	}
	//TODO
	//DISABLED
	/*
	private BeanTableHelper<StatsNode> getMostActiveUser(){
		BeanTableHelper<StatsNode> table = new BeanTableHelper<StatsNode>("mostActiveUserTable", "contentTable", StatsNode.class);
		table.addBeanColumn("User Name", "name");
		table.addBeanColumn("Number of changes", "value");
		try{
			table.addObjects(dbInterface.getNumberNotesPerUser());
		} catch (Exception ex){
			ex.printStackTrace();
		}
		return table;
	}
	
	private BeanTableHelper<StatsNode> getMostActiveCategoriesForUser(String username){
		BeanTableHelper<StatsNode> table = new BeanTableHelper<StatsNode>("mostActiveCategoriesUserTable", "contentTable", StatsNode.class);
		table.addBeanColumn("Categories you changed", "name");
		table.addBeanColumn("Number of changes", "value");
		try{
			table.addObjects(dbInterface.getMostActiveCategoriesForUser(username));
		} catch (Exception ex){
			ex.printStackTrace();
		}
		return table;
	}*/
}

