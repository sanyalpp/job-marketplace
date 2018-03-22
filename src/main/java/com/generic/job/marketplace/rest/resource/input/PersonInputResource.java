package com.generic.job.marketplace.rest.resource.input;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Sanyal, Partha
 *
 */
@Getter
@Setter
@ToString
public class PersonInputResource {

	private String firstName;
	private String lastName;
	private String email;
}
