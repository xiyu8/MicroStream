package com.jason.microstream.core.im.tup;

import com.jason.microstream.core.im.imconpenent.ImService;
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
//                    new Thread(() -> channelHolder.connectSync()).start(); //

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
                    if (ret == 0) {
                        demultiplexer.sendAuth(channelHolder.getContext(), loginPkg, ACTION_AUTH, Coder.MSG_TYPE_REGISTER, callBack);
                    } else {
                        LogTool.f(TAG, "发送时建立长连接失败！");
                        callBack.onSendFailed(new IOException("发送时建立长连接失败！"), null);
                    }
                }
            }).start();
        } else {
            demultiplexer.sendAuth(channelHolder.getContext(), loginPkg, ACTION_AUTH, Coder.MSG_TYPE_REGISTER, callBack);
        }
    }

    private long sendWithInactive(ChannelHolder channelHolder,Object msg,int action, int msgType, SendNode.SendCallback callBack) {
        new Thread(() -> {

            int ret = channelHolder.connectSync();
            if (ret == 0) {
                String uid = null, token = null;
                if ((uid = ImService.getIm().getUid()) != null && (token = ImService.getIm().getToken()) != null) {
                    ImService.getIm().auth(token, uid, new ImService.AuthResult() {
                        @Override
                        public void onAuthFail() {
                            LogTool.e(TAG, "发送时账号验证失败！");
                            callBack.onSendFailed(new IOException("send with auth fail!"), null);
                        }

                        @Override
                        public void onAuthSuccess() {
                            demultiplexer.sendTo(channelHolder, msg, action, msgType, callBack);
                        }
                    });
                }else {
                    LogTool.e(TAG, "发送时本地没有有效账号！");
                    callBack.onSendFailed(new IOException("send with null of uidToken!"), null);
                }
            } else {
                LogTool.e(TAG, "发送时建立长连接失败！");
                callBack.onSendFailed(new IOException("send with connect fail!"), null);
            }

        }).start();
        return 0;
    }

    public long sendTest(Object msg,SendNode.SendCallback callBack) {
        if (channelHolder.getContext() == null) {
            sendWithInactive(channelHolder, msg, ACTION_DATA, Coder.MSG_TYPE_TEST, callBack);
        } else {
            demultiplexer.sendTo(channelHolder, msg, ACTION_DATA, Coder.MSG_TYPE_TEST, callBack);
        }
        return 0;
    }

    public long sendRequest(Object msg,SendNode.SendCallback callBack) {
        if (channelHolder.getContext() == null) {
            sendWithInactive(channelHolder, msg, ACTION_DATA, Coder.MSG_TYPE_REQUEST, callBack);
        } else {
            demultiplexer.sendTo(channelHolder, msg, ACTION_DATA, Coder.MSG_TYPE_REQUEST, callBack);
        }
        return 0;
    }

    public long sendVideoCmd(Object msg, int cmd, SendNode.SendCallback callBack) {
        if (channelHolder.getContext() == null) {
            sendWithInactive(channelHolder, msg, ACTION_DATA, cmd, callBack);
        } else {
            demultiplexer.sendTo(channelHolder, msg, ACTION_DATA, cmd, callBack);
        }
        return 0;
    }

    /**
     * 暴露给外面的原生接口：action为data，但msgType交给外部自行定义
     * @param msg
     * @param msgType
     * @param callBack
     * @return
     */
    public long sendTo(Object msg, int msgType, SendNode.SendCallback callBack) {
        if (channelHolder.getContext() == null) {
            sendWithInactive(channelHolder, msg, ACTION_DATA, msgType, callBack);
        } else {
            demultiplexer.sendTo(channelHolder, msg, ACTION_DATA, msgType, callBack);
        }
        return 0;
    }

    public void channelDown() {
        demultiplexer.handleCloseConnect(channelHolder.getContext(),channelHolder.mSocketChannel);

    }



    public MsgNotifier getMsgNotifier() {
        return msgNotify;
    }

}
