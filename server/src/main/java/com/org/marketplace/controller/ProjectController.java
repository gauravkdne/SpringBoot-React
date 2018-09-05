package com.org.marketplace.controller;

import java.net.URI;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.org.marketplace.entity.Bid;
import com.org.marketplace.entity.Project;
import com.org.marketplace.exception.BadRequestException;
import com.org.marketplace.payload.ApiResponse;
import com.org.marketplace.payload.BidRequest;
import com.org.marketplace.payload.PagedResponse;
import com.org.marketplace.payload.ProjectRequest;
import com.org.marketplace.payload.ProjectResponse;
import com.org.marketplace.security.CurrentUser;
import com.org.marketplace.security.UserPrincipal;
import com.org.marketplace.service.BidService;
import com.org.marketplace.service.ProjectService;
import com.org.marketplace.util.AppConstants;

/**
 * This class exposes REST APIs for managing projects
 * 
 * @author gauravkahadane
 *
 */
@RestController
@RequestMapping("/api/projects")
public class ProjectController {

	private static final Logger LOGGER = LoggerFactory.getLogger(ProjectController.class);

	@Autowired
	private ProjectService projectService;
	
	@Autowired
	private BidService bidService;

	/**
	 * Retrieves all the projects
	 * 
	 * @param page page index
	 * @param size size of the page
	 * @return Paged response for projects
	 */
	@GetMapping
	public PagedResponse<ProjectResponse> getProjects(
			@RequestParam(value = "page", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) int page,
			@RequestParam(value = "size", defaultValue = AppConstants.DEFAULT_PAGE_SIZE) int size) {

		return projectService.getAllProjects(page, size);
	}

	/**
	 * Retrieves a project by given id
	 * 
	 * @param projectId the id of project to be retrieved
	 * @return project response
	 */
	@GetMapping("/{projectId}")
	public ProjectResponse getprojectById(@PathVariable Long projectId) {
		return projectService.getProjectById(projectId);
	}

	/**
	 * Creates a project
	 * 
	 * @param projectRequest contains project details
	 * @param currentUser    authenticated user
	 * @return REST response
	 * @throws Exception REST API exception
	 */
	@PostMapping
	@PreAuthorize("hasAnyRole('ADMIN')")
	public ResponseEntity<?> createProject(@Valid @RequestBody ProjectRequest projectRequest,
			@CurrentUser UserPrincipal currentUser) throws Exception {

		URI location;
		try {
			Project project = projectService.createProject(projectRequest, currentUser);

			location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{projectId}")
					.buildAndExpand(project.getId()).toUri();
		} catch (Exception e) {
			LOGGER.error("Failed to create a project: " + e);
			throw e;
		}

		return ResponseEntity.created(location).body(new ApiResponse(true, "Project Created Successfully"));
	}

	/**
	 * Places the bid on a project
	 * 
	 * @param bidRequest  contains bid details
	 * @param currentUser authenticated user
	 * @return REST response
	 */
	@PostMapping("/placeBid")
	@PreAuthorize("hasRole('USER')")
	public ResponseEntity<?> placeBid(@Valid @RequestBody BidRequest bidRequest,
			@CurrentUser UserPrincipal currentUser) {

		URI location;
		try {
			if (bidRequest.getBid() == null || bidRequest.getProjectId() == null) {
				throw new BadRequestException("Bid cannot be null");
			}
			if (bidRequest.getProjectId() == null) {
				throw new BadRequestException("Project id cannot be null");
			}

			Bid project = bidService.placeBid(bidRequest, currentUser);

			location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{bidValue}")
					.buildAndExpand(project.getId()).toUri();
		} catch (Exception e) {
			LOGGER.error("Failed to place the bid on project: " + e);
			throw e;
		}

		return ResponseEntity.created(location).body(new ApiResponse(true, "Bid Placed Successfully"));
	}
}
