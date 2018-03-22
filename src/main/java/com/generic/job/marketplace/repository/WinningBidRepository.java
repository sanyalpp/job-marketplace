package com.generic.job.marketplace.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import com.generic.job.marketplace.entity.WinningBid;

public interface WinningBidRepository
		extends
			JpaRepository<WinningBid, String> {

	WinningBid findByProjectId(@Param("projectId") String projectId);
}
