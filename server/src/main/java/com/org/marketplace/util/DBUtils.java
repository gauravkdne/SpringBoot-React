package com.org.marketplace.util;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.org.marketplace.config.ApplicationContextProvider;
import com.org.marketplace.entity.Project;
import com.org.marketplace.entity.User;
import com.org.marketplace.repository.UserRepository;

/**
 * @author gauravkahadane
 *
 */
public class DBUtils {

	/**
	 * Maps a project to its creator user
	 * 
	 * @param projects list of projects
	 * @return project to creator user map
	 */
	public static Map<Long, User> getProjectCreatorMap(List<Project> projects) {
		List<Long> creatorIds = projects.stream().map(Project::getCreatedBy).distinct().collect(Collectors.toList());

		UserRepository userRepository = ApplicationContextProvider.getBean(UserRepository.class);
		List<User> creators = userRepository.findByIdIn(creatorIds);
		Map<Long, User> creatorMap = creators.stream().collect(Collectors.toMap(User::getId, Function.identity()));

		return creatorMap;
	}
}
