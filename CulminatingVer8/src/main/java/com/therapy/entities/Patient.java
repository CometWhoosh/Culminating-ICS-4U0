package com.therapy.entities;


import org.bson.types.ObjectId;

import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Updates;
import static com.mongodb.client.model.Filters.eq;

/**
 * This class represents a patient. Since it is one of the two types of users,
 * it extends the <code>User</code> class.
 *
 * @author Yousef Bulbulia
 */
public class Patient extends User {
	
	/**
	 * Creates a new <code>User</code> belonging to the given database with
	 * the passed <code>_id</code> field.
	 * 
	 * @param id       the <code>_id</code> field of this <code>Patient</code>
	 * @param database the database this <code>Patient</code> belongs to
	 */
    public Patient(ObjectId id, MongoDatabase database) {
    	
    	super(id,database);
    	collection = database.getCollection("patients");
    	
    }
    
    /**
     * 
     * @return the <code>Therapist</code> of this <code>Patient</code>, or null
     * 		   if the <code>Patient</code> does not have one
     */
    public Therapist getTherapist() {
    	
    	ObjectId therapistId = getDocument().getObjectId("therapist_id");
    	
    	if(therapistId == null) {
    		return null;
    	}
    	
    	return new Therapist(therapistId, database);
    }
    
    /**
     * 
     * @return the <code>Chat</code> of this <code>Patient</code>, or null
     * 		   if the <code>Patient</code> does not have one
     */
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
