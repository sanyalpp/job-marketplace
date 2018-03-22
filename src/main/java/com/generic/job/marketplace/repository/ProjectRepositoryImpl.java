package com.generic.job.marketplace.repository;

import java.sql.Timestamp;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.generic.job.marketplace.entity.Project;
import com.generic.job.marketplace.utility.DateTimeUtil;
import com.generic.job.marketplace.utility.Filter;

public class ProjectRepositoryImpl implements ProjectRepositoryCustom {
	private static final Logger LOGGER = LoggerFactory
			.getLogger(ProjectRepositoryImpl.class);

	private static final String FILTER_BASE_QUERY = "select p.* from jobmarket.project p";

	@Autowired
	private EntityManager entityManager;

	@Autowired
	private BidViewRepository bidViewRepository;

	@SuppressWarnings("unchecked")
	@Override
	public List<Project> filterProjects(Filter type, String projectOwnerId) {

		Query query = getDynamicQuery(type, projectOwnerId);
		try {
			List<Project> projects = query.getResultList();
			// Eager fetch for the bid view doesn't work with entitymanager,
			// hence have to manually do this
			projects.forEach(project -> project.setLowestBid(
					bidViewRepository.findByProjectId(project.getId())));
			return projects;
		} catch (NoResultException e) {
			LOGGER.debug("No results found. Nothing to do here!", e);
		}
		return Collections.emptyList();
	}

	/**
	 * Creates a query dynamically based on the query parameters.
	 * 
	 * @param type
	 * @param projectOwnerId
	 * @return
	 */
	private Query getDynamicQuery(Filter type, String projectOwnerId) {
		Map<Integer, Object> positionalParameters = new HashMap<>();

		String dynamicFilterQuery = FILTER_BASE_QUERY;

		if (Objects.nonNull(type)) {

			Timestamp now = DateTimeUtil.timeNowInUTC();
			switch (type) {
				case OPEN :
					dynamicFilterQuery = checkWhereClause(dynamicFilterQuery);
					dynamicFilterQuery += "last_date_time > ?1";
					positionalParameters.put(1, now);
					break;

				case ALL :
					// this is the default case
					break;

				case CLOSED :
					dynamicFilterQuery = checkWhereClause(dynamicFilterQuery);
					dynamicFilterQuery += "last_date_time < ?1";
					positionalParameters.put(1, now);
					break;

				default :
					break;
			}
		}

		if (StringUtils.isNotBlank(projectOwnerId)) {
			dynamicFilterQuery = checkWhereClause(dynamicFilterQuery);
			dynamicFilterQuery += "project_owner_id = ?2";
			positionalParameters.put(2, projectOwnerId);
		}

		Query query = entityManager.createNativeQuery(dynamicFilterQuery,
				Project.class);
		positionalParameters.forEach(query::setParameter);
		return query;
	}

	private String checkWhereClause(String query) {
		if (query.contains("where")) {
			return query.concat(" and ");
		} else {
			return query.concat(" where ");
		}
	}

}
