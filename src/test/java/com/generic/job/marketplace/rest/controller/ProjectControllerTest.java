package com.generic.job.marketplace.rest.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.CollectionUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.http.HttpEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.generic.job.marketplace.TestData;
import com.generic.job.marketplace.UnitTestConfig;
import com.generic.job.marketplace.entity.Bid;
import com.generic.job.marketplace.entity.Project;
import com.generic.job.marketplace.entity.WinningBid;
import com.generic.job.marketplace.exception.BadRequestException;
import com.generic.job.marketplace.exception.MarketplaceApplicationException;
import com.generic.job.marketplace.exception.NoContentFoundException;
import com.generic.job.marketplace.processor.BidProcessor;
import com.generic.job.marketplace.processor.ProjectProcessor;
import com.generic.job.marketplace.rest.controller.ProjectController;
import com.generic.job.marketplace.rest.resource.input.BidInputResource;
import com.generic.job.marketplace.rest.resource.input.ProjectInputResource;
import com.generic.job.marketplace.rest.resource.output.BidOutpuResource;
import com.generic.job.marketplace.rest.resource.output.ProjectOutputRespurce;
import com.generic.job.marketplace.utility.DateTimeUtil;

@RunWith(SpringJUnit4ClassRunner.class)
public class ProjectControllerTest extends UnitTestConfig {
	protected static final String URI = "http://localhost:8080/v1/projects";

	@Rule
	public final ExpectedException exception = ExpectedException.none();

	@InjectMocks
	private ProjectController projectController;

	@Mock
	private ProjectProcessor projectProcessor;

	@Mock
	private BidProcessor bidProcessor;

	@Mock
	private HttpServletRequest request;

	@Before
	public void before() {
		when(request.getRequestURL()).thenReturn(new StringBuffer(URI));
		RequestContextHolder
				.setRequestAttributes(new ServletRequestAttributes(request));
	}

	@Test
	public void test_getAllProjects_call() {
		Resources<Resource<?>> resources = projectController
				.getAllProjects(request);
		assertTrue(CollectionUtils.isNotEmpty(resources.getLinks()));
		resources.getLinks()
				.forEach(resource -> assertEquals("filter", resource.getRel()));
		resources.getLinks().forEach(resource -> assertTrue(resource.getHref()
				.contains("/filter{?type=ALL/OPEN/CLOSED,projectOwnerId}")));

	}

	@SuppressWarnings("unchecked")
	@Test
	public void test_projects_filter_with_invalid_type_parameter()
			throws MarketplaceApplicationException {

		exception.expect(BadRequestException.class);
		when(projectProcessor.findAllProjectsFilter(anyString(), anyString()))
				.thenThrow(BadRequestException.class);
		projectController.searchProjects(null, "projectOwnerId");
	}

	@Test
	public void test_projects_filter_with_valid_type_parameter()
			throws MarketplaceApplicationException {

		Project project = TestData.createProject(UUID.randomUUID().toString(),
				UUID.randomUUID().toString(), DateTimeUtil.timeNowInUTC(),
				100.0);
		List<Project> projects = Arrays.asList(project);
		when(projectProcessor.findAllProjectsFilter(anyString(), anyString()))
				.thenReturn(projects);
		HttpEntity<List<ProjectOutputRespurce>> response = projectController
				.searchProjects("open", "projectOwnerId");
		assertEquals(1, response.getBody().size());
	}

	@Test
	public void test_create_project_withValid_input()
			throws MarketplaceApplicationException {

		String projectName = UUID.randomUUID().toString();
		ProjectInputResource projectInputResource = TestData
				.createProjectInputResource(projectName,
						DateTimeUtil.getUTCDateString(new Date()), 100.0);
		Project project = TestData.createProject(projectName,
				UUID.randomUUID().toString(), DateTimeUtil.timeNowInUTC(),
				100.0);
		when(projectProcessor.createProject(projectInputResource))
				.thenReturn(project);
		HttpEntity<ProjectOutputRespurce> response = projectController
				.createProject(projectInputResource);
		assertEquals(projectName, response.getBody().getProjectName());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void test_create_project_withinvalid_project_name()
			throws MarketplaceApplicationException {

		exception.expect(BadRequestException.class);
		ProjectInputResource projectInputResource = TestData
				.createProjectInputResource(null,
						DateTimeUtil.getUTCDateString(new Date()), 100.0);
		when(projectProcessor.createProject(projectInputResource))
				.thenThrow(BadRequestException.class);
		projectController.createProject(projectInputResource);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void test_get_project_by_id_with_invalid_id()
			throws MarketplaceApplicationException {

		exception.expect(BadRequestException.class);
		when(projectProcessor.findProjectById(null))
				.thenThrow(BadRequestException.class);
		projectController.getProjectById(null);
	}

	@Test
	public void test_get_project_with_valid_project_id()
			throws MarketplaceApplicationException {

		String validProjectId = "validProjectId";
		Project project = TestData.createProject(UUID.randomUUID().toString(),
				UUID.randomUUID().toString(), DateTimeUtil.timeNowInUTC(),
				100.0);
		project.setId(validProjectId);
		when(projectProcessor.findProjectById(validProjectId))
				.thenReturn(project);
		HttpEntity<ProjectOutputRespurce> response = projectController
				.getProjectById(validProjectId);
		assertEquals(validProjectId, response.getBody().getProjectId());
	}

	@Test
	public void test_create_project_bid_with_valid_project_id()
			throws MarketplaceApplicationException {

		String validProjectId = "validProjectId";
		String validBidderId = "validBidderId";
		Bid bid = TestData.createBid(1000.0, validBidderId, validProjectId);
		BidInputResource bidInputResource = new BidInputResource();
		when(bidProcessor.createBid(eq(bidInputResource), anyString()))
				.thenReturn(bid);
		HttpEntity<BidOutpuResource> response = projectController
				.createProjectBid(validProjectId, bidInputResource);
		assertEquals(validBidderId, response.getBody().getBidderId());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void test_create_project_bid_with_invalid_project_id()
			throws MarketplaceApplicationException {

		String invalidProjectId = "invalidProjectId";
		exception.expect(BadRequestException.class);
		BidInputResource bidInputResource = new BidInputResource();
		when(bidProcessor.createBid(eq(bidInputResource), anyString()))
				.thenThrow(BadRequestException.class);
		projectController.createProjectBid(invalidProjectId, bidInputResource);
	}

	@Test
	public void test_get_bid_by_bid_id_with_valid_bid_and_project_id()
			throws MarketplaceApplicationException {

		String validProjectId = "validProjectId";
		String validBidId = "validBiddId";
		Bid bid = TestData.createBid(1000.0, UUID.randomUUID().toString(),
				validProjectId);
		bid.setId(validBidId);
		when(bidProcessor.getBidByIdAndProjectId(validBidId, validProjectId))
				.thenReturn(bid);
		HttpEntity<BidOutpuResource> response = projectController
				.getBidByIdAndProjectId(validProjectId, validBidId);
		assertEquals(validBidId, response.getBody().getBidId());
	}

	@Test
	public void test_get_all_bids_for_a_valid_project_id()
			throws MarketplaceApplicationException {
		String validProjectId = "validProjectId";
		Bid bid = TestData.createBid(1000.0, UUID.randomUUID().toString(),
				validProjectId);
		Bid bid2 = TestData.createBid(1000.0, UUID.randomUUID().toString(),
				validProjectId);
		List<Bid> bids = Arrays.asList(bid, bid2);
		when(bidProcessor.getBidsByProjectId(validProjectId)).thenReturn(bids);
		HttpEntity<List<BidOutpuResource>> response = projectController
				.getAllBidsByProjectId(validProjectId);
		assertEquals(2, response.getBody().size());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void test_get_all_bids_for_a_valid_project_id_but_no_bids()
			throws MarketplaceApplicationException {
		exception.expect(NoContentFoundException.class);
		String validProjectId = "validProjectId";
		when(bidProcessor.getBidsByProjectId(validProjectId))
				.thenThrow(NoContentFoundException.class);
		projectController.getAllBidsByProjectId(validProjectId);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void test_get_winning_bid_with_no_winning_bid()
			throws MarketplaceApplicationException {
		exception.expect(NoContentFoundException.class);
		String validProjectId = "validProjectId";
		when(bidProcessor.getWinningBidByProjectId(validProjectId))
				.thenThrow(NoContentFoundException.class);
		projectController.getWinningBidProjectId(validProjectId);
	}

	@Test
	public void test_get_winning_bid_with_one_winning_bid()
			throws MarketplaceApplicationException {
		String validProjectId = "validProjectId";
		WinningBid winningBid = TestData.createWinningBid(1000, "bidderId",
				validProjectId);

		when(bidProcessor.getWinningBidByProjectId(validProjectId))
				.thenReturn(winningBid);
		HttpEntity<BidOutpuResource> response = projectController
				.getWinningBidProjectId(validProjectId);
		assertEquals("bidderId", response.getBody().getBidderId());
	}

}
