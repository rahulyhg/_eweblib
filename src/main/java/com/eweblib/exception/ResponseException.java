package com.eweblib.exception;


public class ResponseException extends RuntimeException {
	/**
     * 
     */
	private static final long serialVersionUID = 1L;

	/**
	 * Constructs an <code>ApiApiIllegalArgumentException</code> with no detail
	 * message.
	 */
	@SuppressWarnings("unused")
	private ResponseException() {
		super();
	}

	/**
	 * Constructs an <code>ApiApiIllegalArgumentException</code> with the
	 * specified detail message.
	 * 
	 * @param s
	 *            the detail message.
	 * 
	 */
	public ResponseException(String message) {
		super(message);
	}


}
