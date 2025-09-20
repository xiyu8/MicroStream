package com.jason.microstream.model;

public class User {
    private String uid;

    private String username;

    private String password;

    private String type;

    public User() {
    }

    public User(String uid, String name) {
        this.uid = uid;
        this.username = name;
    }

    public User(String uid, String name, String pwd) {
        this.uid = uid;
        this.username = name;
        this.password = pwd;
    }

    public User(String uid, String name, String pwd, String type) {
        this.uid = uid;
        this.username = name;
        this.password = pwd;
        this.type = type;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return username;
    }

    public void setName(String name) {
        this.username = name;
    }

    public String getPwd() {
        return username;
    }

    public void setPwd(String pwd) {
        this.username = pwd;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }


}
