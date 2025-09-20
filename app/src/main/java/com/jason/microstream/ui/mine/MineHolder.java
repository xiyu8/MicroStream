package com.jason.microstream.ui.mine;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.jason.microstream.ui.compenent.recyclerview.BasicAdapter;
import com.jason.microstream.ui.compenent.recyclerview.BasicHolder;

import java.util.List;

public class MineHolder  extends BasicHolder<MineHolder.Item> {

    public MineHolder(@NonNull View itemView) {
        super(itemView);
    }

    public MineHolder(Context context, ViewGroup parent, int itemViewId) {
        super(context, parent, itemViewId);
    }

    public MineHolder(Context context, ViewGroup parent, int itemViewId, BasicAdapter.ItemClickListener<Item> itemClickListener, BasicAdapter.ItemChildClickListener<Item> itemChildClickListener) {
        super(context, parent, itemViewId, itemClickListener, itemChildClickListener);
    }

    @Override
    protected void bindView() {

    }

    @Override
    public void bindData(Item item, int position) {

    }

    public static class Item extends BasicHolder.Item{
        public String cid;
        public int type;

        public boolean isTop = false;
        public String chatName;
        public String draft;
        public int unreadCount;
        public boolean isMute;
        public long lastTime;
        public int unreadMsgType;
        public boolean isAt;

        public static class LastMsg{
            public int type;
            public String content;
            public long timeStp;
            public String sendId;
            public String sendName;
            public int status;
        }
    }
}
