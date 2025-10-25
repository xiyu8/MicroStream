package com.jason.microstream.core.im.reqresp.data.bean.chat;

public class ReqCreateChat {


    public String cid;
    public String cName;
    public int cType;
    public int cState;
    public String withUid;
    public String creatorId;
    public long createTime;

    public long lastMsgId;
    public String lastMsgContent;
    public long lastMsgTime;
    public String lastMsgSenderId;
    public String lastMsgSenderName;

    public int memberCount;

    public String extData;

    /////////////////////////////////////////////////
    public int unreadCount;
    public boolean isTop;
    public boolean isMute;
    public boolean isHidden;

    public long lastReadMsgId;
    public long lastReadMsgTime;

    public String nickName;
    public int role;
    public long joinTime;

    public static final class ChatType{
        public static final int TYPE_SINGLE = 1;
        public static final int TYPE_GROUP = 2;
    }
    public static final class ChatState{
        public static final int TYPE_NORMAL = 0;
        public static final int TYPE_DISMISS = 1;
        public static final int TYPE_FORBIDDEN = 2;
    }


}
