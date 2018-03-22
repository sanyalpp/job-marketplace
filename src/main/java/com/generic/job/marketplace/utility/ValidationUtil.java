package com.generic.job.marketplace.utility;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.generic.job.marketplace.entity.Person;
import com.generic.job.marketplace.exception.BadRequestException;
import com.generic.job.marketplace.logging.ErrorMessages;
import com.generic.job.marketplace.logging.MessageRetriever;
import com.generic.job.marketplace.repository.PersonRepository;

/**
 * Various common validations can be done using these utility methods.
 * 
 * @author Sanyal, Partha
 *
 */
@Component
public class ValidationUtil {

	@Autowired
	private PersonRepository personRepository;

	private ValidationUtil() {

	}

	public static final Pattern VALID_EMAIL_ADDRESS_REGEX = Pattern.compile(
			"^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$",
			Pattern.CASE_INSENSITIVE);

	/***
	 * Checks for blank property and throws a BadRequestException if blank.**
	 * 
	 * @param property
	 * @param propertyName
	 * @throws BadRequestException
	 */
	public void checkForBlank(String property, String propertyName)
			throws BadRequestException {
		if (StringUtils.isBlank(property)) {
			throw new BadRequestException(MessageRetriever.getMessage(
					ErrorMessages.NOT_NULL_OR_BLANK_MSG, propertyName));
		}
	}

	/**
	 * Validates email.
	 * 
	 * @param email
	 * @throws BadRequestException
	 */
	public void validateEmail(String email) throws BadRequestException {
		Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(email);

		if (!matcher.find()) {
			throw new BadRequestException(
					ErrorMessages.EMAIL_ADDRESS_NOT_VALID);
		}
	}

	/**
	 * Validate if the person with the given person exists.
	 * 
	 * @param personId
	 * @param typeOfPerson
	 * @throws BadRequestException
	 */
	public void validatePersonExists(String personId, String typeOfPerson)
			throws BadRequestException {
		Person person = personRepository.findById(personId);
		if (Objects.isNull(person)) {
			throw new BadRequestException(MessageRetriever.getMessage(
					ErrorMessages.NO_EXISTING_PERSON, typeOfPerson));
		}
	}
}
