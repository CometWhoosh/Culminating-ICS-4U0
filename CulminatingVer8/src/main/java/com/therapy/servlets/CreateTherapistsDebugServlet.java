package com.therapy.servlets;

import java.io.IOException;
import java.util.Random;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.bson.Document;

import com.github.javafaker.Faker;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

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
			
			Document doc = new Document("userType", "Therapist")
			.append("first_name", firstName)
			.append("last_name", lastName)
			.append("email", email)
			.append("rating", rating)
			.append("number_of_raters", numberOfRaters)
			.append("can_receive_requests", canReceiveRequests);
			
			collection.insertOne(doc);
			
			System.out.println(firstName);
			System.out.println(lastName);
			System.out.println(rating);
			System.out.println(numberOfRaters);
			System.out.println("========================");
			
		}
		
	}
	
}
