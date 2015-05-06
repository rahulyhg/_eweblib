package com.eweblib.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class DateUtil {

	private static Logger logger = LogManager.getLogger(DateUtil.class);
	public static final String format = "yyyy-MM-dd HH:mm:ss";

	public static final String formatSimple = "yyyy-MM-dd";
	
	public static final String FORMATE_2 = "yyyyMMdd";

	public static String getDateStringTime(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		return sdf.format(date);
	}

	public static Date getDateTime(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat(format);

		if (EweblibUtil.isEmpty(date)) {
			return null;
		}
		try {
			return sdf.parse(sdf.format(date));
		} catch (ParseException e) {
			sdf = new SimpleDateFormat(formatSimple);
			try {
				return sdf.parse(sdf.format(date));
			} catch (ParseException e1) {
				sdf = new SimpleDateFormat("MM/dd/yyyy");
				try {
					return sdf.parse(sdf.format(date));
				} catch (ParseException e2) {

					logger.error(e2);
				}
			}
		}
		return null;
	}

	public static Date getDateTime(String date) {

		if (EweblibUtil.isEmpty(date)) {
			return null;
		}
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		if (EweblibUtil.isEmpty(date)) {
			return null;
		}
		try {
			return sdf.parse(date);
		} catch (ParseException e) {
			sdf = new SimpleDateFormat(formatSimple);
			try {
				return sdf.parse(date);
			} catch (ParseException e1) {
				sdf = new SimpleDateFormat("MM/dd/yyyy");
				try {
					return sdf.parse(date);
				} catch (ParseException e2) {
					// e2.printStackTrace();
					logger.error(e2);
				}
			}
		}
		return null;
	}

	public static String getDateString(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat(formatSimple);
		return sdf.format(date);
	}
	
	public static String getDateString(Date date, String formate) {
		SimpleDateFormat sdf = new SimpleDateFormat(formate);
		return sdf.format(date);
	}

	public static Date getDate(String date, String format) {
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		try {
			return sdf.parse(date);
		} catch (ParseException e) {

			logger.error("Parser date error " + date, e);
		}
		return null;
	}

	public static String getDateStringByLong(Long times) {
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		String time = sdf.format(new Date(times));

		// FIXME other solution for date field not datetime field?
		if (time.contains("00:00:00")) {
			time = time.replace("00:00:00", "").trim();
		}

		return time;
	}

}
