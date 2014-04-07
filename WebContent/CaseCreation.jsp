<%@page import="ch.ethz.inf.dbproject.CaseCreationServlet"%>
<%@page import="ch.ethz.inf.dbproject.util.UserManagement"%>
<%@page import="ch.ethz.inf.dbproject.model.User"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="Header.jsp" %>

<%
	final User user = (User) session.getAttribute(UserManagement.SHARED_SESSION_USER);
%>

<% 
if (user == null) {
%>
	
Please <a href="Home">login</a> or create an <a href="UserCreation">account</a> first.

<%
} else {
%>

	<form action="CaseCreation" method="get">
	<input type="hidden" name="action" value="creation" />
	<table>
		<tr>
			<th>Title of the case</th>
			<td><input type="text" name="title" value="" /></td>
		</tr>
		<tr>
			<th>Location (Street, City, Zip Code)</th>
			<td><input type="text" name="street" value="" /></td>
			<td><input type="text" name="city" value=""></td>
			<td><input type="text" name="zipCode" value=""></td>
		</tr>
		<tr>
			<th>Description</th>
			<td>
			<textarea rows="4" cols="50" name="description"></textarea>
			</td>
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