package com.mpe85.grampa.exception;

/**
 * Base class for all Grampa exceptions
 * 
 * @author mpe85
 */
public class GrampaException extends RuntimeException {
	
	public GrampaException(final String message) {
		super(message);
	}
	
	public GrampaException(final Throwable cause) {
		super(cause);
	}
	
	public GrampaException(final String message, final Throwable cause) {
		super(message, cause);
	}
	
	
	private static final long serialVersionUID = 5796921255320228162L;
}
