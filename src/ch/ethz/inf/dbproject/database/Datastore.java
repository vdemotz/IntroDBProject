package ch.ethz.inf.dbproject.database;

import java.util.Arrays;
import java.util.List;

import ch.ethz.inf.dbproject.model.ModelObject;
import ch.ethz.inf.dbproject.sqlRevisited.Connection;
import ch.ethz.inf.dbproject.sqlRevisited.PreparedStatement;
import ch.ethz.inf.dbproject.sqlRevisited.ResultSet;
import ch.ethz.inf.dbproject.sqlRevisited.SQLException;

public class Datastore {

	protected static Connection sqlConnection;
	// TODO Change this line to point to the homemade DB. (New instance of ch.ethz.inf.dbproject.sqlRevisited.Connection)

	public Datastore() {
		try {
			synchronized (this.getClass()) {
				sqlConnection = new Connection(System.getProperty("user.home")+ "/SQLRevisited");
			}
			prepareStatements();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	protected void prepareStatements() throws SQLException
	{
		
	}
	
	/**
	 * Executes a statement, and tries to instantiate a list of ModelObjects of the specified modelClass using the resultSet from the statement
	 * If the execution of the statement or instantiation raises an SQLException, null is returned.
	 * @param statement the configured statement to execute and get the results of
	 * @return a list of modelObjects representing the result of the execution of the statement
	 */
	protected <T extends ModelObject> List<T> getResults(Class<T> modelClass, PreparedStatement statement)
	{
		 try {
			 ResultSet rs = statement.executeQuery();
			return ModelObject.getAllModelObjectsWithClassFromResultSet(modelClass, rs);
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
}
