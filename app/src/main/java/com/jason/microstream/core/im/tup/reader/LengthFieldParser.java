package com.jason.microstream.core.im.tup.reader;



import com.jason.microstream.core.im.tup.channelcontext.ReadStatus;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class LengthFieldParser implements ProtocolParser {
    private static final String TAG = LengthFieldParser.class.getSimpleName();
    private final int lengthFieldOffset;
    private final int lengthFieldLength;

    public LengthFieldParser(int offset, int length) {
        this.lengthFieldOffset = offset;
        this.lengthFieldLength = length;
    }

    @Override
    public List<byte[]> parse(ByteBuffer data, ReadStatus context) {
        List<byte[]> result = new ArrayList<>();
        ByteBuffer accumulateBuffer = context.getAccumulateBuffer();

        // 将新数据添加到累积缓冲区
        if (accumulateBuffer == null) {
            accumulateBuffer = ByteBuffer.allocate(8192); // 初始大小
            context.setAccumulateBuffer(accumulateBuffer);
        }

        accumulateBuffer.put(data);
        accumulateBuffer.flip(); // 切换为读模式

        while (accumulateBuffer.remaining() >= lengthFieldOffset + lengthFieldLength) {
            // 读取长度字段
            int length = readLengthField(accumulateBuffer);

            // 检查是否有足够的数据组成完整包
            if (accumulateBuffer.remaining() < lengthFieldOffset + lengthFieldLength + length) {
                break; // 数据不足，等待更多数据
            }

            // 跳过长度字段偏移量
            accumulateBuffer.position(accumulateBuffer.position() + lengthFieldOffset + lengthFieldLength);

            // 读取完整数据包
            byte[] packet = new byte[length];
            accumulateBuffer.get(packet);
            result.add(packet);

            // 跳过长度字段（如果位置在数据包前面）
//            accumulateBuffer.position(accumulateBuffer.position() + lengthFieldLength);
        }

        // 压缩缓冲区，保留未处理的数据
        accumulateBuffer.compact();

        return result;
    }

    private int readLengthField(ByteBuffer buffer) {
        int position = buffer.position();
        buffer.position(position + lengthFieldOffset);

        int length = 0;
        for (int i = 0; i < lengthFieldLength; i++) {
            length = (length << 8) | (buffer.get() & 0xFF);
        }

        buffer.position(position);
        return length;
    }

    @Override
    public ProtocolType getProtocolType() {
        return ProtocolType.LENGTH_FIELD;
    }
}