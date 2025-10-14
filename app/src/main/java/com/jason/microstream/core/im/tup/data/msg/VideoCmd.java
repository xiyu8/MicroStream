package com.jason.microstream.core.im.tup.data.msg;

import com.jason.microstream.core.im.im_mode.msg.BaseMsg;

public class VideoCmd extends BaseMsg {
    public String cmdContent;
    public String peerId;
    public int cmd;

    public VideoCmd() {
    }

    public VideoCmd(String fromId, String toId, String cmdContent, String peerId, int cmd) {
        this.cmdContent = cmdContent;
        this.peerId = peerId;
        this.cmd = cmd;
        this.fromId = fromId;
        this.toId = toId;
    }
}
