package com.jason.microstream.core.im.tup.data;

import java.io.IOException;
import java.nio.ByteBuffer;

public class SendNode {
    public byte[] data;
    public String toId;
    public String sendMode;
    public String time;

    public long stubId;
    public long seqId;
    public int acked = 0;
    public int isAcked = 0;
    public int retryCount = 0;

    public SendCallback callback; //public 改default，改分包路径
    public Object original;

    public SendNode(byte[] data, long seqId, SendCallback callback) {
//        this.data = data;
        setData(data);
        this.seqId = seqId;
        this.callback = callback;
    }

    public void setData(byte[] data) {
        this.data = data;
        this.dataWrap = ByteBuffer.wrap(data);
    }

    public ByteBuffer dataWrap;

    public interface SendCallback{
        void onSendSuccess(SendNode node);
        void onSendFailed(IOException e, SendNode node);

    }
}
