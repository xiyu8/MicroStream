package com.jason.microstream.core.im.im_mode.msg;


import com.jason.microstream.core.im.tup.data.msg.Msg;

//IM message
public class BaseMsg extends Msg {

    private int msgType = ImMsgConfig.ImMsgType.TYPE_DEFAULT;
    private String content;
    private String contentAbstract;
    private String extra;

    private int state;
    private String cid;
    private int cType;

    private String fromId;
    private String fromName;
    private String toId;
    private String toName;

    public BaseMsg() {
    }

    public BaseMsg(BaseMsg baseMsg) {
        this.setSeqId(baseMsg.seqId);
        this.setStubId(baseMsg.stubId);
        this.setMsgType(baseMsg.getMsgType());
        this.setContent(baseMsg.getContent());
        this.setContentAbstract(baseMsg.getContentAbstract());
        this.setExtra(baseMsg.getExtra());
        this.setState(baseMsg.getState());
        this.setCid(baseMsg.getCid());
        this.setcType(baseMsg.getcType());
        this.setFromId(baseMsg.getFromId());
        this.setFromName(baseMsg.getFromName());
        this.setToId(baseMsg.getToId());
        this.setToName(baseMsg.getToName());
        this.setSendTime(baseMsg.sendTime);
    }

    public void setMsgType(int msgType) {
        this.msgType = msgType;
    }

    public int getMsgType() {
        return msgType;
    }


    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getContentAbstract() {
        return contentAbstract;
    }

    public void setContentAbstract(String contentAbstract) {
        this.contentAbstract = contentAbstract;
    }

    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public int getcType() {
        return cType;
    }

    public void setcType(int cType) {
        this.cType = cType;
    }

    public String getFromId() {
        return fromId;
    }

    public void setFromId(String fromId) {
        this.fromId = fromId;
    }

    public String getFromName() {
        return fromName;
    }

    public void setFromName(String fromName) {
        this.fromName = fromName;
    }

    public String getToId() {
        return toId;
    }

    public void setToId(String toId) {
        this.toId = toId;
    }

    public String getToName() {
        return toName;
    }

    public void setToName(String toName) {
        this.toName = toName;
    }

    protected void setSendTime(long sendTime) {
        this.sendTime = sendTime;
    }
}
