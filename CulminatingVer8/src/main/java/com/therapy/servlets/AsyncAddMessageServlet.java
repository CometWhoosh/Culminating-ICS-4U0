package com.therapy.servlets;

import java.io.IOException;

import javax.servlet.AsyncContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
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
import com.therapy.entities.Therapist;
import com.therapy.entities.User;

@WebServlet(urlPatterns={"/addMessageAsync"}, asyncSupported=true)
public class AsyncAddMessageServlet extends HttpServlet {


	@Override
	public void doPost(HttpServletRequest request, 
			HttpServletResponse response) throws IOException, ServletException {
		
		response.setContentType("text/html;charset=UTF-8");
	      final AsyncContext acontext = request.startAsync();
	      acontext.start(new Runnable() {
	    	  
	    	  public void run() {
	    		  
	    		  System.out.println("Add messages servlet happened!");
	    		  
	    		  MongoClient client = Util.getMongoClient();
	    		  MongoDatabase database = client.getDatabase(Util.DATABASE_NAME);
	    		  
	    		  //Get message from cookie
	    		  String messageContent = null;
	    		  Cookie[] cookies = request.getCookies();
	    		  for(Cookie cookie : cookies) {
	    			  
	    			  if(cookie.getName().equals("message")) {
	    				  messageContent = cookie.getValue();
	    			  }
	    			  
	    		  }
	    		  
	    		  System.out.println("Message: " + messageContent);
	    		  
	    		  //Add message to chat
	    		  HttpSession session = request.getSession();
	    		  String userType = (String)session.getAttribute("userType");
	    		  if(userType.equals("Patient")) {
	    			  
	    			  Patient patient = new Patient((ObjectId)session.getAttribute("id"), database);
		    		  Chat chat = patient.getChat();
		    		  
		    		  //!@!@! Chat is null because patient and therapist have not accepted the request yet
		    		  Message message = new Message(chat.getPatient(), chat.getTherapist(), true, messageContent, database);
		    		  chat.addMessage(message);
		    		  acontext.complete();
		    		  return;
		    		  
	    		  } else {
	    			  
	    			  Chat chat = (Chat)session.getAttribute("activeChat");
	    			  
	    			  Message message = new Message(chat.getPatient(), chat.getTherapist(), true, messageContent, database);
		    		  chat.addMessage(message);
		    		  acontext.complete();
	    			  return;
	    			  
	    		  }
	    		  
	    	  }
	    	  
	      });
	      
	}
	
}
