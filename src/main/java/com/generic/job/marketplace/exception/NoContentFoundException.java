package com.generic.job.marketplace.exception;

/**
 * @author Sanyal, Partha
 *
 */
public class NoContentFoundException
		extends
			MarketplaceApplicationException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7976580400155930407L;

	/**
	 * @param message
	 */
	public NoContentFoundException(String message) {
		super(message);
	}

	/**
	 * @param message
	 * @param throwable
	 */
	public NoContentFoundException(String message, Throwable throwable) {
		super(message, throwable);
	}

	/**
	 * @param throwable
	 */
	public NoContentFoundException(Throwable throwable) {
		super(throwable);
	}

}
