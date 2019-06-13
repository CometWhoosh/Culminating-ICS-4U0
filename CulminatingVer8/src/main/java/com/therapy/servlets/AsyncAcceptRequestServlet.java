package com.therapy.servlets;

import java.io.IOException;

import javax.servlet.AsyncContext;
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

@WebServlet(urlPatterns={"/asyncAcceptRequest"}, asyncSupported=true)
public class AsyncAcceptRequestServlet extends HttpServlet { 

	@Override
	public void doPost(HttpServletRequest request, 
			HttpServletResponse response) throws IOException, ServletException {
		
		response.setContentType("text/html;charset=UTF-8");
	    final AsyncContext acontext = request.startAsync();
	    acontext.start(new Runnable() {
	    	  
	    	public void run() {
	    		
	    		System.out.println("Async accept");
	    		
	    		//Get the database
	    		MongoClient client = Util.getMongoClient();
	    		MongoDatabase database = client.getDatabase(Util.DATABASE_NAME);
	    		
	    		//Get the <code>Request</code>
	    		String idHexString = request.getParameter("id");
	    		Request patientRequest = new Request(new ObjectId(idHexString), database);
	    		System.out.println("AyncServlet: " + idHexString);
	    		//Accept the request
	    		HttpSession session = request.getSession();
	    		String userType = (String)session.getAttribute("userType");
	    		if(userType.equals("Patient") ) {
	    			patientRequest.accept(Patient.class);
	    		} else if(userType.equals("Therapist")) {
	    			patientRequest.accept(Therapist.class);
	    		}
	    		
	    		System.out.println("request accepted");
	    		
	    		acontext.complete();
	    		
	    	}
	    	
	    });
	    
	}

}
