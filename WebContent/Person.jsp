<%@page import="ch.ethz.inf.dbproject.model.User"%>
<%@page import="ch.ethz.inf.dbproject.util.UserManagement"%>
<%@page import="ch.ethz.inf.dbproject.HomeServlet"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="Header.jsp" %>
<%
	final User user = (User) session.getAttribute(UserManagement.SHARED_SESSION_USER);
%>

<h2>Person Details</h2>

<%
if (request.getAttribute(HomeServlet.REQUEST_ERROR_MESSAGE) == null) {
%>

	<%=request.getAttribute("personDetailsTable")%>
	
	<%
	if (user != null) {
		// User is logged in. He can add a comment
	%>
	 	<form action="Person" method="get">
	 		<input type="hidden" name="id" value = <%=request.getParameter("id")%> >
			<input type="hidden" name="action" value="add_comment" />
			<input type="hidden" name="user_id" value="<%= user.getUsername() %>" />
			Add Comment
			<br />
			<textarea rows="4" cols="50" name="comment"></textarea>
			<br />
			<input type="submit" value="Submit" />
		</form>
		
	<%
	}
	%>
	
	Cases for which the person is convicted :
	<%=request.getAttribute("casesPersonConvicted") %>
	
	Cases for which the person is suspected :
	<%=request.getAttribute("casesPersonSuspected") %>
	
	Person Notes
	<%=request.getAttribute("personNotesTable") %>

<%
} else {
%>
 	<%@ include file="Error.jsp" %>
<%
}
%>

<%@ include file="Footer.jsp"%>