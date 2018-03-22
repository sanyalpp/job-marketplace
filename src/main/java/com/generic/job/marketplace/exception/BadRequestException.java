package com.generic.job.marketplace.exception;

/**
 * @author Sanyal, Partha
 *
 */
public class BadRequestException
		extends
			MarketplaceApplicationException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8047908915189046152L;

	/**
	 * @param message
	 */
	public BadRequestException(String message) {
		super(message);
	}

	/**
	 * @param message
	 * @param throwable
	 */
	public BadRequestException(String message, Throwable throwable) {
		super(message, throwable);
	}

	/**
	 * @param throwable
	 */
	public BadRequestException(Throwable throwable) {
		super(throwable);
	}

}
