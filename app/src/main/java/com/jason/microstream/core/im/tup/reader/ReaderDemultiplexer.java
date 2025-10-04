package com.jason.microstream.core.im.tup.reader;

import com.jason.microstream.core.im.tup.Demultiplexer;
import com.jason.microstream.core.im.tup.channelcontext.ChannelContext;

import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class ReaderDemultiplexer {
    private static final String TAG = ReaderDemultiplexer.class.getSimpleName();


    Demultiplexer demultiplexer;
    private int CAPACITY = 1;

    private final ConcurrentHashMap<Long, Integer> demultipleIndexMap;
    private final List<Reader> readers;
    private final AtomicInteger counter = new AtomicInteger(0);



    public ReaderDemultiplexer(Demultiplexer demultiplexer) {
        this.demultiplexer = demultiplexer;
        readers =new ArrayList<>(CAPACITY);
        for (int i = 0; i < CAPACITY; i++) {
            readers.add(new Reader(demultiplexer));
        }
        demultipleIndexMap = new ConcurrentHashMap<>();

    }

    public boolean registerRead(SocketChannel socketChannel, ChannelContext channelContext) {
        int readerIndex = demultipleIndexMap.computeIfAbsent((long) socketChannel.hashCode(), code -> {
            return counter.getAndIncrement() % CAPACITY;
        });
        Reader reader;
        if ((reader = readers.get(readerIndex)) == null) {
            reader = new Reader(this.demultiplexer);
            readers.set(readerIndex, reader);
        }
        boolean ret = reader.registerRead(socketChannel, channelContext);
        return ret;
    }

    public void unregister(SocketChannel socketChannel) {
        Integer readerIndex = demultipleIndexMap.get((long) (socketChannel.hashCode()));
        if (readerIndex != null) {
            Reader reader = readers.get(readerIndex);
            reader.unregister(socketChannel);
        }

    }

}