package com.generic.job.marketplace.rest.resource.input;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProjectInputResource {

	private String projectName;
	private String projectOwnerId;
	private String requirements;
	private double maximumBudget;
	private String lastDateTime;
}
