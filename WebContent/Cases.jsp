<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="Header.jsp" %>

<h2>Cases</h2>

Create <a href="CaseCreation">new case</a>.

<%= request.getAttribute("cases") %>

<%@ include file="Footer.jsp" %>