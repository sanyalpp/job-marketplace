package com.generic.job.marketplace.processor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.generic.job.marketplace.repository.BidViewRepository;

@Component
public class AsyncRefreshViewProcessor {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(AsyncRefreshViewProcessor.class);

	@Autowired
	private BidViewRepository bidViewRepository;

	/**
	 * The view has to be refreshed asynchronously because, we don't want the
	 * main thread to wait when it is getting refreshed. The main thread should
	 * return immediately.
	 * 
	 * Concurrent refresh of views anyway guarantees that the threads wanting to
	 * read data from the view are not blocked.
	 */
	@Async
	public void refreshMaterializedView() {
		bidViewRepository.refreshMaterializedView();
		LOGGER.info(
				"Initiated the process of refreshing the view concurrently.");
	}

}
