<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="Header.jsp" %>
<%@page import="ch.ethz.inf.dbproject.StatisticsServlet"%>

<h2> Statistics</h2>

<h3> Cases By Category</h3>

<%= session.getAttribute(StatisticsServlet.SESSION_CATEGORY_SUMMARY_TABLE) %>

<%= session.getAttribute(StatisticsServlet.STATISTICS_ADD_CATEGORY) %>

<form method="get" action="Statistics">
<div>
	<input type="hidden" name="action" value="categoryCreation" />
	Add a new Category:
	<input type="text" name="description" />
	<input type="submit" value="Creation" title="Add new category" />
</div>
</form>


<%@ include file="Footer.jsp" %>