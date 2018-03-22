package com.generic.job.marketplace.processor;

import java.lang.reflect.InvocationTargetException;
import java.sql.Timestamp;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.generic.job.marketplace.entity.LowestBid;
import com.generic.job.marketplace.entity.Project;
import com.generic.job.marketplace.entity.WinningBid;
import com.generic.job.marketplace.repository.BidViewRepository;
import com.generic.job.marketplace.repository.ProjectRepository;
import com.generic.job.marketplace.repository.WinningBidRepository;
import com.generic.job.marketplace.utility.DateTimeUtil;

/**
 * This class runs a background cronjob to select the winning bids and populate
 * the WinningBid table.
 * 
 * NOTE : This is just a demonstration of a cronjob that is required to support
 * the idea of winning bids. In a real production grade software application,
 * cronjobs should never be tied up with an application itself. There should be
 * a separate job scheduler and also fallback/retry mechanism if the
 * jobProcessor fails to process records because of whatsoever reason.
 * 
 * Also, a separate strategy has be to be deviced in case there are more than
 * one bid having the same lowest bid amount, may be select based on the
 * earliest bid time. This case of contention resolution has also been
 * implemented.
 * 
 * @author Sanyal, Partha
 *
 */
@Component
@EnableScheduling
public class WinningBidSelectionScheduler {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(WinningBidSelectionScheduler.class);

	@Autowired
	private ProjectRepository projectRepository;

	@Autowired
	private BidViewRepository bidViewRepository;

	@Autowired
	private WinningBidRepository winningBidRepository;

	/**
	 * After the Project is past its lastDateTime, the Bid acceptance process
	 * for the project is closed already. This is guaranteed via the bid
	 * creation API. The winning bid is decided based on the lowest bid amount
	 * received at the close of the Project.
	 * 
	 * The scheduler runs every 5 minutes, and processes projects within a delay
	 * of 5 minutes in the past. For example, if it runs at say 10:30p, it will
	 * process all the projects that got closed between 10:20p and 10:25p, to
	 * select the winning bid.
	 * 
	 * All these values (the rate of run, time gap etc) can be made
	 * configurable.
	 * 
	 */
	@Scheduled(fixedRate = 300000)
	public void chooseWinningBid() {
		LOGGER.info("Winning Bid Selection Scheduler kicked off!");

		Timestamp nowMinusRemoteDelayUnit = DateTimeUtil.subtractTime(10,
				ChronoUnit.MINUTES);
		Timestamp nowMinusOneUnit = DateTimeUtil.subtractTime(5,
				ChronoUnit.MINUTES);

		// Get all the projects that are closed within the last two minutes.
		List<Project> projects = projectRepository
				.findProjectsBetween(nowMinusRemoteDelayUnit, nowMinusOneUnit);

		if (CollectionUtils.isNotEmpty(projects)) {
			// extract the projectIds
			Set<String> projectIds = projects.stream().map(Project::getId)
					.collect(Collectors.toSet());

			// use the project ids to get the respective lowest bid for each
			// project from the view
			List<LowestBid> lowestBids = bidViewRepository
					.findAllByProjectSet(projectIds);

			if (CollectionUtils.isNotEmpty(lowestBids)) {
				List<WinningBid> listOfwinningBids = new ArrayList<>();

				for (LowestBid lowestBid : lowestBids) {
					WinningBid winningBid = new WinningBid();
					try {
						BeanUtils.copyProperties(winningBid, lowestBid);
						winningBid.setProjectId(lowestBid.getProject().getId());
					} catch (IllegalAccessException
							| InvocationTargetException e) {
						LOGGER.error("Unable to copy", e);
					}
					listOfwinningBids.add(winningBid);
				}

				// finally save everything to the winning bid repository
				winningBidRepository.save(listOfwinningBids);
			}

		}
	}

}
