package com.jason.microstream.core.im.tup.joint;

import com.jason.microstream.localbroadcast.Events;
import com.jason.microstream.localbroadcast.LocBroadcast;

public class Account {

    private volatile static Account account;
    private Account() {
    }
    public static Account get() {
        if (account == null) {
            synchronized (Account.class) {
                if (account == null) {
                    account = new Account();
                }
            }
        }
        return account;
    }

    public String uid;
    public String token;
    public boolean iaAuth;


    public void forceLogout() {
        LocBroadcast.getInstance().sendBroadcast(Events.ACTION_ON_LOGOUT, null);

    }

    public void authed() {

    }
}
