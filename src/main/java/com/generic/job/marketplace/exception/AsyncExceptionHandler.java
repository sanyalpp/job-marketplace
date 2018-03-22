package com.generic.job.marketplace.exception;

import java.lang.reflect.Method;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.stereotype.Component;

/**
 * This gets called if there is an exception happens when the asynchronous
 * execution is happening.
 * 
 * @author Sanyal, Partha
 *
 */

@Component
public class AsyncExceptionHandler implements AsyncUncaughtExceptionHandler {
	private static final Logger LOGGER = LoggerFactory
			.getLogger(AsyncExceptionHandler.class);

	@Override
	public void handleUncaughtException(Throwable throwable, Method method,
			Object... params) {
		LOGGER.error("Error Occured while processing asynchronously: ",
				throwable);
	}
}
