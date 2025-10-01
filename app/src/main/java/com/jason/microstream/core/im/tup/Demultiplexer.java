package com.jason.microstream.core.im.tup;

import com.jason.microstream.core.im.tup.channelcontext.ChannelContext;
import com.jason.microstream.core.im.tup.data.SendNode;
import com.jason.microstream.core.im.tup.data.msg.Msg;
import com.jason.microstream.core.im.tup.reader.ReaderDemultiplexer;
import com.jason.microstream.core.im.tup.sender.SenderDemultiplexer;
import com.jason.microstream.tool.log.LogTool;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class Demultiplexer {
    private static final String TAG = Demultiplexer.class.getSimpleName();

    private ChannelIntrinsicSet channelIntrinsicSet = new ChannelIntrinsicSet();


    private final ReaderDemultiplexer readerDemultiplexer;
    private final SenderDemultiplexer senderDemultiplexer;

    public Demultiplexer() {
        readerDemultiplexer = new ReaderDemultiplexer(this);
        senderDemultiplexer = new SenderDemultiplexer(this);
    }

    public int handleConnectingChannel(SocketChannel clientChannel, SelectionKey serverSelectionKey,ChannelHolder channelHolder) {
        LogTool.e(TAG, "handleConnectingChannel:"
        );
        int ret = 1;

        ChannelContext channelContext = genContext(clientChannel, channelHolder);
        channelHolder.setContext(channelContext);

        //????先register上Sender再register上reader，防止没有registerSender就去发送消息的情况
        // 冲突：attachSendContext是registerSend中调用的，用于公用一个attachment的context，但，要保证绝对的 没有registerSender就去发送消息的情况 要先registerSend
        if (readerDemultiplexer.registerRead(clientChannel, channelContext)
                && senderDemultiplexer.registerSend(clientChannel, channelContext)) {
            ret = 0;
        } else {
            ret = 1;
            LogTool.e(TAG, "handleConnectingChannel:register new client socket error!!!");
            handleCloseConnect(channelContext, clientChannel);
        }
        return ret;
    }

    // TODO: 未认证的channel在等待一定时间后 需要把它关闭
    private Map<SocketChannel, ChannelContext> unAthuedContextMap = new ConcurrentHashMap<>();
    private ChannelContext genContext(SocketChannel channel,ChannelHolder channelHolder) {
        ChannelContext channelContext;
        if ((channelContext = unAthuedContextMap.get(channel)) != null) {
            return channelContext;
        }
        channelContext = new ChannelContext(genSessionId(channel), channel);
        channelContext.setChannelHolder(channelHolder);
        channelContext.setAccessTime(System.currentTimeMillis());
        channelContext.channelAuth.setAuthFlag(false);
        unAthuedContextMap.put(channel, channelContext);
        return channelContext;
    }

    private static final AtomicLong idGenerator = new AtomicLong(0);
    private long genSessionId(SocketChannel clientChannel) {
        //TODO:临时的写法
        return idGenerator.incrementAndGet();
    }

    public void authedChannel(ChannelContext channelContext, String uid, String token) {
        if (!channelContext.isActive()) {
            return;
        }

        LogTool.e(TAG, "attachChannel:"
                +"-userId::::"+uid
                +"-token::::"+token
        );

        channelContext.channelAuth.setAuthFlag(true);
        channelContext.channelAuth.setAuthTime(System.currentTimeMillis());
        channelContext.channelAuth.setUid(uid);
        channelContext.channelAuth.setToken(token);
        channelContext.setIntrinsicMark(uid);

        if (channelIntrinsicSet.get(channelContext.getIntrinsicMark()) == null) {
            channelIntrinsicSet.add(channelContext.getIntrinsicMark(), channelContext);
        }

        unAthuedContextMap.remove(channelContext.getSocket());
    }

    public void handleCloseConnect(ChannelContext channelContext, SocketChannel channel) {
        LogTool.e(TAG, "handleCloseConnect:"
        );
        if (channelContext != null) {
            readerDemultiplexer.unregister(channelContext.getSocket());
            senderDemultiplexer.unregister(channelContext.getSocket());
            channelIntrinsicSet.remove(channelContext);
            if (channelContext.getChannelHolder() != null) {
                channelContext.getChannelHolder().nioDisconnect();
            } else {
                try {
                    channelContext.getSocket().close();
                } catch (IOException e) {
                    LogTool.e(TAG, "handleCloseConnect-channelSocket.close() Exception:" +
                            "-IOException e:" + e.getMessage());
                }
            }
        } else {
            unAthuedContextMap.remove(channel);
            try {
                channel.close();
            } catch (IOException e) {
                LogTool.e(TAG, "handleCloseConnect-channelSocket.close() Exception:" +
                        "-IOException e:" + e.getMessage());
            }
        }
    }




///////////////////////////////////////////////////////////////////////////////////////////////////////

    public void ackedCandidateNode(ChannelContext channelContext, Coder.CoderData coderData) {
        senderDemultiplexer.ackedCandidateNode(channelContext, coderData);
    }

    //服务端用法时，配合uid查找IntrinsicSet 找到对应的通道
    public long sendTo(Object ob, int action, int msgType, String uid, SendNode.SendCallback callBack) {
        ChannelContext channelContext = channelIntrinsicSet.get(uid);
        long retId = -1;
        if (channelContext != null) {
            retId = sendTo(channelContext, ob, action, msgType, callBack);
        } else {
            if (callBack != null) {
                LogTool.e(TAG, "sendTo-none of userId with channel:"
                        +"-userId:" + uid
                        + "-((Msg) ob).getSeqId():" + ((ob instanceof Msg) ? ((Msg) ob).getSeqId() : ob)
                        + "-((Msg) ob).getStubId():" + ((ob instanceof Msg) ? ((Msg) ob).getStubId() : ob)
                );
                callBack.onSendFailed(new IOException("none of userId with channel"), null);
            }
        }
        return retId;
    }

    //客户端用法...
    public long sendTo(ChannelHolder channelHolder, Object ob, int action, int msgType, SendNode.SendCallback callBack) {
        if (channelHolder == null) {
            throw new RuntimeException("SMIM send without init ,null of channelHolder");
        }
        long retId = -1;
        ChannelContext channelContext = null;
        if ((channelContext = channelHolder.getContext()) != null) {
            retId = senderDemultiplexer.sendData(channelContext, ob, action, msgType, callBack);
        } else {
            LogTool.e(TAG, "sendTo-none of userId with channelContext:"
                    + "-((Msg) ob).getSeqId():" + ((ob instanceof Msg) ? ((Msg) ob).getSeqId() : ob)
                    + "-((Msg) ob).getStubId():" + ((ob instanceof Msg) ? ((Msg) ob).getStubId() : ob)
            );
            if (callBack != null) {
                callBack.onSendFailed(new IOException("none of userId with channelContext"), null);
            }
        }
        return retId;
    }

    public long sendTo(ChannelContext channelContext, Object ob, int action, int msgType, SendNode.SendCallback callBack) {
        long retId = -1;
        if (channelContext != null) {
            retId = senderDemultiplexer.sendData(channelContext, ob, action, msgType, callBack);
        } else {
            if (callBack != null) {
                LogTool.e(TAG, "sendTo-none of userId with channel:"
                        + "-((Msg) ob).getSeqId():" + ((ob instanceof Msg) ? ((Msg) ob).getSeqId() : ob)
                        + "-((Msg) ob).getStubId():" + ((ob instanceof Msg) ? ((Msg) ob).getStubId() : ob)
                );
                callBack.onSendFailed(new IOException("none of userId with channel"), null);
            }
        }
        return retId;
    }


    public void sendAuth(ChannelContext channelContext, Object ob, int action, int msgType, SendNode.SendCallback callback) {
        LogTool.e(TAG, "sendAuth_:" + (action == Core.ACTION_AUTH ? "Sender.ACTION_AUTH" : "Sender.ACTION_DATA"));
        if (channelContext == null) {
            LogTool.e(TAG, "sendAuth_Error:sendAuth with channelContext == null!!!!!");
            throw new RuntimeException();
        }
        if (!channelContext.isActive()) { //???是否有必要检查
            return;
        }
        //TODO: 是否存在handleConnectingChannel 时 没有senderDemultiplexer.registerSend的情况
        senderDemultiplexer.sendData(channelContext, ob, action, msgType,callback);
    }



    public void sendAck(Coder.CoderData coderData, ChannelContext channelContext) {
        if (!channelContext.isActive()) {
            return;
        }
        //TODO: 是否存在handleConnectingChannel 时 没有senderDemultiplexer.registerSend的情况
        senderDemultiplexer.sendAck(channelContext, coderData);
    }



}
