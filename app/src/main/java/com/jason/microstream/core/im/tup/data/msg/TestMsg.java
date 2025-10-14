package com.jason.microstream.core.im.tup.data.msg;

import com.jason.microstream.core.im.im_mode.msg.BaseMsg;

public class TestMsg extends BaseMsg {
    public String id;
    public String content;

    public TestMsg() {
    }

    public TestMsg(String fromId, String toId, String id, String content) {
        this.id = id;
        this.content = content;
        this.fromId = fromId;
        this.toId = toId;
    }
}
