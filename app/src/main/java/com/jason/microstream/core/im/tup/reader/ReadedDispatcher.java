package com.jason.microstream.core.im.tup.reader;

import com.jason.microstream.core.im.tup.Coder;
import com.jason.microstream.core.im.tup.Core;
import com.jason.microstream.core.im.tup.Demultiplexer;
import com.jason.microstream.core.im.tup.MsgDistributor;
import com.jason.microstream.core.im.tup.channelcontext.ChannelContext;
import com.jason.microstream.tool.log.LogTool;

import java.util.HashSet;
import java.util.Set;

public class ReadedDispatcher {
    private final String TAG = ReadedDispatcher.class.getSimpleName();
    private Coder coder;

    private Demultiplexer demultiplexer;
    private MsgDistributor msgDistributor;

    public ReadedDispatcher(Demultiplexer demultiplexer) {
        this.demultiplexer = demultiplexer;
        this.coder = new Coder();
        msgDistributor = new MsgDistributor(demultiplexer);
    }

    public void dispatchData(byte[] data, ChannelContext channelContext) {
        Coder.CoderData coderData = coder.decode(data);
        if (coderData == null) {
            LogTool.e(TAG, "dispatchData:" +"-coderData == null");
            receiverException(ReadedDispatcher.SendCmd.READ_DATA_EXCEPTION);
            return;
        }

        if (coderData.ackFlag == 1) {
            LogTool.e(TAG, "dispatchData:"
                    + "demultiplexer.ackedCandidateNode(receiver.socketChannel,coderData)"
                    + "-coderData.msgAction == Sender.ACTION_AUTH:" + (coderData.msgAction == Core.ACTION_AUTH)
                    + "-coderData.msgAction:" + coderData.msgAction
            );
            if (coderData.msgAction == Core.ACTION_AUTH) {
                LogTool.e(TAG, "-------------dispatchData收到登录消息的ack:"
                );
            }
            demultiplexer.ackedCandidateNode(channelContext,coderData); //根据ack取原数据
        } else {
//            coderData.seqId = getSeqId();
            coderData.stubId = getStubId();

            LogTool.e(TAG, "dispatchData:"
                    + "-coderData.msgAction == Sender.ACTION_AUTH:" + (coderData.msgAction == Core.ACTION_AUTH)
                    + "-coderData.msgAction:" + coderData.msgAction
                    + "-coderData.stubId:" + coderData.stubId
                    + "-coderData.seqId:" + coderData.seqId
            );
            demultiplexer.sendAck(coderData.clone(),channelContext);
            int dispatched = msgDistributor.distribute(channelContext,false,coderData.msgData, coderData.msgAction, coderData.msgType); //消息可能重复，业务层处理
        }


    }

    private Set<Long> idsSet = new HashSet<>();
    private long getStubId() {
        long id = System.currentTimeMillis();
        while (idsSet.contains(id)) {
            id++;
        }
        idsSet.add(id);
        return id;
    }


    private void receiverException(int code) {
//        if (code == Dispatcher.SendCmd.READ_IO_EXCEPTION) {
////            channelHolder.nioDisconnect();
//
//        } else if (code == Dispatcher.SendCmd.READ_DATA_EXCEPTION) {
////            channelHolder.nioDisconnect();
//
//        }
    }

    public static class SendCmd {
        public static final int SEND_CHANNEL_NULL = 101;
        public static final int SEND_CHANNEL_UNCONNECTED = 102;
        public static final int SEND_CHANNEL_UNCHECK = 103;
        public static final int SEND_CHANNEL_UNAUTH = 104;
        public static final int SEND_IO_EXCEPTION = 105;

        public static final int READ_IO_EXCEPTION = 201;
        public static final int READ_DATA_EXCEPTION = 202;


        public static final int SOCKET_OPEN_FAIL = 1001;
        public static final int SOCKET_CONNECT_FAIL = 1002;

    }
}
