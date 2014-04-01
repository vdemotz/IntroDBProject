package ch.ethz.inf.dbproject;

import java.io.IOException;
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

/**
 * Servlet implementation class of Case Details Page
 */
@WebServlet(description = "Displays a specific case.", urlPatterns = { "/Case" })
public final class CaseServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private final DatastoreInterface dbInterface = new DatastoreInterface();

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public CaseServlet() {
		super();
	}
	
	protected BeanTableHelper<CaseDetail> getCaseTableForId(int caseId)
	{
		BeanTableHelper<CaseDetail> table = new BeanTableHelper<CaseDetail>("cases", "casesTable", CaseDetail.class);
		
		CaseDetail caseDetail = this.dbInterface.getCaseForId(caseId);
		table.addObject(caseDetail);
		
		table.addBeanColumn("Case ID", "caseId");
		table.addBeanColumn("Title", "title");
		table.addBeanColumn("Location", "location");
		table.addBeanColumn("Open", "isOpen");
		table.addBeanColumn("Date / Time", "dateTimeFormated");
		table.addBeanColumn("Description", "description");
		table.addBeanColumn("Author Name", "authorName");
		
		table.setVertical(true);
		return table;
	}
	
	protected BeanTableHelper<CaseNote> getCaseNotesTableForCase(int caseId)
	{
		BeanTableHelper<CaseNote> tableComment = new BeanTableHelper<CaseNote>("comments", "commentsTable", CaseNote.class);
	
		List<CaseNote> cases = this.dbInterface.getCaseNotesForCase(caseId);//TODO: handle null, empty list
		tableComment.addObjects(cases);
		
		//tableComment.addBeanColumn("Case ID", "caseId");
		tableComment.addBeanColumn("Case Note ID", "caseNoteId");
		tableComment.addBeanColumn("Comment", "text");
		tableComment.addBeanColumn("Date", "date");
		tableComment.addBeanColumn("Author Name", "authorUsername");
		
		return tableComment;
	}
	
	protected BeanTableHelper<Person> getSuspectsTableForCase(int caseId)
	{
		BeanTableHelper<Person> suspectsTable = new BeanTableHelper<Person>("suspects", "suspectsTable", Person.class);
		
		List<Person> suspects = this.dbInterface.getSuspectsForCase(caseId);
		suspectsTable.addObjects(suspects);
		
		suspectsTable.addBeanColumn("Suspect Id", "personId");
		suspectsTable.addBeanColumn("Name", "name");
		suspectsTable.addLinkColumn("", "Person Details", "Person?id=", "personId");
		
		return suspectsTable;
	}
	
	protected BeanTableHelper<Person> getConvictsTableForCase(int caseId)
	{
		BeanTableHelper<Person> table = new BeanTableHelper<Person>("convicts", "convictsTable", Person.class);
		
		List<Person> convicts = this.dbInterface.getConvictsForCase(caseId);
		table.addObjects(convicts);
		
		table.addBeanColumn("Convict Id", "personId");
		table.addBeanColumn("Name", "name");
		table.addLinkColumn("", "Person Details", "Person?id=", "personId");
		
		return table;
	}
	
	protected BeanTableHelper<Category> getCategoriesTableForCase(int caseId)
	{
		BeanTableHelper<Category> table = new BeanTableHelper<Category>("categories", "categoryTable", Category.class);
		
		List<Category> list = this.dbInterface.getCategoriesForCase(caseId);
		table.addObjects(list);
		
		table.addBeanColumn("Categories", "name");
		
		//table.setVertical(true);
		
		return table;
	}
	
	private void handleInvalidRequest(final HttpServletRequest request) throws ServletException, IOException
	{
		System.err.println("invalid case id");
		
	}


	private CaseDetail handleActionsForRequest(final HttpServletRequest request, final CaseDetail caseDetail)
	{
		final HttpSession session = request.getSession(true);
		final int id = caseDetail.getCaseId();
		
		final User loggedUser = UserManagement.getCurrentlyLoggedInUser(session);
		final String username = request.getParameter("username");
		
		boolean didUpdateCase = false;
		
		if (loggedUser != null && loggedUser.getUsername().equals(username)) {//do not modify if the user is logged out, or the user changed in the mean time
			
			final String action = request.getParameter("action");
			
			if ("addComment".equals(action) && caseDetail.getIsOpen()) {//cannot add comments to closed cases
				final String comment = request.getParameter("comment");
				CaseNote cn = dbInterface.insertIntoCaseNote(id, comment, username);
				
			} else if ("closeCase".equals(action)) {
				dbInterface.insertIntoCaseNote(id, "closed case", username);//keep notes of the closing / opening
				didUpdateCase = dbInterface.updateCaseIsOpen(id, false);
				
			} else if ("openCase".equals(action)) {
				didUpdateCase = dbInterface.updateCaseIsOpen(id, true);
				dbInterface.insertIntoCaseNote(id, "opened case", username);//keep notes of the closing / opening
			}
		}
		
		if (didUpdateCase) {
			return this.dbInterface.getCaseForId(id);
		}
		return caseDetail;
	}
	
	private void handleValidRequest(final HttpServletRequest request, CaseDetail caseDetail)
	{	
		final HttpSession session = request.getSession(true);
		
		//perform actions, if any and get the new case detail
		caseDetail = handleActionsForRequest(request, caseDetail);
		final int id = caseDetail.getCaseId();
		session.setAttribute("caseDetail", caseDetail);
		
		//set the CaseDetail (the header) wanted by the user
		session.setAttribute("caseTable", getCaseTableForId(id));
		//set the Categories for the case
		session.setAttribute("categoryTable", getCategoriesTableForCase(id));
		//list the case notes				
		session.setAttribute("commentTable", getCaseNotesTableForCase(id));
		//list the suspects
		session.setAttribute("suspectsTable", getSuspectsTableForCase(id));
		//list the convicts
		session.setAttribute("convictsTable", getConvictsTableForCase(id));
	}
	
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected final void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
		
		final String idString = request.getParameter("caseId");
		
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