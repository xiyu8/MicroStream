package com.jason.microstream.core.im.tup.reader;

import java.util.EnumMap;
import java.util.Map;

public class ProtocolParserFactory {
    private static final Map<ProtocolParser.ProtocolType, ProtocolParser> parsers = new EnumMap<>(ProtocolParser.ProtocolType.class);

    static {
//        parsers.put(ProtocolParser.ProtocolType.FIXED_LENGTH, new FixedLengthParser(1024)); // 假设定长1024字节
//        parsers.put(ProtocolParser.ProtocolType.DELIMITER_BASED, new DelimiterBasedParser("\r\n".getBytes()));
        parsers.put(ProtocolParser.ProtocolType.LENGTH_FIELD, new LengthFieldParser(0, 4)); // 长度字段在开头，占4字节
    }

    public static ProtocolParser getParser(ProtocolParser.ProtocolType type) {
        return parsers.get(type);
    }

    public static ProtocolParser getParser(ProtocolParser.ProtocolType type, Object... params) {
        // 根据参数创建特定的解析器
        switch (type) {
//            case FIXED_LENGTH:
//                return new FixedLengthParser((Integer) params[0]);
//            case DELIMITER_BASED:
//                return new DelimiterBasedParser((byte[]) params[0]);
            case LENGTH_FIELD:
                return new LengthFieldParser((Integer) params[0], (Integer) params[1]);
            default:
                return getParser(type);
        }
    }
}