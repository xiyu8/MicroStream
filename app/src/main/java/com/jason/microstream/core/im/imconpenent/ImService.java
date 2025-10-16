package com.jason.microstream.core.im.imconpenent;

import static com.jason.microstream.core.im.tup.Coder.MSG_TYPE_OFFER_SDP;
import static com.jason.microstream.core.im.tup.Coder.MSG_TYPE_SWAP_ICE;
import static com.jason.microstream.core.im.tup.Coder.MSG_TYPE_SWAP_SDP;
import static com.jason.microstream.core.im.tup.MsgDistributor.CMD_TAG;

import android.app.Application;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.util.Log;

import com.google.gson.Gson;
import com.jason.microstream.core.im.receiver.NetWorkStateReceiver;
import com.jason.microstream.core.im.tup.Core;
import com.jason.microstream.core.im.tup.data.SendNode;
import com.jason.microstream.core.im.tup.data.msg.TestMsg;
import com.jason.microstream.core.im.tup.data.msg.VideoCmd;
import com.jason.microstream.localbroadcast.Events;
import com.jason.microstream.localbroadcast.LocBroadcast;
import com.jason.microstream.tool.log.LogTool;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;


public class ImService implements NetChanger {
    public static final String TAG = "ImService";

    private static volatile ImService imService;
    private ImService() {
        gson = new Gson();
    }
    public static ImService getIm() {
        if (imService == null) {
            synchronized (ImService.class){
                if (imService == null) {
                    imService = new ImService();
                }
            }
        }
        return imService;
    }

    private String host;
    private String port;
    private Application application;
    private volatile boolean isInit = false;
    private Core core;
    private MsgHandler msgHandler;
    private Gson gson;
    public void init(Application application, String host, String port) {
        if (application == null || host == null || port == null
                || host.equals("") || port.equals("")) {
            LocBroadcast.getInstance().sendBroadcast(Events.ACTION_ON_INIT_FAIL,"init fail");
        }
        if (!isInit) {
            synchronized (this) {
                if (!isInit) {
                    this.host = host;
                    this.port = port;
                    this.application = application;
                    initNetState(application);

                    core = Core.getCore();
                    msgHandler = new MsgHandler();
                    core.init(msgHandler, host, port);

                    isInit = true;
                }
            }
        }
    }


    private void initNetState(Application application) {
        NetWorkStateReceiver netWorkStateReceiver = new NetWorkStateReceiver(this);
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        application.registerReceiver(netWorkStateReceiver, filter);
    }

//////////////auth//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private String uid;
    private String token;
    private boolean isAuthed = false;
    private AuthResult authResultCallback;
    private boolean logining = false;
    public void auth(String imToken, String uid, AuthResult authResult) {
        if (!isInit) {
            LocBroadcast.getInstance().sendBroadcast(Events.ACTION_ON_LOGIN_FAIL,"login without init");
            LogTool.f(TAG, "auth失败：登录前需要初始化！！！");
            isAuthed = false;
            if (authResult != null) authResult.onAuthFail();
            return;
        }
        if (imToken == null || uid == null) {
            LogTool.f(TAG, "auth失败：账号为null");
            if (authResult != null) authResult.onAuthFail();
            return;
        }
        if (logining) {
            LogTool.f(TAG, "auth失败：正在登录中......");
            if (authResult != null) authResult.onAuthFail();
            return;
        }

        if (isAuthed) {
            if (this.uid != null && this.token != null
                    && (!this.uid.equals(uid) || !this.token.equals(imToken))) {
                LogTool.f(TAG, "auth失败：账号不一致");
                if (authResult != null) authResult.onAuthFail();
            }
            return;
        }

        logining = true;
        this.uid = uid;
        this.token = imToken;
        this.authResultCallback = authResult;
        //login timeout timer  ???????????????????
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                if (logining) {
                    setAuthed(false, null, null);
                    logining = false;
                }
            }
        };
        Timer timer = new Timer("timer");
        timer.schedule(task, 5000);

        core.sendAuth(uid, imToken, new SendNode.SendCallback() {
            @Override
            public void onSendSuccess(SendNode node) {
                LogTool.f(TAG, "登录消息发送成功(验证了长连接是否能发数据)");
            }

            @Override
            public void onSendFailed(IOException e, SendNode node) {
                LogTool.f(TAG, "登录消息发送失败(验证了长连接是否能发数据)");
                isAuthed = false;
                logining = false;
                if (authResult != null) authResult.onAuthFail();
            }
        });
    }
    // TODO:登录结果的策略另定：失败断开长连接、失败是否业务层登出  等等
    public void setAuthed(boolean loginResult, String uid, String token) {
        logining = false;
        if (authResultCallback != null) {
            if (loginResult) {
                authResultCallback.onAuthSuccess();
            } else {
                core.channelDown();
                //TODO:当服务端返回 登录失败时未处理(清除token并登出)
                authResultCallback.onAuthFail();
            }
        }
    }

    public interface AuthResult{
        void onAuthFail();
        void onAuthSuccess();
    }

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    public void reset() {
        this.uid = null;
        this.token = null;

        core.channelDown();
    }

    public void onNetChanged(boolean haveNet) {
        if (!isInit) {
            return;
        }
        if (token == null || token.equals("")) {
            return;
        }

        if (!haveNet) {
            core.channelDown();
            return;
        }
        auth(token, uid, null);
    }


    public void sendTest(String toId, String ss, SendNode.SendCallback callBack) {
        TestMsg testMsg = new TestMsg(uid, toId, toId, ss);
        core.sendTest(testMsg, callBack);
    }

    public void sendVideoCmd(Object oj, String peerId, int cmd, SendNode.SendCallback callBack) {
        VideoCmd videoCmd = new VideoCmd(uid,peerId,gson.toJson(oj), peerId, cmd);
        if (cmd == MSG_TYPE_OFFER_SDP) {
            Log.e(CMD_TAG, "send cmd MSG_TYPE_OFFER_SDP:");
        } else if (cmd == MSG_TYPE_SWAP_ICE) {
            Log.e(CMD_TAG, "send cmd MSG_TYPE_SWAP_ICE:");
        } else if (cmd == MSG_TYPE_SWAP_SDP) {
            Log.e(CMD_TAG, "send cmd MSG_TYPE_SWAP_SDP:");
        } else {
            Log.e(CMD_TAG, "send cmd MSG_TYPE_OFFER_SDP:");
        }
        core.sendVideoCmd(videoCmd, cmd,callBack);
    }

    public String getHost() {
        return host;
    }

    public String getPort() {
        return port;
    }

    public String getUid() {
        return uid;
    }

    public String getToken() {
        return token;
    }


}
