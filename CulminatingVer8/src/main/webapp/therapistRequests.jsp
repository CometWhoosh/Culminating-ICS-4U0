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
	
	Therapist therapist = new Therapist((ObjectId)session.getAttribute("id"), database);
	Request[] requests = therapist.getRequests();
		
%>    

<!DOCTYPE html>
<html>
<head>
	<meta charset="ISO-8859-1">
	<title>Insert title here</title>
	<script src="https://code.jquery.com/jquery-1.10.2.js"></script>
</head>
	<body>
	
	<%if(requests != null) {%>
		<%for(int i = 0; i < requests.length; i++) { %>
			<p><%=requests[i].getPatient().getFullName() + ":"%></p>
			<button type="button" value="<%=requests[i].getId().toHexString()%>">Accept</button>
		<%}%>
	<%} else { %>
			<p>You have no requests at the moment...</p>
	<%}%>
	
	<!-- When one of the buttons are pressed, accept the corresponding request in /asyncAcceptRequest -->
	<script type="text/javascript">
		
			$("button").click(function(event) {
				$.post("/CulminatingVer8/asyncAcceptRequest", 
						{ 
							id: this.value 
						},
						function() {
							//Replace the accept button with a sign that says "pending"
							var pendingSign = document.createElement("p");
							pendingSign.innherHTML = "Pending";
							pendingSign.style.backgroundColor = "grey";
							this.replaceWith(pendingSign);
						});
			});
			
	</script>
	
</body>
</html>