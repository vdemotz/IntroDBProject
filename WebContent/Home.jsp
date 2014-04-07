<%@page import="ch.ethz.inf.dbproject.model.User"%>
<%@page import="ch.ethz.inf.dbproject.HomeServlet"%>
<%@page import="ch.ethz.inf.dbproject.util.UserManagement"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="Header.jsp" %>

<%
	final User user = (User) session.getAttribute(UserManagement.SHARED_SESSION_USER);

if (user != null) {
	// There is a user logged in! Display a greeting!
%>
	Welcome back <%=user.getFirstName()%>
	
	<%= session.getAttribute(HomeServlet.SESSION_USER_DETAILS) %>
	
	The cases you modified / created :
	
	
	<%= session.getAttribute(HomeServlet.SESSION_USER_CASES) %>
	
<form method="get" action="Home">
	<div>
		<input type="hidden" name="action" value="logout" />
		<input type="submit" value="Logout" title="Logout" />
	</div>
</form>
	
<%
} else {
	// No user logged in.%>
	Welcome!
	
	
	<form action="Home" method="get">
	<input type="hidden" name="action" value="login" />
	<table> 
		<% if (((Boolean) session.getAttribute(HomeServlet.SESSION_WRONG_PASSWORD))) {
			//User provided wrong password. %>
		Sorry, wrong password/username
		<% } %>
		<tr>
			<th>Username</th>
			<td><input type="text" name="username" value="" /></td>
		</tr>
		<tr>
			<th>Password</th>
			<td><input type="password" name="password" value="" /></td>
		</tr>
		<tr>
			<th colspan="2">
				<input type="submit" value="Login" />
			</th>
		</tr>
	</table>
	</form>
	
	New User ? Create an <a href="UserCreation">account</a>.
<%
}
%>

<br /><br />
See all available <a href="Cases">cases</a> and <a href="PersonsOfInterest">persons of interest</a>.

<%@ include file="Footer.jsp" %>