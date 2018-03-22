package com.generic.job.marketplace.repository;

import java.sql.Timestamp;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.generic.job.marketplace.entity.Project;

public interface ProjectRepository
		extends
			JpaRepository<Project, String>,
			ProjectRepositoryCustom {

	Project findById(@Param("id") String projectId);

	@Query(value = "SELECT p.* from JOBMARKET.project p" + " WHERE"
			+ " p.last_date_time " + " BETWEEN ?1 AND ?2", nativeQuery = true)
	List<Project> findProjectsBetween(Timestamp pastTimeStampByTwoUnit,
			Timestamp pastDateByOneUnit);
}
