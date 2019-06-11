<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
	<meta charset="ISO-8859-1">
	<title>Sign Up</title>
</head>
<body>


	<h1>Sign Up</h1>
	<!--onsubmit="return verifyNumberOnlyInput()"-->
	<form method="post"  action="/CulminatingVer8/signUpServlet">
	
		<p>I am a:</p>
		<select name="userType" id="userType" onchange="checkIfTherapist()" required>
			<option value="Patient">Patient</option>
			<option value="Therapist">Therapist</option>
		</select> <br>
		
		<p>First name</p>
		<input name="first_name" type="text" required> <br>
		
		<p>Last name</p>
		<input name="last_name" type="text" required> <br>
		
		<p>Email</p>
		<input name="email" type="text" required> <br>
		
		<p>Password<p>
		<input name="password" type="password" required> <br> 
		
		<p id="patientLimit" style="display: none">What is the maximum number of patients you will take on?</p>
		<input name="patientLimitInput" id="patientLimitInput" type="text" style="display: none"> 
		<br id="break" style="display: none">
		
		<input type="submit" value="Next">
		
	</form>
	
	<script type="text/javascript">
	
		function checkIfTherapist() {

			var patientLimitInput = document.getElementById("patientLimitInput");
			var patientLimit = document.getElementById("patientLimit");
			var breakTag = document.getElementById("break");
			var userType = document.getElementById("userType");
			
			if(userType.options[userType.selectedIndex].value == "Therapist") {
				breakTag.style.display = "block";
				patientLimitInput.style.display = "block";
				patientLimit.style.display = "block";
			} else {
				breakTag.style.display = "none";
				patientLimitInput.style.display = "none";
				patientLimit.style.display = "none";
			}
			
		}
		
		<!--
		function verifyNumberOnlyInput() {
			
			var patientLimitInput = document.getElementById("patientLimitInput").value;
			var re = /[0-9]*/;
			
			var verified = re.exec(patientLimitInput);
			
			if(!verified) {
				alert("Patient limit can only be a number");
				return false;
			}
			
			return true;
			
		}
		-->
		
	</script>


	<%
	
		String emailErrorParameter = request.getParameter("emailError");
		String databaseErrorParameter = request.getParameter("databaseError");
		String numberFormatError = request.getParameter("numberFormatError");
		
		try {
			
			if(emailErrorParameter.equals("1")) {
				out.println("<p style=\"color:red;\">This email is already registered with an account</p>");
			}
			
			if(databaseErrorParameter.equals("1")) {
				out.println("<p style=\"color:red;\">There was an error writing to the database. Please try again</p>");
			}
			
			if(numberFormatError.equals("1")) {
				out.println("<p style=\"color:red;\">Patient limit must be a number</p>");
			}
			
		} catch(NullPointerException e) {
			
		}
	
	%>
	
	<%
		String string = "Hi";
		out.println("hi");
	%>
	
	<script type="text/javascript">
		function o() {
			
			<%
				out.println("hi");
			%>
			
		}
	</script>	
	
	
	<p>Already have an account?<p>
	<a href="login.jsp">Log in</a>
	
</body>
</html>