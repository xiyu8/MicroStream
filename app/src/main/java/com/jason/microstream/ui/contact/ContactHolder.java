package com.jason.microstream.ui.contact;

import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jason.microstream.R;
import com.jason.microstream.core.im.reqresp.data.bean.RespUser;
import com.jason.microstream.ui.compenent.recyclerview.BasicAdapter;
import com.jason.microstream.ui.compenent.recyclerview.BasicHolder;


public class ContactHolder extends BasicHolder<ContactHolder.Item> {

    public ContactHolder(ViewGroup parent, int itemViewId
            , BasicAdapter.ItemClickListener<Item> itemClickListener
            , BasicAdapter.ItemChildClickListener<Item> itemChildClickListener) {
        super(parent, itemViewId, itemClickListener, itemChildClickListener);
    }

    private ImageView user_avatar;
    private TextView user_name;
    private TextView user_hint;
    @Override
    protected void bindView() {
        user_avatar = itemView.findViewById(R.id.user_avatar);
        user_name = itemView.findViewById(R.id.user_name);
        user_hint = itemView.findViewById(R.id.user_hint);

    }

    @Override
    public void bindData(Item item, int position) {
//        user_avatar.setImageResource();
        if(item==null) return;
        user_name.setText(item.user.getName());
        user_hint.setText(item.user.getUid());
        if (position == 0) {
            itemView.setBackgroundResource((R.drawable.selector_item_click_first));
        } else if (position == getItems().size() - 1) {
            itemView.setBackgroundResource((R.drawable.selector_item_click_last));
        } else {
            itemView.setBackgroundResource((R.drawable.selector_item_click));
        }


    }

    public static class Item extends BasicHolder.Item {
        RespUser user;
    }
}

