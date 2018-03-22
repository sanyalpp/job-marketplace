package com.generic.job.marketplace.validator;

import java.util.Locale;

import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.generic.job.marketplace.exception.BadRequestException;
import com.generic.job.marketplace.exception.MarketplaceApplicationException;
import com.generic.job.marketplace.logging.ErrorMessages;
import com.generic.job.marketplace.rest.resource.input.ProjectInputResource;
import com.generic.job.marketplace.utility.DateTimeUtil;
import com.generic.job.marketplace.utility.Filter;
import com.generic.job.marketplace.utility.ValidationUtil;

@Component
public class ProjectValidator implements Validator<ProjectInputResource> {

	@Autowired
	private ValidationUtil validationUtil;

	@Override
	public void validate(ProjectInputResource projectInputResource)
			throws BadRequestException {

		// Project name can't be blank
		validationUtil.checkForBlank(projectInputResource.getProjectName(),
				"Project Name");
		// project owner id can't be blank
		validationUtil.checkForBlank(projectInputResource.getProjectOwnerId(),
				"Project owner Id");
		// Requirements can't be blank, there is a possibility of SQL injection
		// with this field, so may be some special handling needs to be done and
		// verified against a whitelist of special characters.
		validationUtil.checkForBlank(projectInputResource.getRequirements(),
				"Requirements");

		// max budget shouldnt be less than 0
		if (projectInputResource.getMaximumBudget() <= 0) {
			throw new BadRequestException(
					ErrorMessages.MAX_BUDGET_LESS_THAN_ZERO);
		}

		validateLastDateTime(projectInputResource);

		// validate the projectOwner
		validationUtil.validatePersonExists(
				projectInputResource.getProjectOwnerId(), "Project owner");

	}

	/**
	 * Validates the last date time of the project, checks whether the format is
	 * correct as per ISO 8601, also if the date is in future
	 * 
	 * @param projectInputResource
	 * @throws BadRequestException
	 */
	private void validateLastDateTime(ProjectInputResource projectInputResource)
			throws BadRequestException {
		// check for valid UTC date
		if (!DateTimeUtil
				.isValidUTCDate(projectInputResource.getLastDateTime())) {
			throw new BadRequestException(
					ErrorMessages.LAST_DATE_TIME_NOT_VALID);
		}

		// check whether the last date time is in the future
		if (!DateTimeUtil
				.isUTCDateInFuture(projectInputResource.getLastDateTime())) {
			throw new BadRequestException(
					ErrorMessages.LAST_DATE_TIME_NOT_VALID);
		}

	}

	/**
	 * This method validates the filter parameters
	 * 
	 * @param type
	 * @param projectOwnerId
	 * @throws MarketplaceApplicationException
	 */
	public void validateFilter(String type, String projectOwnerId)
			throws MarketplaceApplicationException {

		// both filter parameters can't be blank
		if (StringUtils.isEmpty(type) && StringUtils.isEmpty(projectOwnerId)) {
			throw new BadRequestException(
					ErrorMessages.FILTER_PARAMS_NOT_VALID);
		}

		// filter "type" should be either of "open", "closed", or "all"
		if (StringUtils.isNotBlank(type) && !EnumUtils.isValidEnum(Filter.class,
				type.toUpperCase(Locale.US))) {
			throw new BadRequestException(ErrorMessages.FILTER_TYPE_NOT_VALID);
		}
	}

}
