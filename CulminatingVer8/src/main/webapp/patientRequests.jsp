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
	Request[] requests = patient.getRequests();
		
%>
    
<!DOCTYPE html>
<html>
<head>
	<meta charset="ISO-8859-1">
	<title>Requests</title>
	<script src="https://code.jquery.com/jquery-1.10.2.js"></script>
</head>
<body>

	<%for(int i = 0; i < requests.length; i++) { %>
		
		<p><%=requests[i].getTherapist().getFullName() + ":"%></p>
		
		<%String id = "p" + i;
		  if(requests[i].therapistAccepted()) { %>
			<p style="background-color:lightblue" id="<%=i%>">therapist Accepted!</p>
		<%} else {%>
			<p style="background-color:grey" id="<%=i%>">Pending</p>
		<%}%>
		
		<!-- If the therapist accepted, then display an option for the patient to accept -->
		<%if(requests[i].therapistAccepted()) { %> 
			<button id="accept" type="button" value="<%=requests[i].getId().toHexString()%>">Accept</button>
		 <%}%> 
		
	<% } %>
	
	<!-- If the "accept" button was clicked, then accept the request in /asyncAcceptRequet servlet -->
	<script type="text/javascript">
		
			$("button").click(function(event) {
				$.post("/CulminatingVer8/asyncAcceptRequest", 
						{ 
							id: this.value 
						},
						function() { 
							//Replace the accept button with a sign that says "accepted"
							var pendingSign = document.createElement("p");
							acceptedSign.innherHTML = "Accepted";
							acceptedSign.style.backgroundColor = "grey";
							this.replaceWith(acceptedSign);
						});
			});
			
	</script>
	
</body>
</html>