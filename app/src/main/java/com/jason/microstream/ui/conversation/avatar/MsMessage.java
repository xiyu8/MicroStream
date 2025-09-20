package com.jason.microstream.ui.conversation.avatar;

public class MsMessage {

    public static final int TYPE_SYSTEM = 9;//忽略，安卓内部使用的系统消息

    public static final int TYPE_TXT = 1;  //文本
    public static final int TYPE_IMAGE = 2;  //图片
    public static final int TYPE_VOICE = 3; //语音
    public static final int TYPE_SMILE = 4;//老的动画表情
    public static final int TYPE_DISK = 5;//文件
    public static final int TYPE_VIDEO = 6;//文件

    //    public static final int TYPE_ENC = 999;//加密类型
    public static final int STATUS_SECCUSS = 0;
    public static final int STATUS_SENDING = 1;
    public static final int STATUS_FAIL = 2;
}
