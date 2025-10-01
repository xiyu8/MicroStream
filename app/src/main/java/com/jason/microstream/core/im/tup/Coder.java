package com.jason.microstream.core.im.tup;

import com.google.gson.Gson;
import com.jason.microstream.Tool;

import java.util.HashMap;
import java.util.Map;


public class Coder {
    private Gson gson;

    public Coder() {
        gson = new Gson();
    }

    public static final int MSG_TYPE_SIZE = 4;
    public static final int MSG_TYPE_TEST = 1;
    public static final int MSG_TYPE_REGISTER = 1;
    public static final int MSG_TYPE_SWAP_ICE = 102;
    public static final int MSG_TYPE_SWAP_SDP = 103;
    public static final int MSG_TYPE_OFFER_SDP = 104;
    public static final int MSG_TYPE_ID_SIZE = 32;


    public byte[] encode(CoderData coderData) {
        if (coderData == null) {
            return null;
        }
        int msgDataLength = coderData.msgData == null ? 0 : coderData.msgData.length;
        byte[] data = new byte[getHeaderSize() + msgDataLength + PKG_LENGTH];

        int offset = data.length;
        offset = encodeMsg(coderData, data, offset);
        offset = encodeBatchOpt(coderData, data, offset);
        offset = encodeRedundancy(coderData, data, offset);
        offset = encodeService(coderData, data, offset);
        offset = encodePkg(coderData, data, offset);

        if (offset == -1) { //data error
            return null;
        }
        return data;
    }

    private int encodeMsg(CoderData coderData, byte[] data,int offset) {
        int msgDataLength = coderData.msgData == null ? 0 : coderData.msgData.length;
        if (msgDataLength != 0) {
            System.arraycopy(coderData.msgData, 0, data, offset = offset - msgDataLength, msgDataLength);
        }
        System.arraycopy(Tool.intToByte4(coderData.msgType), 0, data, offset = offset - MSG_TYPE_SIZE, MSG_TYPE_SIZE);
        System.arraycopy(Tool.intToByte4(coderData.msgAction), 0, data, offset = offset - MSG_ACTION_SIZE, MSG_ACTION_SIZE);
        return offset;
    }
    private int encodeBatchOpt(CoderData coderData, byte[] data,int offset) {
        System.arraycopy(Tool.intToByte4(coderData.ackFlag), 0, data, offset = offset - IM_ACK_FLAG_SIZE, IM_ACK_FLAG_SIZE);
        System.arraycopy(Tool.longToByte8(coderData.stubId), 0, data, offset = offset - IM_STUB_SIZE, IM_STUB_SIZE);
        System.arraycopy(Tool.longToByte8(coderData.seqId), 0, data, offset = offset - IM_SEQ_SIZE, IM_SEQ_SIZE);
        return offset;
    }
    private int encodeRedundancy(CoderData coderData, byte[] data,int offset) {
        System.arraycopy(Tool.intToByte4(coderData.redundancy), 0, data, offset = offset - IM_BK_SIZE, IM_BK_SIZE);
        return offset;
    }
    private int encodeService(CoderData coderData, byte[] data,int offset) {
        System.arraycopy(Tool.intToByte4(coderData.service), 0, data, offset = offset - IM_SERVICE_SIZE, IM_SERVICE_SIZE);
        System.arraycopy(Tool.intToByte4(coderData.version), 0, data, offset = offset - IM_VERSION_SIZE, IM_VERSION_SIZE);
        return offset;
    }
    private int encodePkg(CoderData coderData, byte[] data,int offset) {
        System.arraycopy(Tool.intToByte4(data.length - PKG_LENGTH), 0, data, offset = offset - PKG_LENGTH, PKG_LENGTH);
        return offset;
    }



    //    public static final String imService = "TEST";
    public static final int PKG_LENGTH = 4;
    public static final int IM_VERSION = 1;
    public static final int IM_VERSION_SIZE = 4;
    public static final int IM_SERVICE = 1;
    public static final int IM_SERVICE_SIZE = 4;
    public static final int IM_BK = 0;
    public static final int IM_BK_SIZE = 4;

    public static final int IM_SEQ_SIZE = 8;
    public static final int IM_STUB_SIZE = 8;
    public static final int IM_ACK_FLAG_SIZE = 4;
    public static final String imService = "MICRO_STREAM";
    public static final Map<String, Integer> imServiceMap = new HashMap<String, Integer>() {{
        put("TEST", 0);
        put(imService, 1);
    }};
    public int getImServiceSize() {
        return IM_VERSION_SIZE + IM_SERVICE_SIZE;
    }
    public int getRedundancySize() {
        return IM_BK_SIZE;
    }
    public int getBatchOptSize() {
        return IM_SEQ_SIZE + IM_STUB_SIZE + IM_ACK_FLAG_SIZE;
    }
    private int getHeaderSize() {
        return getImServiceSize() + getRedundancySize() + getBatchOptSize() + MSG_ACTION_SIZE + MSG_TYPE_SIZE;
    }
    private byte[] encodeService(byte[] bytes) {
        int serviceDataLength = bytes.length + getImServiceSize();
        byte[] serviceData = new byte[serviceDataLength];
        System.arraycopy(Tool.intToByte4(IM_VERSION), 0, serviceData, 0, IM_VERSION_SIZE);
        System.arraycopy(Tool.intToByte4(IM_SERVICE), 0, serviceData, IM_VERSION_SIZE, IM_SERVICE_SIZE);
        System.arraycopy(Tool.intToByte4(IM_BK), 0, serviceData, IM_VERSION_SIZE + IM_SERVICE_SIZE, IM_BK_SIZE);
        System.arraycopy(bytes, 0, serviceData, IM_VERSION_SIZE + IM_SERVICE_SIZE + IM_BK_SIZE, bytes.length);
        return serviceData;
    }

    private byte[] encapsulate(byte[] bytes) {
        byte[] cellData = new byte[bytes.length + 4];
        System.arraycopy(Tool.intToByte4(bytes.length), 0, cellData, 0, 4);
        System.arraycopy(bytes, 0, cellData, 4, bytes.length);
        return cellData;
    }


    public String flatObject(Object ob) {
        if (ob == null) {
            return "";
        }
        return gson.toJson(ob);
    }


///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static final int MSG_ACTION_SIZE = 4;
    public  CoderData decode(byte[] data) {
        if (data == null || data.length == 0) {
            return null;
        }
        CoderData coderData = new CoderData();

        int offset = 0;
        offset = decodeService(data, offset,coderData);
        offset = decodeRedundancy(data, offset,coderData);
        offset = decodeBatchOpt(data, offset,coderData);
        offset = decodeMsg(data, offset,coderData);

        if (offset == -1) { //data error
            return null;
        }
        return coderData;
    }


    private int decodeService(byte[] data,int offset, CoderData coderData) {
        if (data.length < offset + getImServiceSize()) {
            return -1;
        }
        coderData.version = Tool.byte4ToInt(data, offset);
        coderData.service = Tool.byte4ToInt(data, offset = offset + IM_VERSION_SIZE);

        return offset + IM_SERVICE_SIZE;
    }

    private int decodeRedundancy(byte[] data, int offset, CoderData coderData) {
        if (data.length < offset + getRedundancySize()) {
            return -1;
        }
        coderData.redundancy = Tool.byte4ToInt(data, offset + IM_BK_SIZE);

        return offset + getRedundancySize();
    }

    private int decodeBatchOpt(byte[] data, Integer offset, CoderData coderData) {
        if (data.length < offset + getBatchOptSize()) {
            return -1;
        }
        coderData.seqId = Tool.byte8ToLong(data, offset);
        coderData.stubId = Tool.byte8ToLong(data, offset + IM_SEQ_SIZE);
        coderData.ackFlag = Tool.byte4ToInt(data, offset + IM_SEQ_SIZE + IM_STUB_SIZE);

        return offset + getBatchOptSize();
    }

    private int decodeMsg(byte[] data, Integer offset, CoderData coderData) {
        if (data.length < offset + MSG_ACTION_SIZE + MSG_TYPE_SIZE) {
            return -1;
        }
        coderData.msgAction = Tool.byte4ToInt(data, offset);
        coderData.msgType = Tool.byte4ToInt(data, offset = offset + MSG_ACTION_SIZE);
        coderData.msgData = new byte[data.length - (offset/* + MSG_TYPE_SIZE*/)];
        System.arraycopy(data, offset , coderData.msgData, 0, coderData.msgData.length);
        return offset + coderData.msgData.length;
    }

    private byte[] decodeBatch(byte[] batchData, Integer action){
        byte[] msgModeData = new byte[batchData.length - 4];
        System.arraycopy(batchData, 4, msgModeData, 0, msgModeData.length);
        return msgModeData;
    }

    private int parseAction(byte[] batchData) {
        byte[] actionData =new byte[MSG_ACTION_SIZE];
        System.arraycopy(batchData, 0, actionData, 0, actionData.length);
        int action = Tool.byte4ToInt(actionData, 0);
        return action;
    }



/////////////////////////////////////////////////////////////////////////////////////



/////////////////////////////////////////////////////////////////////////////////////

    public static class CoderData {
        public int length;
        public int version;
        public int service;
        public int redundancy;

        public long seqId;
        public long stubId;
        public int ackFlag;

        public int msgAction;
        public int msgType;
        public byte[] msgData;

        public CoderData clone() {
            CoderData c = new CoderData();
            c.length = length;
            c.version = version;
            c.service = service;
            c.redundancy = redundancy;
            c.seqId = seqId;
            c.stubId = stubId;
            c.ackFlag = ackFlag;
            c.msgAction = msgAction;
            c.msgType = msgType;
            if (msgData != null) {
                c.msgData = new byte[msgData.length];
                System.arraycopy(msgData, 0, c.msgData, 0, msgData.length);
            }
            return c;
        }

    }

}
