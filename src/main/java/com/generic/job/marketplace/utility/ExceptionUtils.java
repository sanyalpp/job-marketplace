package com.generic.job.marketplace.utility;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.generic.job.marketplace.rest.resource.output.ErrorOutputResource;

/**
 * Exception Resource creation is done here.
 * 
 * @author Sanyal, Partha
 *
 */
public class ExceptionUtils {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(ExceptionUtils.class);
	private static final String MESSAGE = "message";
	private static final String CODE = "code";

	public ErrorOutputResource setErrorObject(String message,
			HttpStatus httpStatus) {

		ErrorOutputResource errorResourceOutput = new ErrorOutputResource();

		// Setting the path for the json output
		HttpServletRequest request = getRequest();

		if (request != null) {
			String url = request.getRequestURL().toString();
			errorResourceOutput.setPath(url);

			// Setting the timestamp for the json output
			errorResourceOutput
					.setTimestamp(DateTimeUtil.getUTCDateString(new Date()));

			// Set the error code and message
			HttpStatus status = getStatus(httpStatus);
			String code = String.valueOf(status.value());

			List<Map<String, String>> errors = errorResourceOutput.getErrors();
			Map<String, String> errorMap = new HashMap<>();
			errorMap.put(MESSAGE, message);
			errorMap.put(CODE, code);
			errors.add(errorMap);
		}

		return errorResourceOutput;
	}

	/**
	 * Method to return HTTP servlet request.
	 * 
	 * @return
	 */
	public HttpServletRequest getRequest() {
		return ((ServletRequestAttributes) RequestContextHolder
				.currentRequestAttributes()).getRequest();
	}

	/**
	 * Method to retrieve the status attribute from the request context.
	 * 
	 * @param httpStatus
	 * @return
	 */
	public HttpStatus getStatus(HttpStatus httpStatus) {
		if (httpStatus == null) {
			return HttpStatus.INTERNAL_SERVER_ERROR;
		}
		return httpStatus;
	}

	/**
	 * Method to log exceptions.
	 * 
	 * @param exception
	 */
	public void logException(Exception exception) {
		String errorMsg = exception.getMessage();
		LOGGER.error("Error Msg : {}", errorMsg);
		LOGGER.error("Stack Trace : "
				+ org.apache.commons.lang.exception.ExceptionUtils
						.getFullStackTrace(exception));
	}

}
