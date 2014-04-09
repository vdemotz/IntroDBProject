package ch.ethz.inf.dbproject.database;

import ch.ethz.inf.dbproject.model.*;

public interface ConvictionDatastoreInterface {

	////
	//QUERY
	////
	
	/**
	 * @param convictionId
	 * @return
	 */
	public Conviction getConvictionForId(int convictionId);
	
	////
	//MODIFY
	////
	
	/**
	 * @param startDate not null
	 * @param endDate not null
	 * @return A Conviction representing the newly inserted values if insertion was successful, otherwise null
	 */
	public Conviction insertIntoConviction(java.util.Date startDate, java.util.Date endDate);
	
	/**
	 * @param conviction Id
	 * @param personId
	 * @param caseId
	 * @return true if the insertion was successful, null otherwise
	 */
	public boolean insertIntoConvicted(int personId, Integer caseId, int convictionId);
	
}
