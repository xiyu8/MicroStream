package com.jason.microstream.core.im.tup.reader;

import com.jason.microstream.core.im.tup.channelcontext.ChannelContext;

import java.nio.channels.SocketChannel;

public class RawData {
    private final byte[] data;
    private final ChannelContext channelContext;

    public RawData(SocketChannel channel, byte[] data, ChannelContext channelContext) {
        this.channelContext = channelContext;
        this.data = data;
    }

    public byte[] getData() {
        return data;
    }


    public ChannelContext getChannelContext() {
        return channelContext;
    }
}
