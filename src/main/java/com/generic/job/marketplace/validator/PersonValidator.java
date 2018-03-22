package com.generic.job.marketplace.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.generic.job.marketplace.exception.BadRequestException;
import com.generic.job.marketplace.rest.resource.input.PersonInputResource;
import com.generic.job.marketplace.utility.ValidationUtil;

/**
 * Class that acts as a validator for validating a person input resource.
 * 
 * @author Sanyal, Partha
 *
 */
@Component
public class PersonValidator implements Validator<PersonInputResource> {

	@Autowired
	private ValidationUtil validationUtil;

	@Override
	public void validate(PersonInputResource personInputResource)
			throws BadRequestException {

		validationUtil.checkForBlank(personInputResource.getFirstName(),
				"First Name");
		validationUtil.checkForBlank(personInputResource.getLastName(),
				"Last Name");

		validationUtil.checkForBlank(personInputResource.getEmail(), "Email");
		validationUtil.validateEmail(personInputResource.getEmail());
	}

}
