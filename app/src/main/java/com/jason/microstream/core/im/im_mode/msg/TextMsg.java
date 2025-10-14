package com.jason.microstream.core.im.im_mode.msg;


public class TextMsg extends BaseMsg{

    public String text;

    public TextMsg() {
        setMsgType(ImMsgConfig.ImMsgType.TYPE_TEXT);
    }
}
