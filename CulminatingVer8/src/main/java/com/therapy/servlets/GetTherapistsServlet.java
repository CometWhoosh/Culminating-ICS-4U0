package com.therapy.servlets;

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
import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.exists;
import static com.mongodb.client.model.Filters.lte;
import static com.mongodb.client.model.Filters.nor;
import static com.mongodb.client.model.Filters.or;

/**
 * This class is a servlet that retrieves ten <code>Therapist</code> objects for
 * a patient that is signing up for the website to request. 
 * 
 * Nine of these <code>Therapist</code> objects represent therapists that have
 * experience on the site. Experience is defined by having more than six 
 * patients rate them. These nine therapists are ordered from the
 * highest-rated to the lowest-rated. They are retrieved in a 
 * pseudo-random fashion.
 * 
 * The tenth therapist is a therapist with little to no experience.
 * 
 * This ensures that patients are able to choose therapists based on quality
 * while still providing new therapists with a chance to treat patients 
 * without having to worry about having to compete with the ratings of
 * therapists that have been on the site longer than them.
 * 
 * @author Yousef Bulbulia
 *
 */
@WebServlet("/getTherapists")
public class GetTherapistsServlet extends HttpServlet {
	
	/**
	 * Retrieves the ten therapists. Their names are added to the HTTP request 
	 * as the attribute <code>retrievedTherapists</code>. Their _id fields are 
	 * added to the HTTP request as the attribute <code>therapistIds</code>.
	 * 
	 * Afterwards, the patient is forwarded to the RequestTherapistServlet 
	 * servlet.
	 */
	@Override
	public void doPost(HttpServletRequest request, 
			HttpServletResponse response) throws IOException, ServletException {
		
		//Get the collection
		MongoClient mongoClient = Util.getMongoClient();
		MongoDatabase database = mongoClient.getDatabase(Util.DATABASE_NAME);
		MongoCollection<Document> collection = database.getCollection("therapists");
		
		/*
		 * Retrieve one unexperienced therapist.
		 * 
		 * This is a therapist that either has no number_of_raters field, or 
		 * where number_of_raters is less than or equal to 6. The therapist
		 * must not have exceeded their request limit.
		 */
		FindIterable<Document> findIter = collection.find( 
				and(or(exists("number_of_raters", false), lte("number_of_raters", 6)), eq("can_receive_requests", true)) );
	    MongoCursor<Document> newTherapistCursor = findIter.iterator();
		
		List<Document> newTherapists = new ArrayList<>();
		while(newTherapistCursor.hasNext()) {
			newTherapists.add(newTherapistCursor.next());
		}
		
		Document newTherapist = newTherapists.get(new Random().nextInt(newTherapists.size()));
		
		/*
		 * Retrieve all experienced therapists from the database.
		 * 
		 * Then select 30 of these therapists at random, and retrieve the
		 * ten therapists with the highest-rating.
		 */
		MongoCursor<Document> experiencedTherapistsCursor  = collection.find(
												       and(
												    		   nor(exists("number_of_raters", false), 
												    		       lte("number_of_raters", 6)
												    		   ),
												    		   eq("can_receive_requests", true)
												    	   )
												       )
												       .iterator();
		
		List<Document> experiencedTherapists = new ArrayList<Document>();
		
		while(experiencedTherapistsCursor.hasNext()) {
			experiencedTherapists.add(experiencedTherapistsCursor.next());
		}
		
		/*
		 * Get a List of 30 of the experienced therapists randomly.
		 * 
		 * An array of 30 indexes are created which correspond to an index
		 * in experiencedTherapists. These indexes are unique, with no duplicates
		 * among them. The therapists at these indexes are the ones selected for
		 * sorting according to their ratings.
		 */
		int[] indexes = new int[30];
		Document[] randomTherapists = new Document[30];
		for(int i = 0; i < 30; i++) {
			
			boolean duplicateUsed;
			int randomIndex = 0;
			do {
				
				duplicateUsed = false;
				
				randomIndex = new Random().nextInt(experiencedTherapists.size());
				
				for(int e : indexes) {
					if(randomIndex == e) {
						duplicateUsed = true;
						break;
					}
				}
				
				indexes[i] = randomIndex;
				
			} while(duplicateUsed);
			
			randomTherapists[i] = experiencedTherapists.get(randomIndex);
			
		}
		
		//Create a Comparator to sort the therapists from highest to lowest by their ratings
		Comparator<Document> descendingRatingsComparator = new Comparator<Document>() {
		
			public int compare(Document o1, Document o2) {
				return o1.getInteger("rating").compareTo(o2.getInteger("rating"));
			}
			
		}.reversed();
		
		//Sort the 30 therapists using the Comparator
		Arrays.sort(randomTherapists, descendingRatingsComparator);
		
		//Get the highest 9 rated experienced therapists
		Map<String, Integer> therapistsToSend = new LinkedHashMap<>();
		String[] therapistIds = new String[10];
		for(int i = 0; i < 9; i++) {
			
			String firstName  = randomTherapists[i].getString("first_name");
			String lastName = randomTherapists[i].getString("last_name");
			Integer rating = randomTherapists[i].getInteger("rating");
			
			therapistIds[i] = ((ObjectId)randomTherapists[i].get("_id")).toHexString();
			therapistsToSend.put(firstName + " " + lastName, rating);
			
		}
		
		//Set the name and id for the unexperienced therapist
		String newTherapistName = newTherapist.getString("first_name") + " " + newTherapist.getString("last_name");
		therapistsToSend.put(newTherapistName, newTherapist.getInteger("rating"));
		therapistIds[9] = ((ObjectId)newTherapist.get("_id")).toHexString();
		
		//Set the HTTP request attributes
		request.setAttribute("retrievedTherapists", therapistsToSend);
		request.setAttribute("therapistIds", therapistIds);
		request.getRequestDispatcher("patientSetup.jsp").forward(request, response);
		Util.LOGGER.info("Data for 10 random therapists were successfully forwarded to patientSetup.jsp");
		
	}
	
}
