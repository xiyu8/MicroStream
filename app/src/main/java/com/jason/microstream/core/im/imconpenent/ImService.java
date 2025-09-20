package com.jason.microstream.core.im.imconpenent;

import static com.jason.microstream.core.im.tup.MsgDistributor.CMD_TAG;
import static com.jason.microstream.core.im.tup.MsgDistributor.MSG_TYPE_OFFER_SDP;
import static com.jason.microstream.core.im.tup.MsgDistributor.MSG_TYPE_SWAP_ICE;
import static com.jason.microstream.core.im.tup.MsgDistributor.MSG_TYPE_SWAP_SDP;

import android.app.Application;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.util.Log;

import com.google.gson.Gson;
import com.jason.microstream.core.im.tup.Core;
import com.jason.microstream.core.im.receiver.NetWorkStateReceiver;
import com.jason.microstream.core.im.tup.data.msg.TestMsg;
import com.jason.microstream.core.im.tup.data.msg.VideoCmd;
import com.jason.microstream.localbroadcast.Events;
import com.jason.microstream.localbroadcast.LocBroadcast;


public class ImService {
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
    private String uid;
    private String token;
    private Application application;
    boolean isInit = false;
    Core core;
    MsgHandler msgHandler;
    Gson gson;
    public void init(Application application, String host, String port) {
        if (application == null || host == null || port == null
                || host.equals("") || port.equals("")) {
            LocBroadcast.getInstance().sendBroadcast(Events.ACTION_ON_INIT_FAIL,"init fail");
        }

        this.host = host;
        this.port = port;
        this.application = application;

        initNetState(application);

        core = Core.getCore();
        msgHandler = new MsgHandler();
        core.init(application,msgHandler);

        isInit = true;
    }

    private void initNetState(Application application) {
        NetWorkStateReceiver netWorkStateReceiver = new NetWorkStateReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        application.registerReceiver(netWorkStateReceiver, filter);
    }

    public void auth(String imToken, String uid) {
        this.uid = uid;
        this.token = imToken;
        if (!isInit) {
            LocBroadcast.getInstance().sendBroadcast(Events.ACTION_ON_LOGIN_FAIL,"login without init");
            return;
        }

//        application.bindService(new Intent(application, NioPeriodChronicService.class), new ServiceConnection() {
//            @Override
//            public void onServiceConnected(ComponentName name, IBinder service) {
//                nioBinder = (NioPeriodChronicService.NioBinder) service;
//
//                nioBinder.registerNioSelector();
//                nioBinder.initWriteThread();
//                nioBinder.nioConnect(host,port,loginToken,uid);
//            }
//            @Override
//            public void onServiceDisconnected(ComponentName name) {}
//        }, Context.BIND_AUTO_CREATE);

        core.auth(host, port, uid, imToken);

    }

    public void reset() {
        this.uid = null;
        this.token = null;

        core.reset();

    }

    public void netChanged(boolean haveNet) {
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

        core.auth(host, port, uid, token);


    }

    public void send(Object oj) {
        core.send(oj);
    }
    public void sendTest(String toId,String ss) {
        TestMsg testMsg = new TestMsg(uid, toId, toId, ss);
        core.sendTest(testMsg);
    }

    public void sendVideoCmd(Object oj, String peerId, int cmd) {
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
        core.sendVideoCmd(videoCmd, cmd);
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
