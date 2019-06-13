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
import com.therapy.entities.Chat;
import com.therapy.entities.Message;
import com.therapy.entities.Patient;

/**
 * This class is an asynchronous servlet that adds the <code>Message</code>
 * specified in the HTTP request parameter <code>message</code> to the
 * <code>Chat</code> that is currently in use.
 * 
 * @author Yousef Bulbulia
 *
 */
@WebServlet(urlPatterns={"/addMessageAsync"}, asyncSupported=true)
public class AsyncAddMessageServlet extends HttpServlet {

	/**
	 * Adds the <code>Message</code> specified in the HTTP request parameter 
	 * <code>message</code> to the <code>Chat</code> that is currently in use.
	 */
	@Override
	public void doPost(HttpServletRequest request, 
			HttpServletResponse response) throws IOException, ServletException {
		
		response.setContentType("text/html;charset=UTF-8");
	    final AsyncContext acontext = request.startAsync();
	    acontext.start(new Runnable() {
	    	  
	    	public void run() {
	    		
	    		MongoClient client = Util.getMongoClient();
	    		MongoDatabase database = client.getDatabase(Util.DATABASE_NAME);
	    		  
	    		//Get message from POST request
	    		String messageContent = request.getParameter("message");
	    		  
	    		//Add message to chat
	    		HttpSession session = request.getSession();
	    		String userType = (String)session.getAttribute("userType");
	    		if(userType.equals("Patient")) {
	    			  
	    			Patient patient = new Patient((ObjectId)session.getAttribute("id"), database);
		    		Chat chat = patient.getChat();
		    		  
		    		Message message = new Message(chat.getPatient(), chat.getTherapist(),
		    				true, messageContent, database);
		    		
		    		chat.addMessage(message);
		    		  
	    		} else {
	    			  
	    			Chat chat = (Chat)session.getAttribute("chat");
	    			  
	    			Message message = new Message(chat.getPatient(), chat.getTherapist(),
	    					false, messageContent, database);
	    			
		    		chat.addMessage(message);
	    			  
	    		}
	    		
	    		acontext.complete();
	    		  
	    	}
	    	  
	    });
	      
	}
	
}
