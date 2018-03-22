package com.generic.job.marketplace.entitybuilder;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import com.generic.job.marketplace.entity.Bid;
import com.generic.job.marketplace.rest.resource.input.BidInputResource;

@Component
public class BidBuilder implements EntityBuilder<BidInputResource, Bid> {

	@Override
	public Bid buildFresh(BidInputResource inputResource) {
		Bid bid = new Bid();
		BeanUtils.copyProperties(inputResource, bid);
		return bid;
	}

	public Bid build(BidInputResource inputResource, String projectId) {
		Bid bid = buildFresh(inputResource);
		bid.setProjectId(projectId);
		return bid;
	}

}
