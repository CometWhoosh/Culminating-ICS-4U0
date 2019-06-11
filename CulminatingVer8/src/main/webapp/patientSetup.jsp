<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
<head>
	<meta charset="ISO-8859-1">
	<title>Setup</title>
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
	
	<form method="post" action="/CulminatingVer8/requestTherapist">
	
		<% for(int i = 0; i < 10; i++) { %>
			<lable><input type="radio" name="therapist" value="<%=therapistIds[i]%>"><%=names[i]%></lable>
			<% out.println(ratings[i]); %>
			<br>
		<% } %>
		
		<textarea rows="10" cols="55" name="textarea">
			Enter summary of you mental health issues (optional) ...
		</textarea>
		
		<br>
		
		<input type="submit" value="Next">
	
	</form>
	
	<form method="post" action="/CulminatingVer8/getTherapists">
		<input type="submit" value="Get different therapists">
	</form>
	
	
	<!--
	<script type="text/javascript">
		
	</script>
	-->
	

</body>
</html>