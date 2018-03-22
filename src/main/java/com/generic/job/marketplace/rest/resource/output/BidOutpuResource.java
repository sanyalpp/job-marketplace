package com.generic.job.marketplace.rest.resource.output;

import org.springframework.hateoas.ResourceSupport;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
public class BidOutpuResource extends ResourceSupport {

	private String bidId;
	private String bidderId;
	private double bidAmount;
}
