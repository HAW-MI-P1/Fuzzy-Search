package de.haw.model.exception;

public class ConnectionException extends Exception{
	
	// Private static attributes
	
	private static final long serialVersionUID = 5636121443361681572L;
	
	
	// Constructor
	
	public ConnectionException() {
		super("Connection lost or could not be established");
	}
	
}