package com.therapy.servlets;


import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Arrays;
import java.util.Base64;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.bson.Document;
import org.bson.types.Binary;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import static com.mongodb.client.model.Filters.eq;

/**
 * This class is a servlet that takes the data from the HTTP request attributes
 * set by login.jsp and uses them to log in a user.
 *
 * @author Yousef Bulbulia
 * 
 */
@WebServlet("/loginServlet")
public class LoginServlet extends HttpServlet {

	private static final String serverIpAddress = "10.12.195.177"; 
	private static final String serverPort = "8080";
	private static final String projectPath = "http://" + serverIpAddress + ":8080/CulminatingVer8";
	
	/**
	 * Handles the form data. If the email exists in the database, then
	 * the users login information is authenticated. If the password 
	 * does not match, or the email does not exist in the database, then
	 * the user is sent back to login.jsp
	 * 
	 * If the user is a patient, they are forwarded to patientHomepage.jsp, and
	 * if they are a therapist, they are forwarded to therapistHomepage.html.
	 */
	@Override
	public void doPost(HttpServletRequest request, 
			HttpServletResponse response) throws IOException, ServletException {
		
		//Get the form data
		String email = request.getParameter("email");
		String password = request.getParameter("password");
		String userType = request.getParameter("userType");
		
		//Get the collection
		MongoClient mongoClient = Util.getMongoClient();
		MongoDatabase database = mongoClient.getDatabase(Util.DATABASE_NAME);
		Util.intialiazeDatabase();
		MongoCollection<Document> collection = Util.getUserAppropriateCollection(database, userType);
		
		//Get the user's hashed password and salt from database
		Document userDoc = null;
		byte[] salt = null;
		byte[] hashedPassword = null;
		if( (userDoc = collection.find(eq("email", email)).first()) != null) {
			
			salt = userDoc.get("salt", Binary.class).getData();
			hashedPassword = userDoc.get("password_hash", Binary.class).getData();
			
		} else {
			
			response.sendRedirect(projectPath + "/login.jsp?emailNotFound=1");
			return;
			
		}
		
		//Hash the hash of the password given from the form
		KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 65536, 128);
		SecretKeyFactory factory = null;
				
		try {
			factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
		} catch(NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
						
		byte[] hash = null;
				
		try {
			hash = factory.generateSecret(spec).getEncoded();
		} catch(InvalidKeySpecException e) {
			e.printStackTrace();
		}
		
		//Check if password hashes match
		if(Arrays.equals(hash, hashedPassword)) {
			
			HttpSession session = request.getSession();
			session.setAttribute("id", userDoc.getObjectId("_id"));
			session.setAttribute("userType", userType);
			session.setMaxInactiveInterval(-1);
			
			if(userType.equals("Patient")) {

				request.getRequestDispatcher("/patientHomepage.jsp").forward(request, response);
				Util.LOGGER.info("Patient with id " + userDoc.getObjectId("_id") + "was successfully"
						+ "logged in and forwarded to /patientHomepage.jsp");
				return;
				
			} else {
				
				request.getRequestDispatcher("/therapistHomepage.jsp").forward(request, response);
				Util.LOGGER.info("Therapist with id " + userDoc.getObjectId("_id") + "was successfully"
						+ "logged in and forwarded to /therapistHomepage.jsp");
				return;
				
			}
			
		} else {
			
			response.sendRedirect(projectPath + "/login.jsp?passwordMismatch=1");
			Util.LOGGER.info(userType + " with id " + userDoc.getObjectId("_id") + "did not provide"
					+ "the correct password and was redirected back to /login.jsp");
			return;
			
		}
		
	}
	
}