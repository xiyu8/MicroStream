package com.jason.microstream.tool;

import android.content.res.Resources;

import com.jason.microstream.MsApplication;
import com.jason.microstream.R;

import java.util.Calendar;
import java.util.Date;

/**
 * 暂时用来存放和业务逻辑耦合的时间相关的工具方法
 * Created by Administrator on 2016/11/1.
 */
public class TimeUtil2 {
    /**
     * 与服务端时间比较
     *
     * @param time
     * @return
     */
//    final public static boolean isOverdueRealTime(Long time) {
//        return time < AccountUtils.getInstance().getNowTime();
//    }
//
//    final public static boolean beforeToday(Long time) {
//        return time < TimeUtils.theDayBeginning(AccountUtils.getInstance().getNowTime());
//    }

    //返回the day of the month
    public static String getDayOfMonth(long time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        return String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
    }

    public static String getMonth(long date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date(date));
        return cal.get(Calendar.MONTH) + 1 + MsApplication.getInstance().getResources().getString(R.string.month);
    }

    public static String getYear(long date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date(date));
        return cal.get(Calendar.YEAR) + MsApplication.getInstance().getResources().getString(R.string.year);
    }

    public static String getSimpleDateString(long targetDate) {
        long now = System.currentTimeMillis();
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(now);
        int currentYear = calendar.get(Calendar.YEAR);
        int currentMonth = calendar.get(Calendar.MONTH) + 1;

        int currentDay = calendar.get(Calendar.DAY_OF_MONTH);

        calendar.setTimeInMillis(targetDate);
        int targetYear = calendar.get(Calendar.YEAR);
        int targetMonth = calendar.get(Calendar.MONTH) + 1;
        int targetDay = calendar.get(Calendar.DAY_OF_MONTH);

        int targetHour = calendar.get(Calendar.HOUR_OF_DAY);
        String hour = targetHour >= 10 ? String.valueOf(targetHour) : "0" + targetHour;
        int targetMinute = calendar.get(Calendar.MINUTE);
        String minute = targetMinute >= 10 ? String.valueOf(targetMinute) : "0" + targetMinute;

        if (currentYear == targetYear && currentMonth == targetMonth && currentDay == targetDay) {  //今天
            return hour + ":" + minute;
        }
        Resources resource = MsApplication.getInstance().getResources();
        if (TimeUtils.isYestoday(now, targetYear, targetMonth - 1, targetDay)) { //昨天
            return resource.getString(R.string.yestoday);
        }

//        if (TimeUtils.isInWeek(now, targetYear, targetMonth - 1, targetDay)) {  //一周之内
//            int dayOfweek = calendar.get(Calendar.DAY_OF_WEEK);
//            String week = resource.getStringArray(R.array.full_week)[dayOfweek - 1];
//            return week;
//        }
        String month = targetMonth >= 10 ? String.valueOf(targetMonth) : "0" + targetMonth;
        String day = targetDay >= 10 ? String.valueOf(targetDay) : "0" + targetDay;
        if (currentYear == targetYear) {
            return month + "/" + day;
        }
        return String.valueOf(targetYear) + "/" + month + "/" + day;
    }

    public static String getDetailDateString(long targetDate) {
        long now = System.currentTimeMillis();
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(now);
        int currentYear = calendar.get(Calendar.YEAR);
        int currentMonth = calendar.get(Calendar.MONTH) + 1;

        int currentDay = calendar.get(Calendar.DAY_OF_MONTH);

        calendar.setTimeInMillis(targetDate);
        int targetYear = calendar.get(Calendar.YEAR);
        int targetMonth = calendar.get(Calendar.MONTH) + 1;
        int targetDay = calendar.get(Calendar.DAY_OF_MONTH);

        int targetHour = calendar.get(Calendar.HOUR_OF_DAY);
        String hour = targetHour >= 10 ? String.valueOf(targetHour) : "0" + targetHour;
        int targetMinute = calendar.get(Calendar.MINUTE);
        String minute = targetMinute >= 10 ? String.valueOf(targetMinute) : "0" + targetMinute;

        if (currentYear == targetYear && currentMonth == targetMonth && currentDay == targetDay) {  //今天
            return hour + ":" + minute;
        }
        Resources resource = MsApplication.getInstance().getResources();
        if (TimeUtils.isYestoday(now, targetYear, targetMonth - 1, targetDay)) { //昨天
            return resource.getString(R.string.yestoday) + " " + hour + ":" + minute;
        }

//        if (TimeUtils.isInWeek(now, targetYear, targetMonth - 1, targetDay)) {  //一周之内
//            int dayOfweek = calendar.get(Calendar.DAY_OF_WEEK);
//            String week = resource.getStringArray(R.array.full_week)[dayOfweek - 1];
//            return week + " " + hour + ":" + minute;
//        }
        String month = targetMonth >= 10 ? String.valueOf(targetMonth) : "0" + targetMonth;
        String day = targetDay >= 10 ? String.valueOf(targetDay) : "0" + targetDay;
        if (currentYear == targetYear) {
            return month + "/" + day + " " + hour + ":" + minute;
        }
        return String.valueOf(targetYear) + "/" + month + "/" + day + " " + hour + ":" + minute;
    }

    //会议评论时间
    public static String getMeetAttachmentTime(long targetDate) {
        long now = System.currentTimeMillis();
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(now);
        int currentYear = calendar.get(Calendar.YEAR);
        int currentMonth = calendar.get(Calendar.MONTH) + 1;

        int currentDay = calendar.get(Calendar.DAY_OF_MONTH);

        calendar.setTimeInMillis(targetDate);
        int targetYear = calendar.get(Calendar.YEAR);
        int targetMonth = calendar.get(Calendar.MONTH) + 1;
        int targetDay = calendar.get(Calendar.DAY_OF_MONTH);

        int targetHour = calendar.get(Calendar.HOUR_OF_DAY);
        String hour = targetHour >= 10 ? String.valueOf(targetHour) : "0" + targetHour;
        int targetMinute = calendar.get(Calendar.MINUTE);
        String minute = targetMinute >= 10 ? String.valueOf(targetMinute) : "0" + targetMinute;

        if (currentYear == targetYear && currentMonth == targetMonth && currentDay == targetDay) {  //今天
            return hour + ":" + minute;
        }
        Resources resource = MsApplication.getInstance().getResources();
        if (TimeUtils.isYestoday(now, targetYear, targetMonth - 1, targetDay)) { //昨天
            return resource.getString(R.string.yestoday) + " " + hour + ":" + minute;
        }
        String month = targetMonth >= 10 ? String.valueOf(targetMonth) : "0" + targetMonth;
        String day = targetDay >= 10 ? String.valueOf(targetDay) : "0" + targetDay;
        if (currentYear == targetYear) {
            return month + "/" + day + " " + hour + ":" + minute;
        }
        return String.valueOf(targetYear) + "/" + month + "/" + day + " " + hour + ":" + minute;
    }

    public static String getDetailDateStringWithTomorrow(long time) {
        if (TimeUtils.isInSameDay(time, System.currentTimeMillis() + TimeUnit.ONE_DAY)) {
            return MsApplication.getInstance().getResources().getString(R.string.tomorrow_2) + " " + TimeUtils.getHourAndMin(time);
        }
        return getDetailDateString(time);
    }

    public static String getDateString2(long targetDate) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(targetDate);
        int m = calendar.get(Calendar.MONTH) + 1;
        String month = m >= 10 ? String.valueOf(m) : "0" + m; // date.month
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        String day = dayOfMonth >= 10 ? String.valueOf(dayOfMonth) : "0"
                + dayOfMonth;// date.day
        StringBuilder sb = new StringBuilder();

        Resources resource = MsApplication.getInstance().getResources();
        sb.append(month).append(resource.getString(R.string.month)).append(day).append(resource.getString(R.string.day));
        int h = calendar.get(Calendar.HOUR_OF_DAY);
        String hour = h >= 10 ? String.valueOf(h) : "0" + h; // date.hour
        sb.append(" ").append(hour);
        int min = calendar.get(Calendar.MINUTE);
        String minute = min >= 10 ? String.valueOf(min) : "0" + min; // date.minute
        sb.append(":").append(minute);
        return sb.toString();
    }

    public static String getWeekDay(long time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        int weekDay = calendar.get(Calendar.DAY_OF_WEEK);
        String[] array = MsApplication.getInstance().getResources().getStringArray(R.array.full_week);
        return array[weekDay - 1];
    }

//    public static String getDateAndWeekDay(long time) {
//        if (TimeUtils.isInSameDay(AccountUtils.getInstance().getNowTime(), time)) {
//            return TimeUtils.formatChineseMonthDay(time) + " 今天";
//        }
//        if (TimeUtils.isInSameDay(AccountUtils.getInstance().getNowTime() + TimeUnit.ONE_DAY, time)) {
//            return TimeUtils.formatChineseMonthDay(time) + " 明天";
//        }
//        return TimeUtils.formatChineseMonthDay(time) + " " + TimeUtil2.getWeekDay(time);
//    }

    //今天返回今天，明天返回明天，其他显示月份
    public static String getMonthWithToday(long time) {
        if (TimeUtils.isInSameDay(System.currentTimeMillis(), time)) {
            return MsApplication.getInstance().getResources().getString(R.string.today_2);
        }
        if (TimeUtils.isInSameDay(System.currentTimeMillis() + TimeUnit.ONE_DAY, time)) {
            return MsApplication.getInstance().getResources().getString(R.string.tomorrow_2);
        }
        return getMonth(time);
    }

    //今天返回今天，明天返回明天，其他显示 x月x日
    public static String getMonthWithToday2(long time) {
        if (TimeUtils.isInSameDay(System.currentTimeMillis(), time)) {
            return MsApplication.getInstance().getResources().getString(R.string.today_2);
        }
        if (TimeUtils.isInSameDay(System.currentTimeMillis() + TimeUnit.ONE_DAY, time)) {
            return MsApplication.getInstance().getResources().getString(R.string.tomorrow_2);
        }
        return getMonth(time) + getDayOfMonth(time) + MsApplication.getInstance().getResources().getString(R.string.day);
    }

    //今天返回前天,昨天,今天，明天,后天 ，其他显示 x月x日
    public static String getMonthWithNewToday2(long time) {
        if (TimeUtils.isInSameDay(System.currentTimeMillis() - TimeUnit.TWO_DAY, time)) {
            return "前天";
        }
        if (TimeUtils.isInSameDay(System.currentTimeMillis() - TimeUnit.ONE_DAY, time)) {
            return "昨天";
        }
        if (TimeUtils.isInSameDay(System.currentTimeMillis(), time)) {
            return MsApplication.getInstance().getResources().getString(R.string.today_2);
        }
        if (TimeUtils.isInSameDay(System.currentTimeMillis() + TimeUnit.ONE_DAY, time)) {
            return MsApplication.getInstance().getResources().getString(R.string.tomorrow_2);
        }
        if (TimeUtils.isInSameDay(System.currentTimeMillis() + TimeUnit.TWO_DAY, time)) {
            return "后天";
        }
        return getMonth(time) + getDayOfMonth(time) + MsApplication.getInstance().getResources().getString(R.string.day);
    }

    /**
     * 今天返回今天，其他返回周日到周六
     *
     * @param time
     * @return
     */
    public static String getWeekDayWithToday(long time) {
        if (TimeUtils.isInSameDay(System.currentTimeMillis(), time)) {
            return MsApplication.getInstance().getResources().getString(R.string.today_2);
        }
        return getWeekDay(time);
    }

    /**
     * 将时间转换成今天，昨天以及xx月xx日 周x的形式
     *
     * @param time 当天0点0分0秒0毫秒的形式
     * @return
     */
    public static String getDateDes(long time) {
        long today = TimeUtils.getToday();
        long yestday = today - (24 * 60 * 60 * 1000);
        if (today == time) {
            return "今天";
        } else if (time >= yestday) {
            return "昨天";
        } else {
            String date = TimeUtils.formateTimeMM_DD(time);
            String weekDay = getWeekDay(time);
            return date + " " + weekDay;
        }
    }


    //计算当前时间离time还有多少分钟
//    public static int leftMinTime(long time) {
//        long current = AccountUtils.getInstance().getNowTime();
//        if (time <= current) return 1;
//        long passTime = time - current;
//        return (int) (passTime / TimeUnit.ONE_MIN) + 1;
//    }

    public static int getBetweenDay(long date1, long date2) {
        Calendar d1 = TimeUtils.getCalByDefTZ();
        d1.setTimeInMillis(date1);
        Calendar d2 = TimeUtils.getCalByDefTZ();
        d2.setTimeInMillis(date2);
        int days = d2.get(Calendar.DAY_OF_YEAR) - d1.get(Calendar.DAY_OF_YEAR);
        int y2 = d2.get(Calendar.YEAR);
        if (d1.get(Calendar.YEAR) != y2) {
            if (d1.get(Calendar.YEAR) < y2) {
                do {
                    days += d1.getActualMaximum(Calendar.DAY_OF_YEAR);
                    d1.add(Calendar.YEAR, 1);

                } while (d1.get(Calendar.YEAR) != y2);
            } else {
                do {
                    days -= d1.getActualMaximum(Calendar.DAY_OF_YEAR);
                    d1.add(Calendar.YEAR, -1);

                } while (d1.get(Calendar.YEAR) != y2);
            }
        }
        return days;
    }

    public static String getChatLinkTime(long targetDate) {
        long now = System.currentTimeMillis();
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(now);
        int currentYear = calendar.get(Calendar.YEAR);

        calendar.setTimeInMillis(targetDate);
        int targetYear = calendar.get(Calendar.YEAR);
        int targetMonth = calendar.get(Calendar.MONTH) + 1;
        int targetDay = calendar.get(Calendar.DAY_OF_MONTH);

        String month = targetMonth >= 10 ? String.valueOf(targetMonth) : "0" + targetMonth;
        String day = targetDay >= 10 ? String.valueOf(targetDay) : "0" + targetDay;
        if (currentYear == targetYear) {
            return month + "/" + day;
        }
        return targetYear + "/" + month + "/" + day;
    }
}
