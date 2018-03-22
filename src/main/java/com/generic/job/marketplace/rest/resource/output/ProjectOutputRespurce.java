package com.generic.job.marketplace.rest.resource.output;

import org.springframework.hateoas.ResourceSupport;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
public class ProjectOutputRespurce extends ResourceSupport {

	private String projectId;
	private String projectName;
	private String projectOwnerId;
	private String requirements;
	private double maximumBudget;
	private String lastDateTime;
	private double lowestBidAmount;
}
