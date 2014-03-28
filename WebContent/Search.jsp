<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="Header.jsp" %>

<hr/>

<h1>Search Persons :</h1>

<form method="get" action="Search">
<div>
	<input type="hidden" name="filter" value="firstName" />
	Search By First Name:
	<input type="text" name="description" />
	<input type="submit" value="Search" title="Search by First Name-" />
</div>
</form>

<form method="get" action="Search">
<div>
	<input type="hidden" name="filter" value="lastName" />
	Search By Last Name:
	<input type="text" name="description" />
	<input type="submit" value="Search" title="Search by Last Name" />
</div>
</form>

<form method="get" action="Search">
<div>
	<input type="hidden" name="filter" value="convictionType" />
	Search By Conviction Type:
	<input type="text" name="description" />
	<input type="submit" value="Search" title="Search by Conviction type" />
</div>
</form>

<form method="get" action="Search">
<div>
	<input type="hidden" name="filter" value="convictionDate" />
	Search By Date of Conviction:
	<input type="text" name="category" />
	<input type="submit" value="Search" title="Search by date of conviciton" />
</div>
</form>

<hr/>

Search Cases :

<form method="get" action="Search">
<div>
	<input type="hidden" name="filter" value="category" />
	Search By Category:
	<input type="text" name="attribute" />
	<input type="submit" value="Search" title="Search by Category" />
</div>
</form>

<form method="get" action="Search">
<div>
	<input type="hidden" name="filter" value="caseDate" />
	Search By Date of Case:
	<input type="text" name="attribute" />
	<input type="submit" value="Search" title="Search by date of Case" />
</div>
</form>

<hr/>
<%= session.getAttribute("results") %>

<hr/>

<%@ include file="Footer.jsp" %>