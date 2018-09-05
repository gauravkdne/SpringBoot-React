package com.org.marketplace.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedStoredProcedureQuery;
import javax.persistence.ParameterMode;
import javax.persistence.StoredProcedureParameter;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Min;

import com.org.marketplace.entity.audit.DateAudit;

/**
 * Represents bid table in the database
 * 
 * @author gauravkahadane
 *
 */

@Entity
@Table(name = "bid", uniqueConstraints = { @UniqueConstraint(columnNames = { "project_id", "user_id" }) })
@NamedStoredProcedureQuery(name = "getLowestBidsByUser", procedureName = "getLowestBidsByUser", resultClasses = {
		Bid.class }, parameters = {
				@StoredProcedureParameter(name = "userId", mode = ParameterMode.IN, type = Long.class) })
public class Bid extends DateAudit {
	private static final long serialVersionUID = 8630195241252302680L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "project_id", nullable = false)
	private Project project;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@Column(name = "value")
	@Min(1)
	private Double bid;

	public Long getId() {
		return id;
	}

	public Double getBid() {
		return bid;
	}

	public void setBid(Double bid) {
		this.bid = bid;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Project getProject() {
		return project;
	}

	public void setProject(Project project) {
		this.project = project;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
}
