package com.jason.microstream.core.im.tup.reader;

import com.jason.microstream.core.im.tup.channelcontext.ReadStatus;

import java.nio.ByteBuffer;
import java.util.List;

public interface ProtocolParser {
    /**
     * 解析数据包
     * @param data 输入数据
     * @param context 会话上下文
     * @return 解析出的完整包列表（可能为空）
     */
    List<byte[]> parse(ByteBuffer data, ReadStatus context);

    /**
     * 获取协议类型
     */
    ProtocolType getProtocolType();
    public enum ProtocolType {
        FIXED_LENGTH,      // 定长协议
        DELIMITER_BASED,   // 分隔符协议
        LENGTH_FIELD,      // 长度字段协议
        CUSTOM             // 自定义协议
    }
}
