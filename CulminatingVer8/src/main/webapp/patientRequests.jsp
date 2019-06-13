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
	
	<!-- TODO: There shouldn't be an option to accept a request while it is still pending on the 
		 	   therapist to accept it -->
	
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
			String id = "p" + i;
			if(requests[i].therapistAccepted()) {
				out.print("<p style=\"background-color:lightblue\" id=\"" + id + "\">therapist Accepted!</p>");
			} else {
				
				out.print("<p style=\"background-color:grey\" id=\"" + id + "\">Pending</p>");
			}
		
		%>
		
		<!-- If the therapist accepted, then display an option for the patient to accept -->
		<%if(requests[i].therapistAccepted()) { %> 
			<button type="button" value="<%=requests[i].getId().toHexString()%>">Accept</button>
		 <%}%> 
		 
		
		 
		 <!--  TRY THE NEW WAY FIRST
		<script type="text/javascript">
			
			$("button").click(function(event) {
				$.post("/CulminatingVer8/asyncAcceptRequest", 
						{ 
							id: "<%//=requests[i].getId().toHexString()%>" 
						},
						function() {
							document.getElementById("<%//="p" + i%>").style.backgroundColor = "grey"; 
						});
			});
		</script>
		-->
		
	<% } %>
	
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