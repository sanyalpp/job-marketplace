package com.generic.job.marketplace.validator;

import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.generic.job.marketplace.TestData;
import com.generic.job.marketplace.UnitTestConfig;
import com.generic.job.marketplace.exception.BadRequestException;
import com.generic.job.marketplace.exception.MarketplaceApplicationException;
import com.generic.job.marketplace.logging.ErrorMessages;
import com.generic.job.marketplace.logging.MessageRetriever;
import com.generic.job.marketplace.rest.controller.PersonController;
import com.generic.job.marketplace.rest.resource.input.PersonInputResource;
import com.generic.job.marketplace.rest.resource.input.ProjectInputResource;
import com.generic.job.marketplace.rest.resource.output.PersonOutputResource;
import com.generic.job.marketplace.utility.DateTimeUtil;
import com.generic.job.marketplace.validator.ProjectValidator;

@RunWith(SpringJUnit4ClassRunner.class)
public class ProjectValidatorTest extends UnitTestConfig {

	@Autowired
	private ProjectValidator projectValidator;

	@Autowired
	private PersonController personController;

	@Rule
	public final ExpectedException exception = ExpectedException.none();

	@Test
	public void test_project_with_null_project_owner_id()
			throws MarketplaceApplicationException {
		exception.expect(BadRequestException.class);
		exception.expectMessage(MessageRetriever.getMessage(
				ErrorMessages.NOT_NULL_OR_BLANK_MSG, "Project owner Id"));
		ProjectInputResource projectInputResource = TestData
				.createProjectInputResource(null,
						DateTimeUtil.getUTCDateString(new Date()), 1000.0);
		projectValidator.validate(projectInputResource);
	}

	@Test
	public void test_project_with_Max_budget_less_than_zero()
			throws MarketplaceApplicationException {
		exception.expect(BadRequestException.class);
		exception.expectMessage(ErrorMessages.MAX_BUDGET_LESS_THAN_ZERO);
		ProjectInputResource projectInputResource = TestData
				.createProjectInputResource("projectownerId",
						DateTimeUtil.getUTCDateString(new Date()), -1000.0);
		projectValidator.validate(projectInputResource);
	}

	@Test
	public void test_project_with_last_date_in_past()
			throws MarketplaceApplicationException, InterruptedException {
		exception.expect(BadRequestException.class);
		exception.expectMessage(ErrorMessages.LAST_DATE_TIME_NOT_VALID);
		ProjectInputResource projectInputResource = TestData
				.createProjectInputResource("projectownerId",
						DateTimeUtil.getUTCDateString(new Date()), 1000.0);
		Thread.sleep(1000);
		projectValidator.validate(projectInputResource);
	}

	@Test
	public void test_project_with_last_date_invalid()
			throws MarketplaceApplicationException {
		exception.expect(BadRequestException.class);
		exception.expectMessage(ErrorMessages.LAST_DATE_TIME_NOT_VALID);
		ProjectInputResource projectInputResource = TestData
				.createProjectInputResource("projectownerId", "invaliddate",
						1000.0);
		projectValidator.validate(projectInputResource);
	}

	@Test
	public void test_project_with_last_date_null()
			throws MarketplaceApplicationException {
		exception.expect(BadRequestException.class);
		exception.expectMessage(ErrorMessages.LAST_DATE_TIME_NOT_VALID);
		ProjectInputResource projectInputResource = TestData
				.createProjectInputResource("projectownerId", null, 1000.0);
		projectValidator.validate(projectInputResource);
	}

	@Test
	public void test_project_with_invalid_project_owner_id()
			throws MarketplaceApplicationException, InterruptedException {
		exception.expect(BadRequestException.class);
		exception.expectMessage(MessageRetriever
				.getMessage(ErrorMessages.NO_EXISTING_PERSON, "Project owner"));
		ProjectInputResource projectInputResource = TestData
				.createProjectInputResource("projectownerId",
						DateTimeUtil.getUTCDateString(DateTimeUtil
								.subtractTime(-5, ChronoUnit.SECONDS)),
						1000.0);
		projectValidator.validate(projectInputResource);
	}

	@Test
	public void test_project_with_all_valid_parameters() {

		try {
			// Create a valid person
			PersonInputResource personInputResource = TestData.createPerson(
					UUID.randomUUID().toString(), UUID.randomUUID().toString(),
					UUID.randomUUID().toString() + "email@email.com");

			HttpEntity<PersonOutputResource> response = personController
					.createContact(personInputResource);
			String personId = response.getBody().getPersonId();

			ProjectInputResource projectInputResource = TestData
					.createProjectInputResource(personId,
							DateTimeUtil.getUTCDateString(DateTimeUtil
									.subtractTime(-5, ChronoUnit.SECONDS)),
							1000.0);
			projectValidator.validate(projectInputResource);
		} catch (Exception exe) {
			Assert.fail();
		}
	}

	@Test
	public void test_with_invalid_filter_type()
			throws MarketplaceApplicationException {
		exception.expect(BadRequestException.class);
		exception.expectMessage(ErrorMessages.FILTER_TYPE_NOT_VALID);
		projectValidator.validateFilter("invalidFilter", null);
	}

	@Test
	public void test_with_null_filter_type_and_project_owner_id()
			throws MarketplaceApplicationException {
		exception.expect(BadRequestException.class);
		exception.expectMessage(ErrorMessages.FILTER_PARAMS_NOT_VALID);
		projectValidator.validateFilter(null, null);
	}

	@Test
	public void test_with_valid_filter_type()
			throws MarketplaceApplicationException {
		try {
			projectValidator.validateFilter("open", null);
		} catch (Exception exe) {
			Assert.fail();
		}

	}

}
