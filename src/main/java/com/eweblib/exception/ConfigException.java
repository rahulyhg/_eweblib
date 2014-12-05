package com.eweblib.exception;


public class ConfigException extends RuntimeException {
	/**
     * 
     */
	private static final long serialVersionUID = 1L;

	/**
	 * Constructs an <code>ApiApiIllegalArgumentException</code> with no detail
	 * message.
	 */
	@SuppressWarnings("unused")
	private ConfigException() {
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
	public ConfigException(String message) {
		super(message);
	}


}
