package com.generic.job.marketplace.exception;

public class DuplicateEntityException extends MarketplaceApplicationException {

	private static final long serialVersionUID = -3688507202017614775L;

	public DuplicateEntityException() {
	}

	public DuplicateEntityException(String message) {
		super(message);
	}

	public DuplicateEntityException(Throwable cause) {
		super(cause);
	}

	public DuplicateEntityException(String message, Throwable cause) {
		super(message, cause);
	}

	public DuplicateEntityException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
