package com.therapy.servlets;

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
 * This class is a utility class that defines methods for
 * frequently used operations when dealing with a MongoDB
 * database.
 * 
 * @author Yousef Bulbulia
 *
 */
public final class Util {

	public static String DATABASE_NAME = "Therapy";
	
	private Util() {
		
	}
	
	public static MongoClient getMongoClient() {
		return new MongoClient(new MongoClientURI("mongodb://localhost:27017/testDB"));
	}
	
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
