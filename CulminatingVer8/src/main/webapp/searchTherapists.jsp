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
	
	<%@ page import="com.mongodb.MongoClient" %>
	<%@ page import="com.mongodb.client.MongoDatabase" %>
	<%@ page import="com.mongodb.client.MongoCollection" %>
	<%@ page import="org.bson.Document" %>
	
	<%@ page import="org.bson.types.ObjectId" %>
	
	<%@ page import="com.therapy.servlets.Util" %>
	<%@ page import="com.therapy.entities.*" %>
	
	<%@ page import="java.util.List" %>
	
	<%
		//Connect to the database
		MongoClient mongoClient = Util.getMongoClient();
		MongoDatabase database = mongoClient.getDatabase(Util.DATABASE_NAME);
		MongoCollection<Document> collection = database.getCollection("therapists");
	
		List<Therapist> therapists = (List<Therapist>)request.getAttribute("matchingTherapists");
		
	%>
	
	
	<!-- Instead of having to use id's for each element in the loop, just make a div for each iteration,
	then make the elements of divs line up in a line in css -->
	<%for(int i = 0; i < therapists.size(); i++){ %>
		
		<p><%=therapists.get(i).getFullName() %></p>
		<p>Rating: <%=therapists.get(i).getRating()%></p>
		
		<%if(therapists.get(i).canReceiveRequests()) { %>
			<button type="button" value="<%=therapists.get(i).getId().toHexString()%>">Request</button>
		<%} else {%>
			<div style="background-color: #F6F4F4">
				<p>Full</p>
			</div>
		<%}%>
		
	<%}%>
	
	<!-- Request button functionality -->
	<script type="text/javascript">
		
		$("button").click(function(event) {
			$.post("/CulminatingVer8/addRequestAsync", 
					{ 
						id: this.value 
					},
					function() {
						//Replace the accept button with a sign that says "pending"
						var pendingSign = document.createElement("p");
						requestSentSign.innherHTML = "Request sent";
						requestSentSign.style.backgroundColor = "#B0E3B2";
						this.replaceWith(requestSentSign);
					});
		});
	
	</script>
	
</body>
</html>