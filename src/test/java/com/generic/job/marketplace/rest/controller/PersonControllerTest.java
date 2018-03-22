package com.generic.job.marketplace.rest.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.UUID;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.generic.job.marketplace.TestData;
import com.generic.job.marketplace.UnitTestConfig;
import com.generic.job.marketplace.exception.BadRequestException;
import com.generic.job.marketplace.exception.DuplicateEntityException;
import com.generic.job.marketplace.exception.MarketplaceApplicationException;
import com.generic.job.marketplace.exception.NoContentFoundException;
import com.generic.job.marketplace.repository.PersonRepository;
import com.generic.job.marketplace.rest.controller.PersonController;
import com.generic.job.marketplace.rest.resource.input.PersonInputResource;
import com.generic.job.marketplace.rest.resource.output.PersonOutputResource;

/**
 * This is a mix of integration and junit test. It's here to demo that with a
 * in-memory database, complete integration tests can be written as well, using
 * junit framework.
 * 
 * @author Sanyal, Partha
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
public class PersonControllerTest extends UnitTestConfig {

	@Autowired
	private PersonController personController;

	@Autowired
	private PersonRepository personRepository;

	@Rule
	public final ExpectedException exception = ExpectedException.none();

	@Before
	public void setUp() {
		// clear existing data
		personRepository.deleteAll();
	}

	@Test
	public void test_get_with_no_existingContact() throws Exception {
		mockMvc.perform(get("/v1/persons/no-existing-contact-id")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaTypes.HAL_JSON_VALUE))
				.andExpect(status().is4xxClientError()).andReturn();
	}

	@Test
	public void test_valid_person_creation()
			throws MarketplaceApplicationException {
		PersonInputResource personInputResource = TestData.createPerson(
				UUID.randomUUID().toString(), UUID.randomUUID().toString(),
				UUID.randomUUID().toString() + "email@email.com");

		HttpEntity<PersonOutputResource> response = personController
				.createContact(personInputResource);
		assertPersonObjectFields(response.getBody(), personInputResource);

	}

	@Test
	public void test_contact_creation_with_nUll_firstName()
			throws MarketplaceApplicationException {
		exception.expect(BadRequestException.class);
		PersonInputResource personInputResource = TestData.createPerson(null,
				UUID.randomUUID().toString(),
				UUID.randomUUID().toString() + "email@email.com");

		personController.createContact(personInputResource);

	}

	@Test
	public void test_contact_creation_with_nUll_email()
			throws MarketplaceApplicationException {
		exception.expect(BadRequestException.class);
		PersonInputResource personInputResource = TestData.createPerson(
				UUID.randomUUID().toString(), UUID.randomUUID().toString(),
				null);

		personController.createContact(personInputResource);

	}

	@Test
	public void test_contact_creation_with_incorrect_email()
			throws MarketplaceApplicationException {
		exception.expect(BadRequestException.class);
		PersonInputResource personInputResource = TestData.createPerson(
				UUID.randomUUID().toString(), UUID.randomUUID().toString(),
				UUID.randomUUID().toString() + "email-email.com");

		personController.createContact(personInputResource);

	}

	@Test
	public void test_duplicate_person_creation()
			throws MarketplaceApplicationException {
		exception.expect(DuplicateEntityException.class);
		String email = UUID.randomUUID().toString() + "email@email.com";
		PersonInputResource personInputResource = TestData.createPerson(
				UUID.randomUUID().toString(), UUID.randomUUID().toString(),
				email);

		HttpEntity<PersonOutputResource> response = personController
				.createContact(personInputResource);
		assertPersonObjectFields(response.getBody(), personInputResource);
		personController.createContact(personInputResource);

	}

	@Test
	public void test_valid_person_get_by_id()
			throws MarketplaceApplicationException {
		PersonInputResource personInputResource = TestData.createPerson(
				UUID.randomUUID().toString(), UUID.randomUUID().toString(),
				UUID.randomUUID().toString() + "email@email.com");

		HttpEntity<PersonOutputResource> response = personController
				.createContact(personInputResource);
		assertPersonObjectFields(response.getBody(), personInputResource);

		HttpEntity<PersonOutputResource> response2 = personController
				.getContactById(response.getBody().getPersonId());

		assertPersonObjectFields(response2.getBody(), personInputResource);

	}

	@Test
	public void test_valid_person_get_by_invalid_id()
			throws MarketplaceApplicationException {
		exception.expect(BadRequestException.class);
		PersonInputResource personInputResource = TestData.createPerson(
				UUID.randomUUID().toString(), UUID.randomUUID().toString(),
				UUID.randomUUID().toString() + "email@email.com");

		HttpEntity<PersonOutputResource> response = personController
				.createContact(personInputResource);
		assertPersonObjectFields(response.getBody(), personInputResource);

		personController
				.getContactById(response.getBody().getPersonId() + "invalid");

	}

	@Test
	public void test_get_all_users() throws MarketplaceApplicationException {
		PersonInputResource personInputResource = TestData.createPerson(
				UUID.randomUUID().toString(), UUID.randomUUID().toString(),
				UUID.randomUUID().toString() + "email@email.com");

		HttpEntity<PersonOutputResource> response = personController
				.createContact(personInputResource);
		assertPersonObjectFields(response.getBody(), personInputResource);

		HttpEntity<List<PersonOutputResource>> response2 = personController
				.getAllContacts();

		assertTrue(response2.getBody().size() > 0);
		assertPersonObjectFields(response2.getBody().get(0),
				personInputResource);
	}

	@Test
	public void test_get_all_users_with_no_users()
			throws MarketplaceApplicationException {
		exception.expect(NoContentFoundException.class);
		personController.getAllContacts();

	}

	////////////////////////// ------------Private Helper methods
	////////////////////////// --------------//////////////////////////
	private void assertPersonObjectFields(PersonOutputResource output,
			PersonInputResource input) {

		assertEquals(input.getFirstName(), output.getFirstName());
		assertEquals(input.getEmail(), output.getEmail());
		assertEquals(input.getLastName(), output.getLastName());

	}

}
