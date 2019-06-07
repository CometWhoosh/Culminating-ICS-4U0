package com.therapy.entities;

import static com.mongodb.client.model.Filters.eq;

import org.bson.Document;
import org.bson.types.ObjectId;

import com.mongodb.MongoException;
import com.mongodb.MongoWriteConcernException;
import com.mongodb.MongoWriteException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public abstract class Entity {

	protected ObjectId id;
	protected MongoDatabase database;
	protected MongoCollection<Document> collection;
	
	public Entity() {}
	
	public Entity(ObjectId id, MongoDatabase database) {
		this.id = id;
		this.database = database;
	}
	
	protected ObjectId getId() {
		return id;
	}
	
	public abstract void replaceInCollection() throws MongoWriteException, MongoWriteConcernException, MongoException;
	
	//public abstract void insertIntoCollection() throws MongoWriteException, MongoWriteConcernException, MongoException;
	
	public Document getDocumentRepresentation() {
    	return collection.find(eq(id)).first();
    }
	
}
