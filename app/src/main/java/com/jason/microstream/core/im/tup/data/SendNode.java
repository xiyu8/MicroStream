package com.jason.microstream.core.im.tup.data;

public class SendNode {
    public byte[] data;
    public String toId;
    public String sendMode;
    public String time;

    public long stubId;
    public long seqId;
    public int acked = 0;
    public int retryCount = 0;

}
