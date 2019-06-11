package com.therapy.servlets;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import javax.servlet.AsyncContext;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.therapy.entities.*;

@WebServlet(urlPatterns={"/asyncDisplayMessages"}, asyncSupported=true)
public class AsyncDisplayMessagesServlet extends HttpServlet {
	
	@Override
	public void doPost(HttpServletRequest request, 
			HttpServletResponse response) throws IOException, ServletException {
		
		  response.setContentType("text/html;charset=UTF-8");
	      final AsyncContext acontext = request.startAsync();
	      acontext.start(new Runnable() {
	    	  
	    	  public void run() {
	    		  
	    		  while(true) {
	    			  
	    			  try {
	    				  TimeUnit.MILLISECONDS.sleep(500);
	    			  } catch(InterruptedException e) {
	    				  
	    			  }
	    			  
	    			  ServletContext context = request.getSession().getServletContext();
		    		  boolean isNewMessagingSession = (boolean)context.getAttribute("isNewMessagingSession");
		    		  Message mostRecentMessage = (Message)context.getAttribute("mostRecentMessage");
		    		  Chat chat = (Chat)context.getAttribute("chat");
		    		  
		    		  Message[] newMessages = null;
		    		  if(isNewMessagingSession) {
		    			newMessages = chat.getPreviousMessages(15);
		    			context.setAttribute("isNewMessagingSession", false);
		    		  } else {
		    			  
		    			  if(mostRecentMessage != null) {
		    				  newMessages = chat.getMessagesSince(mostRecentMessage);
		    			  }
		    			  
		    		  }
		    		  
		    		  context.setAttribute("newMessages", newMessages);
		    		  context.setAttribute("mostRecentMessage", mostRecentMessage);
	    			  
	    		  }
	    		  
	    	  }
	    	  
	      });
	     
	}     
	
}
