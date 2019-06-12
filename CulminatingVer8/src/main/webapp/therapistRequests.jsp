<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
<head>
	<meta charset="ISO-8859-1">
	<title>Insert title here</title>
	<script src="https://code.jquery.com/jquery-1.10.2.js"></script>
</head>
	<body>
	
	<!-- TODO: After accepting, the're should be a sign saying "pending" -->
	
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
	
	<!--
	<button id="myButton" class="float-left submit-button" >Home</button>

	<script type="text/javascript">
	    document.getElementById("myButton").onclick = function () {
	        location.href = "www.yoursite.com";
	    };
	</script>
	-->
	
	<%
		if(requests != null) {
		for(int i = 0; i < requests.length; i++) { 
		
	%>
		
		<p><%=requests[i].getPatient().getFullName() + ":"%></p>
		
		<button type="button" value="<%=requests[i].getId().toHexString()%>">Accept</button>
		
		
		<!--  TRY THE OTHER WAY FIRST
		<script type="text/javascript">
		
			$("button").click(function(event) {
				$.post("/CulminatingVer8/asyncAcceptRequest", 
						{ 
							id: "<%/*=requests[i].getId().toHexString()*/%>"
						},
						function() {
							var button = document.getElementById("<%/*="button" + i*/%>");
							var pendingSign = document.createElement("p");
							pendingSign.innherHTML = "Pending";
							pendingSign.style.backgroundColor = "grey";
							button.replaceWith(pendingSign);
						});
			});
			
		</script>
		-->
		
	<% 
			}
		} else {
			out.println("You have no requests at the moment...");	
		}
	%>
	
	<script type="text/javascript">
		
			$("button").click(function(event) {
				$.post("/CulminatingVer8/asyncAcceptRequest", 
						{ 
							id: this.value 
						},
						function() 
							//Replace the accept button with a sign that says "pending"
							var pendingSign = document.createElement("p");
							pendingSign.innherHTML = "Pending";
							pendingSign.style.backgroundColor = "grey";
							this.replaceWith(pendingSign);
						});
			});
			
		</script>
	
	
	
	
	
	
	
	
	
	
	<!-- 
	
	<% /*
		
			if(requests[i].therapistAccepted()) {
				out.print("<p style=\"background-color:lightblue\">therapist Accepted!</p>");
			} else {
				String id = "p" + i;
				out.print("<p style=\"background-color:grey\" id=\"" + id + "\">Pending</p>");
			}
		*/
		%>
	
	 -->
	
</body>
</html>