package com.therapy.entities;


import static com.mongodb.client.model.Filters.*;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import org.bson.Document;
import org.bson.types.ObjectId;

import com.mongodb.MongoException;
import com.mongodb.MongoWriteConcernException;
import com.mongodb.MongoWriteException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Updates;

/**
 * This class represents a chat between a patient and therapist. It has fields
 * to identify the patient and therapist that it belongs to, as well as a
 * <code>List</code> that contains all of the messages sent between users. 
 *
 * @author Yousef Bulbulia
 * 
 */
public class Chat extends Entity{
    
    
    /**
     * Creates a new <code>Chat</code> with a unique _id field, and no messages. 
     * After creation, the <code>Chat</code> is inserted into the <code>chats</code> 
     * collection. This is meant for chats that have not been created in the database
     * yet.
     * 
     * @param patient   the patient using this chat
     * @param therapist the therapist using this chat
     * @param database  the database this chat belongs to
     */
    public Chat(Patient patient, Therapist therapist, MongoDatabase database) throws IllegalStateException {
    	
    	
    	super(database);
    	System.out.println("Chat(): 1");
    	collection = database.getCollection("chats");
    	
    	//If there is already a <code>Chat</code> for these users, then throw <code>IllegalStateException</code>
    	System.out.println("Chat(): 2");
    	ObjectId patientId = patient.getId();
    	ObjectId therapistId = therapist.getId();
    	System.out.println("Chat(): 3");
    	Document doc = collection.find(
    			and(
    					eq("patient_id", patientId), eq("therapist_id", therapistId)
    			)).first();
    	System.out.println("Chat(): 4");
    	if(doc != null) {
    		throw new IllegalStateException("This constructor is only for Chats that"
    				+ "have not been created in the database yet");
    	}
    	
    	System.out.println("Chat(): 5");
    	id = getUniqueId();
    	System.out.println("Chat(): 6");
		collection.findOneAndUpdate(eq(id), Updates.set("patient_id", patient.getId()));
		System.out.println("Chat(): 7");
		collection.findOneAndUpdate(eq(id), Updates.set("therapist_id", therapist.getId()));
		System.out.println("Chat(): 8");
    }
    
    public Chat(ObjectId id, MongoDatabase database) {
    	
    	super(id, database);
    	collection = database.getCollection("chats");
    	
    }
    
    /**
     * 
     * @return the patient associated with this chat.
     */
    public Patient getPatient() {
    	
    	ObjectId patientId = getDocument().getObjectId("patient_id");
    	return new Patient(patientId, database);
    	
    }
    
    /**
     * 
     * @return the therapist associated with this chat.
     */
    public Therapist getTherapist() {
    	
    	ObjectId therapistId = getDocument().getObjectId("therapist_id");
    	return new Therapist(therapistId, database);
        
    }
    
    /**
     * 
     * @return the <code>List</code> of messages that make up the chat.
     */
    public Message[] getMessages() {
    	
    	Document doc = getDocument();
    	
    	ObjectId[] messageIds = doc.get("message_ids", ObjectId[].class);
    	
    	Message[] messages = null;
    	if(messageIds != null) {
    		messages = Arrays.stream(messageIds)
    	    		.map(e -> new Message(e, database))
    	    		.toArray( Message[]::new);
    	}
    	
    	return messages;
        
    }
    
    
    
    /**
     * Retrieves a specified number of the most recent messages. The newest
     * <code>Message</code>s are the end of the returned array, while the
     * oldest <code>Message</code>s are at the beginning.
     * 
     * @param numberOfMessages the number of messages to return
     */
    public Message[] getPreviousMessages(int numberOfMessages) throws NoSuchElementException {
    	
    	Document doc = getDocument();
    	
    	ObjectId[] messageIds = doc.get("message_ids", ObjectId[].class);
    	
    	Message[] messages = Arrays.stream(messageIds)
    		.map(e -> new Message(e, database))
    		.toArray( Message[]::new);
    	
    	Message[] previousMessages = null;
    	if(messages != null) {
	    	previousMessages = new Message[numberOfMessages];
	    	for(int i = numberOfMessages - 1, j = messages.length - 1; i < numberOfMessages && i < messages.length; i--, j--) {
	    		previousMessages[i] = messages[j];
	    	}
    	}
    	
        return previousMessages;
        
    }
    
    public Message[] getMessagesSince(Message message) {
    	
    	List<Message> messages = Arrays.asList(getPreviousMessages(100));
    	messages.subList(messages.indexOf(message), messages.size());
    	return messages.toArray(Message[]::new);
    	
    }
    
    /**
     * Adds a message to the <code>Chat</code>
     * 
     * @param message the message to be added to the chat.
     */
    public void addMessage(Message message) {
        collection.findOneAndUpdate(eq(id), Updates.push("message_ids", message.getId()));
    }
	
}
