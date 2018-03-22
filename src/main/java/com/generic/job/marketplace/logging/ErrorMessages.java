package com.generic.job.marketplace.logging;

/**
 * Final class to hold all error messages used in the application.
 * 
 * @author Sanyal, Partha
 *
 */
public final class ErrorMessages {

	private ErrorMessages() {
	}

	public static final String NOT_NULL = "{0} shouldn't be null";
	public static final String EMAIL_ADDRESS_NOT_VALID = "Email address provided is not valid";
	public static final String NOT_NULL_OR_EMPTY = "{0} shouldn't be null or empty";
	public static final String NOT_NULL_OR_BLANK_MSG = "{0} shouldn't be null or blank";
	public static final String NO_EXISTING_PERSON = "No {0} found";
	public static final String DUPLICATE_PERSON = "Duplicate contact found with the email address";
	public static final String PERSON_CREATE_UPDATE_ERROR = "Error occured while {0} contact because {1}";
	public static final String ERROR_OCCURRED = "Error occured while {0} because of {1}";
	public static final String MAX_BUDGET_LESS_THAN_ZERO = "Max budget for a project cant be less than or equal to zero";
	public static final String LAST_DATE_TIME_NOT_VALID = "Bid acceptance last date time is not valid.";
	public static final String UTC_DATE_CONV_ERR_MSG = "Error occured while converting to UTC Format date";
	public static final String PROJECT_NOT_FOUND = "Project not found";
	public static final String PROJECTS_NOT_FOUND = "Projects not found";
	public static final String BIDDING_CLOSED = "Bidding for the project has been closed";
	public static final String BIDDING_AMOUNT_MORE_THAN_MAX_BUDGET = "Bid amount cant be more than the maximum budget of the project";
	public static final String BID_AMOUNT_LESS_THAN_ZERO = "Bid amount for a project cant be less than or equal to zero";
	public static final String BID_NOT_FOUND = "Requested bid not found";
	public static final String NO_BIDS_FOR_PROJECT = "No bids found for the project";
	public static final String NO_WINNING_BID_FOR_PROJECT = "No winning bid found for the project";
	public static final String OWNER_CANT_BE_BIDDER = "Owner(s) of the project cant bid for their own project";
	public static final String FILTER_TYPE_NOT_VALID = "Filter type not valid, allowed : OPEN/CLOSED/ALL only.";
	public static final String FILTER_PARAMS_NOT_VALID = "Filtering should be done with atleast a type or a projectOwnerId parameter.";
}
