package com.jason.microstream.core.im.reqresp;

public interface DataParser {
    void parseData(RespWrapper respWrapper);

    void onFail(Exception e);
}
