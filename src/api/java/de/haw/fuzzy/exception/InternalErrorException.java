package de.haw.fuzzy.exception;

public class InternalErrorException extends RuntimeException{
	
	// Private static attributes
	
	private static final long serialVersionUID = 5636121443361681572L;
	
	
	// Constructor
	
	public InternalErrorException() {
		super("Something goes wrong here");
	}
	
}