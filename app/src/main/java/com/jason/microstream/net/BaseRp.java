package com.jason.microstream.net;

public class BaseRp {
    public int ret = 0;
    public String errorMsg = null;

    public String content = null;

    public BaseRp(int ret, String errorMsg) {
        this.ret = ret;
        this.errorMsg = errorMsg;
    }

    public BaseRp(String content) {
        this.content = content;
    }
}
