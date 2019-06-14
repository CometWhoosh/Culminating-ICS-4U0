package com.therapy.servlets;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.servlet.AsyncContext;
import javax.servlet.ServletContext;
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
 * This class is an asynchronous servlet that gets new <code>Message</code>
 * objects in the current <code>Chat</code>.
 * 
 * It retrieves <code>Message</code> objects that were inserted after the last 
 * time it was executed, or the most recent 15 <code>Message</code> objects in 
 * the current <code>Chat</code> if the servlet is executing for the first time. 
 * 
 * The new <code>Message</code> objects are then written as a JSON array 
 * through the HTTP response. 
 * 
 * @author Yousef Bulbulia
 *
 */
@WebServlet(urlPatterns={"/asyncDisplayMessages"}, asyncSupported=true)
public class AsyncDisplayMessagesServlet extends HttpServlet {
	
	/**
	 * Retrieves the new <code>Message</code> objects and writes them in JSON
	 * format through the HTTP response.
	 */
	@Override
	public void doPost(HttpServletRequest request, 
			HttpServletResponse response) throws IOException, ServletException {
		
		response.setContentType("text/html;charset=UTF-8");
	    final AsyncContext acontext = request.startAsync();
	    acontext.start(new Runnable() {
	    	  
	        public void run() {
	    		
	        	//Wait for 1 second
				try {
					TimeUnit.MILLISECONDS.sleep(1 * 1000);
	    		} catch(InterruptedException e) {
	    			e.printStackTrace();	  
	    	    }
				
				//Get the connection to the database
				MongoClient client = new MongoClient(/*...*/);
				MongoDatabase database = client.getDatabase(Util.DATABASE_NAME);
				
				//Get the current Chat
				HttpSession session = request.getSession();
				Chat chat = null;
				if(session.getAttribute("userType") == "Patient") {
					chat = new Patient((ObjectId)session.getAttribute("id"), database).getChat();
				} else if(session.getAttribute("userType") == "Therapist") {
					chat = (Chat)session.getAttribute("chat");
				}
				
				//Get the data previously set to the ServletContext
				ServletContext context = session.getServletContext();
				boolean isNewMessagingSession = (boolean)context.getAttribute("isNewMessagingSession");
				Message mostRecentMessage = (Message)context.getAttribute("mostRecentMessage");
				
				//Get the new messages
				Message[] newMessages = null;
				if(isNewMessagingSession) {
					newMessages = chat.getPreviousMessages(15);
		    		context.setAttribute("isNewMessagingSession", false);
				} else if(mostRecentMessage != null) {
					newMessages = chat.getMessagesSince(mostRecentMessage);
		    	}
				
				mostRecentMessage = newMessages[newMessages.length - 1];
				context.setAttribute("mostRecentMessage", mostRecentMessage);
				
				/*
				 * Convert newMessages into a JSON array 
				 * contained within a JSON object
				 */
				JsonObject messageContainer;
				
				JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
				
				for(int i = 0; i < newMessages.length; i++) {
					
					JsonObject messageObject = null;
					if(newMessages[i].senderIsPatient()) {
						messageObject = Json.createObjectBuilder()
							.add("content", newMessages[i].getContent())
							.add("userType", "Patient")
							.build();
					} else {
						messageObject = Json.createObjectBuilder()
							.add("content", newMessages[i].getContent())
							.add("userType", "Therapist")
							.build();
					}
					
					arrayBuilder.add(messageObject);
					
				}
				
				messageContainer = Json.createObjectBuilder()
					.add("messages", arrayBuilder)
					.build();
				
				//Write the JSON object to the response
				response.setContentType("application/json");
				response.setCharacterEncoding("UTF-8");
				try {
					response.getWriter().write(messageContainer.toString());
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				acontext.complete();
				  
	    	}
	    	  
	    });
	     
	}     
	
}