<%@page import="ch.ethz.inf.dbproject.PersonsOfInterestServlet"%>
<%@page import="ch.ethz.inf.dbproject.model.User"%>
<%@page import="ch.ethz.inf.dbproject.util.UserManagement"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="Header.jsp" %>

<h2>Persons of Interest</h2>
<%
	final User user = (User) session.getAttribute(UserManagement.SHARED_SESSION_USER);
if (user != null) {
%>
	Do you want to add a person ? Feel free to do it <a href="PersonCreation">here</a>.
<% } %>
<form method="get" action="PersonsOfInterest">
<div>
	<input type="hidden" name="filter" value="suspected" />
	<input type="submit" value="Suspected" title="Suspected" />
</div>
</form>
<form method="get" action="PersonsOfInterest">
<div>
	<input type="hidden" name="filter" value="convicted" />
	<input type="submit" value="Convicted" title="Convicted" />
</div>
</form>

	
<%= session.getAttribute("resultsPersons") %>


<%@ include file="Footer.jsp" %>