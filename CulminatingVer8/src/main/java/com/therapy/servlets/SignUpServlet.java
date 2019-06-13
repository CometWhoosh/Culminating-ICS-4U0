package com.therapy.servlets;

import static com.mongodb.client.model.Filters.eq;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.bson.Document;
import org.bson.types.Binary;
import org.bson.types.ObjectId;

import com.mongodb.MongoClient;
import com.mongodb.MongoWriteException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

/**
 * This class handles the form data sent from signUp.jsp. If
 * the email given does not already exist in the database, then the form
 * data is entered into the database.
 *
 * @author Yousef Bulbulia
 * 
 */
@WebServlet("/signUpServlet")
public class SignUpServlet extends HttpServlet {

	private static final String serverIpAddress = "192.168.56.1"; 
	private static final String serverPort = "8080";
	private static final String projectPath = "http://" + serverIpAddress + ":" + serverPort + "/CulminatingVer8";
	
	/**
	 * Handles the form data. The password is hashed, and if the email given 
	 * does not already exist in the database, the form data is entered 
	 * into the database. If the email does already exist, then the client 
	 * is redirected back to signUp.jsp
	 *  
	 * @ param request  the http request from the client
	 * @ param response the response sent by the servlet to the client
	 */
	@Override
	public void doPost(HttpServletRequest request, 
			HttpServletResponse response) throws IOException, ServletException {
		
		//Get user input
		String userType = request.getParameter("userType");
		String firstName = request.getParameter("first_name");
		String lastName = request.getParameter("last_name");
		String email = request.getParameter("email");
		String password = request.getParameter("password");
		int patientLimit = 0;
		
		
		if(userType.equals("Therapist")) {
			
			try {
				patientLimit = Integer.parseInt(request.getParameter("patientLimitInput"));
			} catch(NumberFormatException e) {
				response.sendRedirect(projectPath + "/signUp.jsp?numberFormatError=1");
				return;
			}
			
			
		}
		
		//Hash password
		SecureRandom random = new SecureRandom();
		byte[] salt = new byte[16];
		random.nextBytes(salt);
		
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
		
		MongoClient mongoClient = Util.getMongoClient();
		MongoDatabase database = mongoClient.getDatabase(Util.DATABASE_NAME);
	    
		Util.intialiazeDatabase();
		
		MongoCollection<Document> collection = Util.getUserAppropriateCollection(database, userType);
		
		if(collection.find(eq("email", email)).first() == null) {
			
			Map<String, Object> fields = new HashMap<String, Object>();
			
			fields.put("first_name", firstName);
			fields.put("last_name", lastName);
			fields.put("email", email);
			fields.put("password_hash", new Binary(hash));
			fields.put("salt", new Binary(salt));
			
			if(userType.equals("Therapist")) {
				fields.put("patient_limit", patientLimit);
				fields.put("can_receive_requests", true);
			}
			
			boolean isDuplicate = false;
			ObjectId id = null;
			
			do {
				
				isDuplicate = false;
				id = ObjectId.get();
				
				fields.remove("_id");
				fields.put("_id", id);
				
				try {
					collection.insertOne(new Document("_id", id));
				} catch(MongoWriteException e) {
					if(e.getCode() == 11000) {
						isDuplicate = true;
					}
				}
				
			} while(isDuplicate);
			
			try {
				collection.replaceOne(eq(id), new Document(fields));
			} catch(MongoWriteException e) {
				response.sendRedirect(projectPath + "/signUp.jsp?databaseError=1");
				return;
			}
			
			HttpSession session = request.getSession();
			session.setAttribute("id", id);
			session.setAttribute("userType", userType);
			session.setMaxInactiveInterval(-1);
			
			//Checking the session id
			System.out.println("SignUpServlet: " + session.getId());
			System.out.println("SignUpServlet - ID: " + id.toHexString());
			
			if(userType.equals("Patient")) {
				request.getRequestDispatcher("/getTherapists").forward(request, response);
				//response.sendRedirect(projectPath + "/getTherapists");
				return;
			} else {
				request.getRequestDispatcher("/therapistHomepage.jsp").forward(request, response);
				return;
			}
			
		} else {
			
			//Write back to user saying email is taken
			response.sendRedirect(projectPath + "/signUp.jsp?emailError=1");
			return;
			
		}
		
	}
	
}
