<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>

<% 
	String emailNotFoundParameter = request.getParameter("emailNotFound"); 
	String passwordMismatchParameter = request.getParameter("passwordMismatch");
%>

<!DOCTYPE html>
<html>
<head>
	<meta charset="ISO-8859-1">
	<title>Login</title>
</head>
<body>

    <h1>Login</h1>
    
    <form method="post" action="/CulminatingVer8/loginServlet">
        <p>I am a:</p>
        <select name="userType" required>
			<option>Patient</option>
			<option>Therapist</option>
		</select>
		<br>

        <%if(emailNotFoundParameter.equals("1")) { %>
			<p style="color:red">The email could not be found</p>
		<%}%>
			
		<p>Email</p>
        <input name="email" type="text" required>
        <br>

        <%if(passwordMismatchParameter.equals("1")) { %>
			<p style="color:red">Incorrect Password</p>
		<%}%>

        <p>Password<p>
        <input name="password" type="password" required> 
        <br>
		<input type="submit" value="Next">
    </form>
    
    <p>Don't have an account?</p>
    <a href="signUp.jsp">Sign up</a>

</body>

</html>