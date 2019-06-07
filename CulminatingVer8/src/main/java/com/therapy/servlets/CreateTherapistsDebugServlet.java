package com.therapy.servlets;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.bson.Document;
import org.bson.types.ObjectId;

import com.github.javafaker.Faker;
import com.mongodb.MongoClient;
import com.mongodb.MongoWriteException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.therapy.entities.Chat;
import com.therapy.entities.Patient;
import com.therapy.entities.Request;

import static com.mongodb.client.model.Filters.*;

@WebServlet("/createTherapists")
public class CreateTherapistsDebugServlet extends HttpServlet {

	private int num = 100;
	
	@Override
	public void doGet(HttpServletRequest request, 
			HttpServletResponse response) throws IOException, ServletException {
		
		MongoClient mongoClient = Util.getMongoClient();
		MongoDatabase database = mongoClient.getDatabase(Util.DATABASE_NAME);
	    
		Util.intialiazeDatabase();
		
		MongoCollection<Document> collection = Util.getUserAppropriateCollection(database, "Therapist");
		
		for(int i = 0; i < num; i++) {
			
			String firstName = new Faker().name().firstName();
			String lastName = new Faker().name().lastName();
			String email = firstName + "." + lastName + "@example.com";
			Integer rating = new Random().nextInt(100 + 1);
			Integer numberOfRaters = -1;
			if(i < 15) {
				numberOfRaters = new Random().nextInt(6 + 1);
			} else {
				numberOfRaters = new Random().nextInt(20 - 6 + 1) + 7 ;
			}
			boolean canReceiveRequests = true;
			
			//Hash password
			SecureRandom random = new SecureRandom();
			byte[] salt = new byte[16];
			random.nextBytes(salt);
			
			KeySpec spec = new PBEKeySpec("therapist".toCharArray(), salt, 65536, 128);
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
		    Integer patientLimit = Integer.valueOf(new Random().nextInt(15));
			
			
			Document doc = new Document("userType", "Therapist")
			.append("first_name", firstName)
			.append("last_name", lastName)
			.append("email", email)
			.append("rating", rating)
			.append("number_of_raters", numberOfRaters)
			.append("can_receive_requests", canReceiveRequests)
			.append("password_hash", hash)
			.append("salt", salt)
			.append("patient_limit", patientLimit);
			
			boolean isDuplicate = false;
			ObjectId id = null;
			
			do {
				
				isDuplicate = false;
				id = ObjectId.get();
				
				try {
					collection.insertOne(new Document("_id", id));
				} catch(MongoWriteException e) {
					if(e.getCode() == 11000) {
						isDuplicate = true;
					} else {
						
					}
				}
				
			} while(isDuplicate);
			
			doc.append("_id", id);
			
			collection.replaceOne(eq(id), doc);
			
			System.out.println(firstName);
			System.out.println(lastName);
			System.out.println(rating);
			System.out.println(numberOfRaters);
			System.out.println("========================");
			
		}
		
	}
	
}
