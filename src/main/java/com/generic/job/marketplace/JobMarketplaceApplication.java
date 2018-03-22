package com.generic.job.marketplace;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.hateoas.config.EnableHypermediaSupport;

/**
 * The entry point for the boot run application.
 * 
 * @author Sanyal, Partha
 *
 */
@ComponentScan({"com.generic.job.marketplace"})
@SpringBootApplication
@EnableAutoConfiguration(exclude = HibernateJpaAutoConfiguration.class)
@EnableHypermediaSupport(type = {EnableHypermediaSupport.HypermediaType.HAL})
public class JobMarketplaceApplication {

	private final String name;

	public JobMarketplaceApplication() {
		name = "JobMarketplaceApplication";
	}

	public static void main(String[] args) {
		SpringApplication.run(JobMarketplaceApplication.class, args);
	}

	public String getName() {
		return name;
	}

}
