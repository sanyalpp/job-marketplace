package com.generic.job.marketplace.processor;

import java.util.List;
import java.util.Objects;

import org.apache.commons.collections.CollectionUtils;
import org.apache.openjpa.persistence.EntityExistsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.stereotype.Component;

import com.generic.job.marketplace.entity.Person;
import com.generic.job.marketplace.entitybuilder.PersonBuilder;
import com.generic.job.marketplace.exception.BadRequestException;
import com.generic.job.marketplace.exception.DuplicateEntityException;
import com.generic.job.marketplace.exception.MarketplaceApplicationException;
import com.generic.job.marketplace.exception.NoContentFoundException;
import com.generic.job.marketplace.logging.ErrorMessages;
import com.generic.job.marketplace.logging.MessageRetriever;
import com.generic.job.marketplace.repository.PersonRepository;
import com.generic.job.marketplace.rest.resource.input.PersonInputResource;
import com.generic.job.marketplace.validator.PersonValidator;

/**
 * This class processes all requests to create/update/get/delete Person. It
 * interacts with the database via the Spring data repository.
 * 
 * @author Sanyal, Partha
 *
 */
@Component
public class PersonProcessor {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(PersonProcessor.class);

	@Autowired
	private PersonRepository personRepository;

	@Autowired
	private PersonValidator personValidator;

	@Autowired
	private PersonBuilder personBuilder;

	/**
	 * Method to create a person.
	 * 
	 * @param personInputResource
	 * @return
	 * @throws MarketplaceApplicationException
	 */
	public Person createPerson(PersonInputResource personInputResource)
			throws MarketplaceApplicationException {

		personValidator.validate(personInputResource);
		Person person = personBuilder.buildFresh(personInputResource);
		return save(person, "creating");
	}

	/**
	 * Get the list of registered users.
	 * 
	 * @return List<Person>
	 * @throws MarketplaceApplicationException
	 */
	public List<Person> getAllPersons() throws MarketplaceApplicationException {
		List<Person> persons = personRepository.findAll();
		if (CollectionUtils.isEmpty(persons)) {
			throw new NoContentFoundException(
					ErrorMessages.NO_EXISTING_PERSON);
		}
		return persons;
	}

	/**
	 * Returns a person by searching on the passed in personId
	 * 
	 * @param personId
	 * @return
	 * @throws MarketplaceApplicationException
	 */
	public Person getPersonById(String personId)
			throws MarketplaceApplicationException {
		Person person = personRepository.findById(personId);

		if (Objects.isNull(person)) {
			throw new BadRequestException(MessageRetriever
					.getMessage(ErrorMessages.NO_EXISTING_PERSON, "person"));
		}
		return person;
	}

	/**
	 * Private method to handle adding/updating an existing Person. In case of
	 * duplicate person DuplicateEntityException is thrown.
	 * 
	 * @param person,
	 *            action
	 * @return
	 * @throws MarketplaceApplicationException
	 */
	private Person save(Person person, String action)
			throws MarketplaceApplicationException {
		// persist the person
		try {
			return personRepository.save(person);
		} catch (JpaSystemException | DataIntegrityViolationException exe) {
			// This exception is thrown when there is a unique constraint
			// violation. In the application there
			// is a unique constraint on customer-name and user-name.
			if (exe.getCause().getCause().getClass()
					.equals(EntityExistsException.class)
					|| exe.getCause().getCause().getClass()
							.equals(DataIntegrityViolationException.class)) {
				LOGGER.error(ErrorMessages.DUPLICATE_PERSON, exe);
				throw new DuplicateEntityException(MessageRetriever
						.getMessage(ErrorMessages.DUPLICATE_PERSON), exe);
			} else {
				LOGGER.error(MessageRetriever.getMessage(
						ErrorMessages.PERSON_CREATE_UPDATE_ERROR, action,
						exe.getMessage()), exe);
				throw new MarketplaceApplicationException(MessageRetriever
						.getMessage(ErrorMessages.PERSON_CREATE_UPDATE_ERROR,
								action, exe.getMessage()),
						exe);
			}

		}
	}

}
