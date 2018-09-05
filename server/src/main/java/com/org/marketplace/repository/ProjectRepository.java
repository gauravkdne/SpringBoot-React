package com.org.marketplace.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.org.marketplace.entity.Project;

/**
 * JPA repository for projects table
 * 
 * @author gauravkahadane
 *
 */
public interface ProjectRepository extends JpaRepository<Project, Long> {

	List<Project> findByIdIn(List<Long> projectIds);

	List<Project> findByIdIn(List<Long> projectIds, Sort sort);
	
	@Query("SELECT p FROM Project p WHERE p.createdBy = :userId")
	Page<Project> findProjectsCreatedBy(@Param("userId") Long userId, Pageable pageable);
	
}
