package com.jason.microstream.ui.mine;

import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.jason.microstream.R;
import com.jason.microstream.ui.compenent.recyclerview.BasicAdapter;

import java.util.ArrayList;

public class MineAdapter extends BasicAdapter<MineHolder,MineHolder.Item> {
    public MineAdapter(ArrayList<MineHolder.Item> items) {
        super(items);
    }

    public MineAdapter(ArrayList<MineHolder.Item> items, ItemClickListener<MineHolder.Item> itemClickListener) {
        super(items, itemClickListener);
    }

    @Override
    public MineHolder onCreateHolder(@NonNull ViewGroup parent, int viewType) {
//        return new MineHolder(parent.getContext(), parent,itemClickListener,itemChildClickListener);
        return new MineHolder(parent, R.layout.item_mine,itemClickListener,itemChildClickListener);
    }
}
