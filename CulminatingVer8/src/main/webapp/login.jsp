<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
<head>
	<meta charset="ISO-8859-1">
	<title>Login</title>
</head>
<body>

	<h1>Login</h1>

	<form class="a" method="post" action="/CulminatingVer8/loginServlet">
	
		<p>I am a:</p>
		<select name="userType" required>
			<option>Patient</option>
			<option>Therapist</option>
		</select> <br>
		
		<%
			String emailNotFoundParameter = request.getParameter("emailNotFound");
			try {
				if(emailNotFoundParameter.equals("1")) {
					out.println("<p style=\"color:red;\">The email could not be found</p>");
				}
			} catch(NullPointerException e) { }
		%>
		
		<p>Email</p>
		<input name="email" type="text" required> <br>
		
		<%
			String passwordMismatchParameter = request.getParameter("passwordMismatch");
			try {
				if(passwordMismatchParameter.equals("1")) {
					out.println("<p style=\"color:red;\">Incorrect Password</p>");
				}
			} catch(NullPointerException e) { }
		%>
		
		<p>Password<p>
		<input name="password" type="password" required> <br> <br>
		
		<input type="submit" value="Next">
		
	</form>
	
	<p id="pTest">Male</p>
	<button>change</button>
	
	<script type="text/javascript">
	$('button').click(function(){
	     $('#pTest').text('test')
	})
	</script>
	
	<p>Don't have an account?<p>
	<a href="signUp.jsp">Sign up</a>

</body>
</html>