package com.therapy.entities;


import org.bson.Document;
import org.bson.types.ObjectId;

import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Updates;
import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;

/**
 * This class represents a message sent in a chat.
 * 
 * @author Yousef Bulbulia
 * 
 */
public class Message extends Entity{
    
	/**
	 * Creates a new <code>Message</code> belonging to the given database with
	 * the passed <code>_id</code> field.
	 * 
	 * @param id       the <code>_id</code> field of this <code>Message>/code>
	 * @param database the database this <code>Message</code> belongs to
	 */
    public Message(ObjectId id, MongoDatabase database) {
    	
    	super(id, database);
    	collection = database.getCollection("patients");
    	
    }
    
    /**
     * Creates a new <code>Message</code> with a unique <code>_id</code> field, 
     * along with the specified parameters. If this constructor is used for 
     * <code>Message</code> objects that have already been created in the 
     * database, an <code>IllegalStateException<code> will be thrown.
     * 
     * @param patient         the patient this message belongs to
     * @param therapist       the therapist this message belongs to
     * 
     * @param senderIsPatient a <code>boolean</code> value which is 
     * 						  <code>true</code> if the patient sent the 
     * 						  <code>Message</code>
     * 
     * @param content         the textual content of the message
     * @param database        the database this message belongs to
     */
    public Message(Patient patient, Therapist therapist, Boolean senderIsPatient, String content,
    		MongoDatabase database) throws IllegalStateException {
    	
    	super(database);
    	collection = database.getCollection("messages");
    	
    	/*
    	 * If there is already a Message for these Users, then throw an 
    	 * IllegalStateException
    	 */
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
    	
		
    	id = getUniqueId();
    	collection.findOneAndUpdate(eq(id), Updates.set("patient_id", patient.getId()));
    	collection.findOneAndUpdate(eq(id), Updates.set("therapist_id", therapist.getId()));
    	collection.findOneAndUpdate(eq(id), Updates.set("sender_is_patient", senderIsPatient));
    	collection.findOneAndUpdate(eq(id), Updates.set("content", content));
    	
    }
    
    /**
     * 
     * @return the <code>Patient</code> that is associated with this
     * 		   <code>Message</code>
     */
    public Patient getPatient() {
    	return new Patient(getDocument().getObjectId("patient_id"), database);
    }

    /**
     * 
     * @return the <code>Therapist</code> that is associated with this
     * 		   <code>Message</code>
     */
    public Therapist getTherapist() {
    	return new Therapist(getDocument().getObjectId("therapist_id"), database);
    }
    
    /**
     * 
     * @return the content of the <code>Message</code>.
     */
    public String getContent() {
        return getDocument().getString("content");
    }
    
    /**
     * 
     * @return a <code>boolean</code> value which is  <code>true</code> if the
     * 		   patient sent the <code>Message</code>
     */
    public Boolean senderIsPatient() {
    	return getDocument().getBoolean("sender_is_patient");
    }
    
}
