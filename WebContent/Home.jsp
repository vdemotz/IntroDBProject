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
	Welcome back <%=user.getFirstName()%>!
	
	<%= request.getAttribute(HomeServlet.REQUEST_USER_DETAILS) %>
	
	You can <a href="CaseCreation">create a new case</a> or <a href="PersonCreation">add a new person</a>.
	<br><br>
<<<<<<< HEAD
	<!--  
=======
	<!-- DISABLED
>>>>>>> master
	The cases you modified / created :
	
	< %= request.getAttribute(HomeServlet.REQUEST_USER_CASES) %>
	-->
<<<<<<< HEAD
	
=======
>>>>>>> master
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
	
	<%= request.getAttribute(HomeServlet.HOME_MESSAGE) %>
	
	<form action="Home" method="get">
	<input type="hidden" name="action" value="login" />
	<table> 
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
	
	<br><br>
<<<<<<< HEAD

=======
	<!--  DISABLED
	The most active users : 
	
	< %= request.getAttribute(HomeServlet.HOME_MOST_ACTIVE_USER) %>
	-->
>>>>>>> master
<%
}
%>

<%= request.getAttribute(HomeServlet.HOME_SUMMARY_CAT_TABLE) %>

<%
if (user != null) {%>
	<%= request.getAttribute(HomeServlet.HOME_ADD_CATEGORY_MESSAGE) %>
	
	<form method="get" action="Home">
	<div>
		<input type="hidden" name="action" value="categoryCreation" />
		Add a new Category:
		<input type="text" name="description" />
		<input type="submit" value="Creation" title="Add new category" />
	</div>
	</form>
<<<<<<< HEAD
	
=======
	<!-- DISABLED 
	< %= request.getAttribute(HomeServlet.HOME_MOST_ACTIVE_CAT_FOR_USER) %>
	-->
>>>>>>> master
<% } %>

<br /><br />
See all available <a href="Cases">cases</a> and <a href="PersonsOfInterest">persons of interest</a>.

<%@ include file="Footer.jsp" %>