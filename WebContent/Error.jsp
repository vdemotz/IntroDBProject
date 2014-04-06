<%@page import="ch.ethz.inf.dbproject.HomeServlet"%>


<table class = "contentTable">
<tr>
<td>
<h3>
<%= session.getAttribute(HomeServlet.SESSION_ERROR_MESSAGE)%>
</h3>
</td>
</tr>
</table>
<%session.setAttribute(HomeServlet.SESSION_ERROR_MESSAGE, null);%>