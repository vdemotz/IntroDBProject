package ch.ethz.inf.dbproject.model;

import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ModelObject {

	public ModelObject()
	{
	}
	
	/**
	 * Subclasses should override this method to set their field using the result set
	 * @param rs
	 */
	public ModelObject(ResultSet rs)
	{
	}

	/**
	 * Tries to process the ResultSet into a list of objects representing it
	 * @param modelClass the class of the objects to instantiate. The class should implement a constructor taking a ResultSet as parameter.
	 * @param rs
	 * @return a list of model objects representing the result from the result set
	 * @throws SQLException if the result set or the construction of a ModelObject throws such an exception
	 */
	public static <T extends ModelObject> List<T> getAllModelObjectsWithClassFromResultSet(Class<T> modelClass, final ResultSet rs) throws SQLException
	{
		return getModelObjectsWithClassFromResultSet(modelClass, rs, Integer.MAX_VALUE);
	}
	
	/**
	 * Tries to process the ResultSet into a list of objects representing it. Only the first maximumCount rows are processed.
	 * @param modelClass the class of the objects to instantiate. The class should implement a constructor taking a ResultSet as parameter.
	 * @param rs
	 * @param maximumCount only process the first maximumCount entries of the set
	 * @return a list of model objects representing the result from the result set
	 * @throws SQLException if the result set or the construction of a ModelObject throws such an exception
	 */
	public static <T extends ModelObject> List<T> getModelObjectsWithClassFromResultSet(Class<T> modelClass, ResultSet rs, int maximumCount) throws SQLException
	{
		final List<T> result = new ArrayList<T>();
		int count = 0;
		while (rs.next() && count < maximumCount) {
			try {
				result.add(modelClass.getConstructor(ResultSet.class).newInstance(rs));
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			} catch (SecurityException e) {
				e.printStackTrace();
			}
			count = count + 1;
		}
		return result;
	}

}
