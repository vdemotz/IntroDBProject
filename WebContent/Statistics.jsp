<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="Header.jsp" %>
<%@page import="ch.ethz.inf.dbproject.StatisticsServlet"%>

<h2> Statistics</h2>

<h3> Cases By Category</h3>

<%= session.getAttribute(StatisticsServlet.SESSION_CATEGORY_SUMMARY_TABLE) %>

<%@ include file="Footer.jsp" %>