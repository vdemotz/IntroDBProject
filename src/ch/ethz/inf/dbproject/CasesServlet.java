package ch.ethz.inf.dbproject;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import ch.ethz.inf.dbproject.database.DatastoreInterface;
import ch.ethz.inf.dbproject.model.CaseDetail;
import ch.ethz.inf.dbproject.util.html.BeanTableHelper;

/**
 * Servlet implementation class of Case list page
 */
@WebServlet(description = "The home page of the project", urlPatterns = { "/Cases" })
public final class CasesServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private final DatastoreInterface dbInterface = new DatastoreInterface();

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public CasesServlet() {
		super();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected final void doGet(final HttpServletRequest request, final HttpServletResponse response) 
			throws ServletException, IOException {

		final HttpSession session = request.getSession(true);

		/*******************************************************
		 * Construct a table to present all our results
		 *******************************************************/
		final BeanTableHelper<CaseDetail> table = new BeanTableHelper<CaseDetail>("cases",
				"casesTable", CaseDetail.class);

		// Add columns to the new table
		table.addBeanColumn("Case ID", "caseId");
		table.addBeanColumn("Title", "title");
		table.addBeanColumn("Location", "location");
		table.addBeanColumn("Open", "isOpen");
		table.addBeanColumn("Date", "date");
		table.addBeanColumn("Description", "description");
		table.addBeanColumn("Author Name", "authorName");

		/*
		 * Column 4: This is a special column. It adds a link to view the
		 * Project. We need to pass the case identifier to the url.
		 */
		table.addLinkColumn(""	/* The header. We will leave it empty */,
				"View Case" 	/* What should be displayed in every row */,
				"Case?id=" 	/* This is the base url. The final url will be composed from the concatenation of this and the parameter below */, 
				"id" 			/* For every case displayed, the ID will be retrieved and will be attached to the url base above */);

		// Pass the table to the session. This will allow the respective jsp page to display the table.
		session.setAttribute("cases", table);

		// The filter parameter defines what to show on the Projects page
		final String filter = request.getParameter("filter");
		final String category = request.getParameter("category");

		if (filter == null && category == null) {

			// If no filter is specified, then we display all the cases!
			table.addObjects(this.dbInterface.getAllCases());

		} else if (category != null) {
			
			table.addObjects(this.dbInterface.getCasesForCategory(category));
			
		} else if (filter != null) {
		
			if(filter.equals("open")) {

				// TODO implement this!
				//table.addObjects(this.dbInterface.getOpenCases());

			} else if (filter.equals("closed")) {

				// TODO implement this!
				// table.addObjects(this.dbInterface.getClosedCases());

			} else if (filter.equals("recent")) {

				// TODO implement this!
				// table.addObjects(this.dbInterface.getMostRecentCases());

			}
			
			else if (filter.equals("oldest")) {

				// TODO implement this!
				// table.addObjects(this.dbInterface.getOldestUnsolvedCases());

			}
			
		} else {
			throw new RuntimeException("Code should not be reachable!");
		}

		// Finally, proceed to the Projects.jsp page which will render the Projects
		this.getServletContext().getRequestDispatcher("/Cases.jsp").forward(request, response);
	}
}