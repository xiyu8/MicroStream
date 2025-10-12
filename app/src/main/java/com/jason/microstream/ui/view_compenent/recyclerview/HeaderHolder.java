package com.jason.microstream.ui.view_compenent.recyclerview;

import android.view.View;
import android.view.ViewGroup;

import com.jason.microstream.R;

import java.util.ArrayList;

public class HeaderHolder extends BasicHolder{

    public HeaderHolder(ViewGroup parent) {
        super(parent, R.layout.item_header_container);

    }

    @Override
    protected void bindView() {

    }

    @Override
    public void bindData(Item item, int position) {

    }

    public void addHeaderViews(ArrayList<View> views) {
        for (View view : views) {
            ((ViewGroup) itemView).addView(view);
        }
    }

}