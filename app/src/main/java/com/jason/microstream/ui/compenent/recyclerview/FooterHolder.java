package com.jason.microstream.ui.compenent.recyclerview;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;


import com.jason.microstream.R;

import java.util.ArrayList;

public class FooterHolder  extends BasicHolder{

    public FooterHolder(Context context, ViewGroup parent) {
        super(context, parent, R.layout.item_header_container);

    }

    @Override
    protected void bindView() {

    }

    @Override
    public void bindData(BasicHolder.Item item, int position) {
    }

    public void addHeaderViews(ArrayList<View> views) {
        for (View view : views) {
            ((ViewGroup) itemView).addView(view);
        }
    }

}