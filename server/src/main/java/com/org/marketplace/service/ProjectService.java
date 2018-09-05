package com.org.marketplace.service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.org.marketplace.entity.Project;
import com.org.marketplace.entity.User;
import com.org.marketplace.exception.BadRequestException;
import com.org.marketplace.exception.ResourceNotFoundException;
import com.org.marketplace.payload.PagedResponse;
import com.org.marketplace.payload.ProjectRequest;
import com.org.marketplace.payload.ProjectResponse;
import com.org.marketplace.repository.ProjectRepository;
import com.org.marketplace.repository.UserRepository;
import com.org.marketplace.security.UserPrincipal;
import com.org.marketplace.util.AppUtils;
import com.org.marketplace.util.ModelUtils;
import com.org.marketplace.util.ValidatorUtils;

/**
 * Service for managing projects
 * 
 * @author gauravkahadane
 *
 */
@Service
public class ProjectService {

	private static final Logger LOGGER = LoggerFactory.getLogger(ProjectService.class);

	@Autowired
	private ProjectRepository projectRepository;


	@Autowired
	private UserRepository userRepository;

	/**
	 * Retrieves all the projects
	 * 
	 * @param page index of a page
	 * @param size size of a page
	 * @return paged response of projects
	 */
	public PagedResponse<ProjectResponse> getAllProjects(int page, int size) {
		List<ProjectResponse> projectResponses;
		Page<Project> projects;

		try {
			ValidatorUtils.validatePageNumberAndSize(page, size);

			Pageable pageable = PageRequest.of(page, size, Sort.Direction.DESC, "createdAt");

			projects = projectRepository.findAll(pageable);

			if (projects.getNumberOfElements() == 0) {
				return new PagedResponse<>(Collections.emptyList(), projects.getNumber(), projects.getSize(),
						projects.getTotalElements(), projects.getTotalPages(), projects.isLast());
			}

			pageable = PageRequest.of(page, size, Sort.Direction.DESC, "createdAt");

			projectResponses = projects.map(project -> {
				return ModelUtils.getProjectResponse(project);
			}).getContent();

		} catch (BadRequestException e) {
			LOGGER.error("Bad Request: " + e);
			throw e;
		} catch (Exception e) {
			LOGGER.error("Failed to fetch projects: " + e);
			throw e;
		}
		return new PagedResponse<>(projectResponses, projects.getNumber(), projects.getSize(),
				projects.getTotalElements(), projects.getTotalPages(), projects.isLast());
	}

	/**
	 * Creates a project
	 * 
	 * @param projectRequest contains project details
	 * @param userPrincipal  authenticated user
	 * @return the created project
	 * @throws Exception
	 */
	public Project createProject(ProjectRequest projectRequest, UserPrincipal userPrincipal) throws Exception {
		try {
			if (!ValidatorUtils.validateName(projectRequest.getName())) {
				throw new Exception("Invalid Project Name");
			}
			Project project = new Project();
			project.setName(projectRequest.getName());
			project.setDescription(projectRequest.getDescription());
			project.setBidExpiry(AppUtils.getDate(projectRequest.getBidExpiry()));
			project.setBudget(projectRequest.getBudget());
			Project savedProject = projectRepository.save(project);

			LOGGER.info("Created a project: " + project.toString());
			return savedProject;
		} catch (Exception e) {
			LOGGER.error("Failed to create project: " + e);
			throw e;
		}
	}

	/**
	 * Retrieves a project by the given id
	 * 
	 * @param projectId id of project
	 * @return project response
	 */
	public ProjectResponse getProjectById(Long projectId) {
		Project project = projectRepository.findById(projectId)
				.orElseThrow(() -> new ResourceNotFoundException("Project", "id", projectId));

		try {
			return ModelUtils.getProjectResponse(project);
		} catch (Exception e) {
			LOGGER.error("Failed to fetch project: " + e);
			throw e;
		}

	}

	/**
	 * Retrieves the projects created by a user
	 * 
	 * @param username
	 * @param currentUser authenticated user
	 * @param page        index of a page
	 * @param size        size of a page
	 * @return projects created by a user
	 */
	public PagedResponse<ProjectResponse> getProjectsCreatedBy(String username, UserPrincipal currentUser, int page,
			int size) {
		try {
			ValidatorUtils.validatePageNumberAndSize(page, size);

			User user = userRepository.findByUsername(username)
					.orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

			Pageable pageable = PageRequest.of(page, size, Sort.Direction.DESC, "createdAt");
			
			Page<Project> userCreatedProjects = projectRepository.findProjectsCreatedBy(user.getId(), pageable);

			if (userCreatedProjects.getNumberOfElements() == 0) {
				return new PagedResponse<>(Collections.emptyList(), userCreatedProjects.getNumber(),
						userCreatedProjects.getSize(), userCreatedProjects.getTotalElements(),
						userCreatedProjects.getTotalPages(), userCreatedProjects.isLast());
			}

			List<Project> projects = userCreatedProjects.getContent();

			List<ProjectResponse> projectResponses = projects.stream().map(project -> {
				return ModelUtils.getProjectResponse(project, user);
			}).collect(Collectors.toList());

			return new PagedResponse<>(projectResponses, userCreatedProjects.getNumber(),
					userCreatedProjects.getSize(), userCreatedProjects.getTotalElements(),
					userCreatedProjects.getTotalPages(), userCreatedProjects.isLast());

		} catch (BadRequestException e) {
			LOGGER.error("Bad Request: " + e);
			throw e;
		} catch (ResourceNotFoundException e) {
			LOGGER.error("Resource Not Found: " + e);
			throw e;
		}
	}
}
