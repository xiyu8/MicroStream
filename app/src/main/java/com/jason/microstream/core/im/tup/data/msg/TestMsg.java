package com.jason.microstream.core.im.tup.data.msg;

public class TestMsg extends Msg{
    public String id;
    public String content;

    public TestMsg() {
    }

    public TestMsg(String fromId, String toId, String id, String content) {
        super(fromId, toId);
        this.id = id;
        this.content = content;
    }
}
