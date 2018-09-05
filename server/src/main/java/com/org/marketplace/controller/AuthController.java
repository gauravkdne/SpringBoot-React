package com.org.marketplace.controller;

import java.net.URI;
import java.util.Collections;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.org.marketplace.entity.Role;
import com.org.marketplace.entity.RoleType;
import com.org.marketplace.entity.User;
import com.org.marketplace.exception.AppException;
import com.org.marketplace.exception.BadRequestException;
import com.org.marketplace.payload.ApiResponse;
import com.org.marketplace.payload.JwtAuthenticationResponse;
import com.org.marketplace.payload.LoginRequest;
import com.org.marketplace.payload.SignUpRequest;
import com.org.marketplace.repository.RoleRepository;
import com.org.marketplace.repository.UserRepository;
import com.org.marketplace.security.JwtTokenProvider;

/**
 * User authentication controller for the application
 * 
 * @author gauravkahadane
 *
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {
	private static final Logger LOGGER = LoggerFactory.getLogger(AuthController.class);

	@Autowired
	AuthenticationManager authenticationManager;

	@Autowired
	UserRepository userRepository;

	@Autowired
	RoleRepository roleRepository;

	@Autowired
	PasswordEncoder passwordEncoder;

	@Autowired
	JwtTokenProvider tokenProvider;

	/**
	 * Signs in the authenticated user
	 * 
	 * @param loginRequest contains user credentials
	 * @return JWT authentication response
	 */
	@PostMapping("/signin")
	public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(loginRequest.getUsernameOrEmail(), loginRequest.getPassword()));

		SecurityContextHolder.getContext().setAuthentication(authentication);

		String jwt = tokenProvider.generateToken(authentication);
		return ResponseEntity.ok(new JwtAuthenticationResponse(jwt));
	}

	/**
	 * Registers a new user
	 * 
	 * @param signUpRequest contains user credentials
	 * @return API response based on whether the user registration is successful or
	 *         failure.
	 */
	@PostMapping("/signup")
	public ResponseEntity<?> registerUser(@Valid @RequestBody SignUpRequest signUpRequest) {
		if (userRepository.existsByUsername(signUpRequest.getUsername())) {
			return new ResponseEntity<ApiResponse>(new ApiResponse(false, "Username is already taken!"),
					HttpStatus.BAD_REQUEST);
		}

		if (userRepository.existsByEmail(signUpRequest.getEmail())) {
			return new ResponseEntity<ApiResponse>(new ApiResponse(false, "Email Address already in use!"),
					HttpStatus.BAD_REQUEST);
		}

		User user = new User(signUpRequest.getName(), signUpRequest.getUsername(), signUpRequest.getEmail(),
				signUpRequest.getPassword());

		user.setPassword(passwordEncoder.encode(user.getPassword()));

		RoleType roleType;
		try {
			roleType = RoleType.valueOf(signUpRequest.getUserRole());
		} catch (Exception e) {
			LOGGER.error("Invalid user role type: " + e);
			throw new BadRequestException("Invalid user role type: " + signUpRequest.getUserRole());
		}

		Role userRole = roleRepository.findByName(roleType).orElseThrow(() -> new AppException("User Role not set."));

		user.setRoles(Collections.singleton(userRole));

		User result = userRepository.save(user);

		URI location = ServletUriComponentsBuilder.fromCurrentContextPath().path("/users/{username}")
				.buildAndExpand(result.getUsername()).toUri();

		return ResponseEntity.created(location).body(new ApiResponse(true, "User registered successfully"));
	}
}
