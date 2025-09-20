package com.jason.microstream.tool;

import android.content.Context;
import android.text.TextUtils;
import android.util.Pair;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class TimeUtils {

    public static final long INTERNEL_TIME = 1000 * 60;
    public static final long INTERNEL_WEEK_TIME = 1000 * 60 * 60 * 24 * 7;
    public final static SimpleDateFormat sFormatToMinute = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    public final static SimpleDateFormat sFormatToSecond = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public final static SimpleDateFormat sFormatToSecond2 = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    public final static SimpleDateFormat sFormatToDay = new SimpleDateFormat("yyyy-MM-dd");
    public final static SimpleDateFormat sFormatToWater = new SimpleDateFormat("yyyy.MM.dd");
    public final static SimpleDateFormat sChineseFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
    public final static SimpleDateFormat sChineseFormat2 = new SimpleDateFormat("MM月dd日 HH:mm");
    public final static SimpleDateFormat sChineseFormat3 = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
    public final static SimpleDateFormat sSimpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm");
    public final static SimpleDateFormat sSimpleDateFormat2 = new SimpleDateFormat("MM/dd HH:mm");
    public final static SimpleDateFormat sSimpleDateFormat3 = new SimpleDateFormat("yyyy/M/d HH:mm");
    public final static SimpleDateFormat sSimpleDateFormat4 = new SimpleDateFormat("MM-dd HH:mm");
    public final static SimpleDateFormat sSimpleDateFormatDay = new SimpleDateFormat("yyyy/MM/dd");
    public final static SimpleDateFormat sSimpleDateFormatDay4 = new SimpleDateFormat("yyyy/M/d");
    public final static SimpleDateFormat sSimpleDateFormatDay2 = new SimpleDateFormat("MM/dd");
    public final static SimpleDateFormat sSimpleDateFormatDay5 = new SimpleDateFormat("M.d");
    public final static SimpleDateFormat sSimpleDateFormatDay3 = new SimpleDateFormat("M/d");
    public final static SimpleDateFormat sHourMinutesFormat = new SimpleDateFormat("HH:mm");
    public final static SimpleDateFormat sHourMinuteSecondsFormat = new SimpleDateFormat("HH:mm:ss");
    public final static SimpleDateFormat sMinuteSecondsFormat = new SimpleDateFormat("mm:ss");
    public final static SimpleDateFormat sMinutesSecondsFormat = new SimpleDateFormat("mm:ss");
    public static SimpleDateFormat sYearMonthFormat = new SimpleDateFormat("yyyy年M月");
    public static final long MINUTE_MILLISECONDS = 1000 * 60;
    public static final long HOUR_MILLISECONDS = MINUTE_MILLISECONDS * 60;
    public static final long DAY_MILLISECONDS = HOUR_MILLISECONDS * 24;
    public static final long DAY_30_MILLISECONDS = DAY_MILLISECONDS * 30;
    public final static SimpleDateFormat sChineseMonthDay = new SimpleDateFormat("M月d日");
    private final static SimpleDateFormat sOnlyChineseMonthDay = new SimpleDateFormat("M月");
    private final static SimpleDateFormat sChineseYearMonthDay = new SimpleDateFormat("yyyy年M月d日");
    private final static SimpleDateFormat sYearMonthDay = new SimpleDateFormat("yyyy年 M月");
    public final static SimpleDateFormat sMonthDayFormat = new SimpleDateFormat("MM-dd HH:mm:ss");
    public final static SimpleDateFormat sYearMonthDayFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public final static SimpleDateFormat sMonthDay = new SimpleDateFormat("MM月dd日");

    public static boolean isYestoday(long now, int year, int month, int day) {
        Calendar calendar = TimeUtils.getCalByDefTZ();
        calendar.set(year, month, day);
        long yestoday = calendar.getTimeInMillis();
        long diff = now - yestoday;
        if (diff > 0 && diff <= 86400000) {
            return true;
        }
        return false;
    }

    public static boolean isYestoday(long time) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(time + DAY_MILLISECONDS);
        Calendar calendar = TimeUtils.getCalByDefTZ();
        calendar.setTimeInMillis(System.currentTimeMillis());
        if (cal.get(Calendar.YEAR) == calendar.get(Calendar.YEAR)
                && cal.get(Calendar.MONTH) == calendar.get(Calendar.MONTH)
                && cal.get(Calendar.DAY_OF_MONTH) == calendar.get(Calendar.DAY_OF_MONTH)) {
            return true;
        }
        return false;
    }

    public static boolean isInWeek(long now, int year, int month, int day) {
        Calendar calendar = TimeUtils.getCalByDefTZ();
        calendar.set(year, month, day);
        long yestoday = calendar.getTimeInMillis();
        long diff = now - yestoday;
        if (diff > 86400000 && diff <= 86400000 * 6) {
            return true;
        }
        return false;
    }

    public static long add8Hours(long time) {
        return time + 8 * 60 * 60 * 1000;
    }

    public static long reduce8Hours(long time) {
        return time - 8 * 60 * 60 * 1000;
    }


    public static boolean isCloseEnough(long time1, long time2) {
        if (time1 - time2 < INTERNEL_TIME) {
            return true;
        } else {
            return false;
        }
    }

    public static String formatTimeDiffYear(long time) {
        if (isSameYear(time)) {
            return formatToChineseDate(time);
        }
        return formatToFullChineseDate(time);
    }

    public static boolean isSameYear(long time) {

        Calendar c1 = TimeUtils.getCalByDefTZ();
        c1.setTimeInMillis(time);

        Calendar c2 = TimeUtils.getCalByDefTZ();
        c2.setTimeInMillis(System.currentTimeMillis());

        if (c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR)) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isInSameYear(long time1, long time2) {
        Calendar calendar = TimeUtils.getCalByDefTZ();
        calendar.setTimeInMillis(time1);
        int year1 = calendar.get(Calendar.YEAR);
        calendar.setTimeInMillis(time2);
        int year2 = calendar.get(Calendar.YEAR);
        return year1 == year2;
    }

    /**
     * @return
     */
    public static String formateNowTime() {
        Calendar c1 = TimeUtils.getCalByDefTZ();
        c1.setTimeInMillis(System.currentTimeMillis());
        return sFormatToSecond.format(c1.getTime());
    }

    public static String formateTime2(long time) {
        Calendar c1 = TimeUtils.getCalByDefTZ();
        c1.setTimeInMillis(time);
        return sFormatToMinute.format(c1.getTime());
    }

    public static String formateTimeYY_MM_DD(long time) {
        Calendar c1 = TimeUtils.getCalByDefTZ();
        c1.setTimeInMillis(time);
        return sFormatToDay.format(c1.getTime());
    }

    public static String formateTimeMM_DD(long time) {
        Calendar c1 = TimeUtils.getCalByDefTZ();
        c1.setTimeInMillis(time);
        return sSimpleDateFormatDay2.format(c1.getTime());
    }

    public static String formateTimeMMDD(long time) {
        synchronized (sSimpleDateFormatDay5) {
            Calendar c1 = TimeUtils.getCalByDefTZ();
            c1.setTimeInMillis(time);
            return sSimpleDateFormatDay5.format(c1.getTime());
        }
    }

    public static String[] formateTimeMM() {
        Calendar c1 = TimeUtils.getCalByDefTZ();
        c1.setTimeInMillis(System.currentTimeMillis());
        String[] result = sFormatToDay.format(c1.getTime()).split("-");
        return result;
    }

    public static String getHourAndMin(long time) {
        try {
            return sHourMinutesFormat.format(new Date(time));
        } catch (Exception e) {
            return null;
        }
    }

    public static String getHourAndMin(String time) {
        long value = 0;
        try {
            value = Long.parseLong(time);
        } catch (Exception e) {

        }
        return sHourMinutesFormat.format(new Date(value));
    }

    /**
     * 00:59
     */
    public static String getMinAndSecond(long time) {
        try {
            return sMinutesSecondsFormat.format(new Date(time));
        } catch (Exception e) {
            return "00:00";
        }
    }

    public static String length(long beginTime, long endTime) {
        if (beginTime > endTime) {
            long tmp = beginTime;
            beginTime = endTime;
            endTime = tmp;
        }
        long left = endTime - beginTime;
        long day = left / TimeUnit.ONE_DAY;
        left = left - day * TimeUnit.ONE_DAY;
        long hour = left / TimeUnit.ONE_HOUR;
        left = left - hour * TimeUnit.ONE_HOUR;
        long min = left / TimeUnit.ONE_MIN;
        if (day > 0)
            return day + "天" + hour + "小时" + min + "分";
        if (hour > 0)
            return hour + "小时" + min + "分";
        return min + "分";
    }

    public static String getVoteTime(long second) {
        //YY-MM-DD HH:MM:SS
        //2015-10-8 22:02:22
        if (equalYear(second)) {
            return formatSimpleDate2(second);
        } else {
            return formatSimpleDate(second);
        }
    }

    /**
     * 计算两个时间占几天长度  2016.9.23 10:00:00 - 2016.9.24 01:00:00 长度为2
     *
     * @param beginTime
     * @param endTime
     * @return
     */
    public static int lengthByDay(long beginTime, long endTime) {
        if (beginTime > endTime) {
            long tmp = beginTime;
            beginTime = endTime;
            endTime = tmp;
        }
        Calendar beginCalendar = TimeUtils.getCalByDefTZ();
        beginCalendar.setTimeInMillis(beginTime);
        beginCalendar.set(Calendar.HOUR_OF_DAY, 0);
        beginCalendar.set(Calendar.SECOND, 0);
        beginCalendar.set(Calendar.MINUTE, 0);
        beginCalendar.set(Calendar.MILLISECOND, 0);
        long newBeginTime = beginCalendar.getTimeInMillis();

        return (int) (Math.ceil(endTime - newBeginTime) / TimeUnit.ONE_DAY) + 1;
    }

    /**
     * 参数最好不要为负值
     *
     * @param year
     * @param month
     * @param day   The first day of the month has value 1
     * @param hour
     * @param min
     * @return
     */
    public static long getTimeInMillis(int year, int month, int day, int hour, int min) {
        Calendar calendar = TimeUtils.getCalByDefTZ();
        year = year > 0 ? year : calendar.get(Calendar.YEAR);
        month = month >= 0 ? month : calendar.get(Calendar.MONTH);
        //The first day of the month has value 1
        day = day > 0 ? day : 1;
        calendar.set(year, month, day, hour, min, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }

    private static String getFormatNumber(int num) {
        if (num < 10) {
            return "0" + num;
        }
        return String.valueOf(num);
    }

    /**
     * 将long转为String yyyy/MM/dd HH:mm
     *
     * @param time
     * @return
     */
    final public static String formatToMinute(long time) {
        synchronized (sSimpleDateFormat) {
            Calendar cal = TimeUtils.getCalByDefTZ();
            cal.setTimeInMillis(time);
            return sSimpleDateFormat.format(cal.getTime());
        }
    }

    final public static String formatMinuteSecond(long time) {
        synchronized (sMinuteSecondsFormat) {
            Calendar cal = TimeUtils.getCalByDefTZ();
            cal.setTimeInMillis(time);
            return sMinuteSecondsFormat.format(cal.getTime());
        }
    }

    //将long转为String yyyy-MM-dd HH:mm
    final public static String formatToMinute2(long time) {
        synchronized (sFormatToMinute) {
            Calendar cal = TimeUtils.getCalByDefTZ();
            cal.setTimeInMillis(time);
            return sFormatToMinute.format(cal.getTime());
        }
    }

    /**
     * 计算一段时间的显示 mm:ss 或 HH:mm:ss
     *
     * @param second
     * @return
     */
    public static String formatToMmss(int second) {
        StringBuffer sb = new StringBuffer();
        int hh = second / 3600;
        if (hh > 0) {
            if (hh < 10) {
                sb.append("0");
            }
            sb.append(hh);
            sb.append(":");
        }
        int mm = second % 3600 / 60;
        if (mm < 10) {
            sb.append("0");
        }
        sb.append(mm);
        sb.append(":");
        int ss = second % 60;
        if (ss < 10) {
            sb.append("0");
        }
        sb.append(ss);
        return sb.toString();
    }

    /**
     * 将Long转为String yyyy/MM/dd HH:mm:ss
     *
     * @param time
     * @return
     */
    final public static String formatToSecond(long time) {
        synchronized (sFormatToSecond2) {
            Calendar cal = TimeUtils.getCalByDefTZ();
            cal.setTimeInMillis(time);
            return sFormatToSecond2.format(cal.getTime());
        }
    }

    final public static String formatToSecond2(long time) {
        synchronized (sFormatToSecond) {
            Calendar cal = TimeUtils.getCalByDefTZ();
            cal.setTimeInMillis(time);
            return sFormatToSecond.format(cal.getTime());
        }
    }

    final public static String formatCaitongTime(long time) {
        Calendar cal = TimeUtils.getCalByDefTZ();
        cal.setTimeInMillis(time);
        if (isToday(time)) {
            synchronized (sHourMinuteSecondsFormat) {
                return "今天 " + sHourMinuteSecondsFormat.format(cal.getTime());
            }
        } else if (isYestoday(time)) {
            synchronized (sHourMinuteSecondsFormat) {
                return "昨天 " + sHourMinuteSecondsFormat.format(cal.getTime());
            }
        } else if (isInSameYear(time, System.currentTimeMillis())) {
            synchronized (sMonthDayFormat) {
                return sMonthDayFormat.format(cal.getTime());
            }
        } else {
            synchronized (sYearMonthDayFormat) {
                return sYearMonthDayFormat.format(cal.getTime());
            }
        }
    }

    final public static String formatHourMinuteSeconds(long time) {
        synchronized (sHourMinuteSecondsFormat) {
            Calendar cal = TimeUtils.getCalByDefTZ();
            cal.setTimeInMillis(time);
            return sHourMinuteSecondsFormat.format(cal.getTime());
        }
    }

    public static String formatTimeLength(long time) {
        StringBuilder result = new StringBuilder();
        if (time >= TimeUnit.ONE_HOUR) {
            long hour = time / TimeUnit.ONE_HOUR;
            time = time - hour * TimeUnit.ONE_HOUR;
            result.append(formatNum(hour)).append(":");
        }
        long min = time / TimeUnit.ONE_MIN;
        long sec = (time - min * TimeUnit.ONE_MIN) / TimeUnit.ONE_SEC;
        result.append(formatNum(min)).append(":").append(formatNum(sec));
        return result.toString();
    }

    private static String formatNum(long num) {
        if (num > 9) {
            return String.valueOf(num);
        }
        return "0" + num;
    }

    /**
     * 返回String格式：yyyy/MM/dd
     *
     * @param time
     * @return
     */
    final public static String formatToDay(long time) {
        synchronized (sSimpleDateFormatDay) {
            Calendar cal = TimeUtils.getCalByDefTZ();
            cal.setTimeInMillis(time);
            return sSimpleDateFormatDay.format(cal.getTime());
        }
    }

    //返回String格式：yyyy-MM-dd
    final public static String formatToDay2(long time) {
        synchronized (sFormatToDay) {
            Calendar cal = TimeUtils.getCalByDefTZ();
            cal.setTimeInMillis(time);
            return sFormatToDay.format(cal.getTime());
        }
    }

    final public static String formatToDay3(long time) {
        synchronized (sFormatToWater) {
            Calendar cal = TimeUtils.getCalByDefTZ();
            cal.setTimeInMillis(time);
            return sFormatToWater.format(cal.getTime());
        }
    }

    //返回String格式：yyyy-MM-dd
    final public static String formatToDay2Today() {
        synchronized (sFormatToDay) {
            Calendar cal = TimeUtils.getCalByDefTZ();
            return sFormatToDay.format(cal.getTime());
        }
    }

    public static String formatToChineseDate(long time) {
        synchronized (sChineseFormat2) {
            Calendar cal = TimeUtils.getCalByDefTZ();
            cal.setTimeInMillis(time);
            return sChineseFormat2.format(cal.getTime());
        }
    }

    public static String formatToChineseDate3(long time) {
        synchronized (sChineseFormat3) {
            Calendar cal = TimeUtils.getCalByDefTZ();
            cal.setTimeInMillis(time);
            return sChineseFormat3.format(cal.getTime());
        }
    }

    public static String formatToFullChineseDate(long time) {
        synchronized (sChineseFormat) {
            Calendar cal = TimeUtils.getCalByDefTZ();
            cal.setTimeInMillis(time);
            return sChineseFormat.format(cal.getTime());
        }
    }

    final public static String formatToYearMonth(long time) {
        if (sYearMonthFormat == null) {
            sYearMonthFormat = new SimpleDateFormat("yyyy年M月");
        }
        Calendar cal = TimeUtils.getCalByDefTZ();
        cal.setTimeInMillis(time);
        return sYearMonthFormat.format(cal.getTime());
    }

    /**
     * @param time
     * @return yyyy/MM/dd H:mm
     */
    public static String formatSimpleDate(long time) {
        synchronized (sSimpleDateFormat) {
            return sSimpleDateFormat.format(new Date(time));
        }
    }

    /**
     * @param time
     * @return yyyy/M/d H:mm
     */
    public static String formatSimpleDate3(long time) {
        synchronized (sSimpleDateFormat3) {
            return sSimpleDateFormat3.format(new Date(time));
        }
    }

    /**
     * @param time
     * @return MM-dd HH:mm
     */
    public static String formatSimpleDate4(long time) {
        synchronized (sSimpleDateFormat4) {
            return sSimpleDateFormat4.format(new Date(time));
        }
    }

    /**
     * @param time
     * @return MM/dd H:mm
     */
    public static String formatSimpleDate2(long time) {
        synchronized (sSimpleDateFormat2) {
            return sSimpleDateFormat2.format(new Date(time));
        }
    }

    public static String formatSimpleDateDay4(long time) {
        synchronized (sSimpleDateFormatDay4) {
            return sSimpleDateFormatDay4.format(new Date(time));
        }
    }

    /**
     * MM/dd
     *
     * @param time
     * @return
     */
    public static String formatSimpleDateDay2(long time) {
        synchronized (sSimpleDateFormatDay2) {
            return sSimpleDateFormatDay2.format(new Date(time));
        }
    }

    /**
     * MM/dd
     *
     * @param time
     * @return
     */
    public static String formatSimpleDateDay3(long time) {
        synchronized (sSimpleDateFormatDay3) {
            return sSimpleDateFormatDay3.format(new Date(time));
        }
    }

    /**
     * @param time
     * @return MM/dd
     */
    public static String formatChineseMonthDay(long time) {
        synchronized (sChineseMonthDay) {
            return sChineseMonthDay.format(new Date(time));
        }
    }

    public static String formatOnlyMonthDay(long time) {
        synchronized (sOnlyChineseMonthDay) {
            return sOnlyChineseMonthDay.format(new Date(time));
        }
    }

    /**
     * @param time
     * @return mm:ss
     */
    public static String formatMinutesSecond(long time) {
        return sHourMinutesFormat.format(new Date(time));
    }

    /**
     * 将string类型的时间转换成long类型
     *
     * @param time yyyy-MM-dd HH:mm 或者 yyyy/MM/dd HH:mm
     * @return 返回0时说明转换失败
     */
    final public static long timeToMinute(String time) {
        if (!TextUtils.isEmpty(time) && time.contains("/")) {
            return timeToMinute1(time);
        } else {
            return timeToMinute2(time);
        }
    }

    //yyyy/MM/dd HH:mm
    private static long timeToMinute1(String time) {
        synchronized (sSimpleDateFormat) {
            try {
                Date date = sSimpleDateFormat.parse(time);
                return date.getTime();
            } catch (ParseException e) {

            }
            return 0;
        }
    }

    //yyyy-MM-dd HH:mm
    private static long timeToMinute2(String time) {
        synchronized (sFormatToMinute) {
            try {
                Date date = sFormatToMinute.parse(time);
                return date.getTime();
            } catch (ParseException e) {

            }
            return 0;
        }
    }

    /**
     * 将string类型的时间转换成long类型
     *
     * @param time yyyy-MM-dd 或者 yyyy/MM/dd
     * @return 返回0时说明转换失败
     */
    final public static long timeToDay(String time) {
        if (!TextUtils.isEmpty(time) && time.contains("/")) {
            return timeToDay1(time);
        } else {
            return timeToDay2(time);
        }
    }

    //yyyy/MM/dd
    public static long timeToDay1(String time) {
        synchronized (sSimpleDateFormatDay) {
            try {
                Date date = sSimpleDateFormatDay.parse(time);
                return date.getTime();
            } catch (ParseException e) {

            }
            return 0;
        }
    }

    //yyyy-MM-dd
    public static long timeToDay2(String time) {
        synchronized (sFormatToDay) {
            try {
                Date date = sFormatToDay.parse(time);
                return date.getTime();
            } catch (ParseException e) {

            }
            return 0;
        }
    }

    /**
     * 将string类型的时间转换成long类型
     *
     * @return 返回0时说明转换失败
     */
    final public static long timeToSecond(String time) {
        if (!TextUtils.isEmpty(time) && time.contains("/")) {
            return timeToSecond1(time);
        } else {
            return timeToSecond2(time);
        }
    }

    //yyyy/MM/dd HH:mm:ss
    private static long timeToSecond1(String time) {
        synchronized (sFormatToSecond2) {
            try {
                Date date = sFormatToSecond2.parse(time);
                return date.getTime();
            } catch (ParseException e) {

            }
            return 0;
        }
    }

    public static long timeToSecond2(String time) {
        synchronized (sFormatToSecond) {
            try {
                Date date = sFormatToSecond.parse(time);
                return date.getTime();
            } catch (Exception e) {

            }
            return 0;
        }
    }

    /**
     * @param completeTime
     * @return
     */
    final public static long fullTimeStrToLong(String completeTime) {
        synchronized (sFormatToSecond) {
            if (TextUtils.isEmpty(completeTime)) {
                return 0;
            }
            Date date = null;
            try {
                date = sFormatToSecond.parse(completeTime);
                return date.getTime();
            } catch (ParseException e) {

            }
            return 0;
        }
    }

    final public static long[] calToDayTime(Calendar cal) {
        long time[] = new long[2];
        Calendar calendar = TimeUtils.getCalByDefTZ();
        calendar.setTimeInMillis(cal.getTimeInMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        time[0] = calendar.getTimeInMillis();
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        time[1] = calendar.getTimeInMillis();
        return time;
    }

    final public static boolean isInSameDay(long time1, long time2) {
        Calendar calendar = TimeUtils.getCalByDefTZ();
        calendar.setTimeInMillis(time1);
        return isInTheDay(time2, calendar);
    }

    public static boolean isInSameMonth(long time1, long time2) {
        Calendar calendar = TimeUtils.getCalByDefTZ();
        calendar.setTimeInMillis(time1);
        int year1 = calendar.get(Calendar.YEAR);
        int month1 = calendar.get(Calendar.MONTH);
        calendar.setTimeInMillis(time2);
        int year2 = calendar.get(Calendar.YEAR);
        int month2 = calendar.get(Calendar.MONTH);
        return year1 == year2 && month1 == month2;
    }


    /**
     * 比较时间 相等还是大于小于
     *
     * @param time1
     * @param time2
     * @return
     */
    public static final int TYPE_EQUAL = 0;
    public static final int TYPE_LESS_THAN = 1;
    public static final int TYPE_GREATER_THAN = 2;

    final public static int compareDay(long time1, long time2) {
        Calendar calendar1 = TimeUtils.getCalByDefTZ();
        calendar1.setTimeInMillis(time1);
        calendar1.set(Calendar.HOUR_OF_DAY, 0);
        calendar1.set(Calendar.MINUTE, 0);
        calendar1.set(Calendar.SECOND, 0);
        calendar1.set(Calendar.MILLISECOND, 0);

        Calendar calendar2 = TimeUtils.getCalByDefTZ();
        calendar2.setTimeInMillis(time2);

        calendar2.set(Calendar.HOUR_OF_DAY, 0);
        calendar2.set(Calendar.MINUTE, 0);
        calendar2.set(Calendar.SECOND, 0);
        calendar2.set(Calendar.MILLISECOND, 0);
        if (calendar2.equals(calendar1)) {
            return TYPE_EQUAL;
        } else {
            return calendar1.before(calendar2) ? TYPE_LESS_THAN : TYPE_GREATER_THAN;
        }
    }

    /**
     * 这一天的0时0分0秒
     *
     * @param time
     * @return
     */
    public static long theDayBeginning(long time) {
        Calendar calendar = TimeUtils.getCalByDefTZ();
        calendar.setTimeInMillis(time);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }

    public static long theMonthBeginning(long time) {
        Calendar calendar = TimeUtils.getCalByDefTZ();
        calendar.setTimeInMillis(time);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }

    public static long theMonthLastDayBegining(long time) {
        Calendar calendar = TimeUtils.getCalByDefTZ();
        calendar.setTimeInMillis(time);
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }

    public static long theYearBeginning(long time) {
        Calendar calendar = TimeUtils.getCalByDefTZ();
        calendar.setTimeInMillis(time);
        calendar.set(Calendar.DAY_OF_YEAR, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }

    public static long theYearLastDayBegining(long time) {
        Calendar calendar = TimeUtils.getCalByDefTZ();
        calendar.setTimeInMillis(time);
        calendar.set(Calendar.MONTH, Calendar.DECEMBER);
        calendar.set(Calendar.DAY_OF_MONTH, 31);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }

    /**
     * 这一天的23时59分59秒999
     *
     * @param time
     * @return
     */
    final public static long theDayEnd(long time) {
        Calendar calendar = TimeUtils.getCalByDefTZ();
        calendar.setTimeInMillis(time);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar.getTimeInMillis();
    }

    public static boolean isDayEnd(long time) {
        Calendar calendar = TimeUtils.getCalByDefTZ();
        calendar.setTimeInMillis(time);
        return calendar.get(Calendar.HOUR_OF_DAY) == 23 && calendar.get(Calendar.MINUTE) == 59;
    }

    final public static boolean isInTheDay(long time, Calendar cal) {
        long[] dayTime = calToDayTime(cal);
        if (time >= dayTime[0] && time <= dayTime[1]) {
            return true;
        }
        return false;
    }

    /**
     * 判断是否已经过了某一日期
     *
     * @param scheduleTime
     */
    final public static boolean isOverdue(Long scheduleTime) {
        return scheduleTime < System.currentTimeMillis();
    }

    public static boolean isToday(Calendar cal) {
        Calendar calendar = TimeUtils.getCalByDefTZ();
        calendar.setTimeInMillis(System.currentTimeMillis());
        if (cal.get(Calendar.YEAR) == calendar.get(Calendar.YEAR)
                && cal.get(Calendar.MONTH) == calendar.get(Calendar.MONTH)
                && cal.get(Calendar.DAY_OF_MONTH) == calendar.get(Calendar.DAY_OF_MONTH)) {
            return true;
        }
        return false;
    }

    public static boolean isToday(long time) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(time);
        Calendar calendar = TimeUtils.getCalByDefTZ();
        calendar.setTimeInMillis(System.currentTimeMillis());
        if (cal.get(Calendar.YEAR) == calendar.get(Calendar.YEAR)
                && cal.get(Calendar.MONTH) == calendar.get(Calendar.MONTH)
                && cal.get(Calendar.DAY_OF_MONTH) == calendar.get(Calendar.DAY_OF_MONTH)) {
            return true;
        }
        return false;
    }

    public static boolean isValidTime(int year, int month, int day, long startTime, long endTime) {
        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        calendar.set(year, month, day);
        if (calendar.getTimeInMillis() > startTime && calendar.getTimeInMillis() < endTime) {
            return true;
        }
        return false;
    }

    public static long getToday() {
        return getTodayCalendar().getTimeInMillis();
    }

    public static Calendar getTodayCalendar() {
        Calendar c = TimeUtils.getCalByDefTZ();
        c.setTimeInMillis(System.currentTimeMillis());
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c;
    }

    public static Calendar getMonthCalendar() {
        Calendar calendar = TimeUtils.getCalByDefTZ();
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar;
    }

    public static long getTomorrow() {
        Calendar calendar = getTodayCalendar();
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        return calendar.getTimeInMillis();
    }

    public static Date getTodayDate() {
        return getTodayCalendar().getTime();
    }

    public static Calendar getYestDayCalendar() {
        Calendar c = TimeUtils.getCalByDefTZ();
        c.add(Calendar.DAY_OF_MONTH, -1);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c;
    }

    public static long getYesterday() {
        return getYestDayCalendar().getTimeInMillis();
    }

    public static long getDayOfCalendar(Calendar calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }

    public static long getDayByOffset(int offset) {
        return getToday() - DAY_MILLISECONDS * offset;
    }

    public static long getDayOfMilliseconds(long time) {
        Calendar calendar = TimeUtils.getCalByDefTZ();
        calendar.setTimeInMillis(time);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }

    public static long getMonthOfMilliseconds(long time) {
        Calendar calendar = TimeUtils.getCalByDefTZ();
        calendar.setTimeInMillis(time);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }

    public static long getDayEndTime(long time) {
        Calendar calendar = TimeUtils.getCalByDefTZ();
        calendar.setTimeInMillis(time);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar.getTimeInMillis();
    }

    public static Date getWeekdayBeforeDate(Date date) {
        Calendar cal = TimeUtils.getCalByDefTZ();
        cal.setTime(date);
        if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
            cal.add(Calendar.DAY_OF_YEAR, -6);
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.MILLISECOND, 0);
        } else {
            cal.add(Calendar.DAY_OF_YEAR, -cal.get(Calendar.DAY_OF_WEEK) + 2);
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.MILLISECOND, 0);
        }
        return cal.getTime();
    }

    /**
     * @param hour   24小时制
     * @param minute
     * @return 返回0时，转换失败
     */
    public static long todayTime(int hour, int minute) {
        StringBuilder timeStr = new StringBuilder();
        Calendar calendar = TimeUtils.getCalByDefTZ();
        calendar.setTimeInMillis(System.currentTimeMillis());
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        timeStr.append(year + "-").append(String.format("%02d", month + 1)).append("-").append(String.format("%02d", day))
                .append(" " + hour + ":" + minute);
        return timeToMinute(timeStr.toString());
    }

    /**
     * 获取任务详情的创建时间
     *
     * @param time
     * @return
     */
    public static String getTaskCreateTime(long time) {
        Calendar calendar = TimeUtils.getCalByDefTZ();
        int currentYear = calendar.get(Calendar.YEAR);
        calendar.setTime(new Date(time));
        int year = calendar.get(Calendar.YEAR);
        if (currentYear != year) {
            synchronized (sChineseYearMonthDay) {
                return sChineseYearMonthDay.format(time);
            }
        } else {
            synchronized (sChineseMonthDay) {
                return sChineseMonthDay.format(time);
            }
        }
    }

    public static String formatChineseTime(long time) {
        synchronized (sChineseYearMonthDay) {
            Calendar cal = TimeUtils.getCalByDefTZ();
            cal.setTimeInMillis(time);
            return sChineseYearMonthDay.format(cal.getTime());
        }
    }


    public static String formatYearMonth(long time) {
        synchronized (sYearMonthDay) {
            Calendar cal = TimeUtils.getCalByDefTZ();
            cal.setTimeInMillis(time);
            return sYearMonthDay.format(cal.getTime());
        }
    }

    public static String formatMonthDay(long time) {
        synchronized (sMonthDay) {
            Calendar cal = TimeUtils.getCalByDefTZ();
            cal.setTimeInMillis(time);
            return sMonthDay.format(cal.getTime());
        }
    }

    public static String getWeekName(long time) {
        return dateToWeekName(new Date(time));
    }

    private static String dateToWeekName(Date date) {
        SimpleDateFormat df = new SimpleDateFormat("E", Locale.getDefault());
        return df.format(date);
    }

    public static String getDateDescription(long time) {
        long diff = System.currentTimeMillis() - time;
        String des;
        if (diff < HOUR_MILLISECONDS) {
            des = (int) (diff / (1000 * 60)) + "分钟前";
        } else if (diff < DAY_MILLISECONDS) {
            des = (int) (diff / (1000 * 60 * 60)) + "小时前";
        } else {
            des = (int) (diff / (1000 * 60 * 60 * 24)) + "天前";
        }
        return des;
    }

    public static String getYearMonth(long date, Context mContext) {
        Calendar cal = TimeUtils.getCalByDefTZ();
        cal.setTime(new Date(date));
        return cal.get(Calendar.YEAR) + "." + (cal.get(Calendar.MONTH) + 1);
    }

    public static boolean equalYear(long date) {
        Calendar cal = TimeUtils.getCalByDefTZ();
        cal.setTime(new Date(date));
        int year = cal.get(Calendar.YEAR);
        cal.setTime(new Date(System.currentTimeMillis()));
        int currentYear = cal.get(Calendar.YEAR);

        return year == currentYear;
    }

    public static long getDefaultPhoneOrderTime() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHH");
        long currentMillis = System.currentTimeMillis();
        try {
            currentMillis = simpleDateFormat.parse(simpleDateFormat.format(new Date())).getTime();
        } catch (ParseException e) {
        }
        return currentMillis + TimeUnit.ONE_HOUR * 2;
    }

    /**
     * if 当前时间  mm <= 30
     * dtime = hh + 1
     * else
     * dtime = hh + 2
     *
     * @return
     */
    public static long getDefaultRemindTime() {
        Calendar calendar = TimeUtils.getCalByDefTZ();
        int minute = calendar.get(Calendar.MINUTE);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHH");
        long currentMillis = System.currentTimeMillis();
        try {
            currentMillis = simpleDateFormat.parse(simpleDateFormat.format(new Date())).getTime();
        } catch (ParseException e) {
        }

        if (minute <= 30) {
            return currentMillis + 1 * 60 * 60 * 1000;
        } else {
            return currentMillis + 2 * 60 * 60 * 1000;
        }
    }

    /**
     * 获取时间是在上午,下午,还是晚上
     * return 0:上午 1:下午 2:晚上
     */
    public static int getEventByTime(long time) {
        Calendar calendar = TimeUtils.getCalByDefTZ();
        calendar.setTimeInMillis(time);
        return getEventByTime(calendar);
    }

    public static int getEventByTime(Calendar calendar) {
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        return getEventByTime(hour);
    }

    public static int getEventByTime(int hour) {
        if (hour < 12) {
            return 0;
        } else if (hour < 18) {
            return 1;
        } else {
            return 2;
        }
    }

    /**
     * 是否跨年
     *
     * @param time1
     * @param time2
     * @return
     */
    public static boolean equalsYear(long time1, long time2) {
        Calendar calendar = TimeUtils.getCalByDefTZ();
        calendar.setTimeInMillis(time1);
        int year1 = calendar.get(Calendar.YEAR);
        calendar.setTimeInMillis(time2);
        int year2 = calendar.get(Calendar.YEAR);
        return year1 == year2;
    }

    public static Calendar getCurrentWeekStart() {
        Calendar cal = TimeUtils.getCalByDefTZ();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        //默认周日是一周的开始，需要定位到上周再取周一
        if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
            cal.add(Calendar.DAY_OF_MONTH, -1);
        }
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        return cal;
    }

    public static Calendar getCalByDefTZ() {
        return Calendar.getInstance(getDefaultTimeZone());
    }

    public static TimeZone getDefaultTimeZone() {
        return TimeZone.getTimeZone("Asia/Shanghai");
    }

    /**
     * 获取以XX小时XX分钟描述的时间
     *
     * @param time 毫秒
     * @return XX小时XX分钟
     */
    public static String getTimeDes(long time) {
        long minutes = time / (1000 * 60);
        if (time % (1000 * 60) != 0) {
            minutes += 1;
        }
        StringBuilder sb = new StringBuilder();
        int hour = (int) (minutes / 60);
        if (hour != 0) {
            sb.append(hour);
            sb.append("小时");
        }
        int min = (int) (minutes % 60);
        //异常终止且只有一个点的时候会导致两个时间一样，现在要求最低1分钟
        if (hour == 0 && min == 0) {
            min = 1;
        }
        sb.append(min);
        sb.append("分钟");
        return sb.toString();
    }

    private final static SimpleDateFormat TimeZoneDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    //根据时区转化时间
    public static long transformTimeZone(long srcTime, TimeZone srcZone, TimeZone desZone) {
        synchronized (TimeZoneDateFormat) {
            try {
                TimeZoneDateFormat.setTimeZone(srcZone);
                String format = TimeZoneDateFormat.format(srcTime);
                TimeZoneDateFormat.setTimeZone(desZone);
                return TimeZoneDateFormat.parse(format).getTime();
            } catch (ParseException e) {
                return 0;
            }
        }
    }

    //合并两个时间，一个取日期，一个取时分
    public static long mergeTime(long date, long time) {
        long dateBegin = TimeUtils.theDayBeginning(date);
        long timeBegin = TimeUtils.theDayBeginning(time);
        return dateBegin + time - timeBegin;
    }

    public static long getCurrentSecond() {
        return System.currentTimeMillis() / 1000;
    }

    public static int getDayOfWeek(Calendar calendar) {
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        if (dayOfWeek == 1) {
            return 7;
        } else {
            return dayOfWeek - 1;
        }
    }

    public static Pair<Integer, Integer> getMonthDay(long time) {
        Calendar calendar = getCalByDefTZ();
        calendar.setTimeInMillis(time);
        return new Pair<>(calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
    }
}