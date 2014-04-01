<%@page import="ch.ethz.inf.dbproject.model.User"%>
<%@page import="ch.ethz.inf.dbproject.model.CaseDetail"%>
<%@page import="ch.ethz.inf.dbproject.util.UserManagement"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="Header.jsp" %>
<% final User user = (User) session.getAttribute(UserManagement.SESSION_USER); %>
<% final CaseDetail caseDetail = (CaseDetail) session.getAttribute("caseDetail"); %>


<h2>Case Details</h2>

<%=session.getAttribute("caseTable")%>

<%
if (user != null) {
%>
	<form action="Case" method="get">
	<input type="hidden" name="caseId" value = <%=request.getParameter("caseId") %> >
	<input type="hidden" name="username" value= <%= user.getUsername() %> />
	<%
	if (caseDetail.getIsOpen()) {
	%>
		<input type="hidden" name="action" value="closeCase" />
		<input type="submit" value="Close Case">
	<%
	} else {
	%>
		<input type="hidden" name="action" value="openCase" />
		<input type="submit" value="Open Case">
	<%
	}
	%>
	</form>
<%
}
%> 

<%=session.getAttribute("categoryTable")%>

<%=session.getAttribute("suspectsTable")%>

<%=session.getAttribute("convictsTable")%>

<%
if (user != null && caseDetail.getIsOpen()) {
	//User is logged in. He can add a comment
%>
	
	<form action="Case" method="get">
 		<input type="hidden" name="caseId" value = <%=request.getParameter("caseId")%> >
		<input type="hidden" name="action" value="addComment" />
		<input type="hidden" name="username" value="<%= user.getUsername() %>" />
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