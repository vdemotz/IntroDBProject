<%@page import="ch.ethz.inf.dbproject.UserCreationServlet"%>
<%@page import="ch.ethz.inf.dbproject.util.UserManagement"%>
<%@page import="ch.ethz.inf.dbproject.model.User"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="Header.jsp" %>

<%
final User user = (User) session.getAttribute(UserManagement.SESSION_USER);
%>

<% 
if (user == null) {
%>
	
Please <a href="Home">login</a> or create an <a href="UserCreation">account</a> first.

<%
} else {
%>

	<form action="PersonCreation" method="get">
	<input type="hidden" name="action" value="creation" />
	<table>
		<tr>
			<th>Last Name</th>
			<td><input type="text" name="lastName" value="" /></td>
		</tr>
		<tr>
			<th>First Name</th>
			<td><input type="text" name="firstName" value="" /></td>
		</tr>
		
		<tr>
			<th>Case ID</th>
			<td><input type="text" name="caseId" value="" /></td>
		</tr>
		<tr>
			<th colspan="2">
				<input type="submit" value="Submit" />
			</th>
		</tr>
	</table>
	</form>

<%
}
%>

<%@ include file="Footer.jsp" %>