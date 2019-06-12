<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
<head>
	<meta charset="ISO-8859-1">
	<title>Insert title here</title>
</head>
<body>
	
	<%@ page import="com.mongodb.MongoClient" %>
	<%@ page import="com.mongodb.client.MongoDatabase" %>
	
	<%@ page import="org.bson.types.ObjectId" %>
	
	<%@ page import="com.therapy.servlets.Util" %>
	<%@ page import="com.therapy.entities.*" %>
	
	<%
		MongoClient mongoClient = Util.getMongoClient();
		MongoDatabase database = mongoClient.getDatabase(Util.DATABASE_NAME);	
		
		//Get Therapist and patient name
		Therapist therapist = new Therapist((ObjectId)session.getAttribute("id"), database);
		String targetPatientName = (String)request.getAttribute("patientChats");
		
		//Get the chat that belongs to the therapist and the specified patient
		Chat[] chats = therapist.getChats();
		Chat chat = null;
		for(int i = 0; i < chats.length; i++) {
			
			//If the patient names match, get the chat for the patient and therapist
			String patientName = chats[i].getPatient().getFullName();
			if(targetPatientName.equals(patientName)) {
				chat = new Chat(chats[i].getPatient(), therapist, database);
			}
			
		}
		
		session.setAttribute("isNewMessagingSession", true);
		session.setAttribute("chat", chat);
		
	%>
	
	<textarea id="textarea" rows="10" cols="55" name="textarea" onkeyup="addMessage()">Type your message here...
		</textarea>
		
	<div id="otherUserMessageDiv">
		<!-- therapist div -->
	</div>
	
	<!-- Display any new messages -->
	<script type="text/javascript">
			
		function loadNewMessages() {
				
			var xhttp = new XMLHttpRequest();
				  
			xhttp.onreadystatechange = function() {
				if (this.readyState == 4 && this.status == 200) {
				      
					//Get the JSON object	
					var newMessages = JSON.parse(this.responseText);
					      
					//display
					for(i = 0; i < newMessages.length; i++) {
					var ptag = document.createElement("p");
				   		var message = document.createTextNode(newMessages[i][0]);
						ptag.appendChild(message);
						document.getElementById("otherUserMessageDiv").appendChild(ptag);
				    }
					  
				  	//Recursively call the function
				 	loadNewMessages();
				      
				}
			};
				  
			xhttp.open("POST", "/asyncDisplayMessages", true);
			xhttp.send(); 
		
		}
			
		function addMessage() {
				
			//Get key that was released
			var key = window.event.keyCode;
				
			var patientMessage = document.getElementById("textarea").innerHTML;
			document.getElementById("textarea").innerHTML = "";
				
			// If ENTER was pressed
			if (key === 13) {
			    	
				$.post("/CulminatingVer8/addMessageAsync",
			    	{
			    		message : patientMessage
			    	});
			    	
			}
				
		}
			
		loadNewMessages();
			
	</script>
	
	
	
	
</body>
</html>