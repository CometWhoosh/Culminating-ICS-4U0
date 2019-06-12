package com.therapy.servlets;

import static com.mongodb.client.model.Filters.eq;

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

/**
 * This class handles the form data sent from login.jsp. It logs
 * the user in.
 *
 * @author Yousef Bulbulia
 * 
 */
@WebServlet("/loginServlet")
public class LoginServlet extends HttpServlet {

	private static final String serverIpAddress = "192.168.56.1"; 
	private static final String serverPort = "8080";
	private static final String projectPath = "http://" + serverIpAddress + ":" + serverPort + "/CulminatingVer8";
	/**
	 * Handles the form data. If the email exists in the database, then
	 * the users login information is authenticated. If the password 
	 * does not match, or the email does not exist in the database, then
	 * the user is sent back to login.jsp
	 *  
	 * @ param request  the http request from the client
	 * @ param response the response sent by the servlet to the client
	 */
	@Override
	public void doPost(HttpServletRequest request, 
			HttpServletResponse response) throws IOException, ServletException {
		
		String email = request.getParameter("email");
		String password = request.getParameter("password");
		String userType = request.getParameter("userType");
		
		MongoClient mongoClient = Util.getMongoClient();
		MongoDatabase database = mongoClient.getDatabase(Util.DATABASE_NAME);
			    
		Util.intialiazeDatabase();
		
		MongoCollection<Document> collection = Util.getUserAppropriateCollection(database, userType);
		
		Document userDoc = null;
		byte[] salt = null;
		byte[] hashedPassword = null;
		if( (userDoc = collection.find(eq("email", email)).first()) != null) {
			
			salt = userDoc.get("salt", Binary.class).getData();
			hashedPassword = userDoc.get("password_hash", Binary.class).getData();
			
		} else {
			
			System.out.println("Emailnone");
			//Write back to user saying email is non-existent in database
			response.sendRedirect(projectPath + "/login.jsp?emailNotFound=1");
			return;
			
		}
		
		//Hash password
		
		//Get salt from database
				
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
		
		//check if password matches
		if(Arrays.equals(hash, hashedPassword)) {
			
			HttpSession session = request.getSession();
			session.setAttribute("id", userDoc.getObjectId("_id"));
			session.setAttribute("userType", userType);
			session.setMaxInactiveInterval(-1);
			
			if(userType.equals("Patient")) {
				request.getRequestDispatcher("/patientHomepage.jsp").forward(request, response);
				return;
			} else {
				request.getRequestDispatcher("/therapistHomepage.jsp").forward(request, response);
				return;
			}
			
		} else {
			
			//Write back to user saying password does not match
			response.sendRedirect(projectPath + "/login.jsp?passwordMismatch=1");
			return;
			
		}
		
	}
	
}