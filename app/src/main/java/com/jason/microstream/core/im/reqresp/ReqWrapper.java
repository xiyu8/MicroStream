package com.jason.microstream.core.im.reqresp;

import com.jason.microstream.core.im.tup.data.msg.Msg;

public class ReqWrapper extends Msg {
    long reqId;

    public String apiChar="/";
    public String requestContent;
    public Object original;

}
