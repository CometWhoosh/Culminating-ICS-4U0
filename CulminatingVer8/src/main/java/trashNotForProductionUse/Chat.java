package trashNotForProductionUse;


import static com.mongodb.client.model.Filters.eq;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import org.bson.Document;
import org.bson.types.ObjectId;

import com.mongodb.client.MongoCollection;
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
	 
    private Patient patient;
    private Therapist therapist;
    private Deque<Message> messages = new ArrayDeque<Message>();
    
    /**
     * Creates a chat with the specified patient and therapist.
     * 
     * @param id
     * @param patient   the patient to be associated with this chat.
     * @param therapist the therapist to be associated with this chat.
     */
    public Chat(ObjectId id, Patient patient, Therapist therapist) {
        
    	id = ObjectId.get();
        this.patient = patient;
        this.therapist = therapist;
        this.messages = new ArrayDeque<>(messages);
        
    }
    
    /** 
     * Creates a chat with the specified patient and therapist.
     * 
     * @param patient   the patient to be associated with this chat.
     * @param therapist the therapist to be associated with this chat.
     */
    public Chat(Patient patient, Therapist therapist,
    		Collection<Message> messages) {
        
    	id = ObjectId.get();
        this.patient = patient;
        this.therapist = therapist;
        this.messages = new ArrayDeque<>(messages);
        
    }
    
    public Chat(ObjectId id) {
    	super(id);
    }
    
    /**
     * 
     * @return the patient associated with this chat.
     */
    public Patient getPatient() {
        return patient;
    }
    
    /**
     * 
     * @return the therapist associated with this chat.
     */
    public Therapist getTherapist() {
        return therapist;
    }
    
    /**
     * 
     * @return the <code>List</code> of messages that make up the chat.
     */
    public Collection<Message> getMessages() {
        return messages;
    }
    
    /**
     * Retrieves the most recent messages.
     * 
     * @param numberOfMessages the number of messages to return
     * @return                 an array of the recent messages. The size of the 
     *                         array is determined by the parameter 
     *                         <code>numberOfMessages</code>.
     */
    public Message[] getPreviousMessages(int numberOfMessages) throws NoSuchElementException {
        
        Iterator<Message> itr = messages.descendingIterator();
        
        Message[] previousMessages = new Message[numberOfMessages];
        
        for(int i = 0; itr.hasNext() && i < numberOfMessages; i++) {
            
            try {
                previousMessages[i] = itr.next();
            } catch(NoSuchElementException e) {
                throw e;
            }
            
        }
        
        return previousMessages;
        
    }
    
    public ObjectId getId() {
    	return id;
    }
    
    /**
     * 
     * @param patient the patient to be associated with the chat.
     */
    public void setPatient(Patient patient) {
        this.patient = patient;
    }
    
    /**
     * 
     * @param therapist the therapist to be associated with the chat.
     */
    public void setTherapist(Therapist therapist) {
        this.therapist = therapist;
    }
    
    /**
     * 
     * @param message the new message to be added to the chat.
     */
    public void addMessage(Message message) {
        messages.add(message);
    }
    
    public void writeToCollection() {
    	
    	//ObjectId id, Patient patient, Therapist therapist, MongoCollection<Document> collection
    	
    	Document doc = new Document("_id",id)
    			.append("patientId", patient.getId())
    			.append("therapistId", therapist.getId())
    			.append("messageIds", null);
    	
    	collection.insertOne(doc);
    	
    	List<ObjectId> messageIds = messages.stream()
    									.map(e -> e.getId())
    									.collect(Collectors.toList());
    	
    	collection.findOneAndUpdate( eq(id), Updates.pushEach("messageIds", messageIds));
    			
    }
	
}

