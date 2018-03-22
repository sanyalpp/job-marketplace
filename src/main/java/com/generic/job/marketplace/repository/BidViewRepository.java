package com.generic.job.marketplace.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.generic.job.marketplace.entity.LowestBid;

public interface BidViewRepository extends JpaRepository<LowestBid, String> {

	@Query(value = "SELECT jobmarket.refresh_materialized_view()", nativeQuery = true)
	void refreshMaterializedView();

	LowestBid findByProjectId(@Param("projectId") String projectId);

	@Query(value = "SELECT lbv FROM LowestBid lbv where lbv.project.id IN :projectIds")
	List<LowestBid> findAllByProjectSet(
			@Param("projectIds") Set<String> projectIds);
}
