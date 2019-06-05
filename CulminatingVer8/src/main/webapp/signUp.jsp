<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<header>
	
</header>
<body>

	<h1>Sign Up</h1>

	<form method="post" action="/CulminatingVer8/signUpServlet">
	
		<p>I am a:</p>
		<select name="userType" required>
			<option>Patient</option>
			<option>Therapist</option>
		</select> <br>
		
		<p>First name</p>
		<input name="first_name" type="text" required> <br>
		
		<p>Last name</p>
		<input name="last_name" type="text" required> <br>
		
		<p>Email</p>
		<input name="email" type="text" required> <br>
		
		<p>Password<p>
		<input name="password" type="password" required> <br> <br>
		
		<input type="submit" value="Next">
		
	</form>


	<%
	
		String emailErrorParameter = request.getParameter("emailError");
		String databaseErrorParameter = request.getParameter("databaseError");
	
		try {
			
			if(emailErrorParameter.equals("1")) {
				out.println("<p style=\"color:red;\">This email is already registered with an account</p>");
			}
			
			if(databaseErrorParameter.equals("1")) {
				out.println("<p style=\"color:red;\">There was an error writing to the database. Please try again</p>");
			}
			
		} catch(NullPointerException e) {
			
		}
	
	%>
	
	
	<p>Already have an account?<p>
	<a href="login.jsp">Log in</a>
	
</body>
</html>