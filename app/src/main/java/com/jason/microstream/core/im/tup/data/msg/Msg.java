package com.jason.microstream.core.im.tup.data.msg;

public class Msg {
    public long stubId;
    public long seqId;

    public int _state;
    public long sendTime;

    public Msg() {
    }

    public long getStubId() {
        return stubId;
    }

    public void setStubId(long stubId) {
        this.stubId = stubId;
    }

    public void setSeqId(long seqId) {
        this.seqId = seqId;

    }

    public long getSeqId() {
        return seqId;
    }

    public static final class STATE {
        public static final int SENDING = 0;
        public static final int SENDED = 1;
        public static final int SEND_FAIL = -1;

    }
}
