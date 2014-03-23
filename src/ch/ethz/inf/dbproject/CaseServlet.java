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

import ch.ethz.inf.dbproject.model.CaseNote;
import ch.ethz.inf.dbproject.model.Conviction;
import ch.ethz.inf.dbproject.model.Comment;
import ch.ethz.inf.dbproject.model.DatastoreInterface;
import ch.ethz.inf.dbproject.model.Case;
import ch.ethz.inf.dbproject.model.User;
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

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected final void doGet(final HttpServletRequest request, 
			final HttpServletResponse response) throws ServletException, 
			IOException {

		final HttpSession session = request.getSession(true);
		final User loggedUser = UserManagement.getCurrentlyLoggedInUser(session);

		final String action = request.getParameter("action");
		final String userId = request.getParameter("user_id");
		final String comment = request.getParameter("comment");
		final String idString = request.getParameter("id");
		
		if (action != null){
			CaseNote cn = new CaseNote(4, comment, new Date(), userId);
		}	
		
		if (idString == null) {
			this.getServletContext().getRequestDispatcher("/Cases.jsp").forward(request, response);
		}
		
		else{
			try {
				final Integer id = Integer.parseInt(idString);
				final Case aCase = this.dbInterface.getCaseById(id);
					//get the case wanted by the user
				
				final BeanTableHelper<Case> table = new BeanTableHelper<Case>("cases",
						"casesTable", Case.class);
					//initialize a new table for the case
	
				table.addBeanColumn("Case ID", "caseId");
				table.addBeanColumn("Title", "title");
				table.addBeanColumn("Location", "location");
				table.addBeanColumn("Open", "isOpen");
				table.addBeanColumn("Date", "date");
				table.addBeanColumn("Description", "description");
				table.addBeanColumn("Author Name", "authorName");
					//add all details to the case table
	
				table.addObject(aCase);
				table.setVertical(true);			
				
				session.setAttribute("caseTable", table);
				
				final BeanTableHelper<CaseNote> tableComment = new BeanTableHelper<CaseNote>("comments",
						"commentsTable", CaseNote.class);
				
				tableComment.addBeanColumn("Case ID", "caseId");
				tableComment.addBeanColumn("Case Note ID", "caseNoteId");
				tableComment.addBeanColumn("Comment", "text");
				tableComment.addBeanColumn("Date", "date");
				tableComment.addBeanColumn("Author Name", "authorUsername");
				
				final List<CaseNote> cases = this.dbInterface.getCaseNoteByCase(id);
				tableComment.addObjects(cases);
				
				session.setAttribute("commentTable", tableComment);
				
			} catch (final Exception ex) {
				System.err.println("not able to display comments or case");
				ex.printStackTrace();
				this.getServletContext().getRequestDispatcher("/Cases.jsp").forward(request, response);
			}
			
			this.getServletContext().getRequestDispatcher("/Case.jsp").forward(request, response);
		}
	}
}