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

/**
 * This class is a servlet that searches for a given therapist, and if found,
 * passes it to searchTherapists.jsp.
 * 
 * @author Yousef Bulbulia
 *
 */
@WebServlet("/searchTherapists")
public class SearchTherapistsServlet extends HttpServlet {
	
	@Override
	public void doPost(HttpServletRequest request, 
			HttpServletResponse response) throws IOException, ServletException {
		
		//Get the collection
		MongoClient mongoClient = Util.getMongoClient();
		MongoDatabase database = mongoClient.getDatabase(Util.DATABASE_NAME);
		MongoCollection<Document> collection = database.getCollection("therapists");
		
		//Get the therapist that was searched for
		String therapistName = request.getParameter("targetTherapist");
		MongoCursor<Document> cursor = collection.find().iterator();
		
		/*
		 * Perform a linear search for therapists with names that match the
		 * name of the therapist that was searched for. Make a list of
		 * the matching therapists 
		 */
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
