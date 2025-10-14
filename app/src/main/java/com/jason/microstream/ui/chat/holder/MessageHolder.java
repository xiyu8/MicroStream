package com.jason.microstream.ui.chat.holder;

import android.content.Context;
import android.view.ViewGroup;

import com.jason.microstream.ui.view_compenent.recyclerview.BasicAdapter;
import com.jason.microstream.ui.view_compenent.recyclerview.BasicHolder;


public abstract class MessageHolder<D extends BasicHolder.Item> extends BasicHolder<D> {
    protected Context context;
    public MessageHolder(ViewGroup parent, int itemViewId) {
        super(parent, itemViewId);
        context = parent.getContext();
    }

    public MessageHolder(ViewGroup parent, int itemViewId, BasicAdapter.ItemClickListener<D> itemClickListener, BasicAdapter.ItemChildClickListener<D> itemChildClickListener) {
        super(parent, itemViewId, itemClickListener, itemChildClickListener);
        context = parent.getContext();
    }

}
