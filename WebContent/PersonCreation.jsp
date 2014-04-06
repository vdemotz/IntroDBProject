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
			<th>Birth Date(yyyy-mm-dd)</th>
			<td><input type="text" name="birthdate" value=""></td>
		</tr>
		
		<td>Optional :</td>
		<tr>
			<th>Case Id</th>
			<td><input type="text" maxlength="10" name="caseId" value=""/></td>
		</tr>
		<tr>
		        <th>Convicted / Suspected :</th>
		        <td><select id="type" name="typeOfPerson">
		          <option value="convicted">Convicted</option>
		          <option value="suspected">Suspected</option>
		        </select></td>
		</tr>
		<tr>
			<th>Date of crime (yyyy-mm-dd)</th>
			<td><input type="text" maxlength="10" name="dateCrime" value=""/></td>
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