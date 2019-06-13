package com.therapy.entities;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bson.Document;
import org.bson.types.Binary;
import org.bson.types.ObjectId;

import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Updates;
import static com.mongodb.client.model.Filters.eq;

/**
 * This class represents a user. It is abstract because it is impractical to 
 * have a user that is not a patient nor a therapist, as it would not be able
 * to talk to a therapist, or help patients.
 * 
 * @author Yousef Bulbulia
 * 
 */
public abstract class User extends Entity{
    
	/**
	 * Creates a new <code>User</code> belonging to the given database with
	 * the passed id.
	 * 
	 * @param id       the id of this <code>User</code>
	 * @param database the database this <code>User</code> belongs to
	 */
    public User(ObjectId id, MongoDatabase database) {
		super(id, database);
    }
    
    /**
     * 
     * @return the first name of the <code>User</code>
     */
    public String getFirstName() {
        return getDocument().getString("first_name");
    }
    
    /**
     * 
     * @return the last name of the <code>User</code>
     */
    public String getLastName() {
        return getDocument().getString("last_name");
    }
    
    /**
     * Returns the full name of the <code>User</code>. A full name consists of
     * the first name followed by a single whitespace character, which is then
     * followed by the last name.
     * 
     * @return the full name of the <code>User</code>
     */
    public String getFullName() {
    	
    	Document doc = getDocument();
    	return doc.getString("first_name") + " " + doc.getString("last_name");
    	
    }
    
    /**
     * 
     * @return the <code>User</code>'s email
     */
    public String getEmail() {
    	return getDocument().getString("email");
    }
    
    /**
     * 
     * @return the hash of the <code>User</code>'s password
     */
    public byte[] getHashedPassword() {
    	return getDocument().get("password_hash", Binary.class).getData();
    }
    
    /**
     * 
     * @return the <code>User</code>'s salt
     */
    public byte[] getSalt() {
    	return getDocument().get("salt", Binary.class).getData();
    }
    
    /**
     * 
     * @param firstName the first name of the <code>User</code>
     */
    public void setFirstName(String firstName) {
        collection.findOneAndUpdate(eq(id), Updates.set("first_name", firstName));
    }
    
    /**
     * 
     * @param lastName the last name of the <code>User</code>
     */
    public void setLastName(String lastName) {
    	 collection.findOneAndUpdate(eq(id), Updates.set("last_name", lastName));
    }
    
    /**
     * 
     * @param email the <code>User</code>'s email
     */
    public void setEmail(String email) {
    	 collection.findOneAndUpdate(eq(id), Updates.set("email", email));
    }
    
    /**
     * 
     * @param hashedPassword the hash of the <code>User</code>'s password
     */
    public void setHashedPassword(byte[] hashedPassword) {
    	 collection.findOneAndUpdate(eq(id), Updates.set("password_hash", new Binary(hashedPassword)));
    }
    
    /**
     * 
     * @param salt the <code>User</code>'s salt
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
    
    /**
     * 
     * @param request the <code>Request</code> to remove
     */
    public void removeRequest(Request request) {
    	collection.findOneAndUpdate(eq(id), Updates.pull("request_ids", request.getId()));
    }
    
    /**
     * 
     * @return an array of the <code>Request</code> objects that belong to the
     * 		   <code>User</code>. If the <code>User</code> has no 
     *         <code>Request</code> objects, this method returns null
     */
    public Request[] getRequests() {
    	
    	Document doc = getDocument();
    	Request[] requests = null;
    	
    		
    	//Get the ObjectIds of the Requests
    	final Class<? extends List> listClass = new ArrayList<ObjectId>().getClass();
    	List<ObjectId> idsAsList = doc.get("request_ids", listClass);
    	ObjectId[] requestIds = idsAsList.toArray(ObjectId[]::new);
    		
    	//Map the ObjectIds to the Request objects they belong to
	    requests = null;
	    if(requestIds != null) {
	    	
	    	requests = Arrays.stream(requestIds)
	    	   		.map(e -> new Request(e, database))
	    	   		.toArray(Request[]::new);
	    	
	    }
	    	
    	return requests;
        
    }
    
}
