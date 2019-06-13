package com.therapy.servlets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.therapy.entities.Therapist;

@WebServlet("/searchTherapists")
public class SearchTherapists extends HttpServlet {
	
	@Override
	public void doPost(HttpServletRequest request, 
			HttpServletResponse response) throws IOException, ServletException {
		
		MongoClient mongoClient = Util.getMongoClient();
		MongoDatabase database = mongoClient.getDatabase(Util.DATABASE_NAME);
		MongoCollection<Document> collection = database.getCollection("therapists");
		
		String therapistName = request.getParameter("targetTherapist");
		MongoCursor<Document> cursor = collection.find().iterator();
		
		//Search for and make a list of the therapists with the same name
		List<Therapist> matchingTherapists = new ArrayList<>();
		while(cursor.hasNext()) {
			
			Document therapistDoc = cursor.next();
			Therapist therapist = new Therapist(therapistDoc.getObjectId("_id"), database);
			
			if(therapistName.equalsIgnoreCase(therapist.getFullName())) {
				matchingTherapists.add(therapist);
			}
			
		}
		
		request.setAttribute("matchingTherapists", matchingTherapists);
		request.getRequestDispatcher("/searchTherapists.jsp").forward(request, response);;
		
	}
	
}
