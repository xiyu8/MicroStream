package com.jason.microstream.core.im.reqresp;

public interface RequestTimeoutI {

    void handleTimeout(RequestMonitor.Task task);
}
