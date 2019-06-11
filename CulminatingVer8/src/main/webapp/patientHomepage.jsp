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
	
	
	
	
	<div id="otherUserMessageDiv">
		<!-- therapist div -->
	</div>
	
	<script type="text/javascript">
		
		function loadNewMessages() {
			
			  var xhttp = new XMLHttpRequest();
			  
			  xhttp.onreadystatechange = function() {
			    if (this.readyState == 4 && this.status == 200) {
			      
			      //Get the JSON object	
			      var newMessages = JSON.parse(this.responseText);
			      
			      //display
			      for(int i = 0; i < newMessages.length; i++) {
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
		
	</script>
	
	<textarea id="textarea" rows="10" cols="55" name="textarea" onkeyup="addMessage()">Type your message here...
		</textarea>
	
	<script type="text/javascript">
	
		
	
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
	
	</script>
	
	
	
	
	
	
	
	
	
	<!-- 
	<form method="post" action="/CulminatingVer8/addMessageAsync">
		
	</form>
	-->
	
	

	
</body>
</html>