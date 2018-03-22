package com.generic.job.marketplace.rest.assembler;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;

import com.generic.job.marketplace.entity.Bid;
import com.generic.job.marketplace.logging.ErrorMessages;
import com.generic.job.marketplace.logging.MessageRetriever;
import com.generic.job.marketplace.rest.controller.PersonController;
import com.generic.job.marketplace.rest.controller.ProjectController;
import com.generic.job.marketplace.rest.resource.output.BidOutpuResource;

public class BidResourceAssembler
		extends
			ResourceAssemblerSupport<Bid, BidOutpuResource> {
	private static final Logger LOGGER = LoggerFactory
			.getLogger(BidResourceAssembler.class);

	public BidResourceAssembler() {
		super(Bid.class, BidOutpuResource.class);

	}

	@Override
	public BidOutpuResource toResource(Bid bid) {
		BidOutpuResource resource = null;
		try {
			resource = instantiateResource(bid);
			resource.setBidId(bid.getId());
			BeanUtils.copyProperties(bid, resource);

			Link selfLink = linkTo(methodOn(ProjectController.class)
					.getBidByIdAndProjectId(bid.getProjectId(), bid.getId()))
							.withRel("self");

			Link projectLink = linkTo(methodOn(ProjectController.class)
					.getProjectById(bid.getProjectId())).withRel("project");

			Link bidderLink = linkTo(methodOn(PersonController.class)
					.getContactById(bid.getBidderId())).withRel("bidder");

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

	@Override
	public List<BidOutpuResource> toResources(Iterable<? extends Bid> bids) {
		List<BidOutpuResource> outputResources = new ArrayList<>();
		for (Bid bid : bids) {
			outputResources.add(this.toResource(bid));
		}
		return outputResources;
	}
}
