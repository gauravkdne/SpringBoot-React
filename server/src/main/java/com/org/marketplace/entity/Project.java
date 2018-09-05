package com.org.marketplace.entity;

import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.org.marketplace.entity.audit.UserDateAudit;

/**
 * Represents project table in the database
 * 
 * @author gauravkahadane
 *
 */
@Entity
@Table(name = "project")
public class Project extends UserDateAudit {

	private static final long serialVersionUID = -6674017353910255564L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotNull
	@Size(max = 40)
	private String name;
	
	@Size(max = 400)
	private String description;

	@Min(1)
	private Double budget;

	@NotNull
	@Column(name = "expiration_date_time")
	private LocalDateTime bidExpiry;

	@OneToMany(mappedBy = "project", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
	@OrderBy("value ASC")
	private List<Bid> bids;

	public Project() {
		super();
	}

	public Project(Long id, @Size(max = 40) @NotNull String name, @Size(max = 400) String description,
			@Min(1) Double budget, @NotNull LocalDateTime bidExpiry, List<Bid> bids) {
		super();
		this.id = id;
		this.name = name;
		this.description = description;
		this.budget = budget;
		this.bidExpiry = bidExpiry;
		this.bids = bids;
	}
	
	public String getName() {
		return name;
	}

	public Project(Long id) {
		super();
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Double getBudget() {
		return budget;
	}

	public void setBudget(Double budget) {
		this.budget = budget;
	}

	public LocalDateTime getBidExpiry() {
		return bidExpiry;
	}

	public void setBidExpiry(LocalDateTime bidExpiry) {
		this.bidExpiry = bidExpiry;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public List<Bid> getBids() {
		return bids;
	}

	public void setBids(List<Bid> bids) {
		this.bids = bids;
	}
}
