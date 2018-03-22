package com.generic.job.marketplace.utility;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.generic.job.marketplace.logging.ErrorMessages;
import com.generic.job.marketplace.logging.MessageRetriever;

/**
 * Util for Date related functions.
 * 
 * @author Sanyal, Partha
 *
 */
public final class DateTimeUtil {

	private static final String UTC_ZONED_DATETIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";
	public static final String TIMESTAMP_FORMAT = "yyyy-MM-dd HH:mm:ss";
	private static final ZoneId UTC_ZONE_ID = ZoneId.of("Z");
	private static final Logger LOGGER = LoggerFactory
			.getLogger(DateTimeUtil.class);

	private DateTimeUtil() {

	}

	/**
	 * Returns a java date object from a String that represents a date in ISO
	 * 8601 format.
	 * 
	 * @param date
	 * @return
	 * @throws ParseException
	 */
	public static Date getUTCDate(String date) {
		LOGGER.debug("Date String parsed : {}", date);
		if (Objects.nonNull(date)) {
			try {
				SimpleDateFormat format = new SimpleDateFormat(
						UTC_ZONED_DATETIME_FORMAT, Locale.US);
				return format.parse(date);
			} catch (ParseException e) {
				LOGGER.error(MessageRetriever.getMessage(
						ErrorMessages.ERROR_OCCURRED, "parsing date",
						e.getMessage()));
			}

		}
		return null;
	}

	/**
	 * Checks if the date is in the valid format that is passed.
	 *
	 * @param date
	 * @return
	 */
	public static Boolean isValidDate(String date, String dateFormat) {
		LOGGER.debug("Date String being parsed : {}", date);
		if (StringUtils.isEmpty(date)) {
			return false;
		}
		try {
			getLocalDateTime(date, dateFormat);
		} catch (DateTimeParseException dtpe) {
			LOGGER.error(dtpe.getMessage(), dtpe);
			return false;
		}
		return true;
	}

	public static Boolean isValidUTCDate(String date) {
		return isValidDate(date, UTC_ZONED_DATETIME_FORMAT);
	}

	/**
	 * Returns an ISO 8601 String representation of a date in UTC.
	 *
	 * @param date
	 * @return
	 */
	public static String getUTCDateString(Date date) {
		SimpleDateFormat isoFormat = new SimpleDateFormat(
				UTC_ZONED_DATETIME_FORMAT, Locale.ENGLISH);
		String dateString = null;
		try {
			dateString = isoFormat.format(date);
		} catch (Exception e) {
			LOGGER.error(ErrorMessages.UTC_DATE_CONV_ERR_MSG, e);
		}
		return dateString;
	}

	public static LocalDateTime utcNow() {
		return LocalDateTime.ofInstant(Instant.now(), UTC_ZONE_ID);
	}

	/**
	 * Return today (Timestamp) in UTC.
	 *
	 * @return Timestamp
	 */
	public static Timestamp timeNowInUTC() {
		SimpleDateFormat dateFormat = new SimpleDateFormat(TIMESTAMP_FORMAT,
				Locale.US);
		dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
		return Timestamp.valueOf(dateFormat.format(new Date()));
	}

	public static Timestamp subtractTime(int timeUnit, ChronoUnit chronoUnit) {
		LocalDateTime dateTime = LocalDateTime.now(UTC_ZONE_ID).minus(timeUnit,
				chronoUnit);
		return Timestamp.valueOf(dateTime);
	}

	public static LocalDateTime getLocalDateTime(String dateTime,
			String format) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
		return LocalDateTime.parse(dateTime, formatter);
	}

	public static LocalDateTime getLocalDateTimeInUTC(String dateTime) {
		DateTimeFormatter formatter = DateTimeFormatter
				.ofPattern(UTC_ZONED_DATETIME_FORMAT);
		return LocalDateTime.parse(dateTime, formatter);
	}

	public static boolean isUTCDateInFuture(String dateTime) {
		return getLocalDateTimeInUTC(dateTime).isAfter(utcNow());
	}

	public static boolean isDateInFuture(Date date) {
		Timestamp now = timeNowInUTC();
		return now.before(date);
	}
}
