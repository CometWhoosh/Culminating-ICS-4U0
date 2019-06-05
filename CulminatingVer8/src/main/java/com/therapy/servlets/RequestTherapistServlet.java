package com.therapy.servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.bson.Document;
import org.bson.types.ObjectId;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import static com.mongodb.client.model.Filters.*;

@WebServlet("/requestTherapist")
public class RequestTherapistServlet extends HttpServlet {

	@Override
	public void doPost(HttpServletRequest request, 
			HttpServletResponse response) throws IOException, ServletException {
		
		
		/*
		 * TODO: For shared resources concern, try updating to mongodb 3.8.2
		 *       and seeing if all of the classes are there. Then, if it works, 
		 *       use multi-doc transactions
		 */
		
		/*
		 * Get selected therapist
		 * Send request
		 */
		
		MongoClient mongoClient = Util.getMongoClient();
		MongoDatabase database = mongoClient.getDatabase(Util.DATABASE_NAME);
		
		MongoCollection<Document> collection = database.getCollection("therapists");
		
		ObjectId therapistId = new ObjectId(request.getParameter("therapist"));
		
		Document therapistDoc = collection.find(eq(therapistId)).first();
		
		
		
	}
	
}
