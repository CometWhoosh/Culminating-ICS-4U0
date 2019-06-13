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
import com.therapy.entities.Chat;
import com.therapy.entities.Therapist;

@WebServlet("/getChat")
public class GetTherapistChatServlet extends HttpServlet {
	
	@Override
	public void doGet(HttpServletRequest request, 
			HttpServletResponse response) throws IOException, ServletException {
		
		MongoClient mongoClient = Util.getMongoClient();
		MongoDatabase database = mongoClient.getDatabase(Util.DATABASE_NAME);	
		
		HttpSession session = request.getSession();
		
		//Get Therapist and patient name
		Therapist therapist = new Therapist((ObjectId)session.getAttribute("id"), database);
		String targetPatientName = (String)request.getAttribute("patientChats");
		
		
		
		/*
		//Check which Chat was pressed
		for(Chat chat : chats) {
			
			
			
		}
		*/
		
		//Get the chat that belongs to the therapist and the specified patient
		Chat[] chats = therapist.getChats();
		Chat chat = null;
		for(int i = 0; i < chats.length; i++) {
			
			//If the patient names match, get the chat for the patient and therapist
			String patientName = chats[i].getPatient().getFullName();
			
			System.out.println("targetPatientName: " + targetPatientName);
			System.out.println("patientName: " + patientName);
		    
			if(targetPatientName.equalsIgnoreCase(patientName)) {
				chat = chats[i];
			}
			
		}
		
		session.setAttribute("isNewMessagingSession", true);
		session.setAttribute("chat", chat);
		request.getRequestDispatcher("/therapistMessenger.html");
		
	}

}
