<%@page import="ch.ethz.inf.dbproject.model.User"%>
<%@page import="ch.ethz.inf.dbproject.util.UserManagement"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="Header.jsp" %>
<% final User user = (User) session.getAttribute(UserManagement.SESSION_USER); %>

<h1>Case Details</h1>

<%=session.getAttribute("caseTable")%>

<%=session.getAttribute("categoryTable")%>

<%=session.getAttribute("suspectsTable")%>

<%=session.getAttribute("convictsTable")%>

<%
	// TODO close or reopen the case
%>

<%
if (user != null) {
	// User is logged in. He can add a comment
%>
 	<form action="Case" method="get">
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

<%=session.getAttribute("commentTable")%>

<%@ include file="Footer.jsp"%>