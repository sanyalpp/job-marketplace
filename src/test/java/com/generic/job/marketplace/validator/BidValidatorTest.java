package com.generic.job.marketplace.validator;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import java.time.temporal.ChronoUnit;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.generic.job.marketplace.TestData;
import com.generic.job.marketplace.UnitTestConfig;
import com.generic.job.marketplace.entity.Project;
import com.generic.job.marketplace.exception.BadRequestException;
import com.generic.job.marketplace.exception.MarketplaceApplicationException;
import com.generic.job.marketplace.logging.ErrorMessages;
import com.generic.job.marketplace.repository.ProjectRepository;
import com.generic.job.marketplace.rest.resource.input.BidInputResource;
import com.generic.job.marketplace.utility.DateTimeUtil;
import com.generic.job.marketplace.utility.ValidationUtil;
import com.generic.job.marketplace.validator.BidValidator;

@RunWith(SpringJUnit4ClassRunner.class)
public class BidValidatorTest extends UnitTestConfig {

	@InjectMocks
	private BidValidator bidValidator;

	@Mock
	private ValidationUtil validationUtil;

	@Mock
	private ProjectRepository projectRepository;

	@Rule
	public final ExpectedException exception = ExpectedException.none();

	@Test
	public void test_bid_with_non_existant_bidder()
			throws MarketplaceApplicationException {
		exception.expect(BadRequestException.class);
		BidInputResource bidInputResource = TestData
				.createBidInputResource(1000, "invalidBidder");
		doThrow(BadRequestException.class).when(validationUtil)
				.validatePersonExists("invalidBidder", "Bidder");
		bidValidator.validate(bidInputResource);
	}

	@Test
	public void test_bid_with_non_bid_amount_less_than_zero()
			throws MarketplaceApplicationException {
		exception.expect(BadRequestException.class);
		exception.expectMessage(ErrorMessages.BID_AMOUNT_LESS_THAN_ZERO);
		BidInputResource bidInputResource = TestData
				.createBidInputResource(-1000, "validBidder");
		doNothing().when(validationUtil).validatePersonExists(anyString(),
				anyString());
		bidValidator.validate(bidInputResource);
	}

	@Test
	public void test_with_valid_user_and_bid_amount()
			throws MarketplaceApplicationException {
		BidInputResource bidInputResource = TestData
				.createBidInputResource(1000, "validBidder");
		doNothing().when(validationUtil).validatePersonExists(anyString(),
				anyString());
		try {
			bidValidator.validate(bidInputResource);
			Mockito.verify(validationUtil).validatePersonExists(anyString(),
					anyString());;
			Mockito.verifyNoMoreInteractions(validationUtil);
			Mockito.verifyNoMoreInteractions(projectRepository);
		} catch (Exception exe) {
			Assert.fail("shouldn't have thrown this exception");
		}
	}

	@Test
	public void test_with_invalid_project_id()
			throws MarketplaceApplicationException {
		exception.expect(BadRequestException.class);
		exception.expectMessage(ErrorMessages.PROJECT_NOT_FOUND);
		BidInputResource bidInputResource = TestData
				.createBidInputResource(1000, "validBidder");
		doNothing().when(validationUtil).validatePersonExists(anyString(),
				anyString());
		when(projectRepository.findById("invalidProjectId")).thenReturn(null);
		bidValidator.validate(bidInputResource, "invalidProject");
	}

	@Test
	public void test_with_closed_for_bidding_project()
			throws MarketplaceApplicationException, InterruptedException {
		exception.expect(BadRequestException.class);
		exception.expectMessage(ErrorMessages.BIDDING_CLOSED);
		BidInputResource bidInputResource = TestData
				.createBidInputResource(1000, "validBidder");
		doNothing().when(validationUtil).validatePersonExists(anyString(),
				anyString());
		Project project = TestData.createProject("projectName",
				"projectOwnerId", DateTimeUtil.timeNowInUTC(), 1000.0);
		Thread.sleep(1000);
		when(projectRepository.findById(anyString())).thenReturn(project);
		bidValidator.validate(bidInputResource, "validProject");
	}

	@Test
	public void test_with_bid_amount_more_than_project_max_budget()
			throws MarketplaceApplicationException, InterruptedException {
		exception.expect(BadRequestException.class);
		exception.expectMessage(
				ErrorMessages.BIDDING_AMOUNT_MORE_THAN_MAX_BUDGET);
		BidInputResource bidInputResource = TestData
				.createBidInputResource(1000.0, "validBidder");
		doNothing().when(validationUtil).validatePersonExists(anyString(),
				anyString());
		Project project = TestData.createProject("projectName",
				"projectOwnerId",
				DateTimeUtil.subtractTime(-5, ChronoUnit.SECONDS), 100.0);
		Thread.sleep(1000);
		when(projectRepository.findById(anyString())).thenReturn(project);
		bidValidator.validate(bidInputResource, "validProject");
	}

	@Test
	public void test_with_bidder_same_as_owner()
			throws MarketplaceApplicationException, InterruptedException {
		exception.expect(BadRequestException.class);
		exception.expectMessage(ErrorMessages.OWNER_CANT_BE_BIDDER);
		BidInputResource bidInputResource = TestData
				.createBidInputResource(900.0, "validBidder");
		doNothing().when(validationUtil).validatePersonExists(anyString(),
				anyString());
		Project project = TestData.createProject("projectName", "validBidder",
				DateTimeUtil.subtractTime(-5, ChronoUnit.SECONDS), 1000.0);
		Thread.sleep(1000);
		when(projectRepository.findById(anyString())).thenReturn(project);
		bidValidator.validate(bidInputResource, "validProject");
	}

	@Test
	public void test_with_all_valid_parameters()
			throws MarketplaceApplicationException, InterruptedException {
		BidInputResource bidInputResource = TestData
				.createBidInputResource(900.0, "validBidder");
		doNothing().when(validationUtil).validatePersonExists(anyString(),
				anyString());
		Project project = TestData.createProject("projectName", "validOwner",
				DateTimeUtil.subtractTime(-5, ChronoUnit.SECONDS), 1000.0);
		Thread.sleep(1000);
		when(projectRepository.findById(anyString())).thenReturn(project);
		try {
			bidValidator.validate(bidInputResource, "validProject");
			Mockito.verify(validationUtil).validatePersonExists(anyString(),
					anyString());;
			Mockito.verify(projectRepository).findById("validProject");
			Mockito.verifyNoMoreInteractions(validationUtil);
			Mockito.verifyNoMoreInteractions(projectRepository);
		} catch (Exception exe) {
			Assert.fail("Shouldnt have come here");
		}

	}

}
