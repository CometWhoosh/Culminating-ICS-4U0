package com.therapy.entities;

import static com.mongodb.client.model.Filters.eq;

import org.bson.Document;
import org.bson.types.Binary;
import org.bson.types.ObjectId;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Updates;

/**
 * This class represents a patient. Since it is one of the two types of users,
 * it extends the User class. It has a field for the list of requests the 
 * patient has sent.
 *
 * @author Yousef Bulbulia
 */
public class Patient extends User {
	
    /**
     * Creates a new <code>Patient</code> that initializes it's data using the data
     * from the database using the passed id.
     * 
     * @param id       the id of the <code>Patient</code>
     * @param database the database that the <code>Patient</code> belongs to
     */
    public Patient(ObjectId id, MongoDatabase database) {
    	
    	super(id,database);
    	collection = database.getCollection("patients");
    	
    }
    
    public Therapist getTherapist() {
    	
    	ObjectId therapistId = getDocument().getObjectId("therapist_id");
    	
    	if(therapistId == null) {
    		return null;
    	}
    	
    	return new Therapist(therapistId, database);
    }
    
    public Chat getChat() {
    	
    	ObjectId chatId = getDocument().getObjectId("chat_id");
    	
    	if(chatId == null) {
    		return null;
    	}
    	
    	return new Chat(chatId, database);
    }
    
    /**
     * 
     * @param therapist the new <code>Therapist</code>
     */
    public void setTherapist(Therapist therapist) {
    	collection.findOneAndUpdate(eq(id), Updates.set("therapist_id", therapist.getId()));
    }
    
    /**
     * 
     * @param chat the new <code>Chat</code>
     */
    public void setChat(Chat chat) {
    	collection.findOneAndUpdate(eq(id), Updates.set("chat_id", chat.getId()));
    }
    

}
