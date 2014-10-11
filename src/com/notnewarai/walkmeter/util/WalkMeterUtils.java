package com.notnewarai.walkmeter.util;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;



public class WalkMeterUtils {
	public static String dateformat(String format, Date date, int afterDay) {
		Calendar ca = Calendar.getInstance();
		ca.setTime(date);
		if (afterDay != 0)
			ca.add(Calendar.DATE, afterDay);
		SimpleDateFormat df = new SimpleDateFormat(format);
		return df.format(ca.getTime());
	}
}
