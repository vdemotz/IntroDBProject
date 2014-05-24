<%@page import="ch.ethz.inf.dbproject.ConvictionCreationServlet"%>
<%@page import="ch.ethz.inf.dbproject.PersonSelectionServlet"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<html>
	
	<head>
	    <link href="style.css" rel="stylesheet" type="text/css">
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<title>Law Enforcement Project</title>
	</head>

	<body>

		<!-- Header -->
		
		<table id="masterTable" cellpadding="0" cellspacing="0">
			<tr>
				<th id="masterHeader" colspan="2">
					<h1>LFORCE</h1>
				</th>
			</tr>
			<tr id="masterContent">
			
				<td id="masterContentMenu">
					
					<div class="menuDiv1"></div>
					<div class="menuDiv1"><a href="Home">Home</a></div>
					<div class="menuDiv1"><a href="Cases">All Cases</a></div>
					<div class="menuDiv2"><a href="Cases?filter=open">Open</a></div>
					<div class="menuDiv2"><a href="Cases?filter=closed">Closed</a></div>
					<div class="menuDiv2"><a href="Cases?filter=recent">Recent</a></div>
					<div class="menuDiv2"><a href="Cases?filter=oldest">Oldest Unsolved</a></div>
					<div class="menuDiv1"><a href="Search">Search</a></div>
					<div class="menuDiv1"><a href=<%= "PersonSelection?" + 
							PersonSelectionServlet.EXTERNAL_TITLE_PARAMETER + "=Select+the+convict+for+the+conviction&" +
							PersonSelectionServlet.EXTERNAL_RETURN_ADDRESS_PARAMETER + "=" + ConvictionCreationServlet.BASE_ADDRESS + "?" + ConvictionCreationServlet.INTERNAL_ACTION_PARAMETER + "=" + ConvictionCreationServlet.INTERNAL_ACTION_SELECT_PERSON_PARAMETER_VALUE
													%>>Add Conviction</a></div>
					<div class="menuDiv1"><a href="PersonsOfInterest">Persons of Interest</a></div>
					<div class="menuDiv2"><a href="PersonCreation">Create new person</a></div>
				</td>
				
				<td id="masterContentPlaceholder">
				
		