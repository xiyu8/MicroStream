package com.jason.microstream.core.im.im_mode.msg;


public class TextMsg extends BaseMsg{

    public String text;

    public TextMsg() {
        setMsgType(ImMsgConfig.ImMsgType.TYPE_TEXT);
    }

    public TextMsg(BaseMsg baseMsg) {
        super(baseMsg);
        this.text = baseMsg.getContent();
//        this.setArriveTime(baseMsg.);
//        this.setReadTime(baseMsg.);
//        this.setRecallTime(baseMsg.);
//        this.setAtUsers(baseMsg.);
//        this.setReplyMsgId(baseMsg.);

    }
}
