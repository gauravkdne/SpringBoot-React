package com.org.marketplace.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.org.marketplace.entity.Role;
import com.org.marketplace.entity.RoleType;

/**
 * JPA repository for roles table
 * 
 * @author gauravkahadane
 *
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
	Optional<Role> findByName(RoleType roleName);

}
