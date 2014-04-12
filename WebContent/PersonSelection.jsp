<%@page import="ch.ethz.inf.dbproject.PersonSelectionServlet"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="Header.jsp" %>

<h2> <%= request.getParameter(PersonSelectionServlet.EXTERNAL_TITLE_PARAMETER).replace('+', ' ') %> </h2>

<form method="get" action= <%=PersonSelectionServlet.BASE_ADDRESS%> >
<div>
	<input type="hidden" name=<%=PersonSelectionServlet.EXTERNAL_TITLE_PARAMETER%> value =  <%= request.getParameter(PersonSelectionServlet.EXTERNAL_TITLE_PARAMETER).replace(' ', '+') %> >
	<input type="hidden" name =<%=PersonSelectionServlet.EXTERNAL_RETURN_ADDRESS_PARAMETER%> value = <%= request.getParameter(PersonSelectionServlet.EXTERNAL_RETURN_ADDRESS_PARAMETER)%> >
	<input type="hidden" name="action" value=<%=PersonSelectionServlet.INTERNAL_SEARCH_ACTION%> />
	Search By Id or First Name and Last Name:
	<br>
	<input type="text" name= <%=PersonSelectionServlet.INTERNAL_SEARCH_PERSON_ID%> />
	<input type="text" name= <%=PersonSelectionServlet.INTERNAL_SEARCH_FIRSTNAME%> />
	<input type="text" name= <%=PersonSelectionServlet.INTERNAL_SEARCH_LASTNAME%> />
	<input type="submit" value="Search" title="Search Persons" />
	<br> Note that you can use '*' and '%'.
</div>
</form>


<%
if (request.getAttribute(PersonSelectionServlet.INTERNAL_SEARCH_RESULT_TABLE) != null) {
%>
	<%=request.getAttribute(PersonSelectionServlet.INTERNAL_SEARCH_RESULT_TABLE) %>
<%
}
%>


<%@ include file="Footer.jsp" %>