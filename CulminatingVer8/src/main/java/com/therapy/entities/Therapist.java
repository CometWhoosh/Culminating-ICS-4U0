package com.therapy.entities;

import static com.mongodb.client.model.Filters.eq;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.bson.Document;
import org.bson.types.Binary;
import org.bson.types.ObjectId;

import com.mongodb.MongoException;
import com.mongodb.MongoWriteConcernException;
import com.mongodb.MongoWriteException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Updates;

/**
 * This class represents a therapist. Since it is one of the two types of users,
 * it extends the <code>User</code> class. It has a field for the number of requests the 
 * therapist allows, the number of requests that they have received, and an 
 * array of requests from patients.  
 * 
 * @author Yousef Bulbulia
 * 
 */
public class Therapist extends User {

	//TODO numberOfRequests is probably a useless field. Just make sure, then remove it.
	//TODO consider making requests, patients, and chats all arrays
	//TODO consider making canReceiveRequests a Boolean instead of boolean. This means making 
	//     can_receive_requests a Boolean, and creating all new therapists. So you need change at ~Line59
	//     Same for double rating.
	private Integer numberOfRequests;
	private boolean canReceiveRequests = false;
	private Integer requestLimit = 0;
    private Integer patientLimit = 0;
    private List<Request> requests = new ArrayList<>();
    private List<Patient> patients = new ArrayList<>();
    private List<Chat> chats = new ArrayList<>();
    private Integer rating = 0;
    private Integer numberOfRaters = 0;
    
   
    public Therapist(ObjectId id, MongoDatabase database) {
    	
    	super(id,database);
    	
    	collection = database.getCollection("therapists");
    	
    	Document therapistDoc = collection.find(eq(id)).first();
    	
    	firstName = therapistDoc.getString("first_name"); 
    	lastName = therapistDoc.getString("last_name");
    	email = therapistDoc.getString("email");
    	hashedPassword = therapistDoc.get("password_hash", Binary.class).getData();
    	salt = therapistDoc.get("salt", Binary.class).getData();
    	
    	numberOfRequests = therapistDoc.getInteger("number_of_requests");
    	canReceiveRequests = therapistDoc.getBoolean("can_receive_requests", false);
    	requestLimit = therapistDoc.getInteger("request_limit");
    	patientLimit = therapistDoc.getInteger("patient_limit");
    	rating = therapistDoc.getInteger("rating");
    	numberOfRaters = therapistDoc.getInteger("number_of_raters");
    	
    	
    	//Initialize <code>patients</code> and <code>chats</code>
    	ArrayList<ObjectId> patientIdList = therapistDoc.get("patient_ids", new ArrayList<ObjectId>().getClass());
    	ObjectId[] patientIds = null;
    	if(patientIdList != null) {
    		patientIds = patientIdList.toArray(ObjectId[]::new);
    	}
    	
    	
    	MongoCollection<Document> chatCollection = database.getCollection("chats");
    	ArrayList<ObjectId> chatIdList = therapistDoc.get("chat_ids", new ArrayList<ObjectId>().getClass());
    	ObjectId[] chatIds = null;
    	if(chatIdList != null) {
    		chatIds = chatIdList.toArray(ObjectId[]::new);
    	}
    	
    	
    	if(patientIds != null && chatIds != null) {
    		
    		for(int i = 0 ; i < patientIds.length; i++) {
        		
        		Patient patient = new Patient(patientIds[i], this, database);
        		patients.add(patient);
        		
        		Document chatDoc = chatCollection.find(eq(chatIds[i])).first();
        		Chat chat = new Chat(patient, this, chatIds[i], database);
        		chats.add(chat);
        		
        	}
    		
    	}
    	
    	
    	
    	ArrayList<ObjectId> requestIdList = therapistDoc.get("request_ids", new ArrayList<ObjectId>().getClass());
    	ObjectId[] requestIds = null;
    	if(requestIdList != null) {
    		requestIds = requestIdList.toArray(ObjectId[]::new);
    	}
    	
    	MongoCollection<Document> requestCollection = database.getCollection("requests");
    	
    	if(requestIds != null) {
    		
    		for(int i = 0; i < requestIds.length; i++) {
        		
        		Document requestDoc = requestCollection.find(eq(requestIds[i])).first();
        		
        		Patient patient = new Patient(requestDoc.getObjectId("patient_id"), this, database);
        		Request request = new Request(patient, this, requestIds[i], database);
        		requests.add(request);
        		
        	}
    		
    	}
    	
    	
    }
    
    //Is this necessary?
	public Therapist(ObjectId id, List<Patient> patients, MongoDatabase database) {
	    	
	    	super(id,database);
	    	
	    	collection = database.getCollection("therapists");
	    	
	    	Document therapistDoc = collection.find(eq(id)).first();
	    	
	    	firstName = therapistDoc.getString("first_name");
	    	lastName = therapistDoc.getString("last_name");
	    	email = therapistDoc.getString("email");
	    	hashedPassword = therapistDoc.get("password_hash", Binary.class).getData();
	    	salt = therapistDoc.get("salt", Binary.class).getData();
	    	
	    	
	    	ArrayList<ObjectId> patientIdList = therapistDoc.get("patient_ids", new ArrayList<ObjectId>().getClass());
	    	ObjectId[] patientIds = patientIdList.toArray(ObjectId[]::new);
	    	
	    	for(ObjectId patientId : patientIds) {
	    		
	    		Patient patient = new Patient(patientId, this, database);
	    		patients.add(patient);
	    		
	    	}
	    	
	    	
	    	ArrayList<ObjectId> requestIdList = therapistDoc.get("request_ids", new ArrayList<ObjectId>().getClass());
	    	ObjectId[] requestIds = requestIdList.toArray(ObjectId[]::new);
	    	
	    	MongoCollection<Document> requestCollection = database.getCollection("requests");
	    	
	    	for(int i = 0; i < requestIds.length; i++) {
	    		
	    		Document requestDoc = requestCollection.find(eq(requestIds[i])).first();
	    		
	    		Patient patient = new Patient(requestDoc.getObjectId("patient_id"), this, database);
	    		Request request = new Request(patient, this, requestIds[i], database);
	    		
	    	}
	    	
	    	
	    	ArrayList<ObjectId> chatIdList = therapistDoc.get("chat_ids", new ArrayList<ObjectId>().getClass());
	    	ObjectId[] chatIds = requestIdList.toArray(ObjectId[]::new);
	    	
	    	MongoCollection<Document> chatCollection = database.getCollection("chats");
	    	
	    	for(int i = 0; i < chatIds.length; i++) {
	    		
	    		Document chatDoc = chatCollection.find(eq(chatIds[i])).first();
	    		
	    		Patient patient = new Patient(chatDoc.getObjectId("patient_id"), this, database);
	    		Request request = new Request(patient, this, chatIds[i], database);
	    		
	    	}
    	
    }
    
    
    
    
    
    public Integer getNumberOfRequests() {
    	return numberOfRequests;
    }
    
    /**
     * 
     * @return the limit to how many requests a therapist can have.
     */
    public Integer getPatientLimit() {
    	return patientLimit;
    }
    
    public Integer getRequestLimit() {
    	return requestLimit;
    }
    
    /**
     * 
     * @return an array of the requests.
     */
    public List<Request> getRequests() {
        return requests;
    }
    
    public List<Patient> getPatients() {
    	return patients;
    }
    
    public double getRating() {
    	return rating;
    }
    
    public Integer getNumberOfRaters() {
    	return numberOfRaters;
    }
    
    public boolean canReceiveRequests() {
    	return canReceiveRequests;
    }
    
    
    /**
     * 
     * @param limit the maximum amount of requests a therapist can receive.
     */
    public void setPatientLimit(Integer limit) {
        patientLimit = limit;
        requestLimit = patientLimit - patients.size();
    }
    
    /**
     * 
     * @param request the request to be added.
     */
    public void addRequest(Request request) throws IllegalStateException {
        
        //If there is no request limit set, throw exception
        if(patientLimit == 0) {
            throw new IllegalStateException("A request limit must be set before adding requests");
        } else if(requests.size() + 1 + patients.size() > patientLimit) {
        	throw new IllegalStateException("Number of requests and patients will exceed the patient limit");
        }
        
        requests.add(request);
        requestLimit = patientLimit - requests.size() - patients.size();
        if(requestLimit == 0) {
        	canReceiveRequests = false;
        }
        
    }
    
    public void addRating(Integer newUserRating) throws IllegalArgumentException {
    	
    	if(newUserRating < 0 || newUserRating > 100) {
    		throw new IllegalArgumentException("Ratings must be between 0 and 100 (inclusive)");
    	}
    	
    	rating =  (rating * numberOfRaters + newUserRating)/(++numberOfRaters);
    	
    }
    
    /**
     * 
     * @param index the index in the array of the request to accept.
     */
    /*public void removeRequest(Request request) {
    	requests.remove(request);
    	patients.add(request.getPatient());
    }*/
    public void acceptRequest(Request request) {
    	requests.remove(request);
    	patients.add(request.getPatient());
    }
    
    public void denyRequest(Request request) {
    	requests.remove(request);
    	requestLimit = patientLimit - requests.size() - patients.size();
    }
    
    /*
    public void insertIntoCollection() throws MongoWriteException, MongoWriteConcernException, MongoException {
    	
    	MongoCollection<Document> collection = database.getCollection("therapists");
    	
    	Document doc = getDocument(collection)
    			.append("number_of_requests", numberOfRequests)
    			.append("can_receive_requests", canReceiveRequests)
    			.append("request_limit", requestLimit)
    			.append("patient_limit", patientLimit)
    			.append("rating", rating)
    			.append("number_of_raters", numberOfRaters);
    	
    	collection.insertOne(doc);
    	
    	//get <code>patients</code> as a List
    	List<ObjectId> patientIds = patients.stream()
    			.map(e -> e.getId())
				.collect(Collectors.toList());
    	
    	//get <code>requests</code> as a List
    	List<ObjectId> requestIds = requests.stream()
    			.map(e -> e.getId())
				.collect(Collectors.toList());
    	
    	//get <code>chats</code> as a List
    	List<ObjectId> chatIds = chats.stream()
    			.map(e -> e.getId())
				.collect(Collectors.toList());
    	
    	collection.findOneAndUpdate( eq(id), Updates.pushEach("patientIds", patientIds));
    	collection.findOneAndUpdate( eq(id), Updates.pushEach("requestIds", requestIds));
    	collection.findOneAndUpdate( eq(id), Updates.pushEach("chatIds", chatIds));
    	
    }
    
    */
    
    
	
}
