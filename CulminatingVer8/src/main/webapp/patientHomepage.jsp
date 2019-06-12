<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
<head>
	<meta charset="ISO-8859-1">
	<title>Homepage</title>
	<script src="https://code.jquery.com/jquery-1.10.2.js"></script>
	<link rel="stylesheet" type="text/css" href="patientHomepage.css">
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
		
		Patient patient = new Patient((ObjectId)session.getAttribute("id"), database);
		Chat chat = patient.getChat();
		
	
		//TODO: Make the chat only visible if the patient actually HAS a chat
		session.setAttribute("isNewMessagingSession", true);
		
	%>
	
	
	<!-- Messaging -->
	
	<div id="otherUserMessageDiv">
		<!-- therapist div -->
	</div>
	
	<textarea id="textarea" rows="10" cols="55" name="textarea" onkeyup="addMessage()">Type your message here...
			</textarea>
	
	<%
		if(chat == null) {
			out.println("<p>After accepting a therapist, your will be able to message theme here</p>");
		} else {
	%>
	
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
	
	
	
		
	
	<% } %>
	
	<!-- Search Therapists -->
	<form id="searchTherapists" method="post" action="/CulminatingVer8/searchTherapists">
		<input id="searchTherapistBox" name="targetTherapist" type="text" required>
	</form>
	
	<!-- Requests -->
	<form id="requests" action="patientRequests.jsp">
		<input type="submit" value="Requests">
	</form>
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	<!-- OLD WAY OF DOING THINGS
		function addMessage() {
			
			var key = window.event.keyCode;

		    // If ENTER was pressed
		    if (key === 13) {
		    	
		    	deleteAllCookies();
		    	
				alert("addMessage()");///////////////////////////DEBUG///////////////////////////////////////
				
				//Get the message text, set a cookie for it, and reset the textarea to an empty string
				var message = document.getElementById("textarea").innerHTML;
				document.getElementById("textarea").innerHTML = "";
				document.cookie="message=" + message;
				
				//AJAX call
				var xhttp = new XMLHttpRequest();
			    xhttp.open("POST", "/CulminatingVer8/addMessageAsync", true);
			    xhttp.send(); 
		    
		    }
			
		}
		
		function deleteAllCookies() {
			
		    var cookies = document.cookie.split("; ");
		    for (var c = 0; c < cookies.length; c++) {
		        var d = window.location.hostname.split(".");
		        while (d.length > 0) {
		            var cookieBase = encodeURIComponent(cookies[c].split(";")[0].split("=")[0]) + '=; expires=Thu, 01-Jan-1970 00:00:01 GMT; domain=' + d.join('.') + ' ;path=';
		            var p = location.pathname.split('/');
		            document.cookie = cookieBase + '/';
		            while (p.length > 0) {
		                document.cookie = cookieBase + p.join('/');
		                p.pop();
		            };
		            d.shift();
		        }
		    }
		}
		
		-->
	
	</script>
	
	
	
	
	
	
	
	<!-- 
	<form method="post" action="/CulminatingVer8/addMessageAsync">
		
	</form>
	-->
	
	

	
</body>
</html>