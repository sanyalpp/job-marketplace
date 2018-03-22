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

@Entity
@Getter
@Setter
@ToString
@Table(name = "Bid", schema = "jobmarket")
public class Bid implements Identifiable<String> {

	@Id
	@Column(name = "bid_id")
	private String id;

	@Column(name = "bidder_id")
	private String bidderId;

	@Column(name = "project_id")
	private String projectId;

	@Column(name = "bid_amount")
	private double bidAmount;

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
		this.setCreatedBy(this.bidderId);
		this.setCreatedDate(new Date());
		this.setUpdatedBy(this.bidderId);
		this.setUpdatedDate(this.createdDate);
	}

	@PreUpdate
	public void onUpdate() {
		this.setUpdatedBy(this.bidderId);
		this.setUpdatedDate(new Date());
	}
}
