package com.jason.microstream.tool;

/**
 * 时间单位为毫秒
 * Created by Administrator on 2016/9/23.
 */
public class TimeUnit {

    public static final long HALF_HOUR = 1000 * 60 * 30;
    public static final long ONE_HOUR = 1000 * 60 * 60;
    public static final long TWO_HOUR = 2 * ONE_HOUR;

    public static final long ONE_DAY = ONE_HOUR * 24;
    public static final long TWO_DAY = ONE_DAY * 2;
    public static final long ONE_WEEK = ONE_DAY * 7;
    public static final long ONE_MONTH = ONE_DAY * 30;

    public static final long ONE_SEC = 1000;

    public static final long ONE_MIN = ONE_SEC * 60;
    public static final long FIVE_MINS = ONE_MIN * 5;
    public static final long TEN_MINS = ONE_MIN * 10;
    public static final long FIFTEEN_MINS = ONE_MIN * 15;
}
