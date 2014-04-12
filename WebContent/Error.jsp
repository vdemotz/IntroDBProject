<%@page import="ch.ethz.inf.dbproject.HomeServlet"%>


<table class = "contentTable">
<tr>
<td>
<h3>
<%= request.getAttribute(HomeServlet.REQUEST_ERROR_MESSAGE)%>
</h3>
</td>
</tr>
</table>