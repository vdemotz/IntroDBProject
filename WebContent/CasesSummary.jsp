<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="Header.jsp" %>

<h2> Cases Summary</h2>

<%= session.getAttribute("casesSummary") %>

<%@ include file="Footer.jsp" %>