package com.jason.microstream.core.im.tup;

import android.app.Application;
import android.content.Context;

import com.jason.microstream.core.im.imconpenent.MsgHandler;
import com.jason.microstream.core.im.tup.joint.MsgNotifier;
import com.jason.microstream.core.im.tup.data.msg.LoginPkg;

import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

public class Core {

    private static Core core;

    private String host;
    private String port;

    private Core() {
    }

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

    public boolean isInit = false;

    private ChannelHolder channelHolder;
    private Coder coder;
    private Dispatcher dispatcher;
    private Receiver receiver;
    private Sender sender;
    MsgNotifier msgNotify;
    MsgDistributor msgDistributor;

    public void init(Application application, MsgHandler msgHandler) {
        this.application = application;
        this.msgNotify = msgHandler;

        channelHolder = new ChannelHolder();
        coder = new Coder();
        receiver = new Receiver();
        channelHolder.setReceiver(receiver);
        sender = new Sender();
        sender.setChannelHolder(channelHolder);

        sender.setCoder(coder);
        // receiver.setCoder(coder);
        SenderQueueTimer queueTimer = new SenderQueueTimer(sender);
        sender.setQueueTimer(queueTimer);

        msgDistributor = new MsgDistributor();
        msgDistributor.setMsgNotifier(msgNotify);

        dispatcher = new Dispatcher();
        dispatcher.setChannelHolder(channelHolder);
        dispatcher.setCore(this);
        dispatcher.setMsgDistributor(msgDistributor);
        dispatcher.setSender(sender);
        dispatcher.setCoder(coder);
        dispatcher.setSenderQueueTimer(queueTimer);
        sender.setDispatcher(dispatcher);
        receiver.setDispatcher(dispatcher);
        channelHolder.setDispatcher(dispatcher);
        queueTimer.setDispatcher(dispatcher);
        msgDistributor.setDispatcher(dispatcher);
        msgDistributor.setChannelHolder(channelHolder);

        isInit = true;
        sender.start();

    }

    public void auth(String host, String port,String uid,String token) {
        this.host = host;
        this.port = port;
        LoginPkg loginPkg = new LoginPkg();
        loginPkg.uid = uid;
        loginPkg.token = token;
        sender.sendRegister(loginPkg);
    }

    public void reset() {
        sender.sendShutdownImp();
        sender.stop();
        channelHolder.shutdown();
    }

    public void channelDown() {
        channelHolder.nioDisconnect();
    }


    public void send(Object oj) {
//        sender.send(msg);
    }

    public void sendTest(Object msg) {
        sender.sendTest(msg);
    }

    public void sendVideoCmd(Object msg, int cmd) {
        sender.sendVideoCmd(msg, cmd);
    }

    protected void sendAck() {

    }

    Application application;
    public Context getContext() {
        return application;
    }

    public String getHost() {
        return host;
    }

    public String getPort() {
        return port;
    }

    public void connectReseted(SocketChannel socketChannel, Selector selector) {
        sender.setSocketChannel(socketChannel);
        receiver.setSocketChannel(socketChannel);
        receiver.setSelector(selector);
//        receiver.start();

    }

}
