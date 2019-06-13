package com.therapy.entities;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;

import java.util.Random;

import org.bson.Document;
import org.bson.types.ObjectId;

import com.mongodb.MongoException;
import com.mongodb.MongoWriteConcernException;
import com.mongodb.MongoWriteException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Updates;

/**
 * This class represents a request that a patient would send to a therapist in 
 * order to be accepted as one of their patients. It has fields for the patient
 * and therapist that are associated with the request, the status of the request
 * (if it is accepted or not), and the summary written by the sender 
 * (the patient) describing their situation.
 *
 * @author Yousef Bulbulia
 * 
 */
public class Request extends Entity{
	
	/**
	 * Creates a new <code>Request</code> with a specified <code>Patient</code> and
	 * <code>Therapist</code>, where the other fields are read in from the database
	 * using the given _id MonogoDB field.
	 * 
	 * @param patient   the patient sending this request
	 * @param therapist the therapist receiving this request
	 * @param id        the _id field that MongoDB will use
	 * @param database  the database this request belongs to
	 */
	public Request(ObjectId id, MongoDatabase database) {
	    	
	    	super(id, database);
	    	collection = database.getCollection("requests");
	    	
    }
	
	/**
	 * Creates a new <code>Request</code> with a unique _id field, witht the specified
	 * <code>Patient</code> and <code>Therapist</code>. After creation, the 
	 * <code>Chat</code> is inserted into the <code>chats</code> collection.
	 *  
	 * @param patient   the patient sending this request
	 * @param therapist the therapist receiving this request
	 * @param database  the database this request belongs to
	 */
	public Request(Patient patient, Therapist therapist, MongoDatabase database) 
			throws IllegalStateException {
		
		super(database);
		
		//Check if a request for these users already exists
		collection = database.getCollection("requests");
		
		ObjectId patientId = patient.getId();
    	ObjectId therapistId = therapist.getId();
    	Document requestDoc = collection.find(
    			and(
    					eq("patient_id", patientId), eq("therapist_id", therapistId)
    			)).first();
    	
    	if(requestDoc != null) {
    		throw new IllegalStateException("This constructor is only for Requests that"
    				+ "have not been created in the database yet");
    	}
    	
    	
		//initialize the class fields
    	id = getUniqueId();
    	collection.findOneAndUpdate(eq(id), Updates.set("patient_id", patient.getId()));
    	collection.findOneAndUpdate(eq(id), Updates.set("therapist_id", therapist.getId()));
    	
	}
	
	/**
	 * Creates a new <code>Request</code> with a unique _id field, with the specified
	 * <code>Patient</code>, <code>Therapist</code, and summary. After creation, the 
	 * <code>Chat</code> is inserted into the <code>chats</code> collection.
	 *  
	 * @param patient   the patient sending this request
	 * @param therapist the therapist receiving this request
	 * @param summary   the summary of the patient's problem(s)
	 * @param database  the database this request belongs to
	 */
	public Request(Patient patient, Therapist therapist, String summary, MongoDatabase database) 
			throws IllegalStateException {
		
		super(database);
		
		//Check if a request for these users already exists
		collection = database.getCollection("requests");
		
		ObjectId patientId = patient.getId();
    	ObjectId therapistId = therapist.getId();
    	Document requestDoc = collection.find(
    			and(
    					eq("patient_id", patientId), eq("therapist_id", therapistId)
    			)).first();
    	
    	if(requestDoc != null) {
    		throw new IllegalStateException("This constructor is only for Requests that"
    				+ "have not been created in the database yet");
    	}
    	
    	
		//initialize the class fields
    	id = getUniqueId();
    	
    	collection.findOneAndUpdate(eq(id), Updates.set("patient_id", patient.getId()));
    	collection.findOneAndUpdate(eq(id), Updates.set("therapist_id", therapist.getId()));
    	collection.findOneAndUpdate(eq(id), Updates.set("summary", summary));
    	
    	collection.findOneAndUpdate(eq(id), Updates.set("patient_accepted", false));
    	collection.findOneAndUpdate(eq(id), Updates.set("patient_denied", false));
    	collection.findOneAndUpdate(eq(id), Updates.set("therapist_accepted", false));
    	collection.findOneAndUpdate(eq(id), Updates.set("therapist_denied", false));
		
	}
    
    /**
     * 
     * @return the patient associated with the request.
     */
    public Patient getPatient() {
    	return new Patient(getDocument().getObjectId("patient_id"), database);
    }
    
    /**
     * 
     * @return the therapist associated with the request.
     */
    public Therapist getTherapist() {
    	return new Therapist(getDocument().getObjectId("therapist_id"), database);
    }
    
    /**
     * 
     * @return the summary associated with the request.
     */
    public String getSummary() {
    	return getDocument().getString("summary");
    }
    
    /**
     * 
     * @return a boolean value which, if <code>true</code>, indicates that the 
     *  request is accepted.
     */
    public boolean isAccepted() {
   
    	boolean patientAccepted = getDocument().getBoolean("patient_accepted", false);
    	boolean therapistAccepted = getDocument().getBoolean("therapist_accepted", false);
    	
    	if(patientAccepted && therapistAccepted) {
    		return true;
    	}
    	
    	return false;
        
    }
    
    /*
    public boolean isDenied() {
    	
    	Boolean patientDenied = getDocument().getBoolean("patient_denied", false);
    	Boolean therapistDenied = getDocument().getBoolean("therapist_denied", false);
    	
    	if(patientDenied || therapistDenied) {
     		return true;
     	}
    	
    	return false;
    	
    }
    */
    
    public boolean patientAccepted() {
    	return getDocument().getBoolean("patient_accepted", false);
    }
    
    public boolean therapistAccepted() {
    	return getDocument().getBoolean("therapist_accepted", false);
    }
    
    /**
     * Accepts the request. 
     */
    public void accept(Class<? extends User> userClass) throws IllegalStateException {
    	
    	if(userClass == Patient.class) {
    		
    		if(therapistAccepted()) {
    			
    			Patient patient = getPatient();
        		Therapist therapist = getTherapist();
        		
        		patient.setTherapist(therapist);
        		therapist.addPatient(patient);
        		
        		Chat chat = new Chat(patient, therapist, database);
            	patient.setChat(chat);
            	therapist.addChat(chat);
            	
    			collection.findOneAndUpdate(eq(id), Updates.set("patient_accepted", Boolean.valueOf(true)));
    			collection.findOneAndDelete(eq(id));
        		
    			
    		} else {
    			throw new IllegalStateException("Therapist must accept first before patient");
    		}
    		
    		
    	} else if(userClass == Therapist.class) {
    		
    		if(!patientAccepted()) {
    			collection.findOneAndUpdate(eq(id), Updates.set("therapist_accepted", Boolean.valueOf(true)));
    		} else {
    			throw new IllegalStateException("Therapist must accept first before patient");
    		}
    		
    	}
    
    }
    
    /**
     * Denies the request. The request is removed from the database.
     */
    public void deny() {
    	remove();
    }
    
    /**
     * Removes the request from the database. Should be called after
     * either accepting or denying the request.
     */
    public void remove() {
    	collection.findOneAndDelete(eq(id));
    }
    
}
