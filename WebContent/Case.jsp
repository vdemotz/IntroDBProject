<%@page import="ch.ethz.inf.dbproject.model.User"%>
<%@page import="ch.ethz.inf.dbproject.model.CaseDetail"%>
<%@page import="ch.ethz.inf.dbproject.CaseServlet"%>
<%@page import="ch.ethz.inf.dbproject.HomeServlet"%>
<%@page import="ch.ethz.inf.dbproject.PersonSelectionServlet"%>
<%@page import ="ch.ethz.inf.dbproject.model.Category"%>
<%@page import ="java.util.List"%>
<%@page import="ch.ethz.inf.dbproject.util.UserManagement"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="Header.jsp" %>
<%
	final User user = (User) session.getAttribute(UserManagement.SHARED_SESSION_USER);
%>
<% final CaseDetail caseDetail = (CaseDetail) session.getAttribute("caseDetail"); %>


<h2>Case Details</h2>


<%
if (session.getAttribute(HomeServlet.SESSION_ERROR_MESSAGE) == null) {
%>

	<%=session.getAttribute(CaseServlet.SESSION_CASE_DETAIL_TABLE)%>
	
	<%
	if (user != null) {
	%>
		<form action=<%=CaseServlet.BASE_ADDRESS%> method="get">
			<input type="hidden" name=<%=CaseServlet.EXTERNAL_CASE_ID_PARAMETER%> value = <%=request.getParameter(CaseServlet.EXTERNAL_CASE_ID_PARAMETER)%> >
			<input type="hidden" name=<%=CaseServlet.EXTERNAL_USERNAME_PARAMETER%> value= <%=user.getUsername()%> />
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
	
	<%=session.getAttribute(CaseServlet.SESSION_CASE_TABLE)%>
	
	<%
	if (user != null && caseDetail.getIsOpen()) {
	//User is logged in. He can manage categories for the case
	%>	
		<form action=<%=CaseServlet.BASE_ADDRESS%> method="get">
		
			Category (select categories you want to change)<br>
			<select multiple name = "categories">
				<% List<Category> catList = (List<Category>) session.getAttribute(CaseServlet.SESSION_CASE_CATEGORIES); 
				for(int i = 0; i < catList.size(); i++) { %>
  				<option value=<%=catList.get(i).getName()%>><%=catList.get(i).getName() %></option>
  				<%} %>
			</select>
			<input type="hidden" name=<%=CaseServlet.EXTERNAL_CASE_ID_PARAMETER%> value = <%=request.getParameter(CaseServlet.EXTERNAL_CASE_ID_PARAMETER)%> >
			<input type="hidden" name = "action" value=<%=CaseServlet.INTERNAL_ACTION_CHANGE_CATEGORIES_VALUES%>>
			<input type="hidden" name=<%=CaseServlet.EXTERNAL_USERNAME_PARAMETER%> value= <%=user.getUsername()%> />
			<input type="submit" value="Change categories" />
		</form>
	<%
	}
	%>
	
	<%=session.getAttribute(CaseServlet.SESSION_SUSPECT_TABLE)%>
	
	
	<%
	if (user != null && caseDetail.getIsOpen()) {
		//User is logged in. She can add Suspects
		String returnAddressFromSelectPerson =  CaseServlet.BASE_ADDRESS + "?" + CaseServlet.EXTERNAL_CASE_ID_PARAMETER + "=" + request.getParameter(CaseServlet.EXTERNAL_CASE_ID_PARAMETER) +
							   "&" + CaseServlet.EXTERNAL_USERNAME_PARAMETER + "=" + user.getUsername();
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
	
	<%=session.getAttribute(CaseServlet.SESSION_CONVICT_TABLE)%>
	
	<%
	if (user != null && caseDetail.getIsOpen()) {
	//User is logged in. He can add convicts
	%>
		<form action=<%=PersonSelectionServlet.BASE_ADDRESS%> method="get">
			<input type="hidden" name=<%=PersonSelectionServlet.EXTERNAL_TITLE_PARAMETER%> value = "Add Convicts to Case">
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
	//User is logged in. She can add a comment
	%>	
		<form action=<%=CaseServlet.BASE_ADDRESS%> method="get">
			<input type="hidden" name=<%=CaseServlet.EXTERNAL_CASE_ID_PARAMETER%> value = <%=request.getParameter(CaseServlet.EXTERNAL_CASE_ID_PARAMETER)%> >
			<input type="hidden" name=<%=CaseServlet.EXTERNAL_USERNAME_PARAMETER%> value= <%= user.getUsername() %> />
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
	
	<br><br>
	
	<%=session.getAttribute(CaseServlet.SESSION_CASE_NOTE_TABLE)%>

<%
} else {
%>
 	<%@ include file="Error.jsp" %>
<%
}
%> 

<%@ include file="Footer.jsp"%>