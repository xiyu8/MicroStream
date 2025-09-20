package com.jason.microstream.tool;



import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Administrator on 2018/10/17.
 */

public class DateUtils {

    public static Long getLongForStringDate(String date, SimpleDateFormat sdf) {
        try {
            return sdf.parse(date).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0L;
    }
    public static Long getLongTimeStampByString(String date) {
        try {
            return new SimpleDateFormat("yyyy年MM月dd日").parse(date).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0L;
    }
    public static long getLongTimeStatisticsByString(String date) {
        try {
            return new SimpleDateFormat("yyyy年MM月").parse(date).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0L;
    }
    public static long getLongTimeStampSSByString(String date) {
        try {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(date).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0L;
    }

    public static String getLongDate(Long date, SimpleDateFormat sdf) {
        Date date1 = new Date(date);
        return sdf.format(date1);
    }

    public static String getLongDate(Long date) {
        Date date1 = new Date(date);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd HH:mm");
        return simpleDateFormat.format(date1);
    }

    public static String getLongHM(Long date) {
        Date date1 = new Date(date);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
        return simpleDateFormat.format(date1);
    }


    public static String getYMDWithChinese(Long date) {
        Date date1 = new Date(date);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日");
        return simpleDateFormat.format(date1);
    }
    public static String getClinicTime(Long date) {
        Date date1 = new Date(date);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM月dd日 HH:mm");
        return simpleDateFormat.format(date1);
    }
    public static String getLongDateSecond(Long date) {
        Date date1 = new Date(date);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        return simpleDateFormat.format(date1);
    }

    public static String getLongDateYear(Long date) {
        Date date1 = new Date(date);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return simpleDateFormat.format(date1);
    }
    public static String getEventDateYear(Long date) {
        Date date1 = new Date(date);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd");
        return simpleDateFormat.format(date1);
    }
    public static String getPublishAt(Long date) {
        Date date1 = new Date(date);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        return simpleDateFormat.format(date1);
    }
    public static String getCashTime(Long date) {
        Date date1 = new Date(date);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return simpleDateFormat.format(date1);
    }

    public static String getStatisticsTime(Long date) {
        Date date1 = new Date(date);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月");
        return simpleDateFormat.format(date1);
    }
    public static String getWithDrawTime(Long date) {
        Date date1 = new Date(date);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年M月");
        return simpleDateFormat.format(date1);
    }

    public static ArrayList<String> getBeforeDays(long from, int dayCount) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(from);
        calendar.add(Calendar.DATE, dayCount * (-1));
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd", Locale.CHINA);
        ArrayList<String> result = new ArrayList<>();
        for (int i = 0; i < dayCount; i++) {
            calendar.getTimeInMillis();
            result.add(simpleDateFormat.format(calendar.getTime()));
            calendar.add(Calendar.DATE, 1);
        }
        return result;
    }


    public static String getYearMouth(Long date) {
        Date date1 = new Date(date);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月");
        return simpleDateFormat.format(date1);
    }

    public static String getYearMouthWithoutZero(Long date) {
        Date date1 = new Date(date);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年M月");
        return simpleDateFormat.format(date1);
    }

    public static String getSingleDay(Long date) {
        Date date1 = new Date(date);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd");
        return simpleDateFormat.format(date1);
    }
    public static String getSingleMonth(Long date) {
        Date date1 = new Date(date);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM");
        return simpleDateFormat.format(date1);
    }
    public static String getSingleYear(Long date) {
        Date date1 = new Date(date);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy");
        return simpleDateFormat.format(date1);
    }

    public static String getYearMouthDay(Long date) {
        Date date1 = new Date(date);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
        return simpleDateFormat.format(date1);
    }





    public static int getYearsFromTimestampToNow(long timeStamp) {
        Date date1 = new Date(timeStamp);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy");
        return Integer.parseInt(simpleDateFormat.format(date1));
    }




    public static String getYear(Long date) {
        Date date1 = new Date(date);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy");
        return simpleDateFormat.format(date1);
    }

    public static String getCreateAt(Long date) {
        Date date1 = new Date(date);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return simpleDateFormat.format(date1);
    }
    public static String getSlashTime(Long date) {
        Date date1 = new Date(date);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm");
        return simpleDateFormat.format(date1);
    }

    public static String getLongDateYear(String date) {
        Date date1 = new Date(date);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return simpleDateFormat.format(date1);
    }


    public  static  long getTodayTime(String date){
        try {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(date).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0L;

    }




}
