package com.generic.job.marketplace.logging;

import java.text.MessageFormat;

/**
 * Logging related Utility class that will append and inject parameters into log
 * messages.
 * 
 * @author Sanyal, Partha
 *
 */
public class MessageRetriever {

	public static String getMessage(String message, Object... parameters) {
		return MessageFormat.format(message, parameters);
	}
}
