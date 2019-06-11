package com.therapy.servlets;

import static com.mongodb.client.model.Filters.eq;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.codec.binary.Hex;
import org.bson.Document;
import org.bson.types.ObjectId;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.therapy.entities.Patient;
import com.therapy.entities.Request;
import com.therapy.entities.Therapist;

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
		
		MongoClient mongoClient = Util.getMongoClient();
		MongoDatabase database = mongoClient.getDatabase(Util.DATABASE_NAME);
		
		HttpSession session = request.getSession(false);
		System.out.println("ReqeuestServlet: " + session.getId());
		
		
		ObjectId patientId = (ObjectId)session.getAttribute("id");
		ObjectId therapistId = new ObjectId(request.getParameter("therapist"));
		String summary = request.getParameter("textarea");
		
		Patient patient = new Patient(patientId, database);
		Therapist therapist = new Therapist(therapistId, database);
		
		Request patientRequest = null;
		if(summary == null || summary == "") {
			patientRequest = new Request(patient, therapist, database);
		} else {
			System.out.println("There is a summary!");
			patientRequest = new Request(patient, therapist, summary, database);
		}
		
		therapist.addRequest(patientRequest);
		patient.addRequest(patientRequest);
		
		request.getRequestDispatcher("/patientHomepage.jsp").forward(request, response);
		
	}
	
}
