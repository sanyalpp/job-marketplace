package com.generic.job.marketplace.util;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.generic.job.marketplace.UnitTestConfig;
import com.generic.job.marketplace.utility.DateTimeUtil;

/**
 * @author Sanyal, Partha
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
public class DateTimeUtilTest extends UnitTestConfig {

	@Test
	public void test_to_uTC_string_with_null_date() {
		assertNull(DateTimeUtil.getUTCDateString(null));
	}

	@Test
	public void test_getUTCDate_with_valid_date() {
		String UTCdate = "2018-02-07T04:53:00Z";
		assertNotNull(DateTimeUtil.getUTCDate(UTCdate));
	}

	@Test
	public void test_getUTCDate_with_invalid_date() {
		String UTCdate = "2018-02-07ZT04:53:00ZZ";
		assertNull(DateTimeUtil.getUTCDate(UTCdate));
	}

	@Test
	public void test_getUTCDate_with_null_date() {
		assertNull(DateTimeUtil.getUTCDate(null));
	}
}
