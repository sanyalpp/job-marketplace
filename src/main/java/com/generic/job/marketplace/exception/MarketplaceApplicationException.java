package com.generic.job.marketplace.exception;

/**
 * @author Sanyal, Partha
 *
 */
public class MarketplaceApplicationException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	public MarketplaceApplicationException() {
	}

	/**
	 * @param message
	 */
	public MarketplaceApplicationException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public MarketplaceApplicationException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public MarketplaceApplicationException(String message,
			Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 * @param cause
	 * @param enableSuppression
	 * @param writableStackTrace
	 */
	public MarketplaceApplicationException(String message,
			Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
