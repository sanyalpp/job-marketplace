package com.generic.job.marketplace.rest.resource.output;

import org.springframework.hateoas.ResourceSupport;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Sanyal, Partha
 *
 */
@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
@ToString
public class PersonOutputResource extends ResourceSupport {

	private String personId;
	private String firstName;
	private String lastName;
	private String email;
}
