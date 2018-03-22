package com.generic.job.marketplace;

import org.junit.Before;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.generic.job.marketplace.JobMarketplaceApplication;

/**
 * Main com.generic.job.marketplace.config file for Unit Tests.
 * 
 * @author Sanyal, Partha
 *
 */
@SpringApplicationConfiguration(classes = JobMarketplaceApplication.class)
@WebAppConfiguration
@ActiveProfiles("default")
public class UnitTestConfig {

	@Autowired
	private WebApplicationContext wac;

	protected MockMvc mockMvc;

	@Before
	public void setup() throws Exception {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
		MockitoAnnotations.initMocks(this);
	}

}
