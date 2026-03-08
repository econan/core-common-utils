package com.impacsys.core.common.utils.datetime;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import jakarta.annotation.Nonnull;

/**
 * TODO: adding more functions.
 *
 * For unit test {#link CommonDateTimeUtilsTest}
 *
 * @author hkb@imcorp.kr
 * @since 2025.7.9
 *
 */
public class CommonDateTimeUtils {

	/* TODO: It should be configured for each RegionType or Env (KR, US,.) */
	public static final String DEFAULT_TIME_ZONE_ID = "Asia/Seoul";
	public static final ZoneId DEFAULT_ZONE_ID = ZoneId.of(DEFAULT_TIME_ZONE_ID);

	public static final String DEFAULT_FORMAT = "yyyy-MM-dd HH:mm:ss";
	public static final String T_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";
	public static final String DATE_FORMAT = "yyyy-MM-dd";
	public static final String BASIC_FORMAT = "yyyyMMdd";

	/**
	 *
	 * @return
	 */
	public static ZonedDateTime now() {

		return ZonedDateTime.now(DEFAULT_ZONE_ID);
	}

	/**
	 *
	 * @return
	 */
	public static LocalDateTime nowLocalDateTime() {

		return LocalDateTime.now(DEFAULT_ZONE_ID);
	}

	/**
	 *
	 * @param zoneId
	 * @return
	 */
	public static ZonedDateTime now(@Nonnull final ZoneId zoneId) {

		return ZonedDateTime.now(zoneId);
	}

	/**
	 *
	 * @param dateString
	 * @return
	 */
	public static ZonedDateTime zonedDateTimeByDefaultZoneId(@Nonnull final String dateString) {

		return ZonedDateTime.parse(
				dateString,
				DateTimeFormatter.ofPattern(DEFAULT_FORMAT).withZone(DEFAULT_ZONE_ID));
	}

	/**
	 *
	 * @param dateString
	 * @param zonedId
	 * @return
	 */
	public static ZonedDateTime zonedDateTimeByDefaultFormat(
			@Nonnull final String dateString, @Nonnull final ZoneId zonedId) {

		return ZonedDateTime.parse(
				dateString,
				DateTimeFormatter.ofPattern(DEFAULT_FORMAT).withZone(zonedId));
	}

	/**
	 *
	 * @param dateString
	 * @param dateFormat
	 * @param zonedId
	 * @return
	 */
	public static ZonedDateTime zonedDateTimeByDateFormatAndZonedId(
			@Nonnull final String dateString,
			@Nonnull final String dateFormat,
			@Nonnull final ZoneId zonedId) {

		return ZonedDateTime.parse(
				dateString,
				DateTimeFormatter.ofPattern(dateFormat).withZone(zonedId));
	}

	/**
	 *
	 * @param dateString YYYY-MM-DD
	 * @param zonedId
	 * @return
	 */
	public static ZonedDateTime zonedDateTimeByDateFormat(
			@Nonnull final String dateString, @Nonnull final ZoneId zonedId) {

		return LocalDate.parse(dateString).atStartOfDay(zonedId);
	}

	/**
	 *
	 * @param dateString YYYYMMDD
	 * @param zonedId
	 * @return
	 */
	public static ZonedDateTime zonedDateTimeByBasicDateFormat(
			@Nonnull final String dateString, @Nonnull final ZoneId zonedId) {

		return LocalDate.parse(dateString, DateTimeFormatter.BASIC_ISO_DATE).atStartOfDay(zonedId);
	}

	/**
	 *
	 * @param zonedDateTime
	 * @param dateFormat
	 * @return
	 */
	public static String zonedDateTimeToString(
			@Nonnull final ZonedDateTime zonedDateTime, final String dateFormat) {

		return zonedDateTime.format(DateTimeFormatter.ofPattern(dateFormat));
	}

	/**
	 *
	 * @param zonedDateTime
	 * @return
	 */
	public static long zonedDateTimeToMillis(@Nonnull final ZonedDateTime zonedDateTime) {

		return zonedDateTime.toInstant().toEpochMilli();
	}

	/**
	 *
	 * @param dateString
	 * @param zonedId
	 * @return
	 */
	public static LocalDateTime localDateTimeByDefaultFormat(
			@Nonnull final String dateString, @Nonnull final ZoneId zonedId) {
		
		try {
			return LocalDateTime.parse(
					dateString,
					DateTimeFormatter.ofPattern(DEFAULT_FORMAT).withZone(zonedId));
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 *
	 * @param dateString
	 * @param zonedId
	 * @return
	 */
	public static LocalDateTime localDateTimeByDefaultFormatAndZoneId(
			@Nonnull final String dateString) {

		try {
			return LocalDateTime.parse(
					dateString,
					DateTimeFormatter.ofPattern(DEFAULT_FORMAT).withZone(DEFAULT_ZONE_ID));
		} catch (DateTimeParseException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 *
	 * @param dateString
	 * @param zonedId
	 * @return
	 */
	public static LocalDateTime localDateTimeByDateFormat(
			@Nonnull final String dateString, @Nonnull final ZoneId zonedId) {

		try {
			return LocalDate.parse(dateString).atStartOfDay(zonedId).toLocalDateTime();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 *
	 * @param localDateTime
	 * @param zonedId
	 * @return
	 */
	public static long localDateTimeToMillis(
			@Nonnull final LocalDateTime localDateTime, @Nonnull final ZoneId zonedId) {

		return localDateTime.atZone(zonedId).toInstant().toEpochMilli();
	}

	/**
	 *
	 * @param localDateTime
	 * @param dateFormat
	 * @return
	 */
	public static String localDateTimeToString(
			@Nonnull final LocalDateTime localDateTime, final String dateFormat) {

		return localDateTime.format(DateTimeFormatter.ofPattern(dateFormat));
	}

	private CommonDateTimeUtils() {

	}

}
