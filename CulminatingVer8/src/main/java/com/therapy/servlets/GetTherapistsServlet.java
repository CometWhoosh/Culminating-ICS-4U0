package com.therapy.servlets;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.exists;
import static com.mongodb.client.model.Filters.lte;
import static com.mongodb.client.model.Filters.nor;
import static com.mongodb.client.model.Filters.or;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.bson.Document;
import org.bson.types.ObjectId;

import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

//TODO: Make the algorithm safe for resource sharing
@WebServlet("/getTherapists")
public class GetTherapistsServlet extends HttpServlet {

	@Override
	public void doPost(HttpServletRequest request, 
			HttpServletResponse response) throws IOException, ServletException {
		
		System.out.println("GetTherapistsServlet");
		
		/*
		 * This HttpSession is not actually used in this servlet. I only added it
		 * in order to check if it's id was the same as the on in SignUpServlet.
		 */
		HttpSession session = request.getSession(false);
		System.out.println("GetTherapistServlet: " + session.getId());
		
		MongoClient mongoClient = Util.getMongoClient();
		MongoDatabase database = mongoClient.getDatabase(Util.DATABASE_NAME);
		
		MongoCollection<Document> collection = database.getCollection("therapists");
		
		Map<String, Integer> therapistsToSend = new LinkedHashMap<>();
		
		/*
		 * Get a therapist that either has no number_of_raters field,
		 * or where number_of_raters is less than or equal to 6. Therapist
		 * must not have exceeded their request limit.
		 */
		
		//Get new therapists
		FindIterable<Document> findIter = collection.find( 
				and(or(exists("number_of_raters", false), lte("number_of_raters", 6)), eq("can_receive_requests", true)) );
	    
		MongoCursor<Document> newTherapistCursor = findIter.iterator();
		
		List<Document> newTherapists = new ArrayList<>();
		
		while(newTherapistCursor.hasNext()) {
			newTherapists.add(newTherapistCursor.next());
		}
		
		Document newTherapist = newTherapists.get(new Random().nextInt(newTherapists.size()));
		
		
		/*int count = 0;
		while(newTherapistIter.hasNext()) { 
			
			newTherapistIter.next();
			count++; 
			
		}
		
		FindIterable<Document> singleElementIter = findIter.skip(new Random().nextInt(count - 1));
		Document newTherapistDoc = singleElementIter.first();
		
		*/
		
		
		
		/*
		 * Sort all therapists from highest to lowest rating. Then,
		 * Get 2 very highly-rated therapists, 2 highly-rated therapists,
		 * 2 well-rated therapists, and 2 moderately-rated therapists.
		 * Therapist must not have exceeded their request limit.
		 */
		
		//Get all therapists that aren't new (ones where number_of_raters > 6)
		
		
		MongoCursor<Document> oldTherapistsIter  = collection.find(
												       and(
												    		   nor(exists("number_of_raters", false), 
												    		       lte("number_of_raters", 6)
												    		   ),
												    		   eq("can_receive_requests", true)
												    	   )
												       )
												       .iterator();
		
		
		MongoCursor<Document> secondIter = oldTherapistsIter;
		
		List<Document> oldTherapists = new ArrayList<Document>();
		
		//Get all old therapists
		while(oldTherapistsIter.hasNext()) {
			oldTherapists.add(oldTherapistsIter.next());
		}
		
		//Get a List of 30 random therapists
		int[] indexes = new int[30];
		Document[] randomTherapists = new Document[30];
		for(int i = 0; i < 30; i++) {
			
			boolean duplicateUsed;
			int randomIndex = 0;
			do {
				
				duplicateUsed = false;
				
				randomIndex = new Random().nextInt(oldTherapists.size());
				
				for(int e : indexes) {
					if(randomIndex == e) {
						duplicateUsed = true;
						break;
					}
				}
				
				indexes[i] = randomIndex;
				
			} while(duplicateUsed);
			
			randomTherapists[i] = oldTherapists.get(randomIndex);
			
		}
		
		Comparator<Document> descendingRatingsComparator = new Comparator<Document>() {
		
			public int compare(Document o1, Document o2) {
				return o1.getInteger("rating").compareTo(o2.getInteger("rating"));
			}
			
		}.reversed();
		
		//Sort the 30 therapists
		Arrays.sort(randomTherapists, descendingRatingsComparator);
		
		//Get the highest 9 rated therapists
		String[] therapistIds = new String[10];
		for(int i = 0; i < 9; i++) {
			String firstName  = randomTherapists[i].getString("first_name");
			String lastName = randomTherapists[i].getString("last_name");
			Integer rating = randomTherapists[i].getInteger("rating");
			
			therapistIds[i] = ((ObjectId)randomTherapists[i].get("_id")).toHexString();
			
			therapistsToSend.put(firstName + " " + lastName, rating);
		}
		
		String newTherapistName = newTherapist.getString("first_name") + " " + newTherapist.getString("last_name");
		therapistsToSend.put(newTherapistName, newTherapist.getInteger("rating"));
		therapistIds[9] = ((ObjectId)newTherapist.get("_id")).toHexString();
		
		request.setAttribute("retrievedTherapists", therapistsToSend);
		request.setAttribute("therapistIds", therapistIds);
		request.getRequestDispatcher("patientSetup.jsp").forward(request, response);
		
	}
	
}
