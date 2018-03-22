package com.generic.job.marketplace.rest.assembler;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;

import com.generic.job.marketplace.entity.Bid;
import com.generic.job.marketplace.entity.WinningBid;
import com.generic.job.marketplace.logging.ErrorMessages;
import com.generic.job.marketplace.logging.MessageRetriever;
import com.generic.job.marketplace.rest.controller.PersonController;
import com.generic.job.marketplace.rest.controller.ProjectController;
import com.generic.job.marketplace.rest.resource.output.BidOutpuResource;

public class WinningBidResourceAssembler
		extends
			ResourceAssemblerSupport<WinningBid, BidOutpuResource> {
	private static final Logger LOGGER = LoggerFactory
			.getLogger(WinningBidResourceAssembler.class);

	public WinningBidResourceAssembler() {
		super(Bid.class, BidOutpuResource.class);

	}

	@Override
	public BidOutpuResource toResource(WinningBid winningBid) {
		BidOutpuResource resource = null;
		try {
			resource = instantiateResource(winningBid);
			resource.setBidId(winningBid.getId());
			BeanUtils.copyProperties(winningBid, resource);

			Link selfLink = linkTo(
					methodOn(ProjectController.class).getBidByIdAndProjectId(
							winningBid.getProjectId(), winningBid.getId()))
									.withRel("self");

			Link projectLink = linkTo(methodOn(ProjectController.class)
					.getProjectById(winningBid.getProjectId()))
							.withRel("project");

			Link bidderLink = linkTo(methodOn(PersonController.class)
					.getContactById(winningBid.getBidderId()))
							.withRel("bidder");

			resource.add(selfLink);
			resource.add(bidderLink);
			resource.add(projectLink);
		} catch (Exception exe) {
			String errMsg = MessageRetriever.getMessage(
					ErrorMessages.ERROR_OCCURRED, "creating BidOutpuResource",
					exe.getCause());
			LOGGER.error(errMsg);
		}
		return resource;
	}
}
