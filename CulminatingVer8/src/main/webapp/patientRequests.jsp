<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
<head>
	<meta charset="ISO-8859-1">
	<title>Requests</title>
	<script src="https://code.jquery.com/jquery-1.10.2.js"></script>
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
		Request[] requests = patient.getRequests();
		
	%>
	
	<!--
	<button id="myButton" class="float-left submit-button" >Home</button>

	<script type="text/javascript">
	    document.getElementById("myButton").onclick = function () {
	        location.href = "www.yoursite.com";
	    };
	</script>
	-->
	
	<%for(int i = 0; i < requests.length; i++) { %>
		
		<p><%=requests[i].getTherapist().getFullName() + ":"%></p>
		
		<%
		
			if(requests[i].therapistAccepted()) {
				out.print("<p style=\"background-color:lightblue\">therapist Accepted!</p>");
			} else {
				String id = "p" + i;
				out.print("<p style=\"background-color:grey\" id=\"" + id + "\">Pending</p>");
			}
		
		%>
		
		<button id="button<%=i%>" type="button" value="<%=requests[i].getId().toHexString()%>">Accept</button>
		
		<script type="text/javascript">
			//In the post request, patient should accept() the request
			$("button").click(function(event) {
				$.post("/CulminatingVer8/asyncAcceptRequest", 
						{ 
							id: "<%=requests[i].getId().toHexString()%>" //the _id of the request as a hex string
						},
						function() {
							document.getElementById("<%="p" + i%>").style.backgroundColor = "grey"; //change the colour of the accept button when done
						});
			});
		</script>
		
	<% } %>
	
</body>
</html>