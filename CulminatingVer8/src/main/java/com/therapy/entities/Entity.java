package com.therapy.entities;

import org.bson.types.ObjectId;

import com.mongodb.MongoException;
import com.mongodb.MongoWriteConcernException;
import com.mongodb.MongoWriteException;
import com.mongodb.client.MongoDatabase;

public abstract class Entity {

	protected ObjectId id;
	protected MongoDatabase database;
	
	public Entity() {}
	
	public Entity(ObjectId id, MongoDatabase database) {
		this.id = id;
		this.database = database;
	}
	
	protected ObjectId getId() {
		return id;
	}
	
	public abstract void updateToCollection() throws MongoWriteException, MongoWriteConcernException, MongoException;
	
	public abstract void insertIntoCollection() throws MongoWriteException, MongoWriteConcernException, MongoException;
	
}
