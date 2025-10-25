package com.jason.microstream.ui.conversation;

import android.view.ViewGroup;

import androidx.annotation.NonNull;


import com.jason.microstream.ui.view_compenent.recyclerview.BasicAdapter;
import com.jason.microstream.ui.conversation.holder.ConversationHolder;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class ConversationAdapter extends BasicAdapter<ConversationHolder,ConversationHolder.Item> {

    public ConversationAdapter(List<ConversationHolder.Item> items, ItemClickListener<ConversationHolder.Item> itemClickListener) {
        super(items, itemClickListener);
    }

    @Override
    public ConversationHolder onCreateHolder(@NonNull ViewGroup parent, int viewType) {
        return new ConversationHolder(parent.getContext(), parent,itemClickListener,itemChildClickListener);
    }

//    @Override
//    public void onBindViewHolder(@NonNull ConversationHolder holder, int position) {
//
//        holder.bindData();
//    }
//
//    @Override
//    public int getItemViewType(int position) {
//        return super.getItemViewType(position);
//    }
//
//    @Override
//    public int getItemCount() {
//        return super.getItemCount();
//    }




}
