<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
<head>
	<meta charset="ISO-8859-1">
	<title>Homepage</title>
</head>
<body>
	
	<%@ page import="com.mongodb.MongoClient" %>
	<%@ page import="com.mongodb.client.MongoDatabase" %>
	
	<%@ page import="org.bson.types.ObjectId" %>
	
	<%@ page import="com.therapy.servlets.Util" %>
	<%@ page import="com.therapy.entities.*" %>
	
	<%@ page import="java.util.Arrays" %>
	
	
	
	<%
		MongoClient mongoClient = Util.getMongoClient();
		MongoDatabase database = mongoClient.getDatabase(Util.DATABASE_NAME);
		
		Patient patient = new Patient((ObjectId)session.getAttribute("id"), database);
		Therapist therapist = patient.getTherapist();
		Chat chat = patient.getChat();
		
		session.setAttribute("isNewMessagingSession", true);
		session.setAttribute("chat", chat);
		
		//request.getRequestDispatcher("/asyncDisplayMessages").include(request, response);
		
	%>
	
	
	<script type="text/javascript">
		
	
		function loadNewMessages() {
			  var xhttp = new XMLHttpRequest();
			  xhttp.onreadystatechange = function() {
			    if (this.readyState == 4 && this.status == 200) {
			      //display
			      
			      var ptag = document.createElement("p");
				  var message = document.createTextNode("");
				  ptag.appendChild(message);
				  document.getElementById("otherUserMessageDiv").appendChild(ptag);
				  
			      loadNewMessages();
			    }
			  };
			  xhttp.open("GET", "ajax_info.txt", true);
			  xhttp.send(); 
	
	</script>
	
	
	<div id="otherUserMessageDiv">
		
	</div>
	
	<form method="post" action="/CulminatingVer8/addMessageAsync">
		<textarea rows="10" cols="55" name="textarea">Type your message here...
		</textarea>
	</form>
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	<!--  
	<script type="text/javascript">
		
		function displayMessage() {
			
			<% 
				Message[] newMessages = (Message[])session.getAttribute("newMessages");
				int length = newMessages.length;
			%>
			
			for(i = 0; i < <%=length%>; i++) {
				
				<%for(int i = 0; i < length; i++ ) { %>
				
				var ptag = document.createElement("p");
				var message = document.createTextNode("<%=newMessages[i].getContent()%>")
				ptag.appendChild(message);
				document.getElementById("otherUserMessageDiv").appendChild(ptag);
				
						
				<% } %>		
			}
			
			
		}
	
	</script>
	-->
	
	
	
	
</body>
</html>