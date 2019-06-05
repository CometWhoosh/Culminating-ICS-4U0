<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title>Insert title here</title>
</head>
<body>

<!-- TODO: Add text box for optional summary -->

	<h1>Choose a Therapist</h1>
	
	<%@ page import="java.util.Map" %>
	<%@ page import="java.util.LinkedHashMap" %>
	
	<% 
		Map<String, Integer> therapists = (LinkedHashMap<String, Integer>)request.getAttribute("retrievedTherapists");
		String[] names = therapists.keySet().toArray(new String[10]);
		Integer[] ratings = therapists.values().toArray(new Integer[10]);
		
		String[] therapistIds = (String[])request.getAttribute("therapistIds");
	%>
	
	<!-- REQUIRES servlet to have the path //requestTherapist -->
	
	<form method="post" id="form" action="/CulminatingVer8/requestTherapist">
	
		<% for(int i = 0; i < 10; i++) { %>
			<lable><input type="radio" name="therapist" value="<%=therapistIds[i]%>"><%=names[i]%></lable>
			<% out.println(ratings[i]); %>
			<br>
		<% } %>
		
		<input type="submit" value="Next">
	
	</form>
	
	<textarea rows="10" cols="55" id="textarea" form="form">
		Enter summary of you mental health issues (optional) ...
	</textarea>
	
	
	<form method="get" action="/CulminatingVer8/getTherapists">
		<input type="submit" value="Get different therapists">
	</form>
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	<!--  get elements by name in javascript
		  set their value attributes to the mongo id
	-->
	
	
	<script type="text/javascript">
		
	</script>
	
	

</body>
</html>