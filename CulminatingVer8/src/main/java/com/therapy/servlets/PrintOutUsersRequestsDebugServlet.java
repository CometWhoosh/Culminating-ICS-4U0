package com.therapy.servlets;

import static com.mongodb.client.model.Filters.eq;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
import com.therapy.entities.Patient;
import com.therapy.entities.Request;
import com.therapy.entities.Therapist;

@WebServlet("/printRequests")
public class PrintOutUsersRequestsDebugServlet extends HttpServlet {
	
	String name = "Bob Vance";
	
	@Override
	public void doGet(HttpServletRequest request, 
			HttpServletResponse response) throws IOException, ServletException {
		
		MongoClient mongoClient = Util.getMongoClient();
		MongoDatabase database = mongoClient.getDatabase(Util.DATABASE_NAME);
		
		MongoCollection<Document> therapistCollection = database.getCollection("therapists");
		
		//Get Therapist
		Document therapistDoc = therapistCollection.find(eq("email", "7:30")).first();
		
		
		ObjectId therapistId = therapistDoc.getObjectId("_id");
		Therapist therapist = new Therapist(therapistId, database);
		
		/*
		//Get Patient
		ObjectId patientId = patientCollection.find(eq("email", "7:18")).first().getObjectId("_id");
		Patient patient = new Patient(patientId, database);
		
		
		final Class<? extends List> listClass = new ArrayList<ObjectId>().getClass();
		List<ObjectId> idsAsList = therapistDoc.get("request_ids", listClass);
		
		ObjectId[] requestIds = idsAsList.toArray(ObjectId[]::new);
		
    	Request[] requests = null;
    	if(requestIds != null) {
    		
    		requests = Arrays.stream(requestIds)
    	    		.map(e -> new Request(e, database))
    	    		.toArray(Request[]::new);
    		
    	}
    	*/
		
		Request[] requests = therapist.getRequests();
		
		if(requests == null) {
			System.out.println("requests was null");
		}
		
    	for(Request e : requests) {
    		System.out.println("Patient: " + e.getPatient().getFullName());
    		System.out.println("Therapist " + e.getTherapist().getFullName());
    		System.out.println("Therapist Accepted?: " + e.therapistAccepted());
    		System.out.println("===============================================");
    	}
		
	}
	
}
