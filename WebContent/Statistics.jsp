<%@page import="ch.ethz.inf.dbproject.StatisticsServlet"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="Header.jsp" %>
<%@page import="ch.ethz.inf.dbproject.StatisticsServlet"%>

<h2> Statistics</h2>

<h3> Cases By Category</h3>

<%= session.getAttribute(StatisticsServlet.SESSION_CATEGORY_SUMMARY_TABLE) %>

<br><br>

<h3> Available tables </h3>

<form method="get" action="Statistics">
<div>
	<input type="hidden" name="filter" value="casesCity" />
	Cases Per City
<input type="submit" value="Cases Per City" title="Cases Per City" />
</div>
</form>

<form method="get" action="Statistics">
<div>
	<input type="hidden" name="filter" value="casesMonth" />
	Cases Per Month
	<input type="submit" value="Cases Per Month" title="Cases Per Month" />
</div>
</form>

<form method="get" action="Statistics">
<div>
	<input type="hidden" name="filter" value="convictionsMonth" />
	Convictions Per Month
	<input type="submit" value="Convictions Per Month" title="Convictions Per Month" />
</div>
</form>

<form method="get" action="Statistics">
<div>
	<input type="hidden" name="filter" value="convictionsCity" />
	Convictions Per City
	<input type="submit" value="Convictions Per City" title="Convictions Per City" />
</div>
</form>

<form method="get" action="Statistics">
<div>
	<input type="hidden" name="filter" value="convictionsCategory" />
	Convictions Per Category
	<input type="submit" value="Convictions Per Category" title="Convictions Per Category" />
</div>
</form>

<br>

<%= session.getAttribute(StatisticsServlet.STATISTICS_STATS_TABLE) %>

<%@ include file="Footer.jsp" %>