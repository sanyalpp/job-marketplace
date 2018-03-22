package com.generic.job.marketplace.rest.resource.input;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BidInputResource {

	private String bidderId;
	private double bidAmount;
}
