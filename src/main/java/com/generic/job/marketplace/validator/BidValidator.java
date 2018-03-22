package com.generic.job.marketplace.validator;

import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.generic.job.marketplace.entity.Project;
import com.generic.job.marketplace.exception.BadRequestException;
import com.generic.job.marketplace.exception.MarketplaceApplicationException;
import com.generic.job.marketplace.logging.ErrorMessages;
import com.generic.job.marketplace.repository.ProjectRepository;
import com.generic.job.marketplace.rest.resource.input.BidInputResource;
import com.generic.job.marketplace.utility.DateTimeUtil;
import com.generic.job.marketplace.utility.ValidationUtil;

@Component
public class BidValidator implements Validator<BidInputResource> {

	@Autowired
	private ValidationUtil validationUtil;

	@Autowired
	private ProjectRepository projectRepository;

	@Override
	public void validate(BidInputResource bidInputResource)
			throws BadRequestException {

		// Validate that bidder exists
		validationUtil.validatePersonExists(bidInputResource.getBidderId(),
				"Bidder");

		// Bid amount cant be less than zero
		if (bidInputResource.getBidAmount() <= 0) {
			throw new BadRequestException(
					ErrorMessages.BID_AMOUNT_LESS_THAN_ZERO);
		}
	}

	public void validate(BidInputResource bidInputResource, String projectId)
			throws MarketplaceApplicationException {

		validate(bidInputResource);

		// validate the project
		Project project = projectRepository.findById(projectId);

		// Project should exist
		if (Objects.isNull(project)) {
			throw new BadRequestException(ErrorMessages.PROJECT_NOT_FOUND);
		}

		// Project should be open to accept bids
		if (!DateTimeUtil.isDateInFuture(project.getLastDateTime())) {
			throw new BadRequestException(ErrorMessages.BIDDING_CLOSED);
		}

		// Bid amount can't be more than maximum budget of project
		if (bidInputResource.getBidAmount() > project.getMaximumBudget()) {
			throw new BadRequestException(
					ErrorMessages.BIDDING_AMOUNT_MORE_THAN_MAX_BUDGET);
		}

		// Project owner can't be the bidder
		if (project.getProjectOwnerId()
				.equals(bidInputResource.getBidderId())) {
			throw new BadRequestException(ErrorMessages.OWNER_CANT_BE_BIDDER);
		}

	}

}
