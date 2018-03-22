package com.generic.job.marketplace.processor;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.generic.job.marketplace.UnitTestConfig;
import com.generic.job.marketplace.entity.Bid;
import com.generic.job.marketplace.entity.WinningBid;
import com.generic.job.marketplace.entitybuilder.BidBuilder;
import com.generic.job.marketplace.exception.BadRequestException;
import com.generic.job.marketplace.exception.MarketplaceApplicationException;
import com.generic.job.marketplace.exception.NoContentFoundException;
import com.generic.job.marketplace.processor.AsyncRefreshViewProcessor;
import com.generic.job.marketplace.processor.BidProcessor;
import com.generic.job.marketplace.repository.BidRepository;
import com.generic.job.marketplace.repository.WinningBidRepository;
import com.generic.job.marketplace.validator.BidValidator;

@RunWith(SpringJUnit4ClassRunner.class)
public class BidProcessorTest extends UnitTestConfig {

	@InjectMocks
	private BidProcessor bidProcessor;

	@Mock
	private BidValidator bidValidator;

	@Mock
	private BidRepository bidRepository;

	@Mock
	private WinningBidRepository winningBidRepository;

	@Mock
	private BidBuilder bidBuilder;

	@Mock
	private AsyncRefreshViewProcessor asyncRefreshViewProcessor;

	@Rule
	public final ExpectedException exception = ExpectedException.none();

	@Test
	public void test_create_bid_with_valid_input()
			throws MarketplaceApplicationException {

		doNothing().when(bidValidator).validate(anyObject(), anyString());
		Bid bid = new Bid();
		when(bidBuilder.build(anyObject(), anyString())).thenReturn(bid);
		when(bidRepository.save(bid)).thenReturn(bid);
		doNothing().when(asyncRefreshViewProcessor).refreshMaterializedView();

		Bid bidRes = bidProcessor.createBid(null, null);
		assertTrue(bid.equals(bidRes));

		Mockito.verify(bidValidator).validate(anyObject(), anyString());
		Mockito.verify(bidBuilder).build(anyObject(), anyString());
		Mockito.verify(bidRepository).save(bid);
		Mockito.verify(asyncRefreshViewProcessor).refreshMaterializedView();
		Mockito.verifyNoMoreInteractions(bidValidator);
		Mockito.verifyNoMoreInteractions(bidBuilder);
		Mockito.verifyNoMoreInteractions(bidRepository);
		Mockito.verifyNoMoreInteractions(asyncRefreshViewProcessor);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void test_scenario_while_saving_bid_where_exception_is_thrown()
			throws MarketplaceApplicationException {
		doNothing().when(bidValidator).validate(anyObject(), anyString());
		Bid bid = new Bid();
		when(bidBuilder.build(anyObject(), anyString())).thenReturn(bid);
		when(bidRepository.save(bid)).thenThrow(JpaSystemException.class);
		exception.expect(MarketplaceApplicationException.class);

		bidProcessor.createBid(null, null);

		Mockito.verify(bidValidator).validate(anyObject(), anyString());
		Mockito.verify(bidBuilder).build(anyObject(), anyString());
		Mockito.verify(bidRepository).save(bid);
		Mockito.verifyNoMoreInteractions(bidValidator);
		Mockito.verifyNoMoreInteractions(bidBuilder);
		Mockito.verifyNoMoreInteractions(bidRepository);
	}

	@Test
	public void test_getBidByIdAndProjectId_when_there_is_a_bid_present()
			throws MarketplaceApplicationException {
		Bid bid = new Bid();
		when(bidRepository.findByIdAndProjectId(anyString(), anyString()))
				.thenReturn(bid);

		Bid resBid = bidProcessor.getBidByIdAndProjectId("bidId", "projectId");
		assertTrue(bid.equals(resBid));
		Mockito.verify(bidRepository).findByIdAndProjectId("bidId",
				"projectId");
		Mockito.verifyNoMoreInteractions(bidRepository);
	}

	@Test
	public void test_getBidByIdAndProjectId_when_a_bid_is_not_present()
			throws MarketplaceApplicationException {
		exception.expect(BadRequestException.class);
		when(bidRepository.findByIdAndProjectId(anyString(), anyString()))
				.thenReturn(null);

		bidProcessor.getBidByIdAndProjectId("bidId", "projectId");
		Mockito.verify(bidRepository).findByIdAndProjectId(anyString(),
				anyString());
		Mockito.verifyNoMoreInteractions(bidRepository);
	}

	@Test
	public void test_getBidsByProjectId_when_there_is_a_bid_present()
			throws MarketplaceApplicationException {
		List<Bid> bids = Arrays.asList(new Bid());
		when(bidRepository.findAllByProjectId(anyString())).thenReturn(bids);

		List<Bid> bidsRes = bidProcessor.getBidsByProjectId("projectId");
		assertTrue(bids.equals(bidsRes));
		Mockito.verify(bidRepository).findAllByProjectId(anyString());
		Mockito.verifyNoMoreInteractions(bidRepository);
	}

	@Test
	public void test_getBidsByProjectId_when_a_bid_is_not_present()
			throws MarketplaceApplicationException {
		exception.expect(NoContentFoundException.class);
		when(bidRepository.findAllByProjectId(anyString())).thenReturn(null);

		bidProcessor.getBidsByProjectId("projectId");
		Mockito.verify(bidRepository).findAllByProjectId(anyString());
		Mockito.verifyNoMoreInteractions(bidRepository);
	}

	@Test
	public void test_getWinningBidByProjectId_when_there_is_a_bid_present()
			throws MarketplaceApplicationException {
		WinningBid bid = new WinningBid();
		when(winningBidRepository.findByProjectId(anyString())).thenReturn(bid);

		WinningBid bidRes = bidProcessor.getWinningBidByProjectId("projectId");
		assertTrue(bid.equals(bidRes));
		Mockito.verify(winningBidRepository).findByProjectId(anyString());
		Mockito.verifyNoMoreInteractions(winningBidRepository);
	}

	@Test
	public void test_getWinningBidByProjectId_when_a_bid_is_not_present()
			throws MarketplaceApplicationException {
		exception.expect(NoContentFoundException.class);
		when(winningBidRepository.findByProjectId(anyString()))
				.thenReturn(null);

		bidProcessor.getWinningBidByProjectId("projectId");
		Mockito.verify(bidRepository).findAllByProjectId(anyString());
		Mockito.verifyNoMoreInteractions(bidRepository);
	}
}
