package ch.ethz.inf.dbproject.model;

/**
 * Object that represents a type of crime (i.e. Theft, Assault...) 
 */
public final class Category {

	private final String name;

	////
	//CONSTRUCTOR
	////
	
	public Category(final String name) {
		super();
		this.name = name;
	}

	////
	// GETTERS
	////
	
	public final String getName() {
		return name;
	}
	
}
