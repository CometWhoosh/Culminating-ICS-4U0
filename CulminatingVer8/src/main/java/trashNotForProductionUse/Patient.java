package trashNotForProductionUse;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;
import org.bson.types.ObjectId;

import com.mongodb.client.MongoCollection;

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
    
    public Patient(ObjectId id, String firstName, String lastName, String email, 
    			byte[] hashedPassword, byte[] salt, Request request, Therapist therapist) {
    	
    	super(id, firstName, lastName, email, hashedPassword, salt);
    	
    	this.request = request;
    	this.therapist = therapist;
    	
    }	
    
    public Patient(String firstName, String lastName, String email, 
			byte[] hashedPassword, byte[] salt, Request request) {
    	
		super(firstName, lastName, email, hashedPassword, salt);
		
		this.request = request;
	
    }
    
    public Patient(ObjectId id) {
    	super(id);
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
    
    

}