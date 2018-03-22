package com.generic.job.marketplace.processor;

import java.util.List;
import java.util.Objects;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.generic.job.marketplace.entity.Bid;
import com.generic.job.marketplace.entity.WinningBid;
import com.generic.job.marketplace.entitybuilder.BidBuilder;
import com.generic.job.marketplace.exception.BadRequestException;
import com.generic.job.marketplace.exception.MarketplaceApplicationException;
import com.generic.job.marketplace.exception.NoContentFoundException;
import com.generic.job.marketplace.logging.ErrorMessages;
import com.generic.job.marketplace.logging.MessageRetriever;
import com.generic.job.marketplace.repository.BidRepository;
import com.generic.job.marketplace.repository.WinningBidRepository;
import com.generic.job.marketplace.rest.resource.input.BidInputResource;
import com.generic.job.marketplace.validator.BidValidator;

@Component
public class BidProcessor {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(BidProcessor.class);

	@Autowired
	private BidValidator bidValidator;

	@Autowired
	private BidRepository bidRepository;

	@Autowired
	private WinningBidRepository winningBidRepository;

	@Autowired
	private BidBuilder bidBuilder;

	@Autowired
	private AsyncRefreshViewProcessor asyncRefreshViewProcessor;

	/**
	 * Method to create a bid. This method needs to be transactional as the
	 * process of creating a bid is complex, the parent project shouldn't be
	 * modified when the bid is being created. For example if the parent project
	 * is being deleted while the child bid is being created, this would be an
	 * indeterminate situation. In general, in multi-threaded environments, it
	 * is difficult to predict and simulate the order of operations.The parent
	 * entity project should be locked, before creating a child bid entity. This
	 * transaction can be handled in several ways :
	 * 
	 * 1. Spring JPA provides a way to lock on the parent Project entity and
	 * carry out the entire operation using the entityManager (this has been not
	 * implemented here).
	 * 
	 * 2. In case of multiple instances of the same service running, bid
	 * creation from different threads. Redis can be used as a mechanism to
	 * handle distributed locking. (Not implemented here)
	 * 
	 * 3. Synchronization (Java) can't be used here as that would impact the
	 * performance when multiple threads are trying to update the same contact.
	 * Other requests have to wait, before the current thread exits after
	 * processing the request. Also, java synchronization wouldn't work when
	 * there are multiple instances of the service running.
	 *
	 * @param bidInputResource
	 * @param projectId
	 * @return
	 * @throws MarketplaceApplicationException
	 */
	@Transactional
	public Bid createBid(BidInputResource bidInputResource, String projectId)
			throws MarketplaceApplicationException {

		bidValidator.validate(bidInputResource, projectId);
		Bid bid = bidBuilder.build(bidInputResource, projectId);
		Bid savedBid = null;
		try {
			savedBid = bidRepository.save(bid);
		} catch (JpaSystemException exe) {
			// handle referential integrity violation
			LOGGER.error("Error", exe);
			throw new MarketplaceApplicationException(
					MessageRetriever.getMessage(ErrorMessages.ERROR_OCCURRED,
							"creating bid", exe.getMessage()));
		}

		// refresh the materialized view asynchronously to update the lowestBid
		asyncRefreshViewProcessor.refreshMaterializedView();
		return savedBid;
	}

	/**
	 * This method gets the bid based on the bid id and the project id.
	 * 
	 * @param bidId
	 * @param projectId
	 * @return
	 * @throws MarketplaceApplicationException
	 */
	public Bid getBidByIdAndProjectId(String bidId, String projectId)
			throws MarketplaceApplicationException {

		Bid bid = bidRepository.findByIdAndProjectId(bidId, projectId);
		if (Objects.isNull(bid)) {
			throw new BadRequestException(ErrorMessages.BID_NOT_FOUND);
		}
		return bid;
	}

	/**
	 * Get all bids for the project.
	 * 
	 * @param projectId
	 * @return
	 * @throws MarketplaceApplicationException
	 */
	public List<Bid> getBidsByProjectId(String projectId)
			throws MarketplaceApplicationException {

		List<Bid> bids = bidRepository.findAllByProjectId(projectId);
		if (CollectionUtils.isEmpty(bids)) {
			throw new NoContentFoundException(
					ErrorMessages.NO_BIDS_FOR_PROJECT);
		}
		return bids;
	}

	/**
	 * Gets the winning bid details.
	 * 
	 * Note: it is expected to return just one bid, in case there is more (say
	 * the bid amount was lowest for one or more bids), a more defined strategy
	 * has to be adopted in selecting the winning bid, for example we may have
	 * to consider who bid first. This has been implemented already.
	 * 
	 * @param projectId
	 * @return
	 * @throws MarketplaceApplicationException
	 */
	public WinningBid getWinningBidByProjectId(String projectId)
			throws MarketplaceApplicationException {

		WinningBid winningBid = winningBidRepository.findByProjectId(projectId);
		if (Objects.isNull(winningBid)) {
			throw new NoContentFoundException(
					ErrorMessages.NO_WINNING_BID_FOR_PROJECT);
		}
		return winningBid;
	}
}
