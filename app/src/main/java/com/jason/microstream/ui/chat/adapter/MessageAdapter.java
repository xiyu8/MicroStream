package com.jason.microstream.ui.chat.adapter;

import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.jason.microstream.account.AccountManager;
import com.jason.microstream.core.im.im_mode.msg.ImMsgConfig;
import com.jason.microstream.ui.chat.holder.MessageHolder;
import com.jason.microstream.ui.chat.holder.ReceiveTextMessageHolder;
import com.jason.microstream.ui.chat.holder.SendTextMessageHolder;
import com.jason.microstream.ui.chat.message.ItemMessage;
import com.jason.microstream.ui.view_compenent.recyclerview.BasicAdapter;

import java.util.ArrayList;

public class MessageAdapter extends BasicAdapter<MessageHolder<ItemMessage>, ItemMessage> {

    private static final int TYPE_SEND_TEXT = 1;
    private static final int TYPE_RECEIVE_TEXT = 2;
    private static final int TYPE_RECEIVE_AI_TEXT = 3;

    public MessageAdapter(ArrayList<ItemMessage> items, BasicAdapter.ItemClickListener<ItemMessage> itemClickListener) {
        super(items, itemClickListener);
    }


    @Override
    public MessageHolder<ItemMessage> onCreateHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_SEND_TEXT) {
            return new SendTextMessageHolder(parent, itemClickListener, itemChildClickListener);
        } else if (viewType == TYPE_RECEIVE_AI_TEXT) {
            return new ReceiveTextMessageHolder(parent, itemClickListener, itemChildClickListener);

        }
        return null;
    }


    @Override
    public int getItemViewType(int position) {
        int temp = super.getItemViewType(position);
        if (temp != 0) {
            return temp;
        }
        ItemMessage aiMessage = getItems().get(getDataItemPosition(position));

        if (aiMessage.msg.getMsgType() == ImMsgConfig.ImMsgType.TYPE_TEXT) {
            if (aiMessage.msg.getFromId().equals(AccountManager.get().getUid())) {
                return MessageAdapter.TYPE_SEND_TEXT;
            } else {
                return MessageAdapter.TYPE_RECEIVE_AI_TEXT;
            }
        } else if (aiMessage.msg.getMsgType() == ImMsgConfig.ImMsgType.TYPE_DEFAULT) {
            return MessageAdapter.TYPE_RECEIVE_AI_TEXT;
        }

//        if (aiMessage.getType() == ItemMessage.Type.NORMAL_TEXT) {
//            return MessageAdapter.TYPE_SEND_TEXT;
//        } else if (aiMessage.getType() == ItemMessage.Type.CMD_TEXT) {
//            return MessageAdapter.TYPE_SEND_TEXT;
//        } else if (aiMessage.getType() == ItemMessage.Type.AI_TEXT) {
//            return MessageAdapter.TYPE_RECEIVE_AI_TEXT;
//        }


        return super.getItemViewType(position);
    }

}
