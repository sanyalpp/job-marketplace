package com.generic.job.marketplace.datasource;

import java.util.Properties;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.sql.DataSource;

import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.orm.jpa.JpaTransactionManager;

/**
 * This file is for configuring H2 Datasource, and linking with persistence.xml
 * 
 * @author Sanyal, Partha
 *
 */
@Configuration
@Profile("default")
public class DatasourceConfigDevH2 {

	@Bean(destroyMethod = "shutdown")
	public DataSource dataSource() {

		return new EmbeddedDatabaseBuilder().setType(EmbeddedDatabaseType.H2)
				.build();
	}

	@Bean
	@DependsOn("flyway")
	public EntityManagerFactory entityManagerFactory() {

		Properties jpaProps = new Properties();
		jpaProps.put("openjpa.ConnectionFactory", dataSource());
		jpaProps.put("openjpa.Log", "log4j");
		jpaProps.put("openjpa.ConnectionFactoryProperties", "true");
		return Persistence.createEntityManagerFactory("acPersistenceUnit",
				jpaProps);
	}

	/**
	 * 
	 * http://blog.trifork.com/2014/12/09/integrating-flywaydb-in-a-spring-
	 * framework-application/ .
	 * 
	 *
	 * 
	 * @return
	 */

	@Bean(initMethod = "migrate")
	Flyway flyway() {

		Flyway flyway = new Flyway();
		flyway.setSchemas("JOBMARKET");
		flyway.setBaselineOnMigrate(true);
		flyway.setDataSource(dataSource());
		flyway.setLocations("db/migration");
		return flyway;

	}

	@Bean
	@Autowired
	public EntityManager entityManager(
			EntityManagerFactory entityManagerFactory) {

		return entityManagerFactory.createEntityManager();
	}

	@Bean
	@Autowired
	public JpaTransactionManager transactionManager(
			EntityManagerFactory entityManagerFactory) {

		JpaTransactionManager txManager = new JpaTransactionManager();
		txManager.setEntityManagerFactory(entityManagerFactory);
		return txManager;
	}

}
