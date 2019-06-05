package trashNotForProductionUse;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;
import org.bson.types.ObjectId;

import com.mongodb.client.MongoCollection;

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
	private Integer numberOfRequests;
	private boolean canReceiveRequests = false;
	private Integer requestLimit = 0;
    private Integer patientLimit = 0;
    private List<Request> requests = new ArrayList<>();
    private List<Patient> patients = new ArrayList<>();
    private List<Chat> chats = new ArrayList<>();
    private double rating = 0;
    private Integer numberOfRaters = 0;
    
    /**
     * Creates a new therapist with the specified request limit.
     * 
     * @param requestLimit the limit to how many requests a therapist can have.
     */
    public Therapist(ObjectId id, String firstName, String lastName, String email,
    		byte[] hashedPassword, byte[] salt, List<Chat> chats, Integer numberOfRequests,
    		Integer patientLimit,  List<Patient> patients, List<Request> requests, 
    		double rating, Integer numberOfRaters) throws IllegalStateException {
    	
    	super(id, firstName, lastName, email, hashedPassword, salt);
    	
    	this.numberOfRequests = numberOfRequests;
    	this.patientLimit = patientLimit;
    	
    	
    	if(patients.size() <= patientLimit) {
    		this.patients.addAll(patients);
    	} else {
    		throw new IllegalStateException("Number of patients exceeds the patient limit");
    	}
    	
    	if(requests.size() + patients.size() <= patientLimit) {
    		this.requests.addAll(requests);
    	} else {
    		throw new IllegalStateException("Number of requests and patients exceed the patient limit");
    	}
    	
    	if(chats.size() == patients.size() && chats.size() <= patientLimit) {
    		this.chats.addAll(chats);
    	} else {
    		throw new IllegalStateException("Number of chats exceed the patient limit");
    	}
    	
    	this.rating = rating;
    	this.numberOfRaters = numberOfRaters;
    	
    	
    }
    
    public Therapist(String firstName, String lastName, String email,
    		byte[] hashedPassword, byte[] salt, List<Chat> chats,Integer numberOfRequests,
    		Integer patientLimit,  List<Patient> patients, List<Request> requests, 
    		double rating, Integer numberOfRaters) throws IllegalStateException {
    	
    	super(firstName, lastName, email, hashedPassword, salt, chats);
    	
    	this.numberOfRequests = numberOfRequests;
    	this.patientLimit = patientLimit;
    	
    	
    	if(patients.size() <= patientLimit) {
    		this.patients.addAll(patients);
    	} else {
    		throw new IllegalStateException("Number of patients exceeds the patient limit");
    	}
    	
    	if(requests.size() + patients.size() <= patientLimit) {
    		this.requests.addAll(requests);
    	} else {
    		throw new IllegalStateException("Number of requests and patients exceed the patient limit");
    	}
    	
    	
    	this.rating = rating;
    	this.numberOfRaters = numberOfRaters;
    	
    }
    
    public Therapist(ObjectId id) {
    	super(id);
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
    
    
	
}