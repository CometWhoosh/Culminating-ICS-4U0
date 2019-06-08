package com.therapy.entities;

import static com.mongodb.client.model.Filters.eq;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.bson.Document;
import org.bson.types.Binary;
import org.bson.types.ObjectId;

import com.mongodb.MongoException;
import com.mongodb.MongoWriteConcernException;
import com.mongodb.MongoWriteException;
import com.mongodb.client.MongoCollection;
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
    
    /**
     * 
     * @return an array of the requests.
     */
    public Request[] getRequests() {
    	
    	Document doc = getDocument();
    	
    	ObjectId[] requestIds = doc.get("request_ids", ObjectId[].class);
    	
    	Request[] requests = Arrays.stream(requestIds)
    		.map(e -> new Request(e, database))
    		.toArray(Request[]::new);
    	
    	return requests;
        
    }
    
    public Patient[] getPatients() {
    	
    	Document doc = getDocument();
    	
    	ObjectId[] patientIds = doc.get("message_ids", ObjectId[].class);
    	
    	Patient[] patients = Arrays.stream(patientIds)
    		.map(e -> new Patient(e, database))
    		.toArray(Patient[]::new);
    	
    	return patients;
    }
    
    public Chat[] getChats() {

    	Document doc = getDocument();
    	
    	ObjectId[] chatIds = doc.get("chat_ids", ObjectId[].class);
    	
    	Chat[] chats = Arrays.stream(chatIds)
    		.map(e -> new Chat(e, database))
    		.toArray( Chat[]::new);
    	
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
