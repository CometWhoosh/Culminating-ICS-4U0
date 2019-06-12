package com.therapy.entities;

import static com.mongodb.client.model.Filters.eq;

import org.bson.Document;
import org.bson.types.ObjectId;

import com.mongodb.MongoWriteException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public abstract class Entity {

	protected ObjectId id;
	protected MongoCollection<Document> collection;
	protected MongoDatabase database;
	
	/**
	 * Creates a new <code>Entity</code>.
	 */
	public Entity(MongoDatabase database) {
		this.database = database;
	}
	
	/**
	 * Creates a new <code>Entity</code> belonging to the passed
	 * database with the passed id.
	 * 
	 * @param id       the id of this <code>Entity</code>.
	 * @param database the database this <code>Entity</code> belongs to.
	 */
	public Entity(ObjectId id, MongoDatabase database) {
		this.database = database;
		this.id = id;
	}
	
	/**
	 * 
	 * @return the id of the <code>Entity</code>.
	 */
	public ObjectId getId() {
		return id;
	}
	
	/**
	 * 
	 * @return a <code>Document</code> representing this <code>Entity</code>
	 */
	public Document getDocument() throws IllegalStateException {
		
		System.out.println("Entity: " + id.toHexString());
		Document doc = collection.find(eq(id)).first();
		
		if(doc == null) {
			throw new IllegalStateException("Document could not be found");
		}
		
    	return doc;
    }
	
	public ObjectId getUniqueId() {
		
		boolean isDuplicate = false;
		ObjectId id = null;
		
		do {
			
			isDuplicate = false;
			id = ObjectId.get();
			
			/*
			 * Try to insert the entity into its collection. If the id 
			 * already belongs to another entity in its collection, then
			 * make a new id and try again.
			 */
			try {
				collection.insertOne(new Document("_id", id));
			} catch(MongoWriteException e) {
				if(e.getCode() == 11000) {
					isDuplicate = true;
				} else {
					throw e;
				}
			}
			
		} while(isDuplicate);
		
		return id;
		
	}
	
}
