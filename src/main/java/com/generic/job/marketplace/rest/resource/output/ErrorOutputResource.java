package com.generic.job.marketplace.rest.resource.output;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * Error Resource, in case there are application errors.
 * 
 * @author Sanyal, Partha
 *
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Setter
@Getter
@EqualsAndHashCode
public class ErrorOutputResource {

	private String timestamp;
	private String path;
	private List<Map<String, String>> errors = new ArrayList<>();

}
