package com.therapy.servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static com.mongodb.client.model.Filters.*;

import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

@WebServlet("/deleteAllPatients")
public class DeleteDocsDebugServlet extends HttpServlet {

	private String userType = "Patient";
	
	@Override
	public void doGet(HttpServletRequest request, 
			HttpServletResponse response) throws IOException, ServletException {
		
		MongoClient mongoClient = Util.getMongoClient();
		MongoDatabase database = mongoClient.getDatabase(Util.DATABASE_NAME);
			    
		Util.intialiazeDatabase();
		
		MongoCollection<Document> collection = Util.getUserAppropriateCollection(database, userType);
		
		collection.deleteMany(exists("first_name"));
		
	}
	
}
