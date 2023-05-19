package com.jason.microstream.tackle.localbroadcast;

/**
 * This interface is about custom broadcast address constants.
 * 自定义广播消息接口
 */
public interface CustomBroadcastConstants
{
    String ACTION_CALL_CONNECTED = "com.huawei.opensdk.call.connected";
    String ACTION_CALL_CONNECTED_FAIL = "com.huawei.opensdk.call.connectedfail";
    String CALL_MEDIA_CONNECTED = "com.huawei.opensdk.call.mediaconnected";
    String CALL_ROUTE_CHANGED = "com.huawei.opensdk.call.routechanged";
    String ACTION_CALL_END = "com.huawei.opensdk.call.end";
    String ACTION_CALL_END_FAILED = "com.huawei.opensdk.call.end.failed";
    String ADD_LOCAL_VIEW = "com.huawei.opensdk.call.addlocalview";
    String MEMBER_CHANGED = "com.huawei.opensdk.video.member.changed";
    String DEL_LOCAL_VIEW = "com.huawei.opensdk.call.dellocalview";
    //String BOOK_RESERVED_CONF_RESULT = "com.huawei.opensdk.conf.bookconfresult";
    String GET_CONF_LIST_FAILED_RESULT = "com.huawei.opensdk.conf.getconflistfailedresult";
    String GET_CONF_LIST_RESULT = "com.huawei.opensdk.conf.getconflistresult";
    String GET_VMR_LIST_RESULT = "com.huawei.opensdk.conf.getvmrlistresult";
    String GET_CONF_DETAIL_RESULT = "com.huawei.opensdk.conf.getconfdetailresult";
    String GET_CONF_SUBSCRIBE_RESULT = "com.huawei.opensdk.conf.getconfsubscriberesult";
    String ADD_SELF_RESULT = "com.huawei.opensdk.conf.addselfresult";
    String ADD_ATTENDEE_RESULT = "com.huawei.opensdk.conf.addattendeeresult";
    String DEL_ATTENDEE_RESULT = "com.huawei.opensdk.conf.delattendeeresult";
    //String GET_CONF_COMING = "com.huawei.opensdk.conf.getconfcoming";
    String GET_CONF_CONNECTED = "com.huawei.opensdk.conf.getconfconnected";
    String GET_CONF_END = "com.huawei.opensdk.conf.getconfend";
    String CONF_STATE_UPDATE = "com.huawei.opensdk.conf.confstateupdate";
    String ACTION_VIDEO_TO_AUDIO = "com.huawei.opensdk.call.videoswitchaudio";
    String CALL_UPGRADE_ACTION = "com.huawei.opensdk.call.upgrade";
    String REQUEST_CONF_RIGHT_RESULT = "com.huawei.opensdk.conf.requestconfrightresult";
    String CONF_CALL_ANSWERED = "com.huawei.opensdk.conf.confcallanswered";
    String LOGIN_SUCCESS = "com.huawei.opensdk.login.success";
    String LOGIN_FAILED = "com.huawei.opensdk.login.failed";
  String FIRST_CHANGE_PASSWORD = "com.huawei.opensdk.login.firstchangepassword";
  String LOGOUT = "com.huawei.opensdk.login.logout";
    String MUTE_ATTENDEE_RESULT = "com.huawei.opensdk.conf.muteattendeeresult";
    String UN_MUTE_ATTENDEE_RESULT = "com.huawei.opensdk.conf.unmuteattendeeresult";
    String MUTE_CONF_RESULT = "com.huawei.opensdk.conf.muteconfresult";
    String UN_MUTE_CONF_RESULT = "com.huawei.opensdk.conf.unmuteconfresult";
    String LOCK_CONF_RESULT = "com.huawei.opensdk.conf.lockconfresult";
    String UN_LOCK_CONF_RESULT = "com.huawei.opensdk.conf.unlockconfresult";
    String START_RECORD_RESULT = "com.huawei.opensdk.conf.startrecordresult";
    String STOP_RECORD_RESULT = "com.huawei.opensdk.conf.stoprecordresult";
    String SESSION_MODIFIED_RESULT = "com.huawei.opensdk.conf.sessionmodifiedresult";
    String REQUEST_CHAIRMAN_RESULT = "com.huawei.opensdk.conf.requestchairmanresult";
    String RELEASE_CHAIRMAN_RESULT = "com.huawei.opensdk.conf.releasechairmanresult";
    String HAND_UP_RESULT = "com.huawei.opensdk.conf.handupresult";
    String CANCEL_HAND_UP_RESULT = "com.huawei.opensdk.conf.cancelhandupresult";
    String WILL_TIMEOUT = "com.huawei.opensdk.conf.willtimeout";
    String POSTPONE_CONF_RESULT = "com.huawei.opensdk.conf.postponeconfresult";
    String SPEAKER_LIST_IND = "com.huawei.opensdk.conf.speakerlistind";
    String REQUEST_PRESENTER_RESULT = "com.huawei.opensdk.conf.requestpesenterresult";

    String REFRESH_SVC_VIEW = "com.huawei.opensdk.conf.refreshsvcview";

    String SET_CONF_MODE_RESULT = "com.huawei.opensdk.conf.setconfmoderesult";
    String HOLD_CALL_RESULT = "com.huawei.opensdk.conf.holdcallresult";
    String CONF_CALL_CONNECTED = "com.huawei.opensdk.conf.confcallconnected";
    String DIVERT_RESULT = "com.huawei.opensdk.conf.divertresult";
    String BLD_TRANSFER_RESULT = "com.huawei.opensdk.conf.bldtransferresult";

    String UPGRADE_CONF_RESULT = "com.huawei.opensdk.conf.upgradeconfresult";
    String WATCH_ATTENDEE_CONF_RESULT = "com.huawei.opensdk.conf.watchattendeeconfresult";
    String BROADCAST_ATTENDEE_CONF_RESULT = "com.huawei.opensdk.conf.broadcastattendeeconfresult";
    String CANCEL_BROADCAST_CONF_RESULT = "com.huawei.opensdk.conf.cancelbroadcastconfresult";

    String ACCESS_RESERVED_CONF = "com.huawei.opensdk.conf.accessreservedconf";

    String GET_TEMP_USER_RESULT = "com.huawei.opensdk.conf.gettempuserresult";

    String CALL_TRANSFER_TO_CONFERENCE = "com.huawei.opensdk.conf.calltransfertoconference";

    //DataConf
    String CONF_INFO_PARAM = "com.huawei.opensdk.conf.dataconfparam";
    String GET_DATA_CONF_PARAM_RESULT = "com.huawei.opensdk.conf.getdataconfparamresult";
    String UPDATE_HOST_INFO = "com.huawei.opensdk.conf.updatehostinfo";
    String DATA_CONF_USER_LEAVE = "com.huawei.opensdk.conf.dataconfuserleave";
    String CONF_MSG_ON_CONFERENCE_TERMINATE = "com.huawei.opensdk.conf.confmsgonconferenceterminate";
    String CONF_MSG_ON_CONFERENCE_LEAVE = "com.huawei.opensdk.conf.confmsgonconferenceleave";
    String COMPT_MSG_VIDEO_ON_NOTIFY = "com.huawei.opensdk.conf.comptmsgvideoonnotify";
    String DATA_CONFERENCE_JOIN_RESULT = "com.huawei.opensdk.conf.dataconferencejoinresult";
    String DATA_CONFERENCE_USER_JOIN = "com.huawei.opensdk.conf.dataconferenceuserjoin";
    String DATA_CONFERENCE_PRESENTER_CHANGE_IND = "com.huawei.opensdk.conf.dataconferencepresenterchangeind";
    String DATA_CONFERENCE_HOST_CHANGE_IND = "com.huawei.opensdk.conf.dataconferencehostchangeind";
    String DATA_CONFERENCE_GET_DEVICE_INFO_RESULT = "com.huawei.opensdk.conf.dataconferencegetdeviceinforesult";
    String DATA_CONFERENCE_EXTEND_DEVICE_INFO = "com.huawei.opensdk.conf.dataconferenceextenddeviceinfo";
    String DATA_CONFERENCE_CAMERA_STATUS_UPDATE = "com.huawei.opensdk.conf.dataconferencecamerastatusupdate";
    String DATE_CONFERENCE_START_SHARE_STATUS = "com.huawei.opensdk.conf.dateconferencestartsharestatus";
    String DATE_CONFERENCE_END_SHARE_STATUS = "com.huawei.opensdk.conf.dateconferenceendsharestatus";
    String DATE_CONFERENCE_AUX_DATA_STATE = "com.huawei.opensdk.conf.dateconferenceauxdatastate";
    String CONF_AUX_DATA_SEND = "com.huawei.opensdk.conf.auxdatasend";
    String CONF_BROADCAST = "com.huawei.opensdk.conf.broadcast";
    String CONF_RECORD = "com.huawei.opensdk.conf.record";
    String CONF_AUX_DATA_FAILED = "com.huawei.opensdk.conf.auxdatafailed";
    String DATE_CONFERENCE_CHAT_MSG = "com.huawei.opensdk.conf.dateconferencechatmsg";

  String VIDEO_NO_STREAM_DURATION_MSG = "com.huawei.opensdk.conf.videonostreamdurationmsg";
  String CANCEL_CONF_MSG = "com.huawei.opensdk.conf.cancelconfmsg";
  String CHECKIN_CONF_MSG = "com.huawei.opensdk.conf.checkinconfmsg";

    //IM
    String ACTION_LOGIN_IM = "com.huawei.opensdk.im.imlogin";
    String ACTION_RECEIVE_SESSION_CHANGE = "com.huawei.opensdk.im.messagereceive";
    String ACTION_IM_LOGIN_SUCCESS = "com.huawei.opensdk.im.loginsuccess";
    String ACTION_IM_LOGIN_ERROR = "com.huawei.opensdk.im.loginerror";
    String ACTION_IM_QUERY_HISTORY = "com.huawei.opensdk.im.queryhistory";
    String ACTION_IM_DELETE_CONTACT_RESULT = "com.huawei.opensdk.im.deletecontactresult";
    String ACTION_IM_SEARCH_CONTACT_RESULT = "com.huawei.opensdk.im.searchcontactresult";
    String ACTION_IM_ADD_CONTACT_RESULT = "com.huawei.opensdk.im.addcontactresult";
    String ACTION_IM_FRIEND_STATUS_CHANGE = "com.huawei.opensdk.im.friendstatuschange";
    String ACTION_DOWNLOAD_PROCESS = "com.huawei.opensdk.im.downloadprocess";
    String ACTION_DOWNLOAD_FINISH = "com.huawei.opensdk.im.downloadfinish";
    String ACTION_SEND_MESSAGE_SUCCESS = "com.huawei.opensdk.im.sendmessagesuccess";
    String ACTION_SEND_MESSAGE_FAIL = "com.huawei.opensdk.im.sendmessagefail";
    String ACTION_QUERY_GROUP_MEMBER = "com.huawei.opensdk.im.querygroupmember";
    String ACTION_REFRESH_GROUP_MEMBER = "com.huawei.opensdk.im.refreshgroupmember";
    String ACTION_MODIFY_GROUP_MEMBER = "com.huawei.opensdk.im.modifygroupmember";
    String ACTION_CREATE_GROUP_RESULT = "com.huawei.opensdk.im.creategroupresult";
    String ACTION_SET_STATUS = "com.huawei.opensdk.im.modifygroupmember";
    String ACTION_IM_SET_HEAD_PHOTO = "com.huawei.opensdk.im.setheadphoto";
    String ACTION_REFRESH_TEAM_MEMBER = "com.huawei.opensdk.im.refreshteammember";

    String ACTION_IM_USER_INFO_CHANGE = "com.huawei.opensdk.im.userinfochange";
    String ACTION_IM_USER_INFO_CHANGE_FAILED = "com.huawei.opensdk.im.userinfochangefailed";
    String ACTION_IM_CHAT_GROUP_ADD = "com.huawei.opensdk.im.chatgroupadd";
    String ACTION_IM_CHAT_GROUP_DISMISS = "com.huawei.opensdk.im.chatgroupupdismiss";
    String ACTION_IM_CHAT_GROUP_UPDATE = "com.huawei.opensdk.im.chatgroupupdate";
    String ACTION_IM_CHAT_GROUP_ADD_MEMBER = "com.huawei.opensdk.im.chatgroupaddmember";
    String ACTION_IM_CHAT_GROUP_DEL_MEMBER = "com.huawei.opensdk.im.chatgroupdelmember";
    String ACTION_IM_CHAT_GROUP_LEAVE_RESULT = "com.huawei.opensdk.im.chatgroupleaveresult";

    //Enterprise address book
    String ACTION_ENTERPRISE_GET_SELF_RESULT = "com.huawei.opensdk.eaddr.getselfresult";
    String ACTION_ENTERPRISE_GET_CONTACT_RESULT = "com.huawei.opensdk.eaddr.getcontactresult";
    String ACTION_ENTERPRISE_GET_CONTACT_NULL = "com.huawei.opensdk.eaddr.getcontactnull";
    String ACTION_ENTERPRISE_GET_CONTACT_FAILED = "com.huawei.opensdk.eaddr.getcontactfailed";
    String ACTION_ENTERPRISE_GET_SELF_PHOTO_RESULT = "com.huawei.opensdk.eaddr.getselfphotoresult";
    String ACTION_ENTERPRISE_GET_HEAD_PHOTO_FAILED = "com.huawei.opensdk.eaddr.getheadphotofailed";
    String ACTION_ENTERPRISE_GET_HEAD_SYS_PHOTO = "com.huawei.opensdk.eaddr.getheadsysphoto";
    String ACTION_ENTERPRISE_GET_HEAD_DEF_PHOTO = "com.huawei.opensdk.eaddr.getheaddefphoto";
    String ACTION_ENTERPRISE_GET_DEPARTMENT_RESULT = "com.huawei.opensdk.eaddr.getdepartmentresult";
    String ACTION_ENTERPRISE_GET_DEPARTMENT_NULL = "com.huawei.opensdk.eaddr.getdepartmentnull";
    String ACTION_ENTERPRISE_GET_DEPARTMENT_FAILED = "com.huawei.opensdk.eaddr.getdepartmentfailed";

    //conference to call
    String CONF_INCOMING_TO_CALL_INCOMING = "com.huawei.opensdk.conf.conftocall";
  String CALL_COMING = "com.huawei.opensdk.conf.callcoming";
  String CALL_GOING = "com.huawei.opensdk.conf.callgoing";
  String BOOK_CONF_SUCCESS = "com.huawei.opensdk.conf.bookconfsuccess";
  String BOOK_CONF_FAIL = "com.huawei.opensdk.conf.bookconffail";
  String CONF_AUDIT_DIR = "com.huawei.opensdk.call.confauditdir";
  String MAINTAIN_LOG_UPLOAD_RESULT = "com.huawei.opensdk.maintain.loguploadresult";
  String GET_S_TERMINAL_INFO_RESULT = "com.huawei.opensdk.maintain.getsterminalinforesult";
  // 前后台切换的broadcast
  String CUSTOM_BROADCAST_CONSTANTS_FRONT = "custom.broadcast.constants.front";
  String CUSTOM_BROADCAST_CONSTANTS_BACK = "custom.broadcast.constants.back";


  String HEADSET_PLUG = "custom.broadcast.constants.headsetplug";
  String BLUETOOTH_CONNECTION_STATE = "custom.broadcast.constants.bluetoothconnectstate";
  String BLUETOOTH_STATE = "custom.broadcast.constants.bluetoothstate";

  String CALL_STATE_IDLE = "custom.broadcast.constants.CALL_STATE_IDLE";
  String CALL_STATE_RINGING = "custom.broadcast.constants.CALL_STATE_RINGING";
  String CALL_STATE_OFFHOOK = "custom.broadcast.constants.CALL_STATE_OFFHOOK";

  String UPDATE_SERVER_NET_LEVEL = "com.thundersoft.cloundlink.UPDATE_SERVER_NET_LEVEL";

  String ACTION_QUERY_CONF_PWD = "com.isoftstone.cloundlink.query_conf_pwd";
    public static final String ACTION_VERIFY_CONF_PWD = "com.isoftstone.cloundlink.verify_conf_pwd";


}
