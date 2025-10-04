package com.jason.microstream.core.im.reqresp;

public interface ReqCallback<RP> {
    void  onSuccess(RP rp);
    void onFail(Exception exception, ReqWrapper req);
}