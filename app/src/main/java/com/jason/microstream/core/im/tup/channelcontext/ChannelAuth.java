package com.jason.microstream.core.im.tup.channelcontext;

public class ChannelAuth {
    private boolean isAuth = false;
    private long authTime;
    private String uid;
    private String token;


    public void setAuthFlag(boolean isAuth) {
        this.isAuth = isAuth;
    }

    public void setAuthTime(long authTime) {
        this.authTime = authTime;
    }

    public boolean isAuth() {
        return isAuth;
    }

    public long getAuthTime() {
        return authTime;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUid() {
        return uid;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }
}
