package com.org.marketplace.util;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Generic utility
 * 
 * @author gauravkahadane
 *
 */
public final class AppUtils {

	private static final Logger LOGGER = LoggerFactory.getLogger(AppUtils.class);

	/**
	 * Converts a date to specific format
	 * 
	 * @param dateStr LocalDate in a string format
	 * @return LocalDate
	 * @throws Exception
	 */
	public static LocalDateTime getDate(String dateStr) throws Exception {
		try {
			if (dateStr == null) {
				throw new NullPointerException("Date is null");
			}

	        Instant instant = Instant.parse(dateStr);
	        return LocalDateTime.ofInstant(instant, ZoneId.of(ZoneOffset.UTC.getId()));
			
		} catch (Exception e) {
			LOGGER.error("Failed to parse the date: " + e);
			throw new Exception("Enter a valid date");
		}
	}
}
