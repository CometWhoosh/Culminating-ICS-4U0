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
 * order to be accepted as one of their patients. First, the therapist can
 * accept or deny the patient. If they deny the patient, then the request is
 * removed from the database. If they accept, then the patient has the option
 * to accept or deny the request. If they deny, the request is removed from the
 * database. If they accept, then the therapist would take on the patient and
 * the request would be removed from the database.
 * 
 * A <code>Request</code> has an optional summary, which is a body of text that
 * the patient writes that describes their problem(s).
 *
 * @author Yousef Bulbulia
 * 
 */
public class Request extends Entity{
	
	/**
	 * Creates a new <code>Request</code> belonging to the given database with
	 * the passed <code>_id</code> field.
	 * 
	 * @param id       the <code>_id</code> field of this <code>Request</code>
	 * @param database the database this <code>Request</code> belongs to
	 */
	public Request(ObjectId id, MongoDatabase database) {
	    	
	    	super(id, database);
	    	collection = database.getCollection("requests");
	    	
    }
	
	/**
	 * Creates a new <code>Request</code> with a unique _id field and the 
	 * specified <code>Patient</code> and <code>Therapist</code>. If this 
     * constructor is used for <code>Request</code> objects that have already 
     * been created in the database, an <code>IllegalStateException<code> will 
     * be thrown.
	 *  
	 * @param patient   the patient sending this request
	 * @param therapist the therapist receiving this request
	 * @param database  the database this request belongs to
	 */
	public Request(Patient patient, Therapist therapist, MongoDatabase database) 
			throws IllegalStateException {
		
		super(database);
		collection = database.getCollection("requests");
		
		/*
    	 * If there is already a Request for these Users, then throw an 
    	 * IllegalStateException
    	 */
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
    	
    	
    	id = getUniqueId();
    	collection.findOneAndUpdate(eq(id), Updates.set("patient_id", patient.getId()));
    	collection.findOneAndUpdate(eq(id), Updates.set("therapist_id", therapist.getId()));
    	
	}
	
	/**
	 * Creates a new <code>Request</code> with a unique _id field and the 
	 * specified <code>Patient</code>, <code>Therapist</code>, and summary. If 
	 * this constructor is used for <code>Request</code> objects that have 
	 * already been created in the database, an 
	 * <code>IllegalStateException<code> will be thrown.
	 *  
	 * @param patient   the patient sending this request
	 * @param therapist the therapist receiving this request
	 * @param summary   the summary of the patient's problem(s)
	 * @param database  the database this request belongs to
	 */
	public Request(Patient patient, Therapist therapist, String summary, MongoDatabase database) 
			throws IllegalStateException {
		
		super(database);
		collection = database.getCollection("requests");
		
		/*
    	 * If there is already a Request for these Users, then throw an 
    	 * IllegalStateException
    	 */
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
     * @return the <code>Patient</code> associated with the 
     * 		   <code>Request</code>
     */
    public Patient getPatient() {
    	return new Patient(getDocument().getObjectId("patient_id"), database);
    }
    
    /**
     * 
     * @return the <code>Therapist</code> associated with the 
     * 		   <code>Request</code>
     */
    public Therapist getTherapist() {
    	return new Therapist(getDocument().getObjectId("therapist_id"), database);
    }
    
    /**
     * 
     * @return the summary associated with the<code>Request</code>
     */
    public String getSummary() {
    	return getDocument().getString("summary");
    }
    
    /**
     * 
     * @return a <code>boolean</code> value which, if <code>true</code>, 
     * 		   indicates that the <code>Request</code> is accepted.
     */
    public boolean isAccepted() {
   
    	boolean patientAccepted = getDocument().getBoolean("patient_accepted", false);
    	boolean therapistAccepted = getDocument().getBoolean("therapist_accepted", false);
    	
    	if(patientAccepted && therapistAccepted) {
    		return true;
    	}
    	
    	return false;
        
    }
    
    /**
     * 
     * @return a <code>boolean</code> value which, if <code>true</code>, 
     * 		   indicates that the patient accepted the <code>Request</code>
     */
    public boolean patientAccepted() {
    	return getDocument().getBoolean("patient_accepted", false);
    }
    
    /**
     * 
     * @return a <code>boolean</code> value which, if <code>true</code>, 
     * 		   indicates that the patient accepted the <code>Request</code>.
     */
    public boolean therapistAccepted() {
    	return getDocument().getBoolean("therapist_accepted", false);
    }
    
    /**
     * Accepts the code>Request</code>.
     * 
     * @param userClass the class of the <code>User</code> calling the method.
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
            	
            	patient.removeRequest(this);
            	therapist.removeRequest(this);
            	
    			collection.findOneAndUpdate(eq(id), Updates.set("patient_accepted", true));
    			collection.findOneAndDelete(eq(id));
    			
        		
    			
    		} else {
    			throw new IllegalStateException("Therapist must accept first before patient");
    		}
    		
    		
    	} else if(userClass == Therapist.class) {
    		
    		if(!patientAccepted()) {
    			collection.findOneAndUpdate(eq(id), Updates.set("therapist_accepted", true));
    		} else {
    			throw new IllegalStateException("Therapist must accept first before patient");
    		}
    		
    	}
    
    }
    
    /**
     * Denies the <code>Request</code>. The <code>Request</code> is removed 
     * from the database.
     */
    public void deny() {
    	remove();
    }
    
    /**
     * Removes the <code>Request</code> from the database. This method should 
     * be called after either accepting or denying the <code>Request</code>.
     */
    public void remove() {
    	collection.findOneAndDelete(eq(id));
    }
    
}
