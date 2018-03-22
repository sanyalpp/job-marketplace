package com.generic.job.marketplace.entity;

import java.util.Date;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;

import org.springframework.hateoas.Identifiable;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Person entity.
 * 
 * @author Sanyal, Partha
 *
 */
@Entity
@Getter
@Setter
@ToString
@Table(name = "Person", schema = "jobmarket")
public class Person implements Identifiable<String> {

	@Id
	@Column(name = "person_id")
	private String id;

	@Column(name = "first_name")
	private String firstName;

	@Column(name = "last_name")
	private String lastName;

	@Column(name = "email")
	private String email;

	@Column(name = "created_by")
	private String createdBy;

	@Column(name = "created_date")
	private Date createdDate;

	@Column(name = "updated_by")
	private String updatedBy;

	@Column(name = "updated_date")
	private Date updatedDate;

	@PrePersist
	public void onCreate() {
		this.id = UUID.randomUUID().toString();
		this.setCreatedBy(this.id);
		this.setCreatedDate(new Date());
		this.setUpdatedBy(this.id);
		this.setUpdatedDate(this.createdDate);
	}

	@PreUpdate
	public void onUpdate() {
		this.setUpdatedBy(this.id);
		this.setUpdatedDate(new Date());
	}
}
