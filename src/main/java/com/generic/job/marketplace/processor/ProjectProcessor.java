package com.generic.job.marketplace.processor;

import java.util.List;
import java.util.Locale;
import java.util.Objects;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.stereotype.Component;

import com.generic.job.marketplace.entity.Project;
import com.generic.job.marketplace.entitybuilder.ProjectBuilder;
import com.generic.job.marketplace.exception.BadRequestException;
import com.generic.job.marketplace.exception.MarketplaceApplicationException;
import com.generic.job.marketplace.exception.NoContentFoundException;
import com.generic.job.marketplace.logging.ErrorMessages;
import com.generic.job.marketplace.logging.MessageRetriever;
import com.generic.job.marketplace.repository.ProjectRepository;
import com.generic.job.marketplace.rest.resource.input.ProjectInputResource;
import com.generic.job.marketplace.utility.Filter;
import com.generic.job.marketplace.validator.ProjectValidator;

/**
 * Does all processing related to Projects.
 * 
 * @author Sanyal, Partha
 *
 */
@Component
public class ProjectProcessor {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(ProjectProcessor.class);

	@Autowired
	private ProjectRepository projectRepository;

	@Autowired
	private ProjectValidator projectValidator;

	@Autowired
	private ProjectBuilder projectBuilder;

	/**
	 * This method creates a new Project. At this time there is no unique
	 * constraint, there is no limit as to how many projects a single owner can
	 * create.
	 * 
	 * @param projectInputResource
	 * @return
	 * @throws MarketplaceApplicationException
	 */
	public Project createProject(ProjectInputResource projectInputResource)
			throws MarketplaceApplicationException {

		projectValidator.validate(projectInputResource);
		Project project = projectBuilder.buildFresh(projectInputResource);
		try {
			return projectRepository.save(project);
		} catch (JpaSystemException exe) {
			// handle referential integrity violation
			LOGGER.error("Error", exe);
			throw new MarketplaceApplicationException(
					MessageRetriever.getMessage(ErrorMessages.ERROR_OCCURRED,
							"creating project", exe.getMessage()));
		}

	}

	/**
	 * This method finds one project based on the projectId passed in.
	 * NoContentFoundException thrown if no project is found.
	 * 
	 * @param projectId
	 * @return
	 * @throws MarketplaceApplicationException
	 */
	public Project findProjectById(String projectId)
			throws MarketplaceApplicationException {
		Project project = projectRepository.findById(projectId);
		if (Objects.isNull(project)) {
			throw new BadRequestException(
					"No project found with id " + projectId);
		}
		return project;
	}

	/**
	 * This method filters projects based on Filter type ALL/OPEN/CLOSED, and
	 * projectOwnerId. Other parameters can be supported as well later one, for
	 * example if a date range is provided.
	 * 
	 * @param type
	 * @param projectOwnerId
	 * @return
	 * @throws MarketplaceApplicationException
	 */
	public List<Project> findAllProjectsFilter(String type,
			String projectOwnerId) throws MarketplaceApplicationException {
		projectValidator.validateFilter(type, projectOwnerId);

		Filter filter = StringUtils.isEmpty(type)
				? null
				: Filter.valueOf(type.toUpperCase(Locale.US));

		List<Project> projects = projectRepository.filterProjects(filter,
				projectOwnerId);
		if (CollectionUtils.isEmpty(projects)) {
			throw new NoContentFoundException(ErrorMessages.PROJECTS_NOT_FOUND);
		}
		return projects;
	}

}
