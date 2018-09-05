package com.org.marketplace.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.EntityManagerFactory;
import javax.persistence.StoredProcedureQuery;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.org.marketplace.entity.Bid;
import com.org.marketplace.entity.Project;
import com.org.marketplace.entity.User;
import com.org.marketplace.exception.BadRequestException;
import com.org.marketplace.exception.ResourceNotFoundException;
import com.org.marketplace.payload.BidRequest;
import com.org.marketplace.payload.PagedResponse;
import com.org.marketplace.payload.ProjectResponse;
import com.org.marketplace.repository.BidRepository;
import com.org.marketplace.repository.ProjectRepository;
import com.org.marketplace.repository.UserRepository;
import com.org.marketplace.security.UserPrincipal;
import com.org.marketplace.util.DBUtils;
import com.org.marketplace.util.ModelUtils;
import com.org.marketplace.util.ValidatorUtils;

/**
 * Service for managing bids placed on a project
 * 
 * @author gauravkahadane
 *
 */
@Service
public class BidService {

	private static final Logger LOGGER = LoggerFactory.getLogger(BidService.class);

	@Autowired
	private BidRepository bidRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ProjectRepository projectRepository;

	@Autowired
	private EntityManagerFactory entityManagerFactory;

	/**
	 * Places the bid on a project
	 * 
	 * @param bidRequest    contains bid details
	 * @param userPrincipal represents authenticated user
	 * @return placed bid
	 */
	public Bid placeBid(BidRequest bidRequest, UserPrincipal userPrincipal) {
		Bid bid = new Bid();
		try {
			if (bidRequest.getBid() == null && bidRequest.getBid() < 0) {
				throw new BadRequestException("Enter a valid bid value");
			}

			bid.setBid(bidRequest.getBid());

			Optional<Project> projectToBeBidded = projectRepository.findById(bidRequest.getProjectId());
			if (projectToBeBidded.isPresent()) {
				Project project = projectToBeBidded.get();
				if (project.getBidExpiry().compareTo(LocalDateTime.now()) < 0) {
					throw new BadRequestException("Bidding is expired for project: " + project.getName());
				} else {
					// Verify if the bid value is unique
					Set<Double> currentBids = bidRepository.findBidsForProject(project.getId());
					if (currentBids.size() > 0 && currentBids.contains(bidRequest.getBid())) {
						throw new BadRequestException("Bid is already placed on project. Enter a different bid");
					}
					bid.setProject(project);
				}
			} else {
				throw new BadRequestException("Project does not exist");
			}

			User user = userRepository.getOne(userPrincipal.getId());
			bid.setUser(user);

			bidRepository.save(bid);
		} catch (Exception e) {
			LOGGER.error("Error persisting bid: " + e);
			throw e;
		}
		return bid;
	}

	/**
	 * Retrieves the projects/bids won by a user
	 * 
	 * @param username    name of a user
	 * @param currentUser represent authenticated user
	 * @param page        index of a page
	 * @param size        size of a page
	 * @return paged project response
	 */
	public PagedResponse<ProjectResponse> getBidsWonBy(String username, UserPrincipal currentUser, int page, int size) {
		Session session = entityManagerFactory.unwrap(SessionFactory.class).openSession();
		PagedResponse<ProjectResponse> response = new PagedResponse<ProjectResponse>();

		try {
			StoredProcedureQuery storedProcQuery = session.createNamedStoredProcedureQuery("getLowestBidsByUser");
			storedProcQuery.setParameter("userId", currentUser.getId());
			storedProcQuery.execute();
			@SuppressWarnings("unchecked")
			List<Bid> bidList = storedProcQuery.getResultList();

			User user = userRepository.findByUsername(currentUser.getUsername())
					.orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

			List<ProjectResponse> projectsWon = new ArrayList<ProjectResponse>();
			for (Bid bid : bidList) {
				Optional<Project> project = projectRepository.findById(bid.getProject().getId());
				if (project.isPresent()) {
					Project biddedProject = project.get();
					if (biddedProject.getBidExpiry().compareTo(LocalDateTime.now()) < 0) {
						projectsWon.add(ModelUtils.getProjectResponse(bid.getProject(), user));
					}
				}
			}

			response.setContent(projectsWon);

		} catch (HibernateException e) {
			LOGGER.error("Failed to fetch bids won by " + currentUser.getUsername() + ": " + e);
			throw e;
		} finally {
			session.close();
		}
		return response;
	}

	/**
	 * Retrieves the projects/bids placed by a user
	 * 
	 * @param username
	 * @param currentUser authenticated user
	 * @param page        index of a page
	 * @param size        size of a page
	 * @return projects on which user has placed the bid
	 */
	public PagedResponse<ProjectResponse> getBidsPlacedBy(String username, UserPrincipal currentUser, int page,
			int size) {
		try {
			ValidatorUtils.validatePageNumberAndSize(page, size);

			User user = userRepository.findByUsername(username)
					.orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

			Pageable pageable = PageRequest.of(page, size, Sort.Direction.DESC, "createdAt");

			Page<Project> biddedProjects = bidRepository.findBiddedProjectsByUserId(user.getId(), pageable);

			if (biddedProjects.getNumberOfElements() == 0) {
				return new PagedResponse<>(Collections.emptyList(), biddedProjects.getNumber(),
						biddedProjects.getSize(), biddedProjects.getTotalElements(), biddedProjects.getTotalPages(),
						biddedProjects.isLast());
			}

			List<Project> projects = biddedProjects.getContent();

			Map<Long, User> creatorMap = DBUtils.getProjectCreatorMap(projects);

			List<ProjectResponse> projectResponses = projects.stream().map(project -> {
				return ModelUtils.getBiddedProjectsByUser(project, user);
			}).collect(Collectors.toList());

			return new PagedResponse<>(projectResponses, biddedProjects.getNumber(), biddedProjects.getSize(),
					biddedProjects.getTotalElements(), biddedProjects.getTotalPages(), biddedProjects.isLast());

		} catch (BadRequestException e) {
			LOGGER.error("Bad Request: " + e);
			throw e;
		} catch (ResourceNotFoundException e) {
			LOGGER.error("Resource Not Found: " + e);
			throw e;
		}
	}

//	/**
//	 * Retrieves the projects/bids placed by a user
//	 * 
//	 * @param username
//	 * @param currentUser authenticated user
//	 * @param page        index of a page
//	 * @param size        size of a page
//	 * @return projects on which user has placed the bid
//	 */
//	public PagedResponse<ProjectResponse> getLowestBidsPlacedBy(String username, UserPrincipal currentUser, int page,
//			int size) {
//		Session session = entityManagerFactory.unwrap(SessionFactory.class).openSession();
//		PagedResponse<ProjectResponse> response = new PagedResponse<ProjectResponse>();
//
//		try {
//			StoredProcedureQuery storedProcQuery = session.createNamedStoredProcedureQuery("getLowestBidsByUser");
//			storedProcQuery.setParameter("userId", currentUser.getId());
//			storedProcQuery.execute();
//			@SuppressWarnings("unchecked")
//			List<Bid> bidList = storedProcQuery.getResultList();
//
//			User user = userRepository.findByUsername(currentUser.getUsername())
//					.orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
//
//			List<ProjectResponse> bidsPlaced = new ArrayList<ProjectResponse>();
//			for (Bid bid : bidList) {
//				Optional<Project> project = projectRepository.findById(bid.getProject().getId());
//				if (project.isPresent()) {
//					bidsPlaced.add(ModelUtils.getProjectResponse(bid.getProject(), user));
//				}
//			}
//
//			response.setContent(bidsPlaced);
//
//		} catch (HibernateException e) {
//			LOGGER.error("Failed to fetch bids won by " + currentUser.getUsername() + ": " + e);
//			throw e;
//		} finally {
//			session.close();
//		}
//		return response;
//	}

}
