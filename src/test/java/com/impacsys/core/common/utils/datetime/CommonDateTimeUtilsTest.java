package com.impacsys.core.common.utils.datetime;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import org.junit.jupiter.api.Test;

/**
 * TODO: add more functions.
 * To test {#link CommonDateTimeUtils}
 *
 * @author hkb@imcorp.kr
 * @since 2025.7.9
 *
 */
public class CommonDateTimeUtilsTest {

	private final ZoneId zoneId = ZoneId.of(CommonDateTimeUtils.DEFAULT_TIME_ZONE_ID);

	private final String date = "2021-08-09";
	private final String full_date = this.date + " 00:00:00";

	@Test
	public void test_ZonedDateTime() {

		final ZonedDateTime zonedDateTime = CommonDateTimeUtils.zonedDateTimeByDefaultFormat(this.full_date, this.zoneId);
		final ZonedDateTime dateTime = CommonDateTimeUtils.zonedDateTimeByDateFormat(this.date, this.zoneId);

		/* verify default format */
		assertEquals(zonedDateTime.toString(), dateTime.toString());
		/* verify milliseconds */
		assertEquals(CommonDateTimeUtils.zonedDateTimeToMillis(zonedDateTime),
				CommonDateTimeUtils.zonedDateTimeToMillis(dateTime));

	}

	@Test
	public void test_LocalDateTime() {

		final LocalDateTime localDateTime = CommonDateTimeUtils.localDateTimeByDefaultFormat(this.full_date, this.zoneId);
		final LocalDateTime dateTime = CommonDateTimeUtils.localDateTimeByDateFormat(this.date, this.zoneId);

		/* verify date string */
		assertEquals(localDateTime.toString(), dateTime.toString());
		/* verify milliseconds */
		assertEquals(CommonDateTimeUtils.localDateTimeToMillis(localDateTime, this.zoneId),
				CommonDateTimeUtils.localDateTimeToMillis(localDateTime, this.zoneId));

	}

	@Test
	public void test_ZonedDateTimeVsLocalDateTime() {

		final ZonedDateTime zonedDateTime = CommonDateTimeUtils.zonedDateTimeByDefaultFormat(this.full_date, this.zoneId);
		final LocalDateTime localDateTime = CommonDateTimeUtils.localDateTimeByDefaultFormat(this.full_date, this.zoneId);

		/* verify default(yyyy-MM-dd HH:mm:ss) format */
		assertEquals(
				CommonDateTimeUtils.zonedDateTimeToString(zonedDateTime, CommonDateTimeUtils.DEFAULT_FORMAT),
				CommonDateTimeUtils.localDateTimeToString(localDateTime, CommonDateTimeUtils.DEFAULT_FORMAT));

		/* verify date(yyyy-MM-dd format */
		assertEquals(
				CommonDateTimeUtils.zonedDateTimeToString(zonedDateTime, CommonDateTimeUtils.DATE_FORMAT),
				CommonDateTimeUtils.localDateTimeToString(localDateTime, CommonDateTimeUtils.DATE_FORMAT));

		/* verify milliseconds */
		assertEquals(CommonDateTimeUtils.zonedDateTimeToMillis(zonedDateTime),
				CommonDateTimeUtils.localDateTimeToMillis(localDateTime, this.zoneId));

	}
}
