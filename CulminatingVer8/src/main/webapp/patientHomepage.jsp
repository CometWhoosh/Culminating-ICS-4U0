<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
    
<%@ page import="com.mongodb.MongoClient" %>
<%@ page import="com.mongodb.client.MongoDatabase" %>
	
<%@ page import="org.bson.types.ObjectId" %>
	
<%@ page import="com.therapy.servlets.Util" %>
<%@ page import="com.therapy.entities.*" %>
	
<%

	MongoClient mongoClient = Util.getMongoClient();
	MongoDatabase database = mongoClient.getDatabase(Util.DATABASE_NAME);
		
	Patient patient = new Patient((ObjectId)session.getAttribute("id"), database);
	Chat chat = patient.getChat();
	
	session.setAttribute("isNewMessagingSession", true);
		
%>

<!DOCTYPE html>
<html>
<head>
	<meta charset="ISO-8859-1">
	<title>Homepage</title>
	<link rel="stylesheet" type="text/css" href="patientHomepage.css">
</head>
<body>
	
	<%if(chat == null) { %>
		<p>After accepting a therapist, you will be able to message them here</p>	
	<%} else {%>
	
		<!-- Messaging -->
		
		<!-- Messages will be added to these divs dynamically -->
		<div id="therapistMessagesDiv"></div>
		<div id="patientMessagesDiv"></div>
		
		<!-- Textarea where new messages should be typed -->
		<textarea id="textarea" rows="10" cols="55" name="textarea" onkeyup="addMessage()">Type your message here...
		</textarea>
	
		<!-- Add messages that were entered into the textarea -->
		<script src="addMessage.js"></script>
	
		<!-- Display any new messages -->
		<script src="loadMessages.js"></script>
		<script type="text/javascript">
			loadMessages();
		</script>
		
	<%}%>
	
	<!-- Search Therapists -->
	<form id="searchTherapists" method="post" action="/CulminatingVer8/searchTherapists">
		<input id="searchTherapistBox" name="targetTherapist" type="text" required>
	</form>
	
	<!-- Requests -->
	<form id="requests" action="patientRequests.jsp">
		<input type="submit" value="Requests">
	</form>
	
</body>
</html>