<%@page import="ch.ethz.inf.dbproject.PersonCreationServlet"%>
<%@page import="ch.ethz.inf.dbproject.util.UserManagement"%>
<%@page import="ch.ethz.inf.dbproject.model.User"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="Header.jsp" %>

<%
final User user = (User) session.getAttribute(UserManagement.SESSION_USER);
final String error = (String) session.getAttribute(PersonCreationServlet.PERSON_CREATION_WRONG_FORM);
%>

<% 
if (user == null) {
%>
	
Please <a href="Home">login</a> or create an <a href="UserCreation">account</a> first.

<%
} else {
	if ( error != null) {
%>
	Sorry, error in form. Please enter valid last name, first name and birthdate.
	<% session.setAttribute(PersonCreationServlet.PERSON_CREATION_WRONG_FORM, null);
	} else { %>
	Thank you. The person has been created.
	<% } %>

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
			<th>Birth Date(yyyy-mm-dd)</th>
			<td><input type="text" name="birthdate" value=""></td>
		</tr>
		
		<td>Optional (if you want to directly set person as suspected):</td>
		<tr>
			<th>Case Id</th>
			<td><input type="text" maxlength="10" name="caseId" value=""/></td>
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