package db;

import java.sql.Connection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


import org.json.JSONArray;
import org.json.JSONObject;

public abstract class DBConnection {
	protected static final int MAX_RECOMMENDED_RESTAURANTS = 10;
    /**
     * Close the connection.
     */
    public abstract void close() ;
    
    /**
     * Insert the visited restaurants for a user.
     * @param userId
     * @param businessIds
     */
    public abstract void setVisitedRestaurants(String userId, List<String> businessIds);


    /**
     * Delete the visited restaurants for a user.
     * @param userId
     * @param businessIds
     */
    public abstract void unsetVisitedRestaurants(String userId, List<String> businessIds);


    /**
     * Get the visited restaurants for a user.
     * @param userId
     * @return
     */
    public abstract Set<String> getVisitedRestaurants(String userId);


    /**
     * Get the restaurant json by id.
     * @param businessId
     * @param isVisited, set the visited field in json.
     * @return
     */
    public abstract JSONObject getRestaurantsById(String businessId, boolean isVisited);


	public JSONArray recommendRestaurants(String userId) {
		try {
//			if (conn == null) {
//				return null;
//			}

			Set<String> visitedRestaurants = getVisitedRestaurants(userId);//step 1
			Set<String> allCategories = new HashSet<>();// why hashSet? //step 2
			for (String restaurant : visitedRestaurants) {
				allCategories.addAll(getCategories(restaurant));
			}
			Set<String> allRestaurants = new HashSet<>();//step 3
			for (String category : allCategories) {
				Set<String> set = getBusinessId(category);
				allRestaurants.addAll(set);
			}
			Set<JSONObject> diff = new HashSet<>();//step 4
			int count = 0;
			for (String businessId : allRestaurants) {
				// Perform filtering
				if (!visitedRestaurants.contains(businessId)) {
					diff.add(getRestaurantsById(businessId, false));
					count++;
					if (count >= MAX_RECOMMENDED_RESTAURANTS) {
						break;
					}
				}
			}
			return new JSONArray(diff);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return null;
	}
    /**
     * Gets categories based on business id
     * @param businessId
     * @return
     */
    public abstract Set<String> getCategories(String businessId);


    /**
     * Gets business id based on category
     * @param category
     * @return
     */
    public abstract Set<String> getBusinessId(String category);
    
    /**
     * Search restaurants near a geolocation.
     * @param userId
     * @param lat
     * @param lon
     * @return
     */
    public abstract JSONArray searchRestaurants(String userId, double lat, double lon);


   /**
     * Verify if the userId matches the password.
     * @param userId
     * @param password
     * @return
     */
    public abstract Boolean verifyLogin(String userId, String password);


    /**
     * Get user's name for the userId.
     * @param userId
     * @return First and Last Name
     */
    public abstract String getFirstLastName(String userId);
}
