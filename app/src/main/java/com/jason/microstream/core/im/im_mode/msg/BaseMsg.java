package com.jason.microstream.core.im.im_mode.msg;


import com.jason.microstream.core.im.tup.data.msg.Msg;

public class BaseMsg extends Msg {
    private int msgType = ImMsgConfig.ImMsgType.TYPE_DEFAULT;

    public String toId;
    public String fromId;

    public int state;

    public void setMsgType(int msgType) {
        this.msgType = msgType;
    }

    public int getMsgType() {
        return msgType;
    }
}
