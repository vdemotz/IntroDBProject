package ch.ethz.inf.dbproject;

import java.io.IOException;
import java.util.ArrayList;
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
import ch.ethz.inf.dbproject.HomeServlet;

/**
 * Servlet implementation class of Case Details Page
 */
@WebServlet(description = "Displays a specific case.", urlPatterns = { "/Case" })
public final class CaseServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private final DatastoreInterface dbInterface = new DatastoreInterface();

	
	public static final String BASE_ADDRESS = "Case";
	
	public static final String SESSION_CASE_DETAIL_TABLE = "CASEdetT";
	public static final String SESSION_CASE_TABLE = "CASEcatT";
	public static final String SESSION_SUSPECT_TABLE = "CASEsusT";
	public static final String SESSION_CONVICT_TABLE = "CASEconT";
	public static final String SESSION_CASE_NOTE_TABLE = "CASEnoteT";
	public static final String SESSION_CASE_CATEGORIES = "CASEchangeCategory";
	
	public static final String EXTERNAL_CASE_ID_PARAMETER = "caseId";
	public static final String EXTERNAL_USERNAME_PARAMETER = "username";
	
	public static final String INTERNAL_COMMENT_PARAMETER ="comment";
	public static final String INTERNAL_ACTION_PARAMETER = "action";
	public static final String INTERNAL_ACTION_ADD_NOTE_PARAMETER_VALUE = "addNote";
	public static final String INTERNAL_ACTION_OPEN_CASE_PARAMETER_VALUE = "openCase";
	public static final String INTERNAL_ACTION_CLOSE_CASE_PARAMETER_VALUE = "closeCase";

	public static final String INTERNAL_ACTION_ADD_CONVICT_PARAMETER_VALUE = "addConv";
	public static final String INTERNAL_ACTION_ADD_SUSPECT_PARAMETER_VALUE = "addSusp";
	
	public static final String INTERNAL_ACTION_CHANGE_CATEGORIES_VALUES = "changeCategories";
	
	
	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public CaseServlet() {
		super();
	}
	
	protected BeanTableHelper<CaseDetail> getCaseTableForId(int caseId)
	{
		BeanTableHelper<CaseDetail> table = new BeanTableHelper<CaseDetail>("cases", "borderedContentTable", CaseDetail.class);
		
		CaseDetail caseDetail = this.dbInterface.getCaseForId(caseId);
		table.addObject(caseDetail);
		
		table.addBeanColumn("Case ID", "caseId");
		table.addBeanColumn("Title", "title");
		table.addBeanColumn("Location", "location");
		table.addBeanColumn("Open", "isOpen");
		table.addBeanColumn("Date / Time", "dateTimeFormated");
		table.addBeanColumn("Description", "description");
		table.addBeanColumn("Opened by", "authorName");
		
		table.setVertical(true);
		return table;
	}
	
	protected BeanTableHelper<CaseNote> getCaseNotesTableForCase(int caseId)
	{
		BeanTableHelper<CaseNote> tableComment = new BeanTableHelper<CaseNote>("comments", "contentTable", CaseNote.class);
	
		List<CaseNote> cases = this.dbInterface.getCaseNotesForCase(caseId);//TODO: handle null, empty list
		tableComment.addObjects(cases);
		
		//tableComment.addBeanColumn("Case ID", "caseId");
		tableComment.addBeanColumn("Case Note ID", "caseNoteId");
		tableComment.addBeanColumn("Comment", "text");
		tableComment.addBeanColumn("Date", "dateTimeFormated");
		tableComment.addBeanColumn("Opened by", "authorUsername");
		
		return tableComment;
	}
	
	protected BeanTableHelper<Person> getSuspectsTableForCase(int caseId)
	{
		BeanTableHelper<Person> suspectsTable = new BeanTableHelper<Person>("suspects", "contentTable", Person.class);
		
		List<Person> suspects = this.dbInterface.getSuspectsForCase(caseId);
		suspectsTable.addObjects(suspects);
		
		suspectsTable.addBeanColumn("Suspect Id", "personId");
		suspectsTable.addBeanColumn("Name", "name");
		suspectsTable.addLinkColumn("", "Person Details", "Person?id=", "personId");
		
		return suspectsTable;
	}
	
	protected BeanTableHelper<Person> getConvictsTableForCase(int caseId)
	{
		BeanTableHelper<Person> table = new BeanTableHelper<Person>("convicts", "contentTable", Person.class);
		
		List<Person> convicts = this.dbInterface.getConvictsForCase(caseId);
		table.addObjects(convicts);
		
		table.addBeanColumn("Convict Id", "personId");
		table.addBeanColumn("Name", "name");
		table.addLinkColumn("", "Person Details", "Person?id=", "personId");
		
		return table;
	}
	
	protected BeanTableHelper<Category> getCategoriesTableForCase(int caseId)
	{
		BeanTableHelper<Category> table = new BeanTableHelper<Category>("categories", "contentTable", Category.class);
		
		List<Category> list = this.dbInterface.getCategoriesForCase(caseId);
		table.addObjects(list);
		
		table.addBeanColumn("Categories", "name");
		
		//table.setVertical(true);
		
		return table;
	}
	
	private List<Category> getCategoriesForCase(int id){
		List<Category> catList = dbInterface.getAllCategories();
		if (catList == null){
			catList = new ArrayList<Category>();
			catList.add(new Category("Other"));
		}
		return catList;
	}
	
	private void handleInvalidRequest(final HttpServletRequest request) throws ServletException, IOException
	{
		request.getSession().setAttribute(HomeServlet.SESSION_ERROR_MESSAGE, "Invalid Case ID");
	}

	private CaseDetail handleActionsForRequest(final HttpServletRequest request, final CaseDetail caseDetail)
	{
		final HttpSession session = request.getSession(true);
		final int id = caseDetail.getCaseId();
		
		final User loggedUser = UserManagement.getCurrentlyLoggedInUser(session);
		final String username = request.getParameter(EXTERNAL_USERNAME_PARAMETER);
		
		boolean didUpdateCase = false;
		
		if (loggedUser != null && loggedUser.getUsername().equals(username)) {//do not modify if the user is logged out, or the user changed in the mean time
			
			final String action = request.getParameter(INTERNAL_ACTION_PARAMETER);
			
			
			if (INTERNAL_ACTION_ADD_NOTE_PARAMETER_VALUE.equals(action) && caseDetail.getIsOpen()) {//cannot add comments to closed cases
				final String comment = request.getParameter(INTERNAL_COMMENT_PARAMETER);
				CaseNote cn = dbInterface.insertIntoCaseNote(id, comment, username);
				
			} else if (INTERNAL_ACTION_CLOSE_CASE_PARAMETER_VALUE.equals(action) && caseDetail.getIsOpen()) {
				dbInterface.insertIntoCaseNote(id, "closed case", username);//keep notes of the closing / opening
				didUpdateCase = dbInterface.updateCaseIsOpen(id, false);
				
			} else if (INTERNAL_ACTION_OPEN_CASE_PARAMETER_VALUE.equals(action) && !caseDetail.getIsOpen()) {
				didUpdateCase = dbInterface.updateCaseIsOpen(id, true);
				dbInterface.insertIntoCaseNote(id, "opened case", username);//keep notes of the closing / opening
			} else if (INTERNAL_ACTION_ADD_SUSPECT_PARAMETER_VALUE.equals(action) && caseDetail.getIsOpen()) {
				final String personIdRaw = request.getParameter(PersonSelectionServlet.EXTERNAL_RESULT_PERSON_ID_PARAMETER);
				int personId = Integer.parseInt(personIdRaw);
				dbInterface.addSuspectToCase(id, personId);
				
			} else if (INTERNAL_ACTION_CHANGE_CATEGORIES_VALUES.equals(action) && caseDetail.getIsOpen()) {
				final String[] categories = request.getParameterValues("categories");
				List<Category> listCatOfCase = this.dbInterface.getCategoriesForCase(id);
				List<String> listCatNameOfCase = new ArrayList<String>();
				if (listCatOfCase != null){
					for (int i = 0; i < listCatOfCase.size(); i++){
						listCatNameOfCase.add(listCatOfCase.get(i).getName());
					}
				}
				if (categories != null){
					for (int i = 0; i < categories.length; i++){
						if (listCatNameOfCase.contains(categories[i])){
							this.dbInterface.deleteCategoryForCaseIdAndCategory(id, categories[i]);
						} else {
							this.dbInterface.insertIntoCategoryForCase(categories[i], id);
						}
					}
				}
			} //TODO: add convict
		}
		
		if (didUpdateCase) {
			return this.dbInterface.getCaseForId(id);
		}
		return caseDetail;
	}
	
	private void handleValidRequest(final HttpServletRequest request, CaseDetail caseDetail)
	{	
		final HttpSession session = request.getSession(true);
		session.setAttribute(HomeServlet.SESSION_ERROR_MESSAGE, null);
		
		//perform actions, if any and get the new case detail
		caseDetail = handleActionsForRequest(request, caseDetail);
		final int id = caseDetail.getCaseId();
		session.setAttribute("caseDetail", caseDetail);
		
		//set the CaseDetail (the header) wanted by the user
		session.setAttribute(SESSION_CASE_DETAIL_TABLE, getCaseTableForId(id));
		//set the Categories for the case
		session.setAttribute(SESSION_CASE_TABLE, getCategoriesTableForCase(id));
		//list the case notes				
		session.setAttribute(SESSION_CASE_NOTE_TABLE, getCaseNotesTableForCase(id));
		//list the suspects
		session.setAttribute(SESSION_SUSPECT_TABLE, getSuspectsTableForCase(id));
		//list the convicts
		session.setAttribute(SESSION_CONVICT_TABLE, getConvictsTableForCase(id));
		//list the categories
		session.setAttribute(SESSION_CASE_CATEGORIES, getCategoriesForCase(id));
	}
	
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected final void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
		request.getSession().removeAttribute(HomeServlet.SESSION_ERROR_MESSAGE);
		final String idString = request.getParameter(EXTERNAL_CASE_ID_PARAMETER);
		
		boolean invalidId = false;
		int id = 0;
		CaseDetail caseDetail = null;
		try {
			id = Integer.parseInt(idString);
			caseDetail = this.dbInterface.getCaseForId(id);
			if (caseDetail == null) invalidId = true;
		} catch (Exception ex) {	
			invalidId = true;
		}

		if (invalidId == true) {
			handleInvalidRequest(request);
		} else {
			handleValidRequest(request, caseDetail);
		}
		
		this.getServletContext().getRequestDispatcher("/Case.jsp").forward(request, response);
	}
}