package com.jason.microstream.core.im.tup;

import com.jason.microstream.core.im.imconpenent.MsgHandler;
import com.jason.microstream.core.im.tup.data.SendNode;
import com.jason.microstream.core.im.tup.data.msg.LoginPkg;
import com.jason.microstream.core.im.tup.joint.MsgNotifier;
import com.jason.microstream.tool.log.LogTool;

import java.io.IOException;

public class Core {
    private static final String TAG = Core.class.getSimpleName();
    public static final int ACTION_DATA = 0;
    public static final int ACTION_AUTH = 1;
    public static final int ACTION_BEAT = -1;
    public static final int ACTION_DISCONNECT = Integer.MAX_VALUE;

    private static Core core;
    private Core() {}
    public static Core getCore() {
        if (core == null) {
            synchronized (Core.class) {
                if (core == null) {
                    core = new Core();
                }
            }
        }
        return core;
    }

    private ChannelHolder channelHolder;
    private Demultiplexer demultiplexer;

    MsgNotifier msgNotify;

    private volatile boolean isInit = false;
    public void init(MsgHandler msgHandler, String host, String port) {
        if (!isInit) {
            synchronized (this) {
                if (!isInit) {
                    this.msgNotify = msgHandler;

                    demultiplexer = new Demultiplexer();
                    channelHolder = new ChannelHolder(host, port);
                    channelHolder.setDemultiplexer(demultiplexer);
                    new Thread(() -> channelHolder.connectSync()).start();

                    isInit = true;
                }
            }
        }
    }

    public void sendAuth(String uid,String token, SendNode.SendCallback callBack) {
        LogTool.e(TAG, "Core_auth:"
        );
        LoginPkg loginPkg = new LoginPkg();
        loginPkg.uid = uid;
        loginPkg.token = token;
//        sender.sendRegister(loginPkg);

        if (channelHolder.getContext() == null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    int ret = channelHolder.connectSync();
                    if (channelHolder.getContext() == null) {
                        LogTool.f(TAG, "发送时建立长连接失败！");
                        callBack.onSendFailed(new IOException("发送时建立长连接失败！"), null);
                    } else {
                        demultiplexer.sendAuth(channelHolder.getContext(), loginPkg, ACTION_AUTH, Coder.MSG_TYPE_REGISTER, callBack);
                    }
                }
            }).start();
        } else {
            demultiplexer.sendAuth(channelHolder.getContext(), loginPkg, ACTION_AUTH, Coder.MSG_TYPE_REGISTER, callBack);
        }
    }

    public long sendTest(Object msg,SendNode.SendCallback callBack) {
        return demultiplexer.sendTo(channelHolder, msg, ACTION_DATA, Coder.MSG_TYPE_TEST, callBack);
    }

    public long sendVideoCmd(Object msg, int cmd, SendNode.SendCallback callBack) {
        return demultiplexer.sendTo(channelHolder, msg, ACTION_DATA, cmd, callBack);
    }

    public void channelDown() {
        demultiplexer.handleCloseConnect(channelHolder.getContext(),channelHolder.mSocketChannel);

    }



    public MsgNotifier getMsgNotifier() {
        return msgNotify;
    }

}
