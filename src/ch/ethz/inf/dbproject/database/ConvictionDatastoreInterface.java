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
	 * @param personId
	 * @param caseId
	 * @param startDate not null
	 * @param endDate not null
	 * @return A Conviction representing the newly inserted values if insertion was successful, otherwise null
	 */
	public Conviction insertIntoConviction(int personId, Integer caseId, java.util.Date startDate, java.util.Date endDate);
	
}
