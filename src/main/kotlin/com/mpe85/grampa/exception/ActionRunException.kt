package com.mpe85.grampa.exception;

/**
 * Thrown when an error occurs during the run of an action.
 * 
 * @author mpe85
 */
public class ActionRunException extends GrampaException {
	
	private static final long serialVersionUID = -1674353148882821963L;
	
	public ActionRunException(final String message) {
		super(message);
	}
	
	public ActionRunException(final Throwable cause) {
		super(cause);
	}
	
	public ActionRunException(final String message, final Throwable cause) {
		super(message, cause);
	}
	
}
