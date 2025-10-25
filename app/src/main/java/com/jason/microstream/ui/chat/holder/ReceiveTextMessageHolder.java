package com.jason.microstream.ui.chat.holder;

import android.view.ViewGroup;

import com.jason.microstream.R;
import com.jason.microstream.core.im.im_mode.msg.ImMsgConfig;
import com.jason.microstream.core.im.im_mode.msg.TextMsg;
import com.jason.microstream.ui.chat.message.ItemMessage;
import com.jason.microstream.ui.chat.views.TwinkleTextView;
import com.jason.microstream.ui.view_compenent.recyclerview.BasicAdapter;


public class ReceiveTextMessageHolder extends MessageHolder<ItemMessage> {



    public ReceiveTextMessageHolder(ViewGroup parent, int itemViewId) {
        super(parent, itemViewId);
    }

    public ReceiveTextMessageHolder(ViewGroup parent, BasicAdapter.ItemClickListener<ItemMessage> itemClickListener, BasicAdapter.ItemChildClickListener<ItemMessage> itemChildClickListener) {
        super(parent, R.layout.item_message_receive_text, itemClickListener, itemChildClickListener);
    }

    TwinkleTextView text_content;

//    View ai_re_gen;
//    View ai_content_copy;
//    View ai_content_occupation;
//    ImageView ai_content_like;
//    ImageView ai_content_dislike;

//    View ai_msg_status_view;
    @Override
    protected void bindView() {
        text_content = itemView.findViewById(R.id.text_content);
//        ai_re_gen = itemView.findViewById(R.id.ai_re_gen);
//        ai_content_copy = itemView.findViewById(R.id.ai_content_copy);
//        ai_content_like = itemView.findViewById(R.id.ai_content_like);
//        ai_content_dislike = itemView.findViewById(R.id.ai_content_dislike);
//        ai_msg_status_view = itemView.findViewById(R.id.ai_msg_status_view);
//        ai_content_occupation = itemView.findViewById(R.id.ai_content_occupation);


//        ai_re_gen.setOnClickListener(this);
//        ai_content_copy.setOnClickListener(this);
//        ai_content_like.setOnClickListener(this);
//        ai_content_dislike.setOnClickListener(this);
//        ai_msg_status_view.setOnClickListener(this);

    }

    @Override
    public void bindData(ItemMessage item, int position) {
        if (item.msg.getMsgType() != ImMsgConfig.ImMsgType.TYPE_TEXT) {
            return;
        }
        TextMsg textMsg = (TextMsg) item.msg;

        if (textMsg.getState() == ImMsgConfig.SendState.SENDING) {
//            ai_msg_status_view.setVisibility(View.VISIBLE);

//                ai_re_gen.setVisibility(View.GONE);
//                ai_content_copy.setVisibility(View.GONE);
//                ai_content_occupation.setVisibility(View.GONE);
//                ai_content_like.setVisibility(View.GONE);
//                ai_content_dislike.setVisibility(View.GONE);

            text_content.setText(textMsg.text);
//            text_content.setTwinkle(false);
        } else if (textMsg.getState() == ImMsgConfig.SendState.SEND_SUCCESS) {
//            ai_msg_status_view.setVisibility(View.GONE);

//                ai_re_gen.setVisibility(View.VISIBLE);
//                ai_content_copy.setVisibility(View.VISIBLE);
//                ai_content_occupation.setVisibility(View.VISIBLE);
//                ai_content_like.setVisibility(View.VISIBLE);
//                ai_content_dislike.setVisibility(View.VISIBLE);

            text_content.setText(textMsg.text);
//            text_content.setTwinkle(false);

//                setLikeState(aiTextMessage.aiText, ai_content_like, ai_content_dislike);
        }
    }

//    private void setLikeState(AITextMessage.AIText aiText, ImageView ai_content_like, ImageView ai_content_dislike) {
//        if (aiText != null) {
//            if (aiText.likeState == 0) {
//                ai_content_like.setImageResource(R.drawable.ai_content_like);
//                ai_content_dislike.setImageResource(R.drawable.ai_content_dislike);
//            } else if (aiText.likeState == 1) {
//                ai_content_like.setImageResource(R.drawable.ai_content_like_selected);
//                ai_content_dislike.setImageResource(R.drawable.ai_content_dislike);
//            } else if (aiText.likeState == 2) {
//                ai_content_like.setImageResource(R.drawable.ai_content_like);
//                ai_content_dislike.setImageResource(R.drawable.ai_content_dislike_selected);
//            } else {
//                ai_content_like.setImageResource(R.drawable.ai_content_like);
//                ai_content_dislike.setImageResource(R.drawable.ai_content_dislike);
//            }
//        } else {
//            ai_content_like.setImageResource(R.drawable.ai_content_like);
//            ai_content_dislike.setImageResource(R.drawable.ai_content_dislike);
//        }
//    }


}
