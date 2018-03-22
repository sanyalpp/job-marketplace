package com.generic.job.marketplace;

import java.util.Date;
import java.util.UUID;

import org.springframework.beans.BeanUtils;

import com.generic.job.marketplace.entity.Bid;
import com.generic.job.marketplace.entity.Project;
import com.generic.job.marketplace.entity.WinningBid;
import com.generic.job.marketplace.rest.resource.input.BidInputResource;
import com.generic.job.marketplace.rest.resource.input.PersonInputResource;
import com.generic.job.marketplace.rest.resource.input.ProjectInputResource;

public class TestData {

	public static BidInputResource createBidInputResource(double bidAmount,
			String bidderId) {
		BidInputResource bidInputResource = new BidInputResource();
		bidInputResource.setBidAmount(bidAmount);
		bidInputResource.setBidderId(bidderId);
		return bidInputResource;
	}
	public static Bid createBid(double bidAmount, String bidderId,
			String projectId) {
		Bid bid = new Bid();

		bid.setBidAmount(bidAmount);
		bid.setBidderId(bidderId);
		bid.setId(UUID.randomUUID().toString());
		bid.setProjectId(projectId);
		return bid;
	}

	public static WinningBid createWinningBid(double bidAmount, String bidderId,
			String projectId) {
		Bid bid = createBid(bidAmount, bidderId, projectId);
		WinningBid winningBid = new WinningBid();
		BeanUtils.copyProperties(bid, winningBid);
		return winningBid;
	}

	public static Project createProject(String projectName,
			String projectOwnerId, Date lastDateTime, double maxBugetAmount) {
		Project project = new Project();
		project.setId(UUID.randomUUID().toString());
		project.setLastDateTime(lastDateTime);
		project.setMaximumBudget(maxBugetAmount);
		project.setProjectName(projectName);
		project.setProjectOwnerId(projectOwnerId);
		project.setRequirements(UUID.randomUUID().toString());

		return project;
	}

	public static ProjectInputResource createProjectInputResource(
			String projectOwnerId, String lastDateTime, double maxBugetAmount) {
		ProjectInputResource project = new ProjectInputResource();
		project.setLastDateTime(lastDateTime);
		project.setMaximumBudget(maxBugetAmount);
		project.setProjectName(UUID.randomUUID().toString());
		project.setProjectOwnerId(projectOwnerId);
		project.setRequirements(UUID.randomUUID().toString());
		return project;
	}

	public static PersonInputResource createPerson(final String firstName,
			final String lastName, final String email) {

		PersonInputResource personInputResource = new PersonInputResource();
		personInputResource.setLastName(lastName);
		personInputResource.setEmail(email);
		personInputResource.setFirstName(firstName);
		return personInputResource;

	}
}
