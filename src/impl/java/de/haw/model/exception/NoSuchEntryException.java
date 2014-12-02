package de.haw.model.exception;

public class NoSuchEntryException extends RuntimeException{
	
	// Private static attributes
	
	private static final long serialVersionUID = 5636121443361681572L;
	
	
	// Constructor
	
	public NoSuchEntryException(String key) {
		super("No result for key: " + key);
	}
	
}
