package com.therapy.entities;

import static com.mongodb.client.model.Filters.eq;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bson.Document;
import org.bson.types.ObjectId;

import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Updates;

/**
 * This class represents a therapist. Since it is one of the two types of 
 * users, it extends the <code>User</code> class.
 * 
 * @author Yousef Bulbulia
 * 
 */
public class Therapist extends User {
   
	/**
	 * Creates a new <code>Therapist</code> belonging to the given database 
	 * with the passed <code>_id</code> field.
	 * 
	 * @param id       the <code>_id</code> field of this 
	 * 				   <code>Therapist</code>
	 * @param database the database this <code>Therapist</code> belongs to
	 */
    public Therapist(ObjectId id, MongoDatabase database) {
    	
    	super(id,database);
    	collection = database.getCollection("therapists");
    	
    }
    
    /**
     * 
     * @return the number of requests this <code>Therapist</code> has
     */
    public Integer getNumberOfRequests() {
    	return getDocument().getInteger("number_of_requests");
    }
    
    /**
     * 
     * @return the limit to how many patients this <code>Therapist</code> can
     * 		   have
     */
    public Integer getPatientLimit() {
    	return getDocument().getInteger("patient_limit");
    }
    
    /**
     * 
     * @return the limit to how many requests this <code>Therapist</code> can
     * 		   have
     */
    public Integer getRequestLimit() {
    	return getDocument().getInteger("request_limit");
    }
    
    /**
     * 
     * @return a <code>boolean</code> indicating whether this
     * 		   <code>Therapist</code> can receive requests
     */
    public Boolean canReceiveRequests() {
    	return getDocument().getBoolean("can_receive_requests");
    }

    /**
     * 
     * @return the rating of this <code>Therapist</code>
     */
    public Integer getRating() {
    	return getDocument().getInteger("rating");
    }
    
    /**
     * 
     * @return the number of patients that have given this
     * 		   <code>Therapist</code> a rating
     */
    public Integer getNumberOfRaters() {
    	return getDocument().getInteger("number_of_raters");
    }
    
    /**
     * 
     * @return an array of <code>Patient</code> objects that represent the
     * 		   patients this <code>Therapist</code> is treating, or null 
     * 		   if this <code>Therapist</code> does not have any
     */
    public Patient[] getPatients() {
    	
    	Document doc = getDocument();
    	
    	//Get an ObjectId[] of patient's _id fields
    	final Class<? extends List> listClass = new ArrayList<ObjectId>().getClass();
    	List<ObjectId> idsAsList = doc.get("patient_ids", listClass);
    	ObjectId[] patientIds = idsAsList.toArray(ObjectId[]::new);
    	
    	//Map the ObjectIds to the Patient objects they belong to
    	Patient[] patients = null;
	    if(patientIds != null) {
	    		
	    	patients = Arrays.stream(patientIds)
	    	   		.map(e -> new Patient(e, database))
	    	   		.toArray(Patient[]::new);
	    		
	    }
	    	
    	return patients;
    }
    
    /**
     * 
     * @return an array of <code>Chat</code> objects that represent the
     * 		   chats this <code>Therapist</code> has, or null 
     * 		   if this <code>Therapist</code> does not have any
     */
    public Chat[] getChats() {

    	Document doc = getDocument();
    	
    	//Get an ObjectId[] of chats's _id fields
    	final Class<? extends List> listClass = new ArrayList<ObjectId>().getClass();
    	List<ObjectId> idsAsList = doc.get("chat_ids", listClass);
    	ObjectId[] chatIds = idsAsList.toArray(ObjectId[]::new);
	    
    	//Map the ObjectIds to the Chat objects they belong to
    	Chat[] chats = null;
	    if(chatIds != null) {
	    		
	    	chats = Arrays.stream(chatIds)
	    	   		.map(e -> new Chat(e, database))
	    	   		.toArray( Chat[]::new);
	    		
	    }	
	    
    	return chats;
    	
    }
    
    /**
     * Adds a <code>Request</code>. If this <code>Therapist</code> is unable
     * to treat more patients without exceeding it's 
     * <code>patient_limit</code>, then this method throws an 
     * <code>IllegalStateException</code>
     * 
     * @param request the <code>Request</code> to be added
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
    
    /**
     * Adds a new <code>Patient</code> to the list of patients the
     * <code>Therapist</code> is treating. 
     * 
     * @param patient the <code>Patient</code> to add
     */
    public void addPatient(Patient patient) {
    	collection.findOneAndUpdate(eq(id), Updates.push("patient_ids", patient.getId()));
    }
    
    /**
     * Adds a new <code>Chat</code> to the list of chats the
     * <code>Therapist</code> has.
     * 
     * @param chat the <code>Chat</code> to add
     */
    public void addChat(Chat chat) {
    	collection.findOneAndUpdate(eq(id), Updates.push("chat_ids", chat.getId()));
    }
    
    /**
     * Takes a new rating from a patient and averages it with the existing
     * ratings for this <code>Therapist</code>. The updated rating is then
     * written to the database.
     * 
     * @param newUserRating the new rating
     */
    public void addRating(int newUserRating) throws IllegalArgumentException {
    	
    	Document doc = getDocument();
    	
    	Integer rating = doc.getInteger("rating");
    	Integer numberOfRaters = doc.getInteger("number_of_raters");
    	
    	if(newUserRating < 1 || newUserRating > 100) {
    		throw new IllegalArgumentException("Ratings must be between 1 and 100 (inclusive)");
    	}
    	
    	rating =  Math.round((rating * numberOfRaters + newUserRating)/(++numberOfRaters));
    	
    	collection.findOneAndUpdate(eq(id), Updates.set("rating", rating));
    	collection.findOneAndUpdate(eq(id), Updates.set("number_of_raters", numberOfRaters));
    	
    }

}
