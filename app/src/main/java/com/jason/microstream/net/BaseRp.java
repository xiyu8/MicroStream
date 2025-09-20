package com.jason.microstream.net;

public class BaseRp {
    public int ret = 0;
    public String errorMsg = null;

    public String data = null;

    public BaseRp(int ret, String errorMsg) {
        this.ret = ret;
        this.errorMsg = errorMsg;
    }

    public BaseRp(String content) {
        this.data = content;
    }
}
