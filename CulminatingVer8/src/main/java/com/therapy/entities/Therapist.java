package com.therapy.entities;

import static com.mongodb.client.model.Filters.eq;

import java.util.ArrayList;
import java.util.Arrays;

import org.bson.Document;
import org.bson.types.ObjectId;

import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Updates;

/**
 * This class represents a therapist. Since it is one of the two types of users,
 * it extends the <code>User</code> class. It has a field for the number of requests the 
 * therapist allows, the number of requests that they have received, and an 
 * array of requests from patients.  
 * 
 * @author Yousef Bulbulia
 * 
 */
public class Therapist extends User {
   
    public Therapist(ObjectId id, MongoDatabase database) {
    	
    	super(id,database);
    	collection = database.getCollection("therapists");
    	
    }
    
    public Integer getNumberOfRequests() {
    	return getDocument().getInteger("number_of_requests");
    }
    
    /**
     * 
     * @return the limit to how many requests a therapist can have.
     */
    public Integer getPatientLimit() {
    	return getDocument().getInteger("patient_limit");
    }
    
    public Integer getRequestLimit() {
    	return getDocument().getInteger("request_limit");
    }
    
    public Boolean canReceiveRequests() {
    	return getDocument().getBoolean("can_receive_requests");
    }

    public double getRating() {
    	return getDocument().getInteger("rating");
    }
    
    public Integer getNumberOfRaters() {
    	return getDocument().getInteger("number_of_raters");
    }
    
    
    public Patient[] getPatients() {
    	
    	Document doc = getDocument();
    	Patient[] patients = null;
    	
    	try {
    		
	    	ObjectId[] patientIds = doc.get("message_ids", ObjectId[].class);
	    	//!@!@ Look at lines 137 - 140 in User class for solution
	    
	    	patients = null;
	    	if(patientIds != null) {
	    		
	    		patients = Arrays.stream(patientIds)
	    	    		.map(e -> new Patient(e, database))
	    	    		.toArray(Patient[]::new);
	    		
	    	}
	    	
    	} catch(NullPointerException e) {
    		
    	}
    	
    	
    	return patients;
    }
    
    public Chat[] getChats() {

    	Document doc = getDocument();
    	Chat[] chats = null;
    	
    	try {
    		
	    	ObjectId[] chatIds = doc.get("chat_ids", ObjectId[].class);
	    	
	    	chats = null;
	    	if(chatIds != null) {
	    		
	    		chats = Arrays.stream(chatIds)
	    	    		.map(e -> new Chat(e, database))
	    	    		.toArray( Chat[]::new);
	    	    	
	    		
	    	}	
	    	
		} catch(NullPointerException e) {
			
		}
    	
    	return chats;
    	
    }
    
    
    
    /**
     * 
     * @param request the request to be added.
     */
    public void addRequest(Request request) throws IllegalStateException {
        
    	Document doc = getDocument();
    	
    	int patientLimit = doc.getInteger("patient_limit", 0);
    	Request[] requests = getRequests();
    	Patient[] patients = getPatients();
    	
    	if(requests == null) {
    		requests = new Request[0];
    	}
    	
    	if(patients == null) {
    		patients = new Patient[0];
    	}
    	
    	if(patientLimit == 0) {
            throw new IllegalStateException("A request limit must be set before adding requests");
        } else if(requests.length + 1 + patients.length > patientLimit) {
        	throw new IllegalStateException("Number of requests and patients will exceed the patient limit");
        }
        
        int requestLimit = patientLimit - (requests.length + 1) - patients.length;
        if(requestLimit == 0) {
        	collection.findOneAndUpdate(eq(id), Updates.set("can_receive_requests", Boolean.valueOf(false)));
        }
    	
        collection.findOneAndUpdate(eq(id), Updates.push("request_ids", request.getId()));
        collection.findOneAndUpdate(eq(id), Updates.set("request_limit", requestLimit));
        
    }
    
    
    
    public void addPatient(Patient patient) {
    	
    	collection.findOneAndUpdate(eq(id), Updates.push("patient_ids", patient.getId()));
    	
    	Chat chat = new Chat(patient, this, database);
    	collection.findOneAndUpdate(eq(id), Updates.push("chat_ids", chat.getId()));
    	
    }
    
    public void addRating(Integer newUserRating) throws IllegalArgumentException {
    	
    	Document doc = getDocument();
    	
    	Integer rating = doc.getInteger("rating");
    	Integer numberOfRaters = doc.getInteger("number_of_raters");
    	
    	if(newUserRating < 0 || newUserRating > 100) {
    		throw new IllegalArgumentException("Ratings must be between 0 and 100 (inclusive)");
    	}
    	
    	rating =  Math.round((rating * numberOfRaters + newUserRating)/(++numberOfRaters));
    	
    	collection.findOneAndUpdate(eq(id), Updates.set("rating", rating));
    	collection.findOneAndUpdate(eq(id), Updates.set("number_of_raters", numberOfRaters));
    	
    }

}
