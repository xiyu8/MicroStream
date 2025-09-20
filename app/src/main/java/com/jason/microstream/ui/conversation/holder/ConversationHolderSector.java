package com.jason.microstream.ui.conversation.holder;

import android.content.res.Resources;


import com.jason.microstream.MsApplication;
import com.jason.microstream.R;
import com.jason.microstream.ui.conversation.avatar.MsMessage;

import java.util.Calendar;
import java.util.TimeZone;

public class ConversationHolderSector {


    public String getTime(long targetDate) {
        if (targetDate == 0) {
            return "";
        }
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
        if (isYestoday(now, targetYear, targetMonth - 1, targetDay)) { //昨天
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
    private boolean isYestoday(long now, int year, int month, int day) {
        Calendar calendar = getCalByDefTZ();
        calendar.set(year, month, day);
        long yestoday = calendar.getTimeInMillis();
        long diff = now - yestoday;
        if (diff > 0 && diff <= 86400000) {
            return true;
        }
        return false;
    }
    private Calendar getCalByDefTZ() {
        return Calendar.getInstance(getDefaultTimeZone());
    }
    private static TimeZone getDefaultTimeZone() {
        return TimeZone.getTimeZone("Asia/Shanghai");
    }

    public static String getContentByType(int type, String text) {
        String content = "";
        Resources res = MsApplication.getInstance().getResources();
        switch (type) {
            case MsMessage.TYPE_TXT:
            case MsMessage.TYPE_SYSTEM:
//            case CYMessage.TYPE_KEFU:
//            case CYMessage.TYPE_TXT_IMAGE:
//            case CYMessage.TYPE_MAIL:
//            case CYMessage.TYPE_SIGN:
//            case CYMessage.TYPE_MULTI_IMAGETXT:
//            case CYMessage.TYPE_LOG:
//            case CYMessage.TYPE_APPROVE:
//            case CYMessage.TYPE_PRAISE:
//            case CYMessage.TYPE_NEW_SYSTEM:
//            case CYMessage.TYPE_PEDOMETER_SHARE:
//            case CYMessage.TYPE_WORK:
//            case CYMessage.TYPE_SYSTEM_MONEY:
//            case CYMessage.TYPE_ADD_GROUP:
//            case CYMessage.TYPE_OPEN:
//            case CYMessage.TYPE_BIRTH_CARD:
//            case CYMessage.TYPE_BIRTH_CARD_SYSTEM:
//            case CYMessage.TYPE_RICH_TEXT:
//            case CYMessage.TYPE_MINIAPP_TXTIMAGE:
                content = text;
                break;
//            case CYMessage.TYPE_ENC:
//                content = res.getString(R.string.conversation_enc);
//                break;
            case MsMessage.TYPE_IMAGE:
                content = res.getString(R.string.conversation_image);
                break;
            case MsMessage.TYPE_VIDEO:
                content = res.getString(R.string.conversation_vedio);
                break;
            case MsMessage.TYPE_VOICE:
                content = res.getString(R.string.conversation_voice);
                break;
//            case CYMessage.TYPE_SMILE:
//            case CYMessage.TYPE_NEW_SMILE:
//                content = res.getString(R.string.conversation_smile);
//                break;
//            case CYMessage.TYPE_DISK:
//            case CYMessage.TYPE_NEW_DISK:
//            case CYMessage.TYPE_SHARE_FILE:
//                content = res.getString(R.string.conversation_file);
//                break;
//            case CYMessage.TYPE_VOTE:
//                content = res.getString(R.string.conversation_vote);
//                break;
//            case CYMessage.TYPE_CARD:
//            case CYMessage.TYPE_PERSONAL_CARD:
//                content = res.getString(R.string.conversation_card);
//                break;
//            case CYMessage.TYPE_LINK:
//                content = res.getString(R.string.conversation_link);
//                break;
//            case CYMessage.TYPE_SCHEDULE:
//                content = res.getString(R.string.conversation_schedule_title);
//                break;
//            case CYMessage.TYPE_NEW_MAIL:
//                content = res.getString(R.string.conversation_mail_title);
//                break;
//            case CYMessage.TYPE_POSITION:
//                content = res.getString(R.string.conversation_position);
//                break;
//            case CYMessage.TYPE_TRAIL:
//                content = res.getString(R.string.c_work_trail);
//                break;
//            case CYMessage.TYPE_CUSTOM_SMILE:
//                content = res.getString(R.string.conversation_custom_smile);
//                break;
//            case CYMessage.TYPE_MULTI_MSG:
//                content = res.getString(R.string.conversation_multi_message);
//                break;
//            case CYMessage.TYPE_MINI_APP:
//                content = res.getString(R.string.conversation_mini_app) + text;
//                break;
            default:
                content = res.getString(R.string.conversation_unkown);
                break;
        }
        return content;
    }
}
