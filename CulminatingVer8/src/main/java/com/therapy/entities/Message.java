package com.therapy.entities;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;

import org.bson.Document;
import org.bson.types.ObjectId;

import com.mongodb.MongoException;
import com.mongodb.MongoWriteConcernException;
import com.mongodb.MongoWriteException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Updates;

/**
 * This class represents a message sent in a chat. It has fields for the content
 * of the message as well as the associated patient and therapist.
 * 
 * @author Yousef Bulbulia
 * 
 */
public class Message extends Entity{
    
    /**
     * Creates a new <code>Message</code> that initializes any remaining fields 
     * from the database using the passed id.
     * 
     * @param id        the id of this chat
     * @param database  the database this chat belongs to
     */
    public Message(ObjectId id, MongoDatabase database) {
    	
    	super(id, database);
    	collection = database.getCollection("patients");
    	
    }
    
    public Message(Patient patient, Therapist therapist, Boolean senderIsPatient, String content,
    		MongoDatabase database) throws IllegalStateException {
    	
    	super(database);
    	
    	
    	//Check if a message for these users already exists
    	collection = database.getCollection("messages");
    	
    	ObjectId patientId = patient.getId();
    	ObjectId therapistId = therapist.getId();
    	Document messageDoc = collection.find(
    			and(
    					eq("patient_id", patientId), eq("therapist_id", therapistId)
    			)).first();
    	
    	if(messageDoc != null) {
    		throw new IllegalStateException("This constructor is only for Messages that"
    				+ "have not been created in the database yet");
    	}
    	
		
		//Initialize the class fields using data from the database
    	id = getUniqueId();
    	collection.findOneAndUpdate(eq(id), Updates.set("patient_id", patient.getId()));
    	collection.findOneAndUpdate(eq(id), Updates.set("therapist_id", therapist.getId()));
    	collection.findOneAndUpdate(eq(id), Updates.set("sender_is_patient", senderIsPatient));
    	collection.findOneAndUpdate(eq(id), Updates.set("content", content));
    }
    
    
    
    
    /**
     * 
     * @return the <code>Patient</code> that is associated with the <code>Message</code>.
     */
    public Patient getPatient() {
    	return new Patient(getDocument().getObjectId("patient_id"), database);
    }

    /**
     * 
     * @return the <code>Therapist</code> that is associated with the <code>Message</code>.
     */
    public Therapist getTherapist() {
    	return new Therapist(getDocument().getObjectId("therapist_id"), database);
    }
    
    /**
     * 
     * @return the content of the message.
     */
    public String getContent() {
        return getDocument().getString("content");
    }
    
    public Boolean senderIsPatient() {
    	return getDocument().getBoolean("sender_is_patient");
    }
    
}
