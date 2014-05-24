package ch.ethz.inf.dbproject.model;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import ch.ethz.inf.dbproject.sqlRevisited.ResultSet;
import ch.ethz.inf.dbproject.sqlRevisited.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ModelObject implements Serializable { 

	private static final long serialVersionUID = 1L;
	
	public ModelObject()
	{
	}
	
	/**
	 * Experimental:
	 * Sets all the fields of a ModelObject from a ResultSet assuming default types and the same names for the fields as for the SQL attributes
	 * If one of the Objects fields doesn't match any column in the ResultSet, the field is left unmodified.
	 * @param rs
	 * @throws SQLException
	 */
	public ModelObject(ResultSet rs)
	{
		Class<?> currentClass = this.getClass();
		Field[] fields = currentClass.getDeclaredFields();
		
		for (int i=0; i<fields.length; i++) {
			fields[i].setAccessible(true);//in case the field is final, this makes it possible to assign it anyway
			try {
				String fieldname = fields[i].getName();
				Object value;
				if (fields[i].getType().equals(boolean.class)) {
					value = rs.getBoolean(fieldname);
				} else {
					value = rs.getObject(fieldname);//use default type
				}
				fields[i].set(this, value);
			} catch (Exception e) {
				e.printStackTrace();
			}
			fields[i].setAccessible(false);//enforce java language checks again
		}
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
