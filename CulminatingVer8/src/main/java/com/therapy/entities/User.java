package com.therapy.entities;

import static com.mongodb.client.model.Filters.eq;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bson.Document;
import org.bson.types.Binary;
import org.bson.types.ObjectId;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Updates;

/**
 * This class represents a user. It is abstract because it is impractical to 
 * have a user that is not a patient nor a therapist, as it would not be able
 * to talk to a therapist, or help patients.
 * 
 * @author Yousef Bulbulia
 * 
 */
public abstract class User extends Entity{
    
    public User(ObjectId id, MongoDatabase database) {
		super(id, database);
    }
    
    /**
     * 
     * @return the first name of the user.
     */
    public String getFirstName() {
        return getDocument().getString("first_name");
    }
    
    /**
     * 
     * @return the last name of the user.
     */
    public String getLastName() {
        return getDocument().getString("last_name");
    }
    
    public String getFullName() {
    	
    	Document doc = getDocument();
    	return doc.getString("first_name") + " " + doc.getString("last_name");
    	
    }
    
    /**
     * 
     * @return the user's email.
     */
    public String getEmail() {
    	return getDocument().getString("email");
    }
    
    /**
     * 
     * @return the hashed version of the user's password.
     */
    public byte[] getHashedPassword() {
    	return getDocument().get("password_hash", Binary.class).getData();
    }
    
    /**
     * 
     * @return the user's salt.
     */
    public byte[] getSalt() {
    	return getDocument().get("salt", Binary.class).getData();
    }
    
    /**
     * 
     * @param firstName the first name of the user.
     */
    public void setFirstName(String firstName) {
        collection.findOneAndUpdate(eq(id), Updates.set("first_name", firstName));
    }
    
    /**
     * 
     * @param lastName the last name of the user.
     */
    public void setLastName(String lastName) {
    	 collection.findOneAndUpdate(eq(id), Updates.set("last_name", lastName));
    }
    
    /**
     * 
     * @param email the user's email.
     */
    public void setEmail(String email) {
    	 collection.findOneAndUpdate(eq(id), Updates.set("email", email));
    }
    
    /**
     * 
     * @param hashedPassword the hashed version of the user's password.
     */
    public void setHashedPassword(byte[] hashedPassword) {
    	 collection.findOneAndUpdate(eq(id), Updates.set("password_hash", new Binary(hashedPassword)));
    }
    
    /**
     * 
     * @param salt the user's salt.
     */
    public void setSalt(byte[] salt) {
    	 collection.findOneAndUpdate(eq(id), Updates.set("salt", new Binary(salt)));
    }
    
    /**
     * 
     * @param request the new <code>Request</code>
     */
    public void addRequest(Request request) {
    	collection.findOneAndUpdate(eq(id), Updates.push("request_ids", request.getId()));
    }
    
    public void removeRequest(Request request) {
    	collection.findOneAndUpdate(eq(id), Updates.pull("request_ids", request.getId()));
    }
    
    /**
     * 
     * @return an array of the requests.
     */
    public Request[] getRequests() {
    	
    	Document doc = getDocument();
    	Request[] requests = null;
    	try {
    		
	    	//ObjectId[] requestIds = (ObjectId[])doc.get("request_ids", new ArrayList<ObjectId>().getClass()).toArray();
    		final Class<? extends List> listClass = new ArrayList<ObjectId>().getClass();
    		List<ObjectId> idsAsList = doc.get("request_ids", listClass);
    		ObjectId[] requestIds = idsAsList.toArray(ObjectId[]::new);
    		
	    	requests = null;
	    	if(requestIds != null) {
	    		
	    		requests = Arrays.stream(requestIds)
	    	    		.map(e -> new Request(e, database))
	    	    		.toArray(Request[]::new);
	    		
	    	}
	    	
    	} catch(NullPointerException e) {
    		
    	}
    	
    	return requests;
        
    }
    
}
