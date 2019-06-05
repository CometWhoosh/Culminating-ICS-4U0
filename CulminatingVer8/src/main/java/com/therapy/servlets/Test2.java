package com.therapy.servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

@WebServlet("/test2")
public class Test2 extends HttpServlet{

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
	
	    String path = System.getProperty("java.class.path");
	    System.out.println(path);
		//Connect
		MongoClient mongoClient = new MongoClient(new MongoClientURI("mongodb://localhost:27017/testDB"));
		MongoDatabase database = mongoClient.getDatabase("testDB");
		database.createCollection("testCollection");
		MongoCollection<Document> collection = database.getCollection("testCollection");
		
		
		Document testDoc = new Document("_id", "Hello!");
		collection.insertOne(testDoc);
		
		//close
		mongoClient.close();
	
	}
	
	/*
	 * http://localhost:1024/CulminatingVer3Server/
	 */
	
}