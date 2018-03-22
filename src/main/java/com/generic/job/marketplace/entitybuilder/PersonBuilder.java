package com.generic.job.marketplace.entitybuilder;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import com.generic.job.marketplace.entity.Person;
import com.generic.job.marketplace.rest.resource.input.PersonInputResource;

/**
 * Helper class to create a Person entity from a Person Input Resource.
 * 
 * @author Sanyal, Partha
 *
 */
@Component
public class PersonBuilder
		implements
			EntityBuilder<PersonInputResource, Person> {

	/**
	 * Method for creating a new fresh Person entity object.
	 * 
	 * @param personInputResource
	 * @return
	 */
	public Person buildFresh(PersonInputResource personInputResource) {
		return build(personInputResource, new Person());
	}

	// private method to create a person, in future if an update person method
	// is required, this method can be internally called from a possible
	// buildForUpdate method. Refer interface @EntityBuilder
	private Person build(PersonInputResource personInputResource,
			Person person) {

		BeanUtils.copyProperties(personInputResource, person);
		return person;
	}
}
