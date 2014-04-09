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

	<form action="UserCreation" method="get">
	<input type="hidden" name="action" value="creation" />
	<table>
	   	<%
	   	if ((Boolean) session.getAttribute(UserCreationServlet.USERCREATION_FORM_UN)){
	   	%>
	   	<th>Please enter an username</th>
	   	<% } %>
		<tr>
			<th>Username</th>
			<td><input type="text" name="username" value="" /></td>
		</tr>
		<%
	   	if ((Boolean) session.getAttribute(UserCreationServlet.USERCREATION_FORM_FN)){
	   	%>
	   	<th>Please enter a first name</th>
	   	<% } %>
		<tr>
			<th>First Name</th>
			<td><input type="text" name="firstName" value="" /></td>
		</tr>
		<%
	   	if ((Boolean) session.getAttribute(UserCreationServlet.USERCREATION_FORM_LN)){
	   	%>
	   	<th>Please enter a last name</th>
	   	<% } %>
		<tr>
			<th>Last Name</th>
			<td><input type="text" name="lastName" value="" /></td>
		</tr>
		<%
	   	if ((Boolean) session.getAttribute(UserCreationServlet.USERCREATION_FORM_EP)){
	   	%>
	   	<th>Empty passwords aren't allowed</th>
	   	<% } 
	   	else if ((Boolean) session.getAttribute(UserCreationServlet.USERCREATION_FORM_PNE)){
	   	%>
	   	<th>Passwords don't match (maybe a typo?)</th>
	   	<% } %>
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