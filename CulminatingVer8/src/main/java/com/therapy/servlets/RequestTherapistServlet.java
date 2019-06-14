package com.therapy.servlets;


import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.bson.types.ObjectId;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.therapy.entities.Patient;
import com.therapy.entities.Request;
import com.therapy.entities.Therapist;

/**
 * This class is a servlet that sends a request from the patient that is 
 * currently signing up to the therapist chosen in patientSetup.jsp. 
 * 
 * @author Yousef Bulbulia
 *
 */
@WebServlet("/requestTherapist")
public class RequestTherapistServlet extends HttpServlet {

	/**
	 * Gets the therapist chosen from the HTTP request parameter 
	 * <code>therapist</code> and sends them a request from the patient that is 
	 * currently signing up. If the patient filled in a summary of their 
	 * problems in the form, then retrieve it from the HTTP request parameter
	 * <code>textarea</code> and include it in the creation of the 
	 * <code>Request</code> object.
	 * 
	 * Afterwards, the patient is forwarded to patientHomepage.jsp
	 */
	@Override
	public void doPost(HttpServletRequest request, 
			HttpServletResponse response) throws IOException, ServletException {
		
		//Get the database
		MongoClient mongoClient = Util.getMongoClient();
		MongoDatabase database = mongoClient.getDatabase(Util.DATABASE_NAME);
		
		
		HttpSession session = request.getSession(false);
		
		/*
		 * Get the Patient object for the current patient, the Therapist
		 * object for the requested therapist, and using them create
		 * a Request.
		 * 
		 */
		ObjectId patientId = (ObjectId)session.getAttribute("id");
		ObjectId therapistId = new ObjectId(request.getParameter("therapist"));
		String summary = request.getParameter("textarea");
		
		Patient patient = new Patient(patientId, database);
		Therapist therapist = new Therapist(therapistId, database);
		
		/*
		 * If a summary for the request was provided include it in the creation
		 * of the Request object.
		 */
		Request patientRequest = null;
		if(summary == null || summary == "") {
			patientRequest = new Request(patient, therapist, database);
		} else {
			
			patientRequest = new Request(patient, therapist, summary, database);
			therapist.addRequest(patientRequest);
			patient.addRequest(patientRequest);
		
		}
		
		request.getRequestDispatcher("/patientHomepage.jsp").forward(request, response);
		
	}
	
}
