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

/**
 * This class is an asynchronous servlet that adds a <code>Request</code> to
 * the requesting <code>Patient</code> and requested <code>Therapist</code>.
 * 
 * @author Yousef Bulbulia
 *
 */
@WebServlet(urlPatterns={"/addRequestAsync"}, asyncSupported=true)
public class AsyncAddRequestServlet extends HttpServlet {
	
	/**
	 * Adds a <code>Request</code> to the requesting <code>Patient</code> and
	 * requested <code>Therapist</code>.
	 */
	@Override
	public void doPost(HttpServletRequest request, 
			HttpServletResponse response) throws IOException, ServletException {
		
		response.setContentType("text/html;charset=UTF-8");
	    final AsyncContext acontext = request.startAsync();
	    acontext.start(new Runnable() {
	    	  
	    	public void run() {
	    		
	    		MongoClient mongoClient = Util.getMongoClient();
	    		MongoDatabase database = mongoClient.getDatabase(Util.DATABASE_NAME);
	    		
	    		ObjectId therapistId = new ObjectId(request.getParameter("id"));
	    		Therapist therapist = new Therapist(therapistId, database);
	    		  
	    		HttpSession session = request.getSession();
	    		Patient patient = new Patient((ObjectId)session.getAttribute("id"), database);
	    		  
	    		Request newRequest = new Request(patient, therapist, database);
	    		  
	    		patient.addRequest(newRequest);
	    		therapist.addRequest(newRequest);
	    		  
	    		acontext.complete();
	    		  
	    	}
    		  
    	});
    	  
    }
      
}
	    	  
	

