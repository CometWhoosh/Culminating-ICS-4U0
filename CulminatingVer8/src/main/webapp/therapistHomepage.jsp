<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
<head>
	<meta charset="ISO-8859-1">
	<title>Insert title here</title>
	<script src="https://code.jquery.com/jquery-1.10.2.js"></script>
	<link rel="stylesheet" type="text/css" href="therapistHomepage.css">
</head>
<body>
	
	<%@ page import="com.mongodb.MongoClient" %>
	<%@ page import="com.mongodb.client.MongoDatabase" %>
	
	<%@ page import="org.bson.types.ObjectId" %>
	
	<%@ page import="com.therapy.servlets.Util" %>
	<%@ page import="com.therapy.entities.*" %>
	
	<%
		
		//TODO: Make the chat only visible if the therapist actually HAS a chat
		
		MongoClient mongoClient = Util.getMongoClient();
		MongoDatabase database = mongoClient.getDatabase(Util.DATABASE_NAME);
		
		Therapist therapist = new Therapist((ObjectId)session.getAttribute("id"), database);
		Chat[] chats = therapist.getChats();
			
		session.setAttribute("isNewMessagingSession", true);
		
	%>
	
	
	<!-- Messaging -->

	<%if(chats != null) { %>
	
		<form method="post" action="therapistMessenger.jsp">
			
			<%for(int i = 0; i < chats.length; i++) { %>
				<input type="submit" value="<%=chats[i].getPatient().getFullName()%>">
			<%}%>
			
		</form> 
	
	<%}%>
	
	<!-- Requests -->
	
	<form action="therapistRequests.jsp">
		<input type="submit" value="Requests">
	</form>
	
	<!-- Messaging -->
	<form action="therapistMessenger.jsp">
	
	
	
		<input type="submit" value="Go to chats">
		
		
	</form>
	
</body>
</html>