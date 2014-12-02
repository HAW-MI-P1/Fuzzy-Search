package de.haw.model.exception;

public class IllegalArgumentException extends RuntimeException{
	
	// Private static attributes
	
	private static final long serialVersionUID = 5636121443361681572L;
	
	
	// Constructor
	
	public IllegalArgumentException(String argument) {
		super("Argument is not accepted: " + argument);
	}
	
}