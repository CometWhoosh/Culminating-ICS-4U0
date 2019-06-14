package com.therapy.servlets;

import java.io.IOException;
import java.util.Iterator;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.bson.Document;
import org.bson.types.Binary;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

@WebServlet("/listUsers")
public class ListUsersDebugServlet extends HttpServlet {
	
	private String userType = "Therapist";

	@Override
	public void doGet(HttpServletRequest request, 
			HttpServletResponse response) throws IOException, ServletException {
		
		MongoClient mongoClient = Util.getMongoClient();
		MongoDatabase database = mongoClient.getDatabase(Util.DATABASE_NAME);
			    
		Util.intialiazeDatabase();
		
		MongoCollection<Document> collection= Util.getUserAppropriateCollection(database, userType);
		
		Iterator<Document> docs = collection.find().iterator();
		
		if(userType.equals("Patient")) {
			while(docs.hasNext()) {
				
				Document doc = docs.next();
				System.out.println("PATIENT");
				System.out.println(doc.getString("first_name"));
				System.out.println(doc.getString("last_name"));
				System.out.println(doc.getString("email"));
				System.out.println(doc.get("password_hash", Binary.class).getData());
				System.out.println("====================================");
				
			}
		
		} else if (userType.equals("Therapist")) {
			
				while(docs.hasNext()) {
				
				Document doc = docs.next();
				System.out.println(doc.getString("first_name"));
				System.out.println(doc.getString("last_name"));
				System.out.println(doc.getString("email"));
				System.out.println(doc.getInteger("rating"));
				System.out.println(doc.getInteger("number_of_raters"));
				System.out.println("====================================");
				
			}
		}
		
	}
	
}

