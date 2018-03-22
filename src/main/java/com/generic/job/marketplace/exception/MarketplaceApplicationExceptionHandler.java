package com.generic.job.marketplace.exception;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NO_CONTENT;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import com.generic.job.marketplace.rest.resource.output.ErrorOutputResource;
import com.generic.job.marketplace.utility.ExceptionUtils;

/**
 * Exception Handler for all application related exceptions.
 * 
 * @author Sanyal, Partha
 *
 */
@ControllerAdvice
public class MarketplaceApplicationExceptionHandler {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(MarketplaceApplicationExceptionHandler.class);

	@ExceptionHandler(value = BadRequestException.class)
	@ResponseBody
	public ResponseEntity<ErrorOutputResource> handleBadRequestException(
			BadRequestException exception) {
		LOGGER.info("Handling BadRequestException");
		return handleExceptionInternal(exception, BAD_REQUEST);
	}

	@ExceptionHandler(value = DataIntegrityViolationException.class)
	@ResponseBody
	public ResponseEntity<ErrorOutputResource> handleDataIntegrityViolationException(
			DataIntegrityViolationException exception) {
		LOGGER.info("Handling DataIntegrityViolationException");
		return handleExceptionInternal(exception, BAD_REQUEST);
	}

	@ExceptionHandler(value = DuplicateEntityException.class)
	@ResponseBody
	public ResponseEntity<ErrorOutputResource> handleDuplicateEntityException(
			DuplicateEntityException exception) {
		LOGGER.info("Handling DuplicateEntityException");
		return handleExceptionInternal(exception, BAD_REQUEST);
	}

	/**
	 * Handler for NoContentException.
	 * 
	 * @param exception
	 * @return
	 */
	@ExceptionHandler(value = NoContentFoundException.class)
	@ResponseBody
	public ResponseEntity<ErrorOutputResource> handleNoContentException(
			NoContentFoundException exception) {
		LOGGER.info("Handling NoContentFoundException");
		return handleExceptionInternal(exception, NO_CONTENT);
	}

	@ExceptionHandler(value = MarketplaceApplicationException.class)
	@ResponseBody
	public ResponseEntity<ErrorOutputResource> handleAvenueCodeShoppingApplicationException(
			MarketplaceApplicationException exception) {
		LOGGER.info("Handling MarketplaceApplicationException");
		return handleExceptionInternal(exception, INTERNAL_SERVER_ERROR);
	}
	/**
	 * Single place to customize the response body of all Exception types.
	 *
	 * @param exception
	 * @return
	 */
	public ResponseEntity<ErrorOutputResource> handleExceptionInternal(
			Exception exception, HttpStatus httpStatus) {
		ExceptionUtils exceptionUtils = new ExceptionUtils();

		// Log the exception
		exceptionUtils.logException(exception);

		// Set the error message in the object
		ErrorOutputResource errorResourceOutput = exceptionUtils
				.setErrorObject(exception.getMessage(), httpStatus);

		// Get the status code from request
		HttpStatus status = exceptionUtils.getStatus(httpStatus);
		return new ResponseEntity<>(errorResourceOutput, status);
	}

}
