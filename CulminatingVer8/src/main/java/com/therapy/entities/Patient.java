package com.therapy.entities;

import org.bson.Document;
import org.bson.types.Binary;
import org.bson.types.ObjectId;

import com.mongodb.MongoException;
import com.mongodb.MongoWriteConcernException;
import com.mongodb.MongoWriteException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import static com.mongodb.client.model.Filters.eq;

/**
 * This class represents a patient. Since it is one of the two types of users,
 * it extends the User class. It has a field for the list of requests the 
 * patient has sent.
 *
 * @author Yousef Bulbulia
 */
public class Patient extends User {
	
	//TODO: Make <code>therapists</code> into a single <code>Therapist</code>. Same for requests
	private Request request;
    private Therapist therapist;
    private Chat chat;
    
    public Patient(ObjectId id, MongoDatabase database) {
    	
    	super(id,database);
    	
    	MongoCollection<Document> patientCollection = database.getCollection("patients");
    	
    	Document patientDoc = patientCollection.find(eq(id)).first();
    	
    	firstName = patientDoc.getString("first_name");
    	lastName = patientDoc.getString("last_name");
    	email = patientDoc.getString("email");
    	hashedPassword = patientDoc.get("password_hash", Binary.class).getData();
    	salt = patientDoc.get("salt", Binary.class).getData();
    	
    	ObjectId therapistId = patientDoc.getObjectId("therapist_id");
    	therapist = new Therapist(therapistId, database);
    	
    	ObjectId chatId = patientDoc.getObjectId("chat");
    	chat = new Chat(this, therapist, chatId, database);
    	
    	ObjectId requestId = patientDoc.getObjectId("request");
    	request = new Request(this, therapist, requestId, database);
    	
    }
    
    public Patient(ObjectId id, Therapist therapist, MongoDatabase database) {
    	
    	super(id,database);
    	
    	MongoCollection<Document> patientCollection = database.getCollection("patients");
    	
    	Document patientDoc = patientCollection.find(eq(id)).first();
    	
    	firstName = patientDoc.getString("first_name");
    	lastName = patientDoc.getString("last_name");
    	email = patientDoc.getString("email");
    	hashedPassword = patientDoc.get("password_hash", Binary.class).getData();
    	salt = patientDoc.get("salt", Binary.class).getData();
    	
    	this.therapist = therapist;
    	
    	ObjectId chatId = patientDoc.getObjectId("chat");
    	chat = new Chat(this, therapist, chatId, database);
    	
    	ObjectId requestId = patientDoc.getObjectId("request");
    	request = new Request(this, therapist, requestId, database);
    	
    }
    
    
    
    
    /**
     * 
     * @return a <code>List</code> of the current requests.
     */
    public Request getRequest() {
        return request;
    }
    
    public Therapist getTherapist() {
    	return therapist;
    }
    
    /**
     * 
     * @param request the new <code>Request</code> to be added to the.
     *        <code>List</code>
     */
    public void setRequest(Request request) {
        this.request = request;
    }
    
    public void setTherapist(Therapist therapist) {
    	this.therapist = therapist;
    }
    
    public void insertIntoCollection() throws MongoWriteException, MongoWriteConcernException, MongoException {
    	
    	MongoCollection<Document> collection = database.getCollection("patients");
    	
    	Document doc = getDocument(collection)
    			.append("request_id", request.getId())
    			.append("therapist_id", therapist.getId())
    			.append("chat_id", chat.getId());
    	
    	collection.insertOne(doc);
    	
    }
    
    public void updateToCollection() throws MongoWriteException, MongoWriteConcernException, MongoException {
    	
    	MongoCollection<Document> collection = database.getCollection("patients");
    	
    	Document doc = getDocument(collection)
    			.append("request_id", request.getId())
    			.append("therapist_id", therapist.getId())
    			.append("chat_id", chat.getId());
    	
    	collection.findOneAndUpdate(eq(id), doc);
    	
    }
    
    

}
