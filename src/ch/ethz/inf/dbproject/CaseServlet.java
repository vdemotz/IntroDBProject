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
		table.addBeanColumn("Date", "date");
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
		
		return suspectsTable;
	}
	
	protected BeanTableHelper<Person> getConvictsTableForCase(int caseId)
	{
		BeanTableHelper<Person> suspectsTable = new BeanTableHelper<Person>("convicts", "convictsTable", Person.class);
		
		List<Person> convicts = this.dbInterface.getConvictsForCase(caseId);
		suspectsTable.addObjects(convicts);
		
		suspectsTable.addBeanColumn("Convict Id", "personId");
		suspectsTable.addBeanColumn("Name", "name");
		
		return suspectsTable;
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected final void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {

		final HttpSession session = request.getSession(true);
		final User loggedUser = UserManagement.getCurrentlyLoggedInUser(session);

		final String action = request.getParameter("action");
		final String userId = request.getParameter("user_id");
		final String comment = request.getParameter("comment");
		final String idString = request.getParameter("id");
		
		if (action != null){
			final Object id = session.getAttribute("caseId");//shouldn't the id be sent as part of the request?
			CaseNote cn = dbInterface.addCaseNote(Integer.parseInt(id.toString()), comment, userId);
		}
		
		if (idString == null) {
			this.getServletContext().getRequestDispatcher("/Cases.jsp").forward(request, response);
		} else {
			try {
				final Integer id = Integer.parseInt(idString);
				
				//set the caseId
				session.setAttribute("caseId", id);
				//set the CaseDetail (the header) wanted by the user
				session.setAttribute("caseTable", getCaseTableForId(id));
				//list the case notes				
				session.setAttribute("commentTable", getCaseNotesTableForCase(id));
				//list the suspects
				session.setAttribute("suspectsTable", getSuspectsTableForCase(id));
				//list the convicts
				session.setAttribute("convictsTable", getConvictsTableForCase(id));
				
			} catch (final Exception ex) {
				System.err.println("not able to display the case wanted");
				ex.printStackTrace();
				this.getServletContext().getRequestDispatcher("/Cases.jsp").forward(request, response);
			}
			
			this.getServletContext().getRequestDispatcher("/Case.jsp").forward(request, response);
		}
	}
}