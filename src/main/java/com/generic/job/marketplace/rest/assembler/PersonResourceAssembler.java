package com.generic.job.marketplace.rest.assembler;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;

import com.generic.job.marketplace.entity.Person;
import com.generic.job.marketplace.logging.ErrorMessages;
import com.generic.job.marketplace.logging.MessageRetriever;
import com.generic.job.marketplace.rest.controller.PersonController;
import com.generic.job.marketplace.rest.resource.output.PersonOutputResource;

/**
 * Creates the person output resource from the Person entity and also adds
 * HATEOAS self links to Person resource.
 * 
 * @author Sanyal, Partha
 *
 */
public class PersonResourceAssembler
		extends
			ResourceAssemblerSupport<Person, PersonOutputResource> {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(PersonResourceAssembler.class);

	public PersonResourceAssembler() {
		super(Person.class, PersonOutputResource.class);

	}

	@Override
	public PersonOutputResource toResource(Person person) {

		PersonOutputResource resource = null;
		try {
			resource = instantiateResource(person);
			resource.setPersonId(person.getId());
			BeanUtils.copyProperties(person, resource);

			Link selfLink = linkTo(methodOn(PersonController.class)
					.getContactById(person.getId())).withRel("self");
			Link otherUsers = linkTo(
					methodOn(PersonController.class).getAllContacts())
							.withRel("allUsers");
			resource.add(selfLink);
			resource.add(otherUsers);
		} catch (Exception exe) {
			String errMsg = MessageRetriever.getMessage(
					ErrorMessages.ERROR_OCCURRED,
					"creating PersonOutputResource", exe.getCause());
			LOGGER.error(errMsg);
		}
		return resource;
	}

	@Override
	public List<PersonOutputResource> toResources(
			Iterable<? extends Person> persons) {
		List<PersonOutputResource> outputResources = new ArrayList<>();
		for (Person person : persons) {
			outputResources.add(this.toResource(person));
		}
		return outputResources;
	}
}
