package trashNotForProductionUse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bson.Document;
import org.bson.types.ObjectId;

import com.mongodb.client.MongoCollection;

/**
 * This class represents a user. It is abstract because it is impractical to 
 * have a user that is not a patient nor a therapist, as it would not be able
 * to talk to a therapist, or help patients.
 * 
 * @author Yousef Bulbulia
 * 
 */
public abstract class User extends Entity{

    protected String firstName;
    protected String lastName;
    protected String email;
    protected byte[] hashedPassword;
    protected byte[] salt;
    
    
    public User(ObjectId id, String firstName, String lastName, String email, 
    			byte[] hashedPassword, byte[] salt) {
    	
    	this.id = id;
    	this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.hashedPassword = hashedPassword;
        this.salt = salt;
    	
    }
    
    public User(String firstName, String lastName, String email, 
			byte[] hashedPassword, byte[] salt) {
	
		id = ObjectId.get();
		this.firstName = firstName;
	    this.lastName = lastName;
	    this.email = email;
	    this.hashedPassword = hashedPassword;
	    this.salt = salt;
	
    }
    
    public User(ObjectId id) {
    	super(id);
    }
    
    /**
     * 
     * @return the user id.
     */
    public ObjectId getId() {
        return id;
    }
    
    /**
     * 
     * @return the first name of the user.
     */
    public String getFirstName() {
        return firstName;
    }
    
    /**
     * 
     * @return the last name of the user.
     */
    public String getLastName() {
        return lastName;
    }
    
    
    /**
     * 
     * @return the user's email.
     */
    public String getEmail() {
    	return email;
    }
    
    /**
     * 
     * @return the hashed version of the user's password.
     */
    public byte[] getHashedPassword() {
    	return hashedPassword;
    }
    
    /**
     * 
     * @return the user's salt.
     */
    public byte[] getSalt() {
    	return salt;
    }
    
    /**
     * 
     * @param id the user id. 
     */
    public void setID(ObjectId id) {
        this.id = id;
    }
    
    /**
     * 
     * @param firstName the first name of the user.
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    
    /**
     * 
     * @param lastName the last name of the user.
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    
    /**
     * 
     * @param email the user's email.
     */
    public void setEmail(String email) {
    	this.email = email;
    }
    
    /**
     * 
     * @param hashedPassword the hashed version of the user's password.
     */
    public void setHashedPassword(byte[] hashedPassword) {
    	this.hashedPassword = hashedPassword;
    }
    
    /**
     * 
     * @param salt the user's salt.
     */
    public void setSalt(byte[] salt) {
    	this.salt = salt;
    }
    
    
	
}