package com.yihecode.camera.ai.utils;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.SimpleTimeZone;
import java.util.TimeZone;

/**
* Time Format
*
* @author zhoumingxing
* @mail 465769438@qq.com
*/
public class TimeUtils {

    /**
* Format Change Time _yyyy-MM-dd HH:mm:ss
* @param date
* @return
*/
    public static String toYmdhms(Date date) {
        if(date == null) {
            return "";
        }
        return DateUtil.format(date, "yyyy-MM-dd HH:mm:ss");
    }

    public static String toYmd(String str) {
        if(StrUtil.isBlank(str)) {
            return "";
        }

        return str.replaceAll("-", "");
    }

    public static Date toTime(String str) {
        if(str == null) {
            return null;
        }

        //
try {
Date date = DateUtil.parse(str,"yyyy-MM-dd");

//
Calendar calendar1 = Calendar.getInstance();
calendar1.setTime(date);

//
Calendar calendar = Calendar.getInstance();
calendar.set(Calendar.YEAR, calendar1.get(Calendar.YEAR));
calendar.set(Calendar.MONTH, calendar1.get(Calendar.MONTH));
calendar.set(Calendar.DAY_OF_MONTH, calendar1.get(Calendar.DAY_OF_MONTH));

return calendar.getTime();
} catch (Exception e) {}

return null;
}

public static String day7() {
Calendar calendar = Calendar.getInstance();
calendar.add(Calendar.DAY_OF_MONTH, -8);
return DateUtil.format(calendar.getTime(),"yyyyMMdd");
}

public static String day() {
return DateUtil.format(new Date(),"yyyy-MM-dd");
}

public static String day15() {
return DateUtil.format(DateUtil.offsetDay(new Date(), -16),"yyyyMMdd");
}

public static String month3() {
Calendar calendar = Calendar.getInstance();
calendar.add(Calendar.MONTH, -3);
return DateUtil.format(calendar.getTime(),"yyyyMMdd");
}

public static String month6() {
Calendar calendar = Calendar.getInstance();
calendar.add(Calendar.MONTH, -6);
return DateUtil.format(calendar.getTime(),"yyyyMMdd");
}

public static String getFormatedDateString(float timeZoneOffset) {
if (timeZoneOffset > 13 || timeZoneOffset < -12) {
timeZoneOffset = 0;
}

int newTime = (int) (timeZoneOffset * 60 * 60 * 1000);
TimeZone timeZone;
String[] ids = TimeZone.getAvailableIDs(newTime);
if (ids.length == 0) {
timeZone = TimeZone.getDefault();
} else {
timeZone = new SimpleTimeZone(newTime, ids[0]);
}

SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
sdf.setTimeZone(timeZone);
return sdf.format(new Date());
}

public static Date getZero() {
Calendar calendar = Calendar.getInstance();
calendar.set(Calendar.HOUR_OF_DAY, 0); // Set h for 0
calendar.set(Calendar.MINUTE, 0); // Set min for 0
calendar.set(Calendar.SECOND, 0); // Set s for 0
return calendar.getTime();
}
public static Date getZero(Date date) {
Calendar calendar = Calendar.getInstance();
calendar.setTime(date);
calendar.set(Calendar.HOUR_OF_DAY, 0); // Set h for 0
calendar.set(Calendar.MINUTE, 0); // Set min for 0
calendar.set(Calendar.SECOND, 0); // Set s for 0
return calendar.getTime();
}
public static Date addDay(Date time, int day) {
Calendar calendar = Calendar.getInstance();
calendar.setTime(time);
calendar.add(Calendar.DAY_OF_MONTH, day);
return calendar.getTime();
}

// public static Date firstWeekSundayThisMonth() {
// Calendar calendar = Calendar.getInstance();
// calendar.set(Calendar.DAY_OF_MONTH, 1);
// calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
// return calendar.getTime();
//}
// public static Date lastWeekSaturdayThisMonth() {
// Calendar calendar = Calendar.getInstance();
// calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
// calendar.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
// return calendar.getTime();
//}

/**
* Get This Month No One day
* @return String
**/
public static Date getMonthStart() {
Calendar cal = Calendar.getInstance();
cal.add(Calendar.MONTH, 0);
cal.set(Calendar.DAY_OF_MONTH, 1);
Date time = cal.getTime();
return time;
}
public static Date getMonthStart(Date dd) {
Calendar cal = Calendar.getInstance();
cal.setTime(dd);
cal.add(Calendar.MONTH, 0);
cal.set(Calendar.DAY_OF_MONTH, 1);
Date time = cal.getTime();
return time;
}

/**
* Get This Month most after One day
* @return String
**/
public static Date getMonthEnd() {
Calendar cal = Calendar.getInstance();
cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
Date time = cal.getTime();
return time;
}
public static Date getMonthEnd(Date dd) {
Calendar cal = Calendar.getInstance();
cal.setTime(dd);
cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
Date time = cal.getTime();
return time;
}

/**
* Get This Week No One day
* @return String
**/
public static Date getWeekStart() {
Calendar cal = Calendar.getInstance();
cal.add(Calendar.WEEK_OF_MONTH, 0);
cal.set(Calendar.DAY_OF_WEEK, 2);
Date time = cal.getTime();
return time;
}
public static Date getWeekStart(Date dd) {
Calendar cal = Calendar.getInstance();
cal.setTime(dd);
cal.add(Calendar.WEEK_OF_MONTH, 0);
cal.set(Calendar.DAY_OF_WEEK, 2);
Date time = cal.getTime();
return time;
}

/**
* Get This Week most after One day
* @return String
**/
public static Date getWeekEnd() {
Calendar cal = Calendar.getInstance();
cal.set(Calendar.DAY_OF_WEEK, cal.getActualMaximum(Calendar.DAY_OF_WEEK));
cal.add(Calendar.DAY_OF_WEEK, 1);
Date time = cal.getTime();
return time;
}
public static Date getWeekEnd(Date dd) {
Calendar cal = Calendar.getInstance();
cal.setTime(dd);
cal.set(Calendar.DAY_OF_WEEK, cal.getActualMaximum(Calendar.DAY_OF_WEEK));
cal.add(Calendar.DAY_OF_WEEK, 1);
Date time = cal.getTime();
return time;
}

public static int getMaxDays() {
Calendar calendar = Calendar.getInstance();
int maxDays = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
return maxDays;
}
public static int getMaxDays(Date dd) {
Calendar calendar = Calendar.getInstance();
calendar.setTime(dd);
int maxDays = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
return maxDays;
}
}
