package com.generic.job.marketplace.validator;

import com.generic.job.marketplace.exception.BadRequestException;

/**
 * Validator interface.
 * 
 * 
 * @author Sanyal, Partha
 *
 */
public interface Validator<T> {

	void validate(T t) throws BadRequestException;
}
