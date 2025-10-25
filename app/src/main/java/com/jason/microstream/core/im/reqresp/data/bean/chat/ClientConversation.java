package com.jason.microstream.core.im.reqresp.data.bean.chat;

public class ClientConversation {
    public String cid;
    public String cName;
    public int cType;
    public int cState;
    public String creatorId;
    public long createTime;

    public long lastMsgId;
    public String lastMsgContent;
    public long lastMsgTime;
    public String lastMsgSenderId;
    public String lastMsgSenderName;

    public int memberCount;

    public String extData;
////////////////////////////////////////
    public String uid;
    public String convId;
    public int unreadCount;
    public boolean isTop;
    public boolean isMute;
    public boolean isHidden;

    public long lastReadMsgId;
    public long lastReadMsgTime;

    public String nickName;
    public int role;
    public long joinTime;

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public String getCName() {
        return cName;
    }

    public void setCName(String cName) {
        this.cName = cName;
    }

    public int getCType() {
        return cType;
    }

    public void setCType(int cType) {
        this.cType = cType;
    }

    public int getCState() {
        return cState;
    }

    public void setCState(int cState) {
        this.cState = cState;
    }

    public String getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(String creatorId) {
        this.creatorId = creatorId;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public long getLastMsgId() {
        return lastMsgId;
    }

    public void setLastMsgId(long lastMsgId) {
        this.lastMsgId = lastMsgId;
    }

    public String getLastMsgContent() {
        return lastMsgContent;
    }

    public void setLastMsgContent(String lastMsgContent) {
        this.lastMsgContent = lastMsgContent;
    }

    public long getLastMsgTime() {
        return lastMsgTime;
    }

    public void setLastMsgTime(long lastMsgTime) {
        this.lastMsgTime = lastMsgTime;
    }

    public String getLastMsgSenderId() {
        return lastMsgSenderId;
    }

    public void setLastMsgSenderId(String lastMsgSenderId) {
        this.lastMsgSenderId = lastMsgSenderId;
    }

    public String getLastMsgSenderName() {
        return lastMsgSenderName;
    }

    public void setLastMsgSenderName(String lastMsgSenderName) {
        this.lastMsgSenderName = lastMsgSenderName;
    }

    public int getMemberCount() {
        return memberCount;
    }

    public void setMemberCount(int memberCount) {
        this.memberCount = memberCount;
    }

    public String getExtData() {
        return extData;
    }

    public void setExtData(String extData) {
        this.extData = extData;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getConvId() {
        return convId;
    }

    public void setConvId(String convId) {
        this.convId = convId;
    }

    public int getUnreadCount() {
        return unreadCount;
    }

    public void setUnreadCount(int unreadCount) {
        this.unreadCount = unreadCount;
    }

    public boolean isTop() {
        return isTop;
    }

    public void setTop(boolean top) {
        isTop = top;
    }

    public boolean isMute() {
        return isMute;
    }

    public void setMute(boolean mute) {
        isMute = mute;
    }

    public boolean isHidden() {
        return isHidden;
    }

    public void setHidden(boolean hidden) {
        isHidden = hidden;
    }

    public long getLastReadMsgId() {
        return lastReadMsgId;
    }

    public void setLastReadMsgId(long lastReadMsgId) {
        this.lastReadMsgId = lastReadMsgId;
    }

    public long getLastReadMsgTime() {
        return lastReadMsgTime;
    }

    public void setLastReadMsgTime(long lastReadMsgTime) {
        this.lastReadMsgTime = lastReadMsgTime;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public int getRole() {
        return role;
    }

    public void setRole(int role) {
        this.role = role;
    }

    public long getJoinTime() {
        return joinTime;
    }

    public void setJoinTime(long joinTime) {
        this.joinTime = joinTime;
    }
}
