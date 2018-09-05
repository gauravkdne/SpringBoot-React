package com.org.marketplace.util;

import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.org.marketplace.entity.Bid;
import com.org.marketplace.entity.Project;
import com.org.marketplace.entity.User;
import com.org.marketplace.payload.ProjectResponse;
import com.org.marketplace.payload.UserSummary;

/**
 * Model conversion utility
 * 
 * @author gauravkahadane
 *
 */
public final class ModelUtils {

	private static final Logger LOGGER = LoggerFactory.getLogger(ModelUtils.class);

	/**
	 * Maps project model to a project response to be sent to the client
	 * 
	 * @param project model
	 * @param user    authenticated user
	 * @return project response
	 */
	public static ProjectResponse getProjectResponse(Project project, User user) {
		ProjectResponse projectResponse = new ProjectResponse();

		try {
			if (project == null) {
				throw new Exception("Project is null");
			}

			projectResponse.setId(project.getId());
			projectResponse.setName(project.getName());
			projectResponse.setDescription(project.getDescription());
			projectResponse.setBudget(project.getBudget());

			if (project.getBids() != null && project.getBids().size() > 0) {
				projectResponse.setBid(project.getBids().get(0).getBid());
			}

			if (user != null) {
				UserSummary creatorSummary = new UserSummary(user.getId(), user.getUsername(), user.getName());
				projectResponse.setCreatedBy(creatorSummary);
			}

			if (project.getBidExpiry().compareTo(LocalDateTime.now()) < 0) {
				projectResponse.setIsBiddingExpired(true);
			}

			projectResponse.setBidExpiry(project.getBidExpiry());
			return projectResponse;
		} catch (Exception e) {
			LOGGER.error("Failed to generate project response: " + e);
		}
		return projectResponse;
	}

	/**
	 * Maps project model to a project response to be sent to the client. Sets the
	 * lowest bid on project among the bids placed by user.
	 * 
	 * @param project model
	 * @param user    authenticated user
	 * @return project response
	 */
	public static ProjectResponse getBiddedProjectsByUser(Project project, User user) {
		ProjectResponse projectResponse = new ProjectResponse();

		try {
			if (project == null) {
				throw new Exception("Project is null");
			}

			projectResponse.setId(project.getId());
			projectResponse.setName(project.getName());
			projectResponse.setDescription(project.getDescription());
			projectResponse.setBudget(project.getBudget());

			if (project.getBids() != null && project.getBids().size() > 0) {
				List<Bid> projectBids = project.getBids();

				// Project can have multiple bids place by users
				// Find the lowest bid value placed by current user on project
				Double lowestBid = null;
				for (Bid bid : projectBids) {
					// Filter the bids placed by current user
					if (bid.getUser() != null && bid.getUser().getUsername().equals(user.getUsername())) {
						//initialize first lowest value
						if (lowestBid == null) {
							lowestBid = bid.getBid();
						}
						if (bid.getBid() != null && lowestBid > bid.getBid()) {
							lowestBid = bid.getBid();
						}
					}
				}

				projectResponse.setBid(lowestBid);
			}

			if (user != null) {
				UserSummary creatorSummary = new UserSummary(user.getId(), user.getUsername(), user.getName());
				projectResponse.setCreatedBy(creatorSummary);
			}

			if (project.getBidExpiry().compareTo(LocalDateTime.now()) < 0) {
				projectResponse.setIsBiddingExpired(true);
			}

			projectResponse.setBidExpiry(project.getBidExpiry());
			return projectResponse;
		} catch (Exception e) {
			LOGGER.error("Failed to generate project response: " + e);
		}
		return projectResponse;
	}

	public static ProjectResponse getProjectResponse(Project project) {
		return getProjectResponse(project, null);
	}

}