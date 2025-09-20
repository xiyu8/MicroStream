package com.jason.microstream.core.im.tup.data.msg;

public class VideoCmd extends Msg{
    public String cmdContent;
    public String peerId;
    public int cmd;

    public VideoCmd() {
    }

    public VideoCmd(String fromId, String toId, String cmdContent, String peerId, int cmd) {
        super(fromId, toId);
        this.cmdContent = cmdContent;
        this.peerId = peerId;
        this.cmd = cmd;
    }
}
