package com.generic.job.marketplace.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import com.generic.job.marketplace.entity.Person;

public interface PersonRepository extends JpaRepository<Person, String> {

	Person findById(@Param("id") String personId);

}
