package com.mpe85.grampa.exception;

/**
 * Thrown when an error occurs during the parser creation process.
 * 
 * @author mpe85
 */
public class ParserCreateException extends GrampaException {
	
	public ParserCreateException(final String message) {
		super(message);
	}
	
	public ParserCreateException(final Throwable cause) {
		super(cause);
	}
	
	public ParserCreateException(final String message, final Throwable cause) {
		super(message, cause);
	}
	
	
	private static final long serialVersionUID = 3813999425394378648L;
	
}
