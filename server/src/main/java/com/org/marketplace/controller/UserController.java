package com.org.marketplace.controller;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.org.marketplace.entity.Role;
import com.org.marketplace.entity.RoleType;
import com.org.marketplace.entity.User;
import com.org.marketplace.exception.ResourceNotFoundException;
import com.org.marketplace.payload.PagedResponse;
import com.org.marketplace.payload.ProjectResponse;
import com.org.marketplace.payload.UserIdentityAvailability;
import com.org.marketplace.payload.UserProfile;
import com.org.marketplace.payload.UserSummary;
import com.org.marketplace.repository.UserRepository;
import com.org.marketplace.security.CurrentUser;
import com.org.marketplace.security.UserPrincipal;
import com.org.marketplace.service.BidService;
import com.org.marketplace.service.ProjectService;
import com.org.marketplace.util.AppConstants;

/**
 * Exposes REST APIs relevant to user actions
 * 
 * @author gauravkahadane
 *
 */
@RestController
@RequestMapping("/api")
public class UserController {
	private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ProjectService projectService;

	@Autowired
	private BidService bidService;

	/**
	 * Retrieves the current authenticated user details
	 * 
	 * @param currentUser the authenticated user
	 * @return user summary
	 */
	@GetMapping("/user/me")
	@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
	public UserSummary getCurrentUser(@CurrentUser UserPrincipal currentUser) {
		UserSummary userSummary = new UserSummary(currentUser.getId(), currentUser.getUsername(),
				currentUser.getName());

		if (currentUser.getAuthorities() != null && !currentUser.getAuthorities().isEmpty()) {
			@SuppressWarnings("unchecked")
			Stream<GrantedAuthority> authorityStream = (Stream<GrantedAuthority>) currentUser.getAuthorities().stream();
			Optional<GrantedAuthority> grantedAuthority = authorityStream.findFirst();
			if (grantedAuthority.isPresent()) {
				GrantedAuthority authority = grantedAuthority.get();
				try {
					userSummary.setRole(RoleType.valueOf(authority.getAuthority()));
				} catch (Exception e) {
					LOGGER.error("Failed to set user role: " + e);
				}
			}

		}
		return userSummary;
	}

	/**
	 * Determines user name availability
	 * 
	 * @param username to be checked for availability
	 * @return true if available otherwise false
	 */
	@GetMapping("/user/checkUsernameAvailability")
	public UserIdentityAvailability checkUsernameAvailability(@RequestParam(value = "username") String username) {
		return new UserIdentityAvailability(!userRepository.existsByUsername(username));
	}

	/**
	 * Determines user email availability
	 * 
	 * @param email to be checked for availability
	 * @return true if available otherwise false
	 */
	@GetMapping("/user/checkEmailAvailability")
	public UserIdentityAvailability checkEmailAvailability(@RequestParam(value = "email") String email) {
		return new UserIdentityAvailability(!userRepository.existsByEmail(email));
	}

	/**
	 * Retrieves user profile
	 * 
	 * @param username of the user
	 * @return user profile
	 */
	@GetMapping("/users/{username}")
	public UserProfile getUserProfile(@PathVariable(value = "username") String username) {
		User user = userRepository.findByUsername(username)
				.orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

		UserProfile userProfile = new UserProfile(user.getId(), user.getUsername(), user.getName(),
				user.getCreatedAt());
		
		Set<Role> roles = user.getRoles();
		userProfile.setRoles(roles);
		return userProfile;
	}

	/**
	 * Retrieves the projects on which buyer has placed the bids
	 * 
	 * @param username    buyer name
	 * @param currentUser user principal representing the buyer
	 * @param page        index of the page
	 * @param size        size of the page
	 * @return projects on which user has placed the bids
	 */
	@GetMapping("/users/{username}/bids")
	public PagedResponse<ProjectResponse> getBidsPlacedBy(@PathVariable(value = "username") String username,
			@CurrentUser UserPrincipal currentUser,
			@RequestParam(value = "page", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) int page,
			@RequestParam(value = "size", defaultValue = AppConstants.DEFAULT_PAGE_SIZE) int size) {
		return bidService.getBidsPlacedBy(username, currentUser, page, size);
	}
	
	/**
	 * Retrieves the projects created by a user
	 * 
	 * @param username    user name
	 * @param currentUser user principal
	 * @param page        index of the page
	 * @param size        size of the page
	 * @return projects created by a user
	 */
	@GetMapping("/users/{username}/projects")
	public PagedResponse<ProjectResponse> getProjectsCreatedBy(@PathVariable(value = "username") String username,
			@CurrentUser UserPrincipal currentUser,
			@RequestParam(value = "page", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) int page,
			@RequestParam(value = "size", defaultValue = AppConstants.DEFAULT_PAGE_SIZE) int size) {
		return projectService.getProjectsCreatedBy(username, currentUser, page, size);
	}

	/**
	 * Retrieves the bids won by a buyer
	 * 
	 * @param username    buyer name
	 * @param currentUser user principal representing the buyer
	 * @param page        index of the page
	 * @param size        size of the page
	 * @return project bids won by a buyer
	 */
	@GetMapping("/users/{username}/bidsWon")
	public PagedResponse<ProjectResponse> getBidsWonBy(@PathVariable(value = "username") String username,
			@CurrentUser UserPrincipal currentUser,
			@RequestParam(value = "page", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) int page,
			@RequestParam(value = "size", defaultValue = AppConstants.DEFAULT_PAGE_SIZE) int size) {
		return bidService.getBidsWonBy(username, currentUser, page, size);
	}
}
