package com.therapy.servlets;

import java.util.logging.Logger;

import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoIterable;
import com.mongodb.client.model.IndexOptions;

/**
 * 
 * This is a utility class that defines methods for operations that are 
 * frequently used when dealing with a MongoDB database.
 * 
 * @author Yousef Bulbulia
 *
 */
public final class Util {

	public static final String DATABASE_NAME = "Therapy";
	public final static Logger LOGGER = Logger.getLogger(Util.class.getName());
	
	/**
	 * This constructor is a private, empty constructor. This is so that a 
	 * <code>Util</code> object can never be instantiated, as all of it's 
	 * methods are static and there would never be a need to create such an
	 * object.
	 */
	private Util() {
		
	}
	
	/**
	 * 
	 * @return a <code>MongoClient</code> that is connected to the MongoDB database.
	 */
	public static MongoClient getMongoClient() {
		return new MongoClient(new MongoClientURI("mongodb://localhost:27017/testDB"));
	}
	
	/**
	 * This method checks if the collection specified by 
	 * <code>targetCollection</code> exists in the database specified by
	 * <code>database</code>
	 * 
	 * @param database         the database <code>targetCollection</code> is 
	 * 						   supposed to belong to
	 * @param targetCollection the collection to check
	 * @return                 <code>true</code>, if the collection exists
	 */
	public static boolean collectionExists(MongoDatabase database, String targetCollection) {
		
		MongoIterable<String> collections = database.listCollectionNames();
		MongoCursor<String> cursor = collections.iterator();
		
		while(cursor.hasNext()) {
			
			String collection = cursor.next();
			if(collection.contentEquals(targetCollection)) {
				return true;
			}
			
		}
		
		return false;
		
	}
	
	/**
	 * Initializes the database by creating all the collections needed for this 
	 * website to run, if they have not been created yet.
	 * 
	 * The <code>patients</code> and <code>therapists</code> collections have
	 * the field <code>email</code> set as unique indexes, so that an exception
	 * would be thrown if two <code>email</code> fields in the collection had
	 * the same value.
	 */
	public static void intialiazeDatabase() {
		
		MongoClient mongoClient = getMongoClient();
		MongoDatabase database = mongoClient.getDatabase(DATABASE_NAME);
		
		Document emailIndex = new Document("email", 1);
		
		if(!Util.collectionExists(database, "patients")) {
			
			database.createCollection("patients");
			MongoCollection<Document> patientCollection = database.getCollection("patients");
			patientCollection.createIndex(emailIndex, new IndexOptions().unique(true));
			
			database.createCollection("therapists");
			MongoCollection<Document> therapistCollection = database.getCollection("therapists");
			therapistCollection.createIndex(emailIndex, new IndexOptions().unique(true));
			
			database.createCollection("chats");
			
		}
		
	}
	
	/**
	 * Retrieves the <code>patients</code> collection or the 
	 * <code>therapists</code> depending on the value of <code>userType</code>.
	 * 
	 * @param database the database the collections belong to
	 * 
	 * @param userType the type of user the collection should contain. If it's 
	 * 				   value is <code>"Patient"</code>, then the 
	 * 				   <code>patients</code> collection will be returned. If
	 * 			       it's value is <code>"Therapist"</code>, the 
	 * 				   <code>therapists</code> collection will be returned.
	 * 				   
	 * @return         the appropriate collection
	 */
	public static MongoCollection<Document> getUserAppropriateCollection(MongoDatabase database, String userType) {
		
		String collectionName;
		
		if(userType.equals("Patient")) {
			collectionName = "patients";
		} else {
			collectionName = "therapists";
		}
		
		return database.getCollection(collectionName);
		
	}
	
}
