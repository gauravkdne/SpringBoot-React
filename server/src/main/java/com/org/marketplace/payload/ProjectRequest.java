package com.org.marketplace.payload;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * @author gauravkahadane
 *
 */
public class ProjectRequest {
	@NotBlank
	@Size(min = 3, max = 40)
	private String name;

	@NotNull
	@Size(max = 400)
	@Valid
	private String description;

	private Double budget;

	private String bidExpiry;

	public Double getBudget() {
		return budget;
	}

	public void setBudget(Double budget) {
		this.budget = budget;
	}

	public String getBidExpiry() {
		return bidExpiry;
	}

	public void setBidExpiry(String bidExpiry) {
		this.bidExpiry = bidExpiry;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}
