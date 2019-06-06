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
		
		//MongoCollection<Document> therapistCollection = database.getCollection("therapists");
		
		HttpSession session = request.getSession(false);
		System.out.println("ReqeuestServlet: " + session.getId());
		////
		//System.out.println(session.toString());
		////
		
		//System.out.println(session.toString());
		//System.out.println( new ObjectId((String)session.getAttribute("id")).toHexString() );
		
		
	//	ObjectId id= null;
		
		
			//id = (ObjectId)session.getAttribute("id");
	/*	try {
			System.out.println(((String)session.getAttribute("email")));
		} catch(NullPointerException e) {
			System.out.println("Yeah Null P E");
		} */
		String emailHex = Hex.encodeHexString(((String)session.getAttribute("email")).getBytes());
			
			
		
		
		//MongoCollection<Document> patientCollection = database.getCollection("patients");
		//Document patientDocument = patientCollection.find(eq("email", email)).first();
		
		//System.out.println("RequestTherapistServlet, ObjectID");
		//System.out.println(((ObjectId)session.getAttribute("id")).toString());
		
		//((ObjectId)randomTherapists[i].get("_id"))
		
		
		ObjectId patientId = new ObjectId(emailHex);
		ObjectId therapistId = new ObjectId(request.getParameter("therapist"));
		String summary = request.getParameter("textarea");
		
		//System.out.println(patientId.toHexString());
		
		Patient patient = new Patient(patientId, database);
		Therapist therapist = new Therapist(therapistId, database);
		
		Request patientRequest = null;
		if(summary == null || summary == "") {
			patientRequest = new Request(patient, therapist, database);
		} else {
			patientRequest = new Request(patient, therapist, summary, database);
		}
		
		therapist.addRequest(patientRequest);
		therapist.updateToCollection();
		
		patient.setRequest(patientRequest);
		patient.updateToCollection();
		
		{
			
			MongoCollection<Document> patientCol = database.getCollection("patients");
			MongoCollection<Document> therapistCol = database.getCollection("therapists");
			MongoCollection<Document> requestCol = database.getCollection("requests");
			
			Document patientDoc = patientCol.find(eq(patientId)).first();
			
			Document pReqDoc = requestCol.find(eq(patientDoc.getObjectId("request_id"))).first();
			Document prPatDoc = patientCol.find(eq(pReqDoc.getObjectId("patient_id"))).first();
			Document prTherDoc = therapistCol.find(eq(pReqDoc.getObjectId("therapist_id"))).first();
			System.out.println("REQUEST FROM PATIENT");
			System.out.println("patient:" + prPatDoc.getString("first_name"));
			System.out.println("therapist: " + prTherDoc.getString("first_name"));
			System.out.println("summary: " + pReqDoc.getString("summary"));
			System.out.println("========================================");
			
			
			Document therapistDoc = therapistCol.find(eq(therapistId)).first();
			
			Document tReqDoc = requestCol.find(eq(therapistDoc.getObjectId("request_id"))).first();
			Document trPatDoc = patientCol.find(eq(tReqDoc.getObjectId("patient_id"))).first();
			Document trTherDoc = therapistCol.find(eq(tReqDoc.getObjectId("therapist_id"))).first();
			System.out.println("REQUEST FROM THERAPIST");
			System.out.println("patient:" + trPatDoc.getString("first_name"));
			System.out.println("therapist: " + trTherDoc.getString("first_name"));
			System.out.println("summary: " + tReqDoc.getString("summary"));
			System.out.println("========================================");
			
		}
		
	}
	
}
