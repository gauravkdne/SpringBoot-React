package com.org.marketplace.payload;

import java.time.Instant;
import java.time.LocalDateTime;

/**
 * @author gauravkahadane
 *
 */
public class ProjectResponse {
	private Long id;
	private String name;
	private String description;
	private UserSummary createdBy;
	private Instant creationDateTime;
	private Instant expirationDateTime;
	private Boolean isExpired;
	private Double budget;
	private LocalDateTime bidExpiry;
	private Double bid;
	private Boolean isBiddingExpired;

	public ProjectResponse() {
		super();
		this.bid = (double) 0;
	}

	public ProjectResponse(Long id, String name, String description, UserSummary createdBy, Instant creationDateTime,
			Instant expirationDateTime, Boolean isExpired, Double budget, LocalDateTime bidExpiry, Double bid) {
		super();
		this.id = id;
		this.name = name;
		this.description = description;
		this.createdBy = createdBy;
		this.creationDateTime = creationDateTime;
		this.expirationDateTime = expirationDateTime;
		this.isExpired = isExpired;
		this.budget = budget;
		this.bidExpiry = bidExpiry;
		this.bid = bid;
	}

	public Double getBudget() {
		return budget;
	}

	public void setBudget(Double budget) {
		this.budget = budget;
	}

	public Boolean getIsExpired() {
		return isExpired;
	}

	public void setIsExpired(Boolean isExpired) {
		this.isExpired = isExpired;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public UserSummary getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(UserSummary createdBy) {
		this.createdBy = createdBy;
	}

	public Instant getCreationDateTime() {
		return creationDateTime;
	}

	public void setCreationDateTime(Instant creationDateTime) {
		this.creationDateTime = creationDateTime;
	}

	public Instant getExpirationDateTime() {
		return expirationDateTime;
	}

	public void setExpirationDateTime(Instant expirationDateTime) {
		this.expirationDateTime = expirationDateTime;
	}

	public Boolean getExpired() {
		return isExpired;
	}

	public void setExpired(Boolean expired) {
		isExpired = expired;
	}

	public LocalDateTime getBidExpiry() {
		return bidExpiry;
	}

	public void setBidExpiry(LocalDateTime bidExpiry) {
		this.bidExpiry = bidExpiry;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Double getBid() {
		return bid;
	}

	public void setBid(Double bid) {
		this.bid = bid;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Boolean getIsBiddingExpired() {
		return isBiddingExpired;
	}

	public void setIsBiddingExpired(Boolean isBiddingExpired) {
		this.isBiddingExpired = isBiddingExpired;
	}

}
