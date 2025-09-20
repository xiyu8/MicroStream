package com.jason.microstream.manager.config;

public class EquipConfig {

    public static final boolean enableUnionConf = false;   //融合会议
    public static final boolean enablePeerCall = true;  //点呼通话
    public static final boolean enableMeetingPermissionCheck = false;  //业务视频会议权限登录控制
    public static final boolean enableAudioConf = true;
    public static final boolean enableConfPwd = false;
    public static final boolean enableConfInviteSms = false;
    public static final boolean enableConfRemindSms = false;
    public static final boolean enableConfShareSms = false;
    public static final boolean enableConfQRCode = false;


    public static final boolean enableRecordConf = false;  //服务端录制会议
    public static final boolean enableBroadcastConf = false;  //单流smc二开接口广播成员
    public static final boolean enableScreenshotConf = true;  //截图
    public static final boolean enableRecallByMember = true;  //是否允许非主持人成员 催一催未参会的人


    public static final boolean defaultMic = true;
    public static final boolean defaultCamera = true;
    public static final boolean defaultLoudspeaker = true;
    public static final boolean immiConfMic = true;  //立即会议创建时麦克风默认状态
    public static final boolean immiConfCamera = true;  //立即会议创建时摄像头默认状态
    public static final boolean immiConfLoudspeaker = true;  //立即会议创建时扬声器默认状态

    public static final boolean callDefaultCamera = true;
    public static final boolean callDefaultMic = true;
    public static final boolean callDefaultLoudspeaker = true;
    public static final boolean callOutDefaultCamera = true;  //点呼呼出时摄像头状态
    public static final boolean callOutDefaultMicrophone = true;  //点呼呼出时麦克风状态
    public static final boolean callOutDefaultLoudspeaker = true;  //点呼呼出时扬声器状态

    public static final int maxInviteLimited = 30;
    public static final int maxTemporaryInviteLimited = 10;


}
