package com.therapy.entities;

import org.bson.Document;
import org.bson.types.ObjectId;

import com.mongodb.MongoException;
import com.mongodb.MongoWriteConcernException;
import com.mongodb.MongoWriteException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;

import java.util.Random;

/**
 * This class represents a message sent in a chat. It has fields for the content
 * of the message as well as the associated patient and therapist.
 * 
 * @author Yousef Bulbulia
 * 
 */
public class Message extends Entity{
	
	String content;
	Boolean senderIsPatient;
    Patient patient;
    Therapist therapist;
    
    public Message(Patient patient, Therapist therapist, ObjectId id, MongoDatabase database) {
    	
    	super(id, database);
    	
    	this.patient = patient;
    	this.therapist = therapist;
    	
    	MongoCollection<Document> collection = database.getCollection("messages");
    	Document doc = collection.find(eq(id)).first();
    	
    	senderIsPatient = doc.getBoolean("sender_is_patient").booleanValue();
        content = doc.getString("content");
    	
    }
    
    /**
     * Creates a new <code>Message</code> with a unique _id field.  
     * After creation, the <code>Chat</code> is inserted into the <code>chats</code> 
     * collection.
     * 
     * @param patient   the patient using this chat
     * @param therapist the therapist using this chat
     * @param database  the database this chat belongs to
     */
    public Message(Patient patient, Therapist therapist, Boolean senderIsPatient, String content,
    		MongoDatabase database) {
    	
    	MongoCollection<Document> collection = database.getCollection("messages");
    	
    	ObjectId patientId = patient.getId();
    	ObjectId therapistId = therapist.getId();
    	Document messageDoc = collection.find(
    			and(
    					eq("patient_id", patientId), eq("therapist_id", therapistId)
    			)).first();
    	
    	ObjectId id = null;
    	
    	if(messageDoc == null) {
    		id = messageDoc.getObjectId("_id");
    	} else {
    		throw new IllegalStateException("This constructor is only for Messages that"
    				+ "have not been created in the database yet");
    	}
    	
    	this.id = id;
    	this.database = database;
    	
    	this.patient = patient;
    	this.therapist = therapist;
    	
    	this.senderIsPatient = senderIsPatient;
        this.content = content;
        
        insertIntoCollection();
    	
    }
    
    
    /**
     * 
     * @return the content of the message.
     */
    public String getContent() {
        return content;
    }
    
    /**
     * 
     * @return the patient that is associated with the message.
     */
    public Patient getPatient() {
        return patient;
    }

    /**
     * 
     * @return the therapist that is associated with the message.
     */
    public Therapist getTherapist() {
        return therapist;
    }
    
    
    public void insertIntoCollection() throws MongoWriteException, MongoWriteConcernException, MongoException {
    	
    	MongoCollection<Document> collection = database.getCollection("messages");
    	
    	/*
    	 content;
    Patient patient;
    boolean senderIsPatient;
    Therapist therapist;
    	 */
    	
    	Document doc = new Document("_id", id)
    			.append("content", content)
    			.append("sender_is_patient", senderIsPatient)
    			.append("patient_id", patient.getId())
    			.append("therapist_id", therapist.getId());
    	
    	collection.insertOne(doc);
    	
    }
    
    public void updateToCollection() throws MongoWriteException, MongoWriteConcernException, MongoException {
    	
    	MongoCollection<Document> collection = database.getCollection("messages");
    	
	    	/*
	    	 content;
	    Patient patient;
	    boolean senderIsPatient;
	    Therapist therapist;
	    	 */
    	
    	Document doc = new Document("_id", id)
    			.append("content", content)
    			.append("sender_is_patient", senderIsPatient)
    			.append("patient_id", patient.getId())
    			.append("therapist_id", therapist.getId());
    	
    	collection.findOneAndUpdate(eq(id), doc);
    	
    }
	
}
