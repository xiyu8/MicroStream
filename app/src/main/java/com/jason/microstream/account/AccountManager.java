package com.jason.microstream.account;


import com.jason.microstream.model.User;
import com.jason.microstream.tool.CommonSharePrefsManager;
import com.jason.microstream.tool.TextUtil;
import com.jason.microstream.tool.cipher.AESTool;
import com.jason.microstream.tool.sp.CmSharedPrefs;
import com.jason.microstream.tool.sp.MsSharedPrefs;

public class AccountManager {
    private static final String ID_KEY = "uid";
    private static final String PWD_KEY = "pwd";
    private static final String TOKEN_KEY = "token";
    private static final String EXPIRE_KEY = "expire";

    private static volatile AccountManager accountManager;

    private User user;
    private String token;
    private Long tokenExpireTime;

    private AccountManager() {
    }

    public static AccountManager get() {
        if (accountManager == null) {
            synchronized (AccountManager.class) {
                if (accountManager == null) {
                    accountManager = new AccountManager();
                }
            }
        }
        return accountManager;
    }


    public void resetAccount(User user, String token, String expireAt, String userPwd) {
        setInitAccount(user,token,expireAt);
        saveUserInfoImp(user.getUid(), token, expireAt, userPwd);
    }
    private void setInitAccount(User user, String token, String expireAt) {
        this.user = user;
        this.token = token;
        this.tokenExpireTime = Long.valueOf(expireAt);
    }
    private void saveUserInfoImp(String uid, String token, String expireAt, String userPwd) {
        if (userPwd != null) {
            CmSharedPrefs.getInstance().putString(AccountManager.PWD_KEY, AESTool.newEncrypt(userPwd));
        }
        MsSharedPrefs.getInstance().putString(AccountManager.TOKEN_KEY, AESTool.newEncrypt(token));
        MsSharedPrefs.getInstance().putString(AccountManager.EXPIRE_KEY, expireAt);
        CmSharedPrefs.getInstance().putString(AccountManager.ID_KEY, user.getUid());
    }


    public void resetLogout() {
        clearAccountInfoImp();
        clearSavedUserInfoImp();
    }

    public void forceLogout() {
        resetLogout();
    }

    private void clearAccountInfoImp() {
        this.user = null;
        this.token = null;
        this.tokenExpireTime = 0L;
    }

    private void clearSavedUserInfoImp() {
        CmSharedPrefs.getInstance().remove(AccountManager.ID_KEY);
        // if remember pwd
        CmSharedPrefs.getInstance().remove(AccountManager.PWD_KEY);
        MsSharedPrefs.getInstance().remove(AccountManager.TOKEN_KEY);
        MsSharedPrefs.getInstance().remove(AccountManager.EXPIRE_KEY);
    }

    /**
     * get valid saved user info
     * @return
     */
    public User getSavedInitUser() {
        User user = new User();
        String uid = CmSharedPrefs.getInstance().getString(AccountManager.ID_KEY);
        if(TextUtil.isEmpty(uid)) return null;
        user.setUid(uid);
        MsSharedPrefs.getInstance().check(uid);
        String token = MsSharedPrefs.getInstance().getString(AccountManager.TOKEN_KEY);
        if(TextUtil.isEmpty(token)){
            MsSharedPrefs.getInstance().reset();
            return null;
        } else {
            token = AESTool.newDecrypt(token);
        }
        String expireTime = MsSharedPrefs.getInstance().getString(AccountManager.EXPIRE_KEY);
        if(TextUtil.isEmpty(expireTime)){
            MsSharedPrefs.getInstance().reset();
            return null;
        }
        long tokenExpireTime = Long.parseLong(expireTime);
        if (tokenExpireTime < System.currentTimeMillis()) {
            MsSharedPrefs.getInstance().reset();
            return null;
        }

        setInitAccount(user, token, String.valueOf(tokenExpireTime));
        return user;
    }


    public String getUid() {
        if(user==null) return null;
        return user.getUid();
    }

    public void setUid(String uid) {
        if (user != null) {
            user.setUid(uid);
        }
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setTokenExpireTime(String expireTime) {
        tokenExpireTime = Long.parseLong(expireTime);
    }


    public long getServerTime() {
        return System.currentTimeMillis();
    }


}
