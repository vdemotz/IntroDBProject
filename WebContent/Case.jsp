<%@page import="ch.ethz.inf.dbproject.model.User"%>
<%@page import="ch.ethz.inf.dbproject.model.CaseDetail"%>
<%@page import="ch.ethz.inf.dbproject.CaseServlet"%>
<%@page import="ch.ethz.inf.dbproject.PersonSelectionServlet"%>
<%@page import="ch.ethz.inf.dbproject.util.UserManagement"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="Header.jsp" %>
<% final User user = (User) session.getAttribute(UserManagement.SESSION_USER); %>
<% final CaseDetail caseDetail = (CaseDetail) session.getAttribute("caseDetail"); %>


<h2>Case Details</h2>

<%=session.getAttribute(CaseServlet.CASE_DETAIL_TABLE_ATTRIBUTE)%>

<%
if (user != null) {
%>
	<form action=<%=CaseServlet.BASE_ADDRESS%> method="get">
	<input type="hidden" name=<%=CaseServlet.CASE_ID_PARAMETER%> value = <%=request.getParameter(CaseServlet.CASE_ID_PARAMETER) %> >
	<input type="hidden" name=<%=CaseServlet.USERNAME_PARAMETER%> value= <%= user.getUsername() %> />
	<%
	if (caseDetail.getIsOpen()) {
	%>
		<input type="hidden" name="action" value= <%=CaseServlet.INTERNAL_ACTION_CLOSE_CASE_PARAMETER_VALUE%> />
		<input type="submit" value="Close Case">
	<%
	} else {
	%>
		<input type="hidden" name="action" value=<%=CaseServlet.INTERNAL_ACTION_OPEN_CASE_PARAMETER_VALUE%> />
		<input type="submit" value="Open Case">
	<%
	}
	%>
	</form>
<%
}
%> 

<%=session.getAttribute(CaseServlet.CATEGORY_TABLE_ATTRIBUTE)%>

<%=session.getAttribute(CaseServlet.SUSPECT_TABLE_ATTRIBUTE)%>

<% 
String returnAddressFromSelectPerson = "";
if (user != null && caseDetail.getIsOpen()) {
	//User is logged in. He can add a comment
 	returnAddressFromSelectPerson =  CaseServlet.BASE_ADDRESS + "?" + CaseServlet.CASE_ID_PARAMETER + "=" + request.getParameter(CaseServlet.CASE_ID_PARAMETER) +
										   "&" + CaseServlet.USERNAME_PARAMETER + "=" + user.getUsername();
}
%>
										

<%
if (user != null && caseDetail.getIsOpen()) {
	//User is logged in. He can add a comment
%>
<form action=<%=PersonSelectionServlet.BASE_ADDRESS%> method="get">

<input type="hidden" name=<%=PersonSelectionServlet.EXTERNAL_TITLE_PARAMETER%> value = "Add Suspect to Case">
<input type="hidden" name=<%=PersonSelectionServlet.EXTERNAL_RETURN_ADDRESS_PARAMETER%> value = <%=returnAddressFromSelectPerson + 
			"&" + CaseServlet.INTERNAL_ACTION_PARAMETER + "=" + CaseServlet.INTERNAL_ACTION_ADD_SUSPECT_PARAMETER_VALUE%>>

<input type="submit" value="Add Suspect" />
</form>
<%
}
%> 

<%=session.getAttribute(CaseServlet.CONVICT_TABLE_ATTRIBUTE)%>

<%
if (user != null && caseDetail.getIsOpen()) {
	//User is logged in. He can add a comment
%>
<form action=<%=PersonSelectionServlet.BASE_ADDRESS%> method="get">

<input type="hidden" name=<%=PersonSelectionServlet.EXTERNAL_TITLE_PARAMETER%> value = "Add Suspect to Case">
<input type="hidden" name=<%=PersonSelectionServlet.EXTERNAL_RETURN_ADDRESS_PARAMETER%> value = <%=returnAddressFromSelectPerson + 
			"&" + CaseServlet.INTERNAL_ACTION_PARAMETER + "=" + CaseServlet.INTERNAL_ACTION_ADD_CONVICT_PARAMETER_VALUE%>>

<input type="submit" value="Add Convict" />
</form>
<%
}
%> 

<br><br>

<%
if (user != null && caseDetail.getIsOpen()) {
	//User is logged in. He can add a comment
%>
	
	<form action=<%=CaseServlet.BASE_ADDRESS%> method="get">
		<input type="hidden" name=<%=CaseServlet.CASE_ID_PARAMETER%> value = <%=request.getParameter(CaseServlet.CASE_ID_PARAMETER)%> >
		<input type="hidden" name=<%=CaseServlet.USERNAME_PARAMETER%> value= <%= user.getUsername() %> />
		<input type="hidden" name=<%=CaseServlet.INTERNAL_ACTION_PARAMETER%> value=<%=CaseServlet.INTERNAL_ACTION_ADD_NOTE_PARAMETER_VALUE%>  />
		Add Comment
		<br />
		<textarea rows="4" cols="50" name="comment"></textarea>
		<br />
		<input type="submit" value="Submit" />
	</form>
<%
}
%> 

<%=session.getAttribute(CaseServlet.CASE_NOTE_TABLE_ATTRIBUTE)%>

<%@ include file="Footer.jsp"%>