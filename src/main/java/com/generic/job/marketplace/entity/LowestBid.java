package com.generic.job.marketplace.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.springframework.hateoas.Identifiable;

import com.fasterxml.jackson.annotation.JsonBackReference;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@ToString
@Table(name = "Lowest_Bid_View", schema = "jobmarket")
public class LowestBid implements Identifiable<String> {

	@Id
	@Column(name = "bid_id", insertable = false, updatable = false)
	private String id;

	@OneToOne(targetEntity = Project.class, fetch = FetchType.EAGER)
	@JoinColumn(name = "project_id", referencedColumnName = "project_id", insertable = false, updatable = false)
	@JsonBackReference
	private Project project;

	@Column(name = "bid_amount", insertable = false, updatable = false)
	private double bidAmount;

	@Column(name = "bidder_id", insertable = false, updatable = false)
	private String bidderId;

	@Column(name = "created_by")
	private String createdBy;

	@Column(name = "created_date")
	private Date createdDate;

}
