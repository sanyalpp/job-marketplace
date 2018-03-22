package com.generic.job.marketplace.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.generic.job.marketplace.entity.Bid;

public interface BidRepository extends JpaRepository<Bid, String> {

	Bid findByIdAndProjectId(@Param("id") String id,
			@Param("projectId") String projectId);

	List<Bid> findAllByProjectId(@Param("projectId") String projectId);

	@Query(value = "SELECT bid FROM Bid bid where bid.id IN :bidIds")
	List<Bid> findAllByBidIds(@Param("bidIds") Set<String> bidIds);
}
