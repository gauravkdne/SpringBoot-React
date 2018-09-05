package com.org.marketplace.payload;

import java.time.Instant;
import java.util.Set;

import com.org.marketplace.entity.Role;

/**
 * @author gauravkahadane
 *
 */
public class UserProfile {
	private Long id;
	private String username;
	private String name;
	private Instant joinedAt;
	private Set<Role> roles;
	

	public UserProfile(Long id, String username, String name, Instant joinedAt) {
		this.id = id;
		this.username = username;
		this.name = name;
		this.joinedAt = joinedAt;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Instant getJoinedAt() {
		return joinedAt;
	}

	public void setJoinedAt(Instant joinedAt) {
		this.joinedAt = joinedAt;
	}

	public Set<Role> getRoles() {
		return roles;
	}

	public void setRoles(Set<Role> roles) {
		this.roles = roles;
	}

}
