package com.jason.microstream.core.im.tup;

import com.jason.microstream.core.im.tup.channelcontext.ChannelContext;

import java.nio.channels.SocketChannel;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArraySet;

public class ChannelIntrinsicSet {

    // 定义一个分片数组，大小为16（建议为2的N次方，可以用位运算代替取模）
    private static final int SHARD_COUNT = 16;
    private static final ConcurrentMap<String, Set<ChannelContext>>[] SHARDS = new ConcurrentHashMap[SHARD_COUNT];

    // 初始化每个分片
    static {
        for (int i = 0; i < SHARD_COUNT; i++) {
            SHARDS[i] = new ConcurrentHashMap<>();
        }
    }

    // 根据用户ID计算分片索引
    private static int getShardIndex(String userId) {
        // 使用userId的hashCode并与分片数取模，确保分布均匀
        return (userId.hashCode() & 0x7FFFFFFF) % SHARD_COUNT;
    }

    // 反向映射表同样可以进行分片，key可以用SocketChannel本身
    private static final ConcurrentMap<SocketChannel, String>[] CHANNEL_MAP_SHARDS = new ConcurrentHashMap[SHARD_COUNT];


    public static ChannelContext get(String intrinsic) {
        Set<ChannelContext> sets = SHARDS[getShardIndex(intrinsic)].get(intrinsic);
        if (sets != null && sets.size() > 0) {
            return (ChannelContext) sets.toArray()[0];
        }
        return null;
    }

    public static void add(String intrinsic,ChannelContext channelContext) {
        SHARDS[getShardIndex(intrinsic)].computeIfAbsent(intrinsic, k -> new CopyOnWriteArraySet<>()).add(channelContext);

        //等同上面，仅写法不一样
//        Set<ChannelContext> sets = SHARDS[getShardIndex(intrinsic)].get(intrinsic);
//        if (sets != null) {
//            sets = new CopyOnWriteArraySet<>();
//            sets.add(channelContext);
//        }
    }

    public static void remove(ChannelContext context) {
        String intrinsic;
        if ((intrinsic = context.getIntrinsicMark()) != null) {
            Set<ChannelContext> sets = SHARDS[getShardIndex(intrinsic)].get(intrinsic);
            if (sets != null) {
                sets.removeIf(channelContext -> channelContext.getSocket() == context.getSocket());
            }
        }
    }

}
