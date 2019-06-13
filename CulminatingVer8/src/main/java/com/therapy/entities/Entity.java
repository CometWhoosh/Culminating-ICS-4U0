package com.therapy.entities;

import org.bson.Document;
import org.bson.types.ObjectId;

import com.mongodb.MongoWriteException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import static com.mongodb.client.model.Filters.eq;

/**
 * This class represents an entity in the database. 
 * 
 * The <code>Entity</code> class has methods for retrieving the
 * <code>Entity<code>'s respective <code>_id</code> field in the database, the
 * <code>Document</code> representing the specific instance of the 
 * <code>Entity</code>, as well as a method to set a unique <code>_id</code>
 * field for it.
 * 
 * @author Yousef Bulbulia
 *
 */
public abstract class Entity {

	protected ObjectId id;
	protected MongoCollection<Document> collection;
	protected MongoDatabase database;
	
	/**
	 * Creates a new <code>Entity</code> that belongs to the given database.
	 */
	public Entity(MongoDatabase database) {
		this.database = database;
	}
	
	/**
	 * Creates a new <code>Entity</code> belonging to the given database with
	 * the passed <code>_id</code> field.
	 * 
	 * @param id       the <code>_id</code> field of this <code>Entity</code>.
	 * @param database the database this <code>Entity</code> belongs to.
	 */
	public Entity(ObjectId id, MongoDatabase database) {
		this.database = database;
		this.id = id;
	}
	
	/**
	 * 
	 * @return the <code>_id</code> field of the <code>Entity</code>.
	 */
	public ObjectId getId() {
		return id;
	}
	
	/**
	 * 
	 * @return a <code>Document</code> representing this <code>Entity</code>
	 */
	public Document getDocument() throws IllegalStateException {
		
		Document doc = collection.find(eq(id)).first();
		
		if(doc == null) {
			throw new IllegalStateException("Document could not be found");
		}
		
    	return doc;
    	
    }
	
	/**
	 * Sets a unique <code>_id</code> field for this <code>Entity</code>.
	 * 
	 * @return
	 */
	public ObjectId getUniqueId() {
		
		boolean isDuplicate = false;
		ObjectId id = null;
		do {
			
			isDuplicate = false;
			id = ObjectId.get();
			
			/*
			 * Try to insert the Entity into its collection. If the 
			 * id already belongs to another Entity in it's collection, then
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
