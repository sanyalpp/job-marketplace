package com.generic.job.marketplace.repository;

import java.util.List;

import com.generic.job.marketplace.entity.Project;
import com.generic.job.marketplace.utility.Filter;

public interface ProjectRepositoryCustom {

	List<Project> filterProjects(Filter type, String projectOwnerId);
}
