package com.generic.job.marketplace.processor;

import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.generic.job.marketplace.UnitTestConfig;
import com.generic.job.marketplace.entity.LowestBid;
import com.generic.job.marketplace.entity.Project;
import com.generic.job.marketplace.entity.WinningBid;
import com.generic.job.marketplace.processor.WinningBidSelectionScheduler;
import com.generic.job.marketplace.repository.BidViewRepository;
import com.generic.job.marketplace.repository.ProjectRepository;
import com.generic.job.marketplace.repository.WinningBidRepository;

@RunWith(SpringJUnit4ClassRunner.class)
public class WinningBidSelectionSchedulerTest extends UnitTestConfig {

	@InjectMocks
	private WinningBidSelectionScheduler winningBidSelectionScheduler;

	@Mock
	private ProjectRepository projectRepository;

	@Mock
	private BidViewRepository bidViewRepository;

	@Mock
	private WinningBidRepository winningBidRepository;

	@SuppressWarnings("unchecked")
	@Test
	public void test_one_positive_bid_selection_scenario() {

		Project project = new Project();
		project.setId(UUID.randomUUID().toString());
		List<Project> projects = Arrays.asList(project);

		when(projectRepository.findProjectsBetween(anyObject(), anyObject()))
				.thenReturn(projects);
		Set<String> projectIds = new HashSet<>();
		projectIds.add(project.getId());

		LowestBid lowestBid = new LowestBid();
		lowestBid.setId(UUID.randomUUID().toString());
		lowestBid.setProject(project);
		List<LowestBid> lowestBids = Arrays.asList(lowestBid);

		when(bidViewRepository.findAllByProjectSet(projectIds))
				.thenReturn(lowestBids);

		WinningBid winningBid = new WinningBid();
		winningBid.setId(lowestBid.getId());
		List<WinningBid> winningBidsList = Arrays.asList(winningBid);
		when(winningBidRepository.save(anyList())).thenReturn(winningBidsList);

		winningBidSelectionScheduler.chooseWinningBid();

		Mockito.verify(projectRepository).findProjectsBetween(anyObject(),
				anyObject());
		Mockito.verify(bidViewRepository).findAllByProjectSet(projectIds);
		Mockito.verify(winningBidRepository).save(anyList());
		Mockito.verifyNoMoreInteractions(projectRepository);
		Mockito.verifyNoMoreInteractions(bidViewRepository);
		Mockito.verifyNoMoreInteractions(winningBidRepository);

	}
}
