package com.jason.microstream.core.im.tup.sender;




import com.jason.microstream.core.im.tup.Coder;
import com.jason.microstream.core.im.tup.Core;
import com.jason.microstream.core.im.tup.Demultiplexer;
import com.jason.microstream.core.im.tup.channelcontext.ChannelContext;
import com.jason.microstream.core.im.tup.data.SendNode;
import com.jason.microstream.tool.log.LogTool;

import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class SenderDemultiplexer {
    private static final String TAG = SenderDemultiplexer.class.getSimpleName();


    Demultiplexer demultiplexer;
    private int CAPACITY = 1;

    private final ConcurrentHashMap<Long, Integer> demultipleIndexMap;
    private final List<Sender> senders;
    private final AtomicInteger counter = new AtomicInteger(0);



    public SenderDemultiplexer(Demultiplexer demultiplexer) {
        this.demultiplexer = demultiplexer;
        senders =new ArrayList<>(CAPACITY);
        for (int i = 0; i < CAPACITY; i++) {
            senders.add(new Sender(demultiplexer));
        }
        demultipleIndexMap = new ConcurrentHashMap<>();

    }

    private Integer getDemultiplexerIndex(ChannelContext channelContext) {
        return demultipleIndexMap.get((long) (channelContext.getSocket().hashCode()));
    }

    private Sender getSender(ChannelContext channelContext) {
        Integer senderIndex;
        if ((senderIndex = getDemultiplexerIndex(channelContext)) != null) {
            return senders.get(senderIndex);
        }
        return null;
    }

    public boolean registerSend(SocketChannel socketChannel,ChannelContext channelContext) {
        int senderIndex = demultipleIndexMap.computeIfAbsent((long) socketChannel.hashCode(), code -> {
            return counter.getAndIncrement() % CAPACITY;
        });
        Sender sender;
        if ((sender = senders.get(senderIndex)) == null) {
            sender = new Sender(this.demultiplexer);
            senders.set(senderIndex, sender);
        }
        return sender.registerSend(socketChannel,channelContext);
    }

    public void unregister(SocketChannel socketChannel) {
        Integer senderIndex = demultipleIndexMap.get((long) (socketChannel.hashCode()));
        if (senderIndex != null) {
            Sender sender = senders.get(senderIndex);
            sender.unregister(socketChannel);
        }
    }

    public void ackedCandidateNode(ChannelContext channelContext, Coder.CoderData coderData) {
        Sender sender;
        if ((sender = getSender(channelContext)) != null) {
            sender.ackedCandidateNode(channelContext, coderData);
        }
    }

    public long sendData(ChannelContext channelContext, Object ob, int action, int msgType, SendNode.SendCallback callback) {
        if (callback == null) {
            callback = new SendNode.SendCallback() {
                @Override
                public void onSendSuccess(SendNode node) {
                    LogTool.e(TAG, "SenderDemultiplexer in new SendNode.SendCallback-onSendSuccess："
                            + "-node.seqId:" + node.seqId
                    );
                }

                @Override
                public void onSendFailed(IOException e, SendNode node) {
                    LogTool.e(TAG, "SenderDemultiplexer in new SendNode.SendCallback-onSendFailed："
                            + "-IOException:" + e
                            + "-node.seqId:" + node.seqId
                    );
                }
            };
        }
        Sender sender;
        long retId = -1;
        if ((sender = getSender(channelContext)) != null) {
            LogTool.e(TAG, "sendData_:"+ (action == Core.ACTION_AUTH ? "Sender.ACTION_AUTH" : "Sender.ACTION_DATA")
            );
            retId = sender.sendImp(channelContext, ob, action, msgType, callback);
        } else {
            LogTool.e(TAG, "sendData_:send data without register sender"
            );
            throw new RuntimeException("send data without register sender");
        }
        return retId;
    }

    public void sendAck(ChannelContext channelContext, Coder.CoderData coderData) {
        Sender sender;
        if ((sender = getSender(channelContext)) != null) {
            LogTool.e(TAG, "sendAck:"
                    + "-coderData.stubId:"+coderData.stubId
                    + "-coderData.seqId:"+coderData.seqId
            );
            sender.sendAckImp(channelContext, coderData);
        } else {
            LogTool.e(TAG, "sendAck:send data without register sender");
            throw new RuntimeException("send data without register sender");
        }
    }
}
