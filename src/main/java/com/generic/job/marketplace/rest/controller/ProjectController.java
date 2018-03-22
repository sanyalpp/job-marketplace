package com.generic.job.marketplace.rest.controller;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.hateoas.TemplateVariable;
import org.springframework.hateoas.TemplateVariables;
import org.springframework.hateoas.UriTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.generic.job.marketplace.entity.Bid;
import com.generic.job.marketplace.entity.Project;
import com.generic.job.marketplace.entity.WinningBid;
import com.generic.job.marketplace.exception.MarketplaceApplicationException;
import com.generic.job.marketplace.processor.BidProcessor;
import com.generic.job.marketplace.processor.ProjectProcessor;
import com.generic.job.marketplace.rest.assembler.BidResourceAssembler;
import com.generic.job.marketplace.rest.assembler.ProjectResourceAssembler;
import com.generic.job.marketplace.rest.assembler.WinningBidResourceAssembler;
import com.generic.job.marketplace.rest.resource.input.BidInputResource;
import com.generic.job.marketplace.rest.resource.input.ProjectInputResource;
import com.generic.job.marketplace.rest.resource.output.BidOutpuResource;
import com.generic.job.marketplace.rest.resource.output.ProjectOutputRespurce;
import com.generic.job.marketplace.utility.Filter;

@Controller
@RequestMapping(value = "/v1/projects", produces = {MediaTypes.HAL_JSON_VALUE})
public class ProjectController {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(ProjectController.class);

	@Autowired
	private ProjectProcessor projectProcessor;

	@Autowired
	private BidProcessor bidProcessor;

	/**
	 * Basically returns a list of options available to do search on projects.
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping(method = GET)
	@ResponseBody
	public Resources<Resource<?>> getAllProjects(HttpServletRequest request) {
		return new Resources<>(Collections.emptyList(), linkToFilter(request));
	}

	private Link linkToFilter(HttpServletRequest request) {
		TemplateVariables templateVariables = new TemplateVariables(
				new TemplateVariable(
						"type=" + Filter.ALL + "/" + Filter.OPEN + "/"
								+ Filter.CLOSED,
						TemplateVariable.VariableType.REQUEST_PARAM),
				new TemplateVariable("projectOwnerId",
						TemplateVariable.VariableType.REQUEST_PARAM));
		return new Link(new UriTemplate(request.getRequestURL() + "/filter")
				.with(templateVariables), "filter");
	}

	/**
	 * Filter, that can be applied to finding projects based on OPEN, CLOSED,
	 * ALL as well as projectOwnerId
	 * 
	 * @param type
	 * @param projectOwnerId
	 * @return
	 * @throws MarketplaceApplicationException
	 */
	@RequestMapping(value = "/filter", method = GET)
	@ResponseBody
	public HttpEntity<List<ProjectOutputRespurce>> searchProjects(
			@RequestParam(name = "type", required = false) String type,
			@RequestParam(name = "projectOwnerId", required = false) String projectOwnerId)
			throws MarketplaceApplicationException {

		// Note : Paging can be implemented here, I haven't included that for
		// now.
		List<Project> projects = projectProcessor.findAllProjectsFilter(type,
				projectOwnerId);
		LOGGER.info("Projects retrieved : {}", projects.size());
		ProjectResourceAssembler projectResourceAssembler = new ProjectResourceAssembler();
		return new ResponseEntity<>(
				projectResourceAssembler.toResources(projects), OK);
	}

	/**
	 * Create a project.
	 * 
	 * @param projectInputResource
	 * @return
	 * @throws MarketplaceApplicationException
	 */
	@RequestMapping(method = POST)
	@ResponseBody
	public HttpEntity<ProjectOutputRespurce> createProject(
			@RequestBody ProjectInputResource projectInputResource)
			throws MarketplaceApplicationException {

		Project project = projectProcessor.createProject(projectInputResource);
		ProjectResourceAssembler projectResourceAssembler = new ProjectResourceAssembler();
		return new ResponseEntity<>(
				projectResourceAssembler.toResource(project), CREATED);
	}

	/**
	 * This method gets the project by its id.
	 * 
	 * @param projectId
	 * @return
	 * @throws MarketplaceApplicationException
	 */
	@RequestMapping(value = "/{projectId}", method = GET)
	@ResponseBody
	public HttpEntity<ProjectOutputRespurce> getProjectById(
			@PathVariable("projectId") String projectId)
			throws MarketplaceApplicationException {

		Project project = projectProcessor.findProjectById(projectId);
		ProjectResourceAssembler projectResourceAssembler = new ProjectResourceAssembler();
		return new ResponseEntity<>(
				projectResourceAssembler.toResource(project), OK);
	}

	// The following section handles various bid related apis

	/**
	 * Create a bid for an existing project.
	 * 
	 * @param projectId
	 * @param bidInputResource
	 * @return
	 * @throws MarketplaceApplicationException
	 */
	@RequestMapping(value = "/{projectId}/bids", method = POST)
	@ResponseBody
	public HttpEntity<BidOutpuResource> createProjectBid(
			@PathVariable("projectId") String projectId,
			@RequestBody BidInputResource bidInputResource)
			throws MarketplaceApplicationException {

		Bid bid = bidProcessor.createBid(bidInputResource, projectId);
		BidResourceAssembler bidResourceAssembler = new BidResourceAssembler();
		return new ResponseEntity<>(bidResourceAssembler.toResource(bid),
				CREATED);
	}

	/**
	 * Gets the single bid depending on the bid id and the project id.
	 * 
	 * @param projectId
	 * @param bidId
	 * @return
	 * @throws MarketplaceApplicationException
	 */
	@RequestMapping(value = "/{projectId}/bids/{bidId}", method = GET)
	@ResponseBody
	public HttpEntity<BidOutpuResource> getBidByIdAndProjectId(
			@PathVariable("projectId") String projectId,
			@PathVariable("bidId") String bidId)
			throws MarketplaceApplicationException {

		Bid bid = bidProcessor.getBidByIdAndProjectId(bidId, projectId);
		BidResourceAssembler bidResourceAssembler = new BidResourceAssembler();
		return new ResponseEntity<>(bidResourceAssembler.toResource(bid), OK);
	}

	/**
	 * This methods gets all the bids for the current project. There could be
	 * filters implemented for bids like lowest bid etc. Also, the response
	 * could be page (not implemented)
	 * 
	 * @param projectId
	 * @return
	 * @throws MarketplaceApplicationException
	 */
	@RequestMapping(value = "/{projectId}/bids", method = GET)
	@ResponseBody
	public HttpEntity<List<BidOutpuResource>> getAllBidsByProjectId(
			@PathVariable("projectId") String projectId)
			throws MarketplaceApplicationException {

		List<Bid> bids = bidProcessor.getBidsByProjectId(projectId);
		BidResourceAssembler bidResourceAssembler = new BidResourceAssembler();
		return new ResponseEntity<>(bidResourceAssembler.toResources(bids),
				CREATED);
	}

	/**
	 * Once the project is closed, a scheduler runs and selects the winning bid.
	 * This can be retrieved by this API. This API will return no content when
	 * the project is still OPEN to accept bids.
	 * 
	 * @param projectId
	 * @return
	 * @throws MarketplaceApplicationException
	 */
	@RequestMapping(value = "/{projectId}/winningBid", method = GET)
	@ResponseBody
	public HttpEntity<BidOutpuResource> getWinningBidProjectId(
			@PathVariable("projectId") String projectId)
			throws MarketplaceApplicationException {

		WinningBid winningBid = bidProcessor
				.getWinningBidByProjectId(projectId);
		WinningBidResourceAssembler winningBidResourceAssembler = new WinningBidResourceAssembler();
		return new ResponseEntity<>(
				winningBidResourceAssembler.toResource(winningBid), CREATED);
	}

}
