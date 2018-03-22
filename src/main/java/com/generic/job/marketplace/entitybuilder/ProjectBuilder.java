package com.generic.job.marketplace.entitybuilder;

import java.util.Date;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import com.generic.job.marketplace.entity.Project;
import com.generic.job.marketplace.rest.resource.input.ProjectInputResource;
import com.generic.job.marketplace.utility.DateTimeUtil;

@Component
public class ProjectBuilder
		implements
			EntityBuilder<ProjectInputResource, Project> {

	@Override
	public Project buildFresh(ProjectInputResource inputResource) {

		Project project = new Project();
		BeanUtils.copyProperties(inputResource, project);
		Date lastDateTime = DateTimeUtil
				.getUTCDate(inputResource.getLastDateTime());
		project.setLastDateTime(lastDateTime);
		return project;
	}

}
