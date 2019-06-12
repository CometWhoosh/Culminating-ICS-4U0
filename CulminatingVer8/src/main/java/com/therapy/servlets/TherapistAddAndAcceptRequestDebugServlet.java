package com.therapy.servlets;

import static com.mongodb.client.model.Filters.eq;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.therapy.entities.Patient;
import com.therapy.entities.Request;
import com.therapy.entities.Therapist;

@WebServlet("/accept")
public class TherapistAddAndAcceptRequestDebugServlet extends HttpServlet {

	String email = "rt5";
	
	//This only creates the request and adds it to both the therapst and the patient. It does not
	//accept it though
	
	@Override
	public void doGet(HttpServletRequest request, 
			HttpServletResponse response) throws IOException, ServletException {
		
		MongoClient mongoClient = Util.getMongoClient();
		MongoDatabase database = mongoClient.getDatabase(Util.DATABASE_NAME);
		
		MongoCollection<Document> collection = database.getCollection("therapists");
		
		Document therapistDoc = collection.find(eq("email", email)).first();
		
		if(therapistDoc == null) {
			System.out.println("couldn't find therapist's document");
		} else {
			System.out.println("found the therapist!");
		}
		
		Document patientDoc = collection.find(eq("email", email)).first();
		
		if(patientDoc == null) {
			System.out.println("couldn't find patient's document");
		} else {
			System.out.println("found the patient!");
		}
		
		Therapist therapist = new Therapist(therapistDoc.getObjectId("_id"), database);
		Patient patient = new Patient(patientDoc.getObjectId("_id"), database);
		
		Request patientRequest = new Request(patient, therapist, database);
		
		therapist.addRequest(patientRequest);
		patient.addRequest(patientRequest);
		
		System.out.println("got to the end!");
		
		//Just go and create a new therapist and patient
		
	}
	
}
