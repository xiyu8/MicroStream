package com.jason.microstream.ui.chat.holder;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.jason.microstream.R;
import com.jason.microstream.core.im.im_mode.msg.ImMsgConfig;
import com.jason.microstream.core.im.im_mode.msg.TextMsg;
import com.jason.microstream.ui.chat.message.ItemMessage;
import com.jason.microstream.ui.view_compenent.recyclerview.BasicAdapter;
import com.jason.microstream.ui.view_compenent.recyclerview.BasicHolder;


public class SendTextMessageHolder extends MessageHolder<ItemMessage> {

    public SendTextMessageHolder(ViewGroup parent, BasicAdapter.ItemClickListener<ItemMessage> itemClickListener
            , BasicAdapter.ItemChildClickListener<ItemMessage> itemChildClickListener) {
        super(parent, R.layout.item_message_send_text, itemClickListener, itemChildClickListener);
    }

//    SimpleDraweeView part_ai_message_gen_gif;
    ImageView part_ai_message_gen_gif;
    View part_ai_message_gen_animator_area;
    View send_fail;
//    View ai_msg_status_view;
    TextView text_content;
    @Override
    protected void bindView() {
        part_ai_message_gen_gif = itemView.findViewById(R.id.part_message_send_gif);
        part_ai_message_gen_animator_area = itemView.findViewById(R.id.part_message_send_animator_area);
        send_fail = itemView.findViewById(R.id.send_fail);
//        ai_msg_status_view = itemView.findViewById(R.id.ai_msg_status_view);
        text_content = itemView.findViewById(R.id.text_content);

//        ai_msg_status_view.setOnClickListener(this);
        send_fail.setOnClickListener(this);
    }

    @Override
    public void bindData(ItemMessage item, int position) {

        if (item.msg.getMsgType() != ImMsgConfig.ImMsgType.TYPE_TEXT) {
            return;
        }
        TextMsg textMsg = (TextMsg) item.msg;

        if (textMsg.getState() == ImMsgConfig.SendState.SENDING) {
            part_ai_message_gen_animator_area.setVisibility(View.VISIBLE);
            send_fail.setVisibility(View.INVISIBLE);
//            ai_msg_status_view.setVisibility(View.VISIBLE);

            Glide.with(context).load(R.drawable.part_ai_message_gen_animator).into(part_ai_message_gen_gif);

//            Uri uri = new Uri.Builder().scheme(UriUtil.LOCAL_RESOURCE_SCHEME).path(String.valueOf(R.drawable.part_ai_message_gen_animator)).build();
//            DraweeController draweeController = Fresco.newDraweeControllerBuilder().setUri(uri).setAutoPlayAnimations(true).build();
//            part_ai_message_gen_gif.setController(draweeController);
        } else if (textMsg.getState() == ImMsgConfig.SendState.SEND_FAIL) {
            part_ai_message_gen_animator_area.setVisibility(View.GONE);
            send_fail.setVisibility(View.VISIBLE);
//            ai_msg_status_view.setVisibility(View.GONE);
        } else if (textMsg.getState() == ImMsgConfig.SendState.SEND_SUCCESS) {
            part_ai_message_gen_animator_area.setVisibility(View.GONE);
            send_fail.setVisibility(View.INVISIBLE);
//            ai_msg_status_view.setVisibility(View.GONE);
        }
        text_content.setText(textMsg.text);

    }

    public static class Item extends BasicHolder.Item{

    }

}
