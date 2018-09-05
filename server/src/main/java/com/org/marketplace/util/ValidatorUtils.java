package com.org.marketplace.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.org.marketplace.exception.BadRequestException;

/**
 * Validation utility
 * 
 * @author gauravkahadane
 *
 */
public final class ValidatorUtils {

	private static final Logger LOGGER = LoggerFactory.getLogger(ValidatorUtils.class);

	/**
	 * Validates a name
	 * 
	 * @param name project name
	 * @return true if name is valid otherwise false
	 * @throws NullPointerException
	 * @throws Exception
	 */
	public static boolean validateName(String name) throws NullPointerException, Exception {
		if (name == null) {
			throw new NullPointerException();
		}

		final String pattern = "^[a-zA-Z]{3}\\w*[-]*\\w*$";

		try {
			if (name.matches(pattern)) {
				return true;
			}
		} catch (Exception e) {
			LOGGER.error("Invalid Project Name: " + e);
			throw new Exception("Invalid Project Name");
		}

		return false;
	}

	/**
	 * Validates number and size of a page to be returned for the paged response
	 * 
	 * @param page index of a page
	 * @param size size of a page
	 * @throws BadRequestException
	 */
	public static void validatePageNumberAndSize(int page, int size) throws BadRequestException {
		if (page < 0) {
			throw new BadRequestException("Page index cannot be less than zero");
		}

		if (size > AppConstants.MAX_PAGE_SIZE) {
			throw new BadRequestException("Page size should be less than " + AppConstants.MAX_PAGE_SIZE);
		}
	}
}
