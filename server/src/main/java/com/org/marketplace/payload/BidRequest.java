package com.org.marketplace.payload;

/**
 * @author gauravkahadane
 *
 */
public class BidRequest {
	private Long projectId;
	private Double bid;

	public Long getProjectId() {
		return projectId;
	}

	public void setProjectId(Long projectId) {
		this.projectId = projectId;
	}

	public Double getBid() {
		return bid;
	}

	public void setBid(Double bid) {
		this.bid = bid;
	}

}
