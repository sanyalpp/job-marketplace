package com.generic.job.marketplace.rest.assembler;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.generic.job.marketplace.entity.Project;
import com.generic.job.marketplace.logging.ErrorMessages;
import com.generic.job.marketplace.logging.MessageRetriever;
import com.generic.job.marketplace.rest.controller.PersonController;
import com.generic.job.marketplace.rest.controller.ProjectController;
import com.generic.job.marketplace.rest.resource.output.ProjectOutputRespurce;
import com.generic.job.marketplace.utility.DateTimeUtil;

public class ProjectResourceAssembler
		extends
			ResourceAssemblerSupport<Project, ProjectOutputRespurce> {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(PersonResourceAssembler.class);

	public ProjectResourceAssembler() {
		super(Project.class, ProjectOutputRespurce.class);

	}

	@Override
	public ProjectOutputRespurce toResource(Project project) {
		ProjectOutputRespurce resource = null;
		try {
			resource = instantiateResource(project);
			resource.setProjectId(project.getId());
			BeanUtils.copyProperties(project, resource);

			// Set the lastDateTime
			String lastDateTime = DateTimeUtil
					.getUTCDateString(project.getLastDateTime());
			resource.setLastDateTime(lastDateTime);

			// set Lowest bid
			if (Objects.nonNull(project.getLowestBid())) {
				resource.setLowestBidAmount(
						project.getLowestBid().getBidAmount());
			}

			Link selfLink = linkTo(methodOn(ProjectController.class)
					.getProjectById(project.getId())).withRel("self");
			Link bidsLink = linkTo(methodOn(ProjectController.class)
					.createProjectBid(project.getId(), null)).withRel("bids");
			Link projectOwnerLink = linkTo(methodOn(PersonController.class)
					.getContactById(project.getProjectOwnerId()))
							.withRel("projectOwner");
			Link winningBidLink = linkTo(methodOn(ProjectController.class)
					.getWinningBidProjectId(project.getId()))
							.withRel("winningBid");
			Link filterLink = linkTo(
					methodOn(ProjectController.class).getAllProjects(
							((ServletRequestAttributes) RequestContextHolder
									.getRequestAttributes()).getRequest()))
											.withRel("filterProjects");
			resource.add(selfLink);
			resource.add(projectOwnerLink);
			resource.add(bidsLink);
			resource.add(filterLink);
			resource.add(winningBidLink);
		} catch (Exception exe) {
			String errMsg = MessageRetriever.getMessage(
					ErrorMessages.ERROR_OCCURRED,
					"creating ProjectOutputRespurce", exe.getCause());
			LOGGER.error(errMsg);
		}
		return resource;
	}

	@Override
	public List<ProjectOutputRespurce> toResources(
			Iterable<? extends Project> projects) {
		List<ProjectOutputRespurce> outputResources = new ArrayList<>();
		for (Project project : projects) {
			outputResources.add(this.toResource(project));
		}
		return outputResources;
	}
}
