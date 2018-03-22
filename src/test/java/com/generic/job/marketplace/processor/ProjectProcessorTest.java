package com.generic.job.marketplace.processor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.generic.job.marketplace.TestData;
import com.generic.job.marketplace.UnitTestConfig;
import com.generic.job.marketplace.entity.Project;
import com.generic.job.marketplace.entitybuilder.ProjectBuilder;
import com.generic.job.marketplace.exception.BadRequestException;
import com.generic.job.marketplace.exception.MarketplaceApplicationException;
import com.generic.job.marketplace.exception.NoContentFoundException;
import com.generic.job.marketplace.processor.ProjectProcessor;
import com.generic.job.marketplace.repository.ProjectRepository;
import com.generic.job.marketplace.rest.resource.input.ProjectInputResource;
import com.generic.job.marketplace.utility.Filter;
import com.generic.job.marketplace.validator.ProjectValidator;

@RunWith(SpringJUnit4ClassRunner.class)
public class ProjectProcessorTest extends UnitTestConfig {

	@InjectMocks
	private ProjectProcessor projectProcessor;

	@Mock
	private ProjectRepository projectRepository;

	@Mock
	private ProjectValidator projectValidator;

	@Mock
	private ProjectBuilder projectBuilder;

	@Rule
	public final ExpectedException exception = ExpectedException.none();

	@Test
	public void test_valid_save_scenario()
			throws MarketplaceApplicationException {
		doNothing().when(projectValidator).validate(anyObject());
		Project project = new Project();
		project.setMaximumBudget(10);
		when(projectBuilder.buildFresh(anyObject())).thenReturn(project);
		when(projectRepository.save(project)).thenReturn(project);
		ProjectInputResource resource = TestData
				.createProjectInputResource(null, null, 10);

		Project projectResponse = projectProcessor.createProject(resource);

		assertEquals(project, projectResponse);
		Mockito.verify(projectValidator).validate(anyObject());
		Mockito.verify(projectBuilder).buildFresh(anyObject());
		Mockito.verify(projectRepository).save(project);
		Mockito.verifyNoMoreInteractions(projectValidator);
		Mockito.verifyNoMoreInteractions(projectBuilder);
		Mockito.verifyNoMoreInteractions(projectRepository);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void test_scenario_while_saving_where_exception_is_thrown()
			throws MarketplaceApplicationException {
		doNothing().when(projectValidator).validate(anyObject());
		Project project = new Project();
		project.setMaximumBudget(10);
		when(projectBuilder.buildFresh(anyObject())).thenReturn(project);
		when(projectRepository.save(project))
				.thenThrow(JpaSystemException.class);
		ProjectInputResource resource = TestData
				.createProjectInputResource(null, null, 10);
		exception.expect(MarketplaceApplicationException.class);

		projectProcessor.createProject(resource);
	}

	@Test
	public void test_get_project_by_id_scenario_where_project_id_is_valid()
			throws MarketplaceApplicationException {
		Project project = new Project();
		project.setMaximumBudget(10);
		when(projectRepository.findById(anyString())).thenReturn(project);

		Project projectReturned = projectProcessor.findProjectById("projectId");

		assertEquals(project, projectReturned);
		Mockito.verify(projectRepository).findById("projectId");
		Mockito.verifyZeroInteractions(projectValidator);
		Mockito.verifyZeroInteractions(projectBuilder);
		Mockito.verifyNoMoreInteractions(projectRepository);
	}

	@Test
	public void test_get_project_by_id_scenario_where_project_id_is_invalid()
			throws MarketplaceApplicationException {
		when(projectRepository.findById(anyString())).thenReturn(null);
		exception.expect(BadRequestException.class);
		projectProcessor.findProjectById("projectId");
		Mockito.verify(projectRepository).findById("projectId");
		Mockito.verifyZeroInteractions(projectValidator);
		Mockito.verifyZeroInteractions(projectBuilder);
		Mockito.verifyNoMoreInteractions(projectRepository);
	}

	@Test
	public void test_filter_when_atleast_one_project_is_returned()
			throws MarketplaceApplicationException {
		String open = "OPEN";
		String projectOwnerId = "projectOwnerId";
		List<Project> projects = Arrays.asList(new Project());
		doNothing().when(projectValidator).validateFilter(open, projectOwnerId);
		when(projectRepository.filterProjects(Filter.OPEN, projectOwnerId))
				.thenReturn(projects);

		List<Project> projectsRes = projectProcessor.findAllProjectsFilter(open,
				projectOwnerId);

		assertNotNull(projectsRes);
		assertTrue(CollectionUtils.isNotEmpty(projectsRes));
		assertTrue(projects.equals(projectsRes));
		Mockito.verify(projectValidator).validateFilter(open, projectOwnerId);
		Mockito.verify(projectRepository).filterProjects(Filter.OPEN,
				projectOwnerId);
		Mockito.verifyZeroInteractions(projectBuilder);
		Mockito.verifyNoMoreInteractions(projectValidator);
		Mockito.verifyNoMoreInteractions(projectRepository);
	}

	@Test
	public void test_filter_when_no_project_is_returned()
			throws MarketplaceApplicationException {
		String open = "OPEN";
		String projectOwnerId = "projectOwnerId";
		doNothing().when(projectValidator).validateFilter(open, projectOwnerId);
		when(projectRepository.filterProjects(Filter.OPEN, projectOwnerId))
				.thenReturn(null);
		exception.expect(NoContentFoundException.class);
		projectProcessor.findAllProjectsFilter(open, projectOwnerId);

		Mockito.verify(projectValidator).validateFilter(open, projectOwnerId);
		Mockito.verify(projectRepository).filterProjects(Filter.OPEN,
				projectOwnerId);
		Mockito.verifyZeroInteractions(projectBuilder);
		Mockito.verifyNoMoreInteractions(projectValidator);
		Mockito.verifyNoMoreInteractions(projectRepository);
	}
}
