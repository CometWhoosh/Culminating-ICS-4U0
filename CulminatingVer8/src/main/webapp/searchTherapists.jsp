<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
    
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

<!DOCTYPE html>
<html>
<head>
	<meta charset="ISO-8859-1">
	<title>Insert title here</title>
	<script src="https://code.jquery.com/jquery-1.10.2.js"></script>
</head>
<body>
	
	<%for(int i = 0; i < therapists.size(); i++){ %>
		
		<p><%=therapists.get(i).getFullName() %></p>
		
		<%if(therapists.get(i).getRating() != null) { %>
			<p>Rating: <%=therapists.get(i).getRating()%></p>
		<%} else { %>
			<p>Rating: None</p>
		<%}%>
		<%if(therapists.get(i).canReceiveRequests()) { %>
			<button type="button" value="<%=therapists.get(i).getId().toHexString()%>">Request</button>
		<%} else {%>
			<div style="background-color: #F6F4F4">
				<p>Full</p>
			</div>
		<%}%>
		
	<%}%>
	
	<!-- If one of the request buttons were clicked, add the request in /addRequestAsync servlet -->
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