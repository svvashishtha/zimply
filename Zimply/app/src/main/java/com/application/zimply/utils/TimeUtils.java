package com.application.zimply.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;

public class TimeUtils {

	public static int DATE_TYPE_DD_MM_YY = 1;
	public static int DATE_TYPE_DAY_MON_DD_YYYY = 2;
	public static int DATE_TYPE_DD_MON = 3;

	public static String getTimeStampDate(String timestamp, int type) {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
					Locale.US);
			// sdf.setTimeZone(TimeZone.getTimeZone("UTC"));

			Date date = sdf.parse(timestamp);
			System.out.println("in milliseconds: " + date.getTime());
			if (type == DATE_TYPE_DD_MM_YY) {
				return getDDMMYYDate(date.getTime());
			} else if (type == DATE_TYPE_DD_MON) {
				return getDDMonDate(date.getTime());
			} else {
				return getActivityDate(date.getTime());
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}

	public static String getActivityDate(long time) {

		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(time);

		SpannableStringBuilder builder1 = new SpannableStringBuilder();

		int day = calendar.get(Calendar.DAY_OF_MONTH);

		String dayName = getDayOfWeek(calendar.get(Calendar.DAY_OF_WEEK));
		builder1.append(dayName + ", ");

		String date = getMonth(calendar.get(Calendar.MONTH)) + " "
				+ String.format("%02d", day);

		SpannableString dateSpannable = new SpannableString(date);
		dateSpannable.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0,
				date.length(), 0);
		builder1.append(dateSpannable);

		String year = " " + calendar.get(Calendar.YEAR);
		SpannableString yearSpannable = new SpannableString(year);
		yearSpannable.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0,
				year.length(), 0);
		yearSpannable.setSpan(new RelativeSizeSpan(1.5f), 0, year.length(), 0);
		builder1.append(yearSpannable);

		return builder1.toString();

	}

	public static String getDDMMYYDate(long time) {

		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(time);

		String date = String
				.format("%02d", calendar.get(Calendar.DAY_OF_MONTH))
				+ "/ "
				+ calendar.get(Calendar.MONTH)
				+ "/ "
				+ calendar.get(Calendar.YEAR);

		return date;

	}

	public static String getDDMonDate(long time) {

		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(time);

		String date = String
				.format("%02d", calendar.get(Calendar.DAY_OF_MONTH))
				+ " "
				+ getMonth(calendar.get(Calendar.MONTH));

		return date;

	}

	private static String getDayOfWeek(int dayOfWeek) {
		String result = "";
		switch (dayOfWeek) {
		case Calendar.MONDAY:
			result = "Mon";
			break;
		case Calendar.TUESDAY:
			result = "Tue";
			break;
		case Calendar.WEDNESDAY:
			result = "Wed";
			break;
		case Calendar.THURSDAY:
			result = "Thu";
			break;
		case Calendar.FRIDAY:
			result = "Fri";
			break;
		case Calendar.SATURDAY:
			result = "Sat";
			break;
		case Calendar.SUNDAY:
			result = "Sun";
			break;
		}
		return result;
	}

	public static String getMonth(int month) {

		String result = "";
		switch (month) {

		case Calendar.JANUARY:
			result = "Jan";
			break;

		case Calendar.FEBRUARY:
			result = "Feb";
			break;

		case Calendar.MARCH:
			result = "Mar";
			break;

		case Calendar.APRIL:
			result = "Apr";
			break;

		case Calendar.MAY:
			result = "May";
			break;

		case Calendar.JUNE:
			result = "June";
			break;

		case Calendar.JULY:
			result = "July";
			break;

		case Calendar.AUGUST:
			result = "Aug";
			break;

		case Calendar.SEPTEMBER:
			result = "Sep";
			break;

		case Calendar.OCTOBER:
			result = "Oct";
			break;

		case Calendar.NOVEMBER:
			result = "Nov";
			break;

		case Calendar.DECEMBER:
			result = "Dec";
			break;

		}
		return result;

	}

	public static String getNotificationDateTime() {
		String phase = new String();
		StringBuilder builder = new StringBuilder();
		Calendar cal = Calendar.getInstance();
		int hour = cal.get(Calendar.HOUR_OF_DAY);
		int minute = cal.get(Calendar.MINUTE);
		if (hour < 13) {
			phase = "AM";
		} else {
			hour = hour - 12;
			phase = "PM";
		}
		builder.append(hour);
		builder.append(":");
		if (minute < 10)
			builder.append("0" + minute);
		else
			builder.append(minute);

		builder.append(" ");

		builder.append(phase);
		return builder.toString();
	}

}
