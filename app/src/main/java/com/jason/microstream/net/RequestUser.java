package com.jason.microstream.net;

public class RequestUser {
    private String username;
    private String password;
    private String deviceId;
    private String clientIp;

    public RequestUser() {
    }

    public RequestUser(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public RequestUser(String username, String password, String deviceId, String clientIp) {
        this.username = username;
        this.password = password;
        this.deviceId = deviceId;
        this.clientIp = clientIp;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getClientIp() {
        return clientIp;
    }

    public void setClientIp(String clientIp) {
        this.clientIp = clientIp;
    }
}
