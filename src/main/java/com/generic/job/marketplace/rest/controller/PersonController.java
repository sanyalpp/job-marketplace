package com.generic.job.marketplace.rest.controller;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.generic.job.marketplace.entity.Person;
import com.generic.job.marketplace.exception.MarketplaceApplicationException;
import com.generic.job.marketplace.processor.PersonProcessor;
import com.generic.job.marketplace.rest.assembler.PersonResourceAssembler;
import com.generic.job.marketplace.rest.resource.input.PersonInputResource;
import com.generic.job.marketplace.rest.resource.output.PersonOutputResource;

/**
 * Controller for managing persons.
 * 
 * @author Sanyal, Partha
 *
 */
@Controller
@RequestMapping(value = "/v1/persons", produces = {MediaTypes.HAL_JSON_VALUE})
public class PersonController {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(PersonController.class);

	@Autowired
	private PersonProcessor personProcessor;

	/**
	 * Get method for getting a person by Id.
	 * 
	 * @param conytactId
	 * @return
	 * @throws MarketplaceApplicationException
	 */
	@RequestMapping(value = "/{conytactId}", method = GET)
	@ResponseBody
	public HttpEntity<PersonOutputResource> getContactById(
			@PathVariable("conytactId") String conytactId)
			throws MarketplaceApplicationException {

		Person person = personProcessor.getPersonById(conytactId);

		LOGGER.debug("Retrieved Person : {}", person);
		PersonResourceAssembler personResourceAssembler = new PersonResourceAssembler();
		return new ResponseEntity<>(personResourceAssembler.toResource(person),
				OK);
	}

	/**
	 * Method to create a new person.
	 * 
	 * @param personInputResource
	 * @return
	 * @throws MarketplaceApplicationException
	 */
	@RequestMapping(method = POST)
	@ResponseBody
	public HttpEntity<PersonOutputResource> createContact(
			@RequestBody PersonInputResource personInputResource)
			throws MarketplaceApplicationException {

		Person person = personProcessor.createPerson(personInputResource);

		LOGGER.debug("Created Person : {}", person);
		PersonResourceAssembler personResourceAssembler = new PersonResourceAssembler();
		return new ResponseEntity<>(personResourceAssembler.toResource(person),
				CREATED);
	}

	/**
	 * Method to get all persons. This is specifically an Admin functionality, to
	 * get all the registered users (Buyer/Seller)
	 * 
	 * 
	 * @return
	 * @throws MarketplaceApplicationException
	 */
	@RequestMapping(method = GET)
	@ResponseBody
	public HttpEntity<List<PersonOutputResource>> getAllContacts()
			throws MarketplaceApplicationException {

		List<Person> persons = personProcessor.getAllPersons();

		LOGGER.debug("Retrieved Persons : {}", persons.size());
		PersonResourceAssembler personResourceAssembler = new PersonResourceAssembler();
		return new ResponseEntity<>(
				personResourceAssembler.toResources(persons), OK);
	}
}
