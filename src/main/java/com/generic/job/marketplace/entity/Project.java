package com.generic.job.marketplace.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;

import org.springframework.hateoas.Identifiable;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@ToString
@Table(name = "Project", schema = "jobmarket")
public class Project implements Identifiable<String>, Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "project_id")
	private String id;

	@Column(name = "project_owner_id")
	private String projectOwnerId;

	@Column(name = "project_name")
	private String projectName;

	@Column(name = "requirements")
	private String requirements;

	@Column(name = "max_budget")
	private double maximumBudget;

	@Column(name = "last_date_time")
	private Date lastDateTime;

	@Column(name = "created_by")
	private String createdBy;

	@Column(name = "created_date")
	private Date createdDate;

	@Column(name = "updated_by")
	private String updatedBy;

	@Column(name = "updated_date")
	private Date updatedDate;

	@OneToOne(targetEntity = LowestBid.class, mappedBy = "project", fetch = FetchType.EAGER, cascade = {
			CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST,
			CascadeType.REFRESH})
	@JsonManagedReference
	private LowestBid lowestBid;

	@PrePersist
	public void onCreate() {
		this.id = UUID.randomUUID().toString();
		this.setCreatedBy(this.projectOwnerId);
		this.setCreatedDate(new Date());
		this.setUpdatedBy(this.projectOwnerId);
		this.setUpdatedDate(this.createdDate);
	}

	@PreUpdate
	public void onUpdate() {
		this.setUpdatedBy(this.projectOwnerId);
		this.setUpdatedDate(new Date());
	}

}
