<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="Header.jsp" %>

<h1> Cases Summary</h1>

<hr/>

<%= session.getAttribute("casesSummary") %>

<hr/>

<%@ include file="Footer.jsp" %>