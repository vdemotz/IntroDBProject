<%@page import="ch.ethz.inf.dbproject.ConvictionCreationServlet"%>
<%@page import="ch.ethz.inf.dbproject.PersonSelectionServlet"%>
<%@page import="ch.ethz.inf.dbproject.util.UserManagement"%>
<%@page import="ch.ethz.inf.dbproject.model.User"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="Header.jsp" %>

<%
final User user = (User) session.getAttribute(UserManagement.SHARED_SESSION_USER);
%>

<h2>Create new Conviction</h2>

<% 
if (user == null) {
%>
	<%@ include file="PleaseLogin.jsp" %>


<%
} else {
	//User is logged in. He can add convicts
%>
	
	<%
	if (session.getAttribute(ConvictionCreationServlet.SESSION_ERROR_MESSAGE) != null) {
	%>
		<%=session.getAttribute(ConvictionCreationServlet.SESSION_ERROR_MESSAGE) %>
		<br><br>
		
	<%	session.setAttribute(ConvictionCreationServlet.SESSION_ERROR_MESSAGE, null);
	}
	%>
	
	<%
	Object selectedPersonTable = session.getAttribute(ConvictionCreationServlet.SESSION_SELECTED_PERSON);
	if (selectedPersonTable == null || request.getParameter(PersonSelectionServlet.EXTERNAL_RESULT_PERSON_ID_PARAMETER) == null) {
		//No person selected yet
		String returnAddressFromSelectPerson =  ConvictionCreationServlet.BASE_ADDRESS + "?" + ConvictionCreationServlet.INTERNAL_ACTION_PARAMETER + "=" + ConvictionCreationServlet.INTERNAL_ACTION_SELECT_PERSON_PARAMETER_VALUE;
	%>
	
		<h3>Select a Convict</h3>
		
		<form action=<%=PersonSelectionServlet.BASE_ADDRESS%> method="get">
			<input type="hidden" name=<%=PersonSelectionServlet.EXTERNAL_TITLE_PARAMETER%> value = "Select Convict">
			<input type="hidden" name=<%=PersonSelectionServlet.EXTERNAL_RETURN_ADDRESS_PARAMETER%> value = <%=returnAddressFromSelectPerson%>>
			<input type="submit" value="Add Convict" />
		</form>
	<%
		} else {
			//Person selected, display person and select date, caseId
	%> 
		<h3>Selected Person</h3>
		<%=selectedPersonTable%>
		
		<br>
		Select dates of the conviction and the case the conviction is associated with
		<form action=<%=ConvictionCreationServlet.BASE_ADDRESS%> method="get">
		<input type="hidden" name= <%=ConvictionCreationServlet.INTERNAL_ACTION_PARAMETER%> value=<%=ConvictionCreationServlet.INTERNAL_ACTION_CREATE_PARAMETER_VALUE%> />
		<input type="hidden" name= <%=PersonSelectionServlet.EXTERNAL_RESULT_PERSON_ID_PARAMETER%> value=<%=request.getParameter(PersonSelectionServlet.EXTERNAL_RESULT_PERSON_ID_PARAMETER)%> >
		<table>
			<tr>
				<th>Start Date(yyyy-mm-dd)</th>
				<td><input type="text" name=<%=ConvictionCreationServlet.INTERNAL_START_DATE_PARAMETER %> value=""></td>
			</tr>
			<tr>
				<th>End Date(yyyy-mm-dd)</th>
				<td><input type="text" name=<%=ConvictionCreationServlet.INTERNAL_END_DATE_PARAMETER %> value=""></td>
			</tr>
			<tr>
				<th>Case Id (Optional)</th>
				<td><input type="text" name=<%=ConvictionCreationServlet.INTERNAL_CASE_ID_PARAMETER %> value=""></td>
			</tr>
			<tr>
				<th colspan="2">
					<input type="submit" value="Submit" />
				</th>
			</tr>
		</table>
		</form>
	<%
	}
	%>
<%
}
%>

<%@ include file="Footer.jsp" %>