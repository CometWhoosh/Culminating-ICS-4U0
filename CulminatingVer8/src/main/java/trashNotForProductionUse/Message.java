package trashNotForProductionUse;

import org.bson.Document;
import org.bson.types.ObjectId;

import com.mongodb.client.MongoCollection;

/**
 * This class represents a message sent in a chat. It has fields for the content
 * of the message as well as the associated patient and therapist.
 * 
 * @author Yousef Bulbulia
 * 
 */
public class Message extends Entity{
	
	String content;
    Patient patient;
    Therapist therapist;
    
    /**
     * Creates a message with the specified content, patient, and therapist.
     * 
     * @param id
     * @param content   the content of the message.
     * @param patient   the patient to be associated with the message.
     * @param therapist the therapist to be associated with the message.
     */
    public Message(ObjectId id, Patient patient, Therapist therapist, String content) {
    	
    	id = ObjectId.get();
        this.patient = patient;
        this.therapist = therapist;
        this.content = content;
        
    }
    
    /**
     * Creates a message with the specified content, patient, and therapist.
     * 
     * @param content   the content of the message.
     * @param patient   the patient to be associated with the message.
     * @param therapist the therapist to be associated with the message.
     */
    public Message(Patient patient, Therapist therapist, String content) {
    	
    	id = ObjectId.get();
        this.patient = patient;
        this.therapist = therapist;
        this.content = content;
        
    }
    
    public Message(ObjectId id) {
    	super(id);
    }
    
    
    /**
     * 
     * @return the content of the message.
     */
    public String getContent() {
        return content;
    }
    
    /**
     * 
     * @return the patient that is associated with the message.
     */
    public Patient getPatient() {
        return patient;
    }

    /**
     * 
     * @return the therapist that is associated with the message.
     */
    public Therapist getTherapist() {
        return therapist;
    }
    
    public ObjectId getId() {
    	return id;
    }
	
}
