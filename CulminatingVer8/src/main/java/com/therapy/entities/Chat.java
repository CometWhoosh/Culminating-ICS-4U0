package com.therapy.entities;


import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

import org.bson.Document;
import org.bson.types.ObjectId;

import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Updates;
import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;

/**
 * This class represents a chat between a patient and therapist.
 *
 * @author Yousef Bulbulia
 * 
 */
public class Chat extends Entity{
    
    /**
     * Creates a new <code>Chat</code> with a unique _id field, and no 
     * messages. After creation, the <code>Chat</code> is inserted into the 
     * <code>chats</code> collection. This is meant for <code>Chat</code> 
     * objects that have not been created in the database yet. If this 
     * constructor is used for <code>Chat</code> objects that have already been
     * created in the database, an <code>IllegalStateException<code> will be 
     * thrown.
     * 
     * @param patient   the patient using this chat
     * @param therapist the therapist using this chat
     * @param database  the database this chat belongs to
     */
    public Chat(Patient patient, Therapist therapist, MongoDatabase database) throws IllegalStateException {
    	
    	super(database);
    	collection = database.getCollection("chats");
    	
    	/*
    	 * If there is already a Chat for these Users, then throw an 
    	 * IllegalStateException
    	 */
    	ObjectId patientId = patient.getId();
    	ObjectId therapistId = therapist.getId();
    	Document doc = collection.find(
    			and(
    					eq("patient_id", patientId), eq("therapist_id", therapistId)
    			)).first();
    	
    	if(doc != null) {
    		throw new IllegalStateException("This constructor is only for Chats that"
    				+ "have not been created in the database yet");
    	}
    	
    	id = getUniqueId();
		collection.findOneAndUpdate(eq(id), Updates.set("patient_id", patient.getId()));
		collection.findOneAndUpdate(eq(id), Updates.set("therapist_id", therapist.getId()));
    }
    
    /**
	 * Creates a new <code>Chat</code> belonging to the given database with
	 * the passed <code>_id</code> field.
	 * 
	 * @param id       the <code>_id</code> field of this <code>Chat</code>
	 * @param database the database this <code>Chat</code> belongs to
	 */
    public Chat(ObjectId id, MongoDatabase database) {
    	
    	super(id, database);
    	collection = database.getCollection("chats");
    	
    }
    
    /**
     * 
     * @return the patient associated with this <code>Chat</code>
     */
    public Patient getPatient() {
    	
    	ObjectId patientId = getDocument().getObjectId("patient_id");
    	return new Patient(patientId, database);
    	
    }
    
    /**
     * 
     * @return the therapist associated with this <code>Chat</code>
     */
    public Therapist getTherapist() {
    	
    	ObjectId therapistId = getDocument().getObjectId("therapist_id");
    	return new Therapist(therapistId, database);
        
    }
    
    /**
     * 
     * @return a <code>List</code> of <code>Message</code> objects that make up 
     *         the <code>Chat</code>. If there are no messages in the
     * 		   <code>Chat</code>, then this method returns null.
     */
    public Message[] getMessages() {
    	
    	Document doc = getDocument();
    	
    	ObjectId[] messageIds = doc.get("message_ids", ObjectId[].class);
    	
    	//Map the ObjectIds to the Message objects they belong to
    	Message[] messages = null;
    	if(messageIds != null) {
    		messages = Arrays.stream(messageIds)
    	    		.map(e -> new Message(e, database))
    	    		.toArray( Message[]::new);
    	}
    	
    	return messages;
        
    }
    
    /**
     * Retrieves an array of the most recent <code>Message</code> objects with 
     * a length determined by <code>numberOfMessages</code>. The newest
     * <code>Message</code> objects are the end of the returned array, while 
     * the oldest are at the beginning. If there are no messages in the
     * <code>Chat</code>, then this method returns null.
     * 
     * @param numberOfMessages the number of <code>Message</code> objects to 
     * 						   return
     */
    public Message[] getPreviousMessages(int numberOfMessages) throws NoSuchElementException {
    	
    	Document doc = getDocument();
    	
    	ObjectId[] messageIds = doc.get("message_ids", ObjectId[].class);
    	
    	//Map the ObjectIds to the Message objects they belong to
    	Message[] messages = Arrays.stream(messageIds)
    		.map(e -> new Message(e, database))
    		.toArray( Message[]::new);
    	
    	/*
    	 * Reverse the order of messages so that the Messages are 
    	 * in chronological order
    	 */
    	Message[] previousMessages = null;
    	if(messages != null) {
    		
	    	previousMessages = new Message[numberOfMessages];
	    	for(int i = numberOfMessages - 1, j = messages.length - 1; 
	    			i < numberOfMessages && i < messages.length; i--, j--) {
	    		previousMessages[i] = messages[j];
	    	}
	    	
    	}
    	
        return previousMessages;
        
    }
    
    /**
     * Retrieves an array of <code>Message</code> objects that were inserted
     * into the database since <code>message</code> was inserted.
     * 
     * @param message the <code>Message</code> to use as reference
     * @return        an array of the <code>Message</code> objects that were 
     * 	              inserted into the database since <code>message</code>
     */
    public Message[] getMessagesSince(Message message) {
    	
    	List<Message> messages = Arrays.asList(getPreviousMessages(100));
    	messages.subList(messages.indexOf(message), messages.size());
    	return messages.toArray(Message[]::new);
    	
    }
    
    /**
     * Adds a <code>Message</code> to the <code>Chat</code>
     * 
     * @param message the <code>Message</code> to be added to the 
     * 	              <code>Chat</code>.
     */
    public void addMessage(Message message) {
        collection.findOneAndUpdate(eq(id), Updates.push("message_ids", message.getId()));
    }
	
}
