//package com.jason.microstream.core.im.tup;
//
//import com.jason.microstream.Tool;
//import com.jason.microstream.core.im.tup.data.SendNode;
//import com.jason.microstream.tool.log.LogTool;
//
//import java.nio.channels.Selector;
//import java.nio.channels.SocketChannel;
//
//public class Dispatcher {
//    private final String TAG = Dispatcher.class.getSimpleName();
//
//    private ChannelHolder channelHolder;
//    private Core core;
//    private MsgDistributor msgDistributor;
//    private Coder coder;
//    private Sender sender;
//    private SenderQueueTimer senderQueueTimer;
//
//
//    public void dispatchData(byte[] data) {
//        activeChannelTime();
//        senderQueueTimer.resetBeat();
//        Coder.CoderData coderData = coder.decode(data);
//        LogTool.e(TAG, "dispatchData__coderData.msgAction:" + coderData.msgAction
//                + "-coderData.ackFlag:" + coderData.ackFlag
//                + "-coderData.seqId:" + coderData.seqId
//                + "-coderData.stubId:" + coderData.stubId
//        );
//        if (coderData == null) {
//            receiverException(Dispatcher.SendCmd.READ_DATA_EXCEPTION);
//            return;
//        }
//        if (coderData.ackFlag == 1) {
//            LogTool.e(TAG, "dispatchData:"
//                    + "senderQueueTimer.ackedCandidateNode(coderData)"
//            );
//            data = senderQueueTimer.ackedCandidateNode(coderData); //根据ack取原数据
//            if (data == null) { //ack可能被重复推送
//                return;
//            }
//            coderData = coder.decode(data);
//
//            if (coderData.msgAction == Sender.ACTION_AUTH) {
////                int dispatched = msgDistributor.distribute(coderData.msgData, coderData.msgAction); //消息可能重复，业务层处理
//            } else {
////                int dispatched = msgDistributor.distribute(coderData.msgData, coderData.msgAction); //消息可能重复，业务层处理
//            }
//        } else {
//            LogTool.e(TAG, "dispatchData:"
//                    + "-sender.sendAckImp(coderData.clone())"
//                    + "-coderData.stubId:" + coderData.stubId
//                    + "-coderData.seqId:" + coderData.seqId
//            );
//            if (coderData.msgAction == Sender.ACTION_AUTH) {
//                int dispatched = msgDistributor.distribute(coderData.msgData, coderData.msgAction); //消息可能重复，业务层处理
//            } else {
//                int dispatched = msgDistributor.distribute(coderData.msgData, coderData.msgAction); //消息可能重复，业务层处理
//            }
//            sender.sendAckImp(coderData.clone());
//        }
//
//    }
//
//    private void activeChannelTime() {
//        //TODO:更新socketChannel最近一次激活的时间
//    }
//
//    public void dataSendFail(int errCode, SendNode sendNode) {
//        byte[] trimData = new byte[sendNode.data.length - 4];
//        System.arraycopy(sendNode.data, 4, trimData, 0, trimData.length);
//        Coder.CoderData coderData = coder.decode(trimData);
//        if (coderData == null) {
//            return;
//        }
//
//        if (coderData.msgAction == Sender.ACTION_AUTH) {
//            int dispatched = msgDistributor.msgFail(errCode, coderData.msgData,coderData.msgAction); //消息可能重复，业务层处理
//        } else {
//            int dispatched = msgDistributor.msgFail(errCode, coderData.msgData,coderData.msgAction); //消息可能重复，业务层处理
//        }
//
//    }
//
//    public void dataSendSuccess(SendNode sendNode) {
//        byte[] trimData = new byte[sendNode.data.length - 4];
//        System.arraycopy(sendNode.data, 4, trimData, 0, trimData.length);
//        Coder.CoderData coderData = coder.decode(trimData);
//        if (coderData == null) {
//            return;
//        }
//        coderData.seqId = sendNode.seqId;
//        LogTool.e(TAG, "dataSendSuccess:" + coderData.msgAction
//                + "-coderData.ackFlag:" + coderData.ackFlag
//                + "-coderData.seqId:" + coderData.seqId
//                + "-coderData.stubId:" + coderData.stubId
//        );
//        int dispatched = msgDistributor.msgSendSuccess(coderData.msgData,coderData.msgAction);
//    }
//
//    public void forceLogout() {
//        channelHolder.nioDisconnect();
//
//    }
//
//    public void receiverException(int code) {
//        if (code == SendCmd.READ_IO_EXCEPTION) {
//            channelHolder.nioDisconnect();
//
//        } else if (code == SendCmd.READ_DATA_EXCEPTION) {
//            channelHolder.nioDisconnect();
//
//        }
//    }
//
//    public void senderException(int code) {
//        if (core==null||!core.isInit) {
//            throw new RuntimeException(new Exception("im service without init!!!"));
//        }
//
//        if (code == SendCmd.SEND_CHANNEL_NULL) {
//            channelHolder.connectSync(/*core.getHost(),*/);
//        } else if (code == SendCmd.SEND_CHANNEL_UNCONNECTED) {
//            channelHolder.connectSync(/*core.getHost(),*/);
//
//        } else if (code == SendCmd.SEND_CHANNEL_UNCHECK) {
//            channelHolder.connectSync(/*core.getHost(),*/);
//
//        } else if (code == SendCmd.SEND_CHANNEL_UNAUTH) {
//            channelHolder.connectSync(/*core.getHost(),*/);
//
//        } else if (code == SendCmd.SEND_IO_EXCEPTION) {
//
//        }
//
//
//        else if (code == SendCmd.SOCKET_OPEN_FAIL) {
//
//        } else if (code == SendCmd.SOCKET_CONNECT_FAIL) {
//
//        } else if (code == SendCmd.READ_IO_EXCEPTION) {
//            channelHolder.nioDisconnect();
//
//        } else if (code == SendCmd.SEND_IO_EXCEPTION) {
//
//        } else if (code == SendCmd.SEND_IO_EXCEPTION) {
//
//        } else if (code == SendCmd.SEND_IO_EXCEPTION) {
//
//        }
//
//    }
//
//    public void connectReseted(SocketChannel socketChannel, Selector selector) {
//        core.connectReseted(socketChannel, selector);
//
//    }
//
//    public void setSenderQueueTimer(SenderQueueTimer senderQueueTimer) {
//        this.senderQueueTimer = senderQueueTimer;
//    }
//
//    ////////////////////////////////////////////////////////////////////////////////////////////////////
//    public static class SendCmd {
//        public static final int SEND_CHANNEL_NULL = 101;
//        public static final int SEND_CHANNEL_UNCONNECTED = 102;
//        public static final int SEND_CHANNEL_UNCHECK = 103;
//        public static final int SEND_CHANNEL_UNAUTH = 104;
//        public static final int SEND_IO_EXCEPTION = 105;
//
//        public static final int READ_IO_EXCEPTION = 201;
//        public static final int READ_DATA_EXCEPTION = 202;
//
//
//        public static final int SOCKET_OPEN_FAIL = 1001;
//        public static final int SOCKET_CONNECT_FAIL = 1002;
//
//    }
//
//    public ChannelHolder getChannelHolder() {
//        return channelHolder;
//    }
//
//    public void setChannelHolder(ChannelHolder channelHolder) {
//        this.channelHolder = channelHolder;
//    }
//
//    public MsgDistributor getMsgDistributor() {
//        return msgDistributor;
//    }
//
//    public void setMsgDistributor(MsgDistributor msgDistributor) {
//        this.msgDistributor = msgDistributor;
//    }
//
//    public void setCore(Core core) {
//        this.core = core;
//    }
//    public Core getCore() {
//        return core;
//    }
//
//    public Coder getCoder() {
//        return coder;
//    }
//
//    public void setCoder(Coder coder) {
//        this.coder = coder;
//    }
//
//
//    public void setSender(Sender sender) {
//        this.sender = sender;
//    }
//
//
//}
