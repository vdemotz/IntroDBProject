<%@page import="ch.ethz.inf.dbproject.UserCreationServlet"%>
<%@page import="ch.ethz.inf.dbproject.util.UserManagement"%>
<%@page import="ch.ethz.inf.dbproject.model.User"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="Header.jsp" %>

<%
	final User user = (User) session.getAttribute(UserManagement.SHARED_SESSION_USER);
%>

<% 
if (user != null) {
%>
	
But, you're already logged in mister <%= user.getLastName() %>

<%
} else {
%>

	<%=request.getAttribute(UserCreationServlet.USERCREATION_FORM_MESSAGE)%>
	<form action="UserCreation" method="get">
	<input type="hidden" name="action" value="creation" />
	<table>
		<tr>
			<th>Username</th>
			<td><input type="text" name="username" value="" /></td>
		</tr>
		<tr>
			<th>First Name</th>
			<td><input type="text" name="firstName" value="" /></td>
		</tr>
		<tr>
			<th>Last Name</th>
			<td><input type="text" name="lastName" value="" /></td>
		</tr>
		<tr>
			<th>Password</th>
			<td><input type="password" name="password1" value="" /></td>
		</tr>
		<tr>
			<th>Repeat password</th>
			<td><input type="password" name="password2" value="" /></td>
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