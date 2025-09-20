package com.jason.microstream.ui.conversation.holder;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.DrawableRes;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.jason.microstream.R;
import com.jason.microstream.account.AccountManager;
import com.jason.microstream.tool.TimeUtil2;
import com.jason.microstream.ui.compenent.recyclerview.BasicAdapter;
import com.jason.microstream.ui.compenent.recyclerview.BasicHolder;
import com.jason.microstream.ui.compenent.avatar.AvatarView;
import com.jason.microstream.ui.conversation.avatar.GroupUser;
import com.jason.microstream.ui.conversation.avatar.MsMessage;


import java.util.List;

public class ConversationHolder extends BasicHolder<ConversationHolder.Item> {

//    public ConversationHolder(@NonNull View itemView) {
//        super(itemView);
//    }

    public ConversationHolder(Context context, ViewGroup parent
            , BasicAdapter.ItemClickListener<Item> itemClickListener
            , BasicAdapter.ItemChildClickListener<Item> itemChildClickListener) {
        super(context, parent, R.layout.item_conversation, itemClickListener,itemChildClickListener);
        holderSector = new ConversationHolderSector();
    }

//    public ConversationHolder(Context context,int viewType) {
//        super(context,R.layout.item_conversation);
//    }

    ConversationHolderSector holderSector;
    ImageView is_top;
    ImageView chat_avatar;
    TextView chat_name;
    TextView last_msg_content;
    TextView last_msg_time;
    TextView unread_count;
    ViewGroup mute_area;
    ImageView mute_icon;
    ImageView mute_unread;
    AvatarView group_avatar;
    @Override
    public void bindView() {
        is_top = itemView.findViewById(R.id.is_top);
        chat_avatar = itemView.findViewById(R.id.chat_avatar);
        group_avatar = itemView.findViewById(R.id.group_avatar);
        chat_name = itemView.findViewById(R.id.chat_name);
        last_msg_content = itemView.findViewById(R.id.last_msg_content);
        last_msg_time = itemView.findViewById(R.id.last_msg_time);
        unread_count = itemView.findViewById(R.id.unread_count);
        mute_area = itemView.findViewById(R.id.mute_area);
        mute_icon = itemView.findViewById(R.id.mute_icon);
        mute_unread = itemView.findViewById(R.id.mute_unread);
        addChildClick(chat_avatar);
    }

    @Override
    public void bindData(Item item, int position) {
        @DrawableRes
        int avatarRes = item.type == IConversation.TYPE_P2P ? R.drawable.default_avatar : R.drawable.default_group_avatar;
        Glide.with(chat_avatar).load(avatarRes)
                .apply(RequestOptions.bitmapTransform(new CircleCrop())).into(chat_avatar);
        if (item.type == IConversation.TYPE_P2P) {
            chat_avatar.setVisibility(View.VISIBLE);
            group_avatar.setVisibility(View.INVISIBLE);
            Glide.with(chat_avatar).load(avatarRes)
                    .apply(RequestOptions.bitmapTransform(new CircleCrop())).into(chat_avatar);
        } else {
            chat_avatar.setVisibility(View.INVISIBLE);
            group_avatar.setVisibility(View.VISIBLE);
            group_avatar.setAvatar(item.cid, IConversation.TYPE_GROUP, false, item.memberAvatars, item.chatName);
        }
        is_top.setVisibility(item.isTop ? View.VISIBLE : View.GONE);
//        chat_avatar;
        chat_name.setText(item.chatName);
        if (item.lastMsg == null) {
            last_msg_content.setText("");
            last_msg_time.setText("");

        } else {
            last_msg_content.setText(item.lastMsg.content);
            last_msg_time.setText(holderSector.getTime(item.lastTime));
        }


        if (!TextUtils.isEmpty(item.draft)) {
            String str = last_msg_content.getContext().getResources().getString(R.string.sh_chat_draft);
            SpannableString ss = new SpannableString(str + " " + item.draft);
            ss.setSpan(new ForegroundColorSpan(last_msg_content.getContext().getResources().getColor(R.color.sh_caution)), 0, str.length(),
                    Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
            last_msg_content.setVisibility(View.VISIBLE);
            last_msg_content.setText(ss);
            if (item.lastTime != 0) {
                last_msg_time.setVisibility(View.VISIBLE);
                last_msg_time.setText(TimeUtil2.getSimpleDateString(item.lastTime));
            }
        } else {
            if (item.lastMsg == null) {
                last_msg_content.setVisibility(View.GONE);
                if (item.lastTime != 0) {
                    last_msg_time.setVisibility(View.VISIBLE);
                    last_msg_time.setText(TimeUtil2.getSimpleDateString(item.lastTime));
                } else {
                    last_msg_time.setVisibility(View.GONE);
                }
            } else {
                last_msg_content.setVisibility(View.VISIBLE);
                last_msg_content.setTextColor(last_msg_content.getContext().getResources().getColor(R.color.sh_text_sub_hint));
                int type = item.lastMsg.type;
                String content;
                content = holderSector.getContentByType(item.lastMsg.type, item.lastMsg.content);

                if (item.type == IConversation.TYPE_GROUP && type != MsMessage.TYPE_SYSTEM) {
                    String prex = "";
                    if (!TextUtils.isEmpty(item.lastMsg.sendId)) {
                        if (!item.lastMsg.sendId.equals(AccountManager.get().getUid())) {
                            prex = item.lastMsg.sendName + ": ";
                        }
                    }
                    content = prex + content;
                }
                if (item.unreadCount > 0 && !item.isMute) {
                    content = "[" + item.unreadCount + "条未读]" + content;
                }
                if (item.isAt && item.unreadCount > 0) {
                    String str = last_msg_content.getContext().getResources().getString(R.string.at_you);
                    SpannableString ss = new SpannableString(str + " " + content);
                    ss.setSpan(new ForegroundColorSpan(last_msg_content.getContext().getResources().getColor(R.color.sh_caution)), 0, str.length(),
                            Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
                    last_msg_content.setText(ss);
                } else if ((item.unreadMsgType & IConversation.MESSAGE_TYPE_ATALL) == IConversation.MESSAGE_TYPE_ATALL && item.unreadCount > 0) {
                    String str = last_msg_content.getResources().getString(R.string.at_all);
                    SpannableString ss = new SpannableString(str + " " + content);
                    ss.setSpan(new ForegroundColorSpan(last_msg_content.getResources().getColor(R.color.c_link)), 0, str.length(),
                            Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
                    last_msg_content.setText(ss);
                }  else if ((item.unreadMsgType & IConversation.MESSAGE_TYPE_REPLY) == IConversation.MESSAGE_TYPE_REPLY && item.unreadCount > 0) {
                    String str = last_msg_content.getContext().getResources().getString(R.string.reply_you);
                    SpannableString ss = new SpannableString(str + " " + content);
                    ss.setSpan(new ForegroundColorSpan(last_msg_content.getContext().getResources().getColor(R.color.sh_caution)), 0, str.length(),
                            Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
                    last_msg_content.setText(ss);
                } else if ((item.unreadMsgType & IConversation.MESSAGE_TYPE_NOTICE) == IConversation.MESSAGE_TYPE_NOTICE && item.unreadCount > 0) {
                    String str = last_msg_content.getContext().getResources().getString(R.string.message_notice);
                    SpannableString ss = new SpannableString(str + " " + content);
                    ss.setSpan(new ForegroundColorSpan(last_msg_content.getContext().getResources().getColor(R.color.sh_caution)), 0, str.length(),
                            Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
                    last_msg_content.setText(ss);
                } else if ((item.lastMsg.type & IConversation.MESSAGE_TYPE_REVOKE) == IConversation.MESSAGE_TYPE_REVOKE) {
                    String str = last_msg_content.getContext().getResources().getString(R.string.message_revoke);
                    SpannableString ss = new SpannableString(str + " " + content);
                    ss.setSpan(new ForegroundColorSpan(last_msg_content.getContext().getResources().getColor(R.color.sh_caution)), 0, str.length(),
                            Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
                    last_msg_content.setText(ss);
                } else {
                    if (type == MsMessage.TYPE_TXT) {
                        if (!TextUtils.isEmpty(content)) {
                            Context mContext = last_msg_content.getContext();
//                                last_msg_content.setText(
//                                        SmileUtils.getSmiledText(mContext, content, dip2px(16)));
                            last_msg_content.setText(content);
                        }
                    } else {
                        last_msg_content.setText(content);
                    }
                }

                if (item.lastMsg.status == MsMessage.STATUS_FAIL ||
                        item.lastMsg.status == MsMessage.STATUS_SENDING) {
                    if (!TextUtils.isEmpty(item.lastMsg.sendId) && item.lastMsg.sendId.equals(AccountManager.get().getUid())) {
                        String str = last_msg_content.getContext().getResources().getString(R.string.message_send_fail);
                        SpannableString ss = new SpannableString(str + " " + content);
                        ss.setSpan(new ForegroundColorSpan(last_msg_content.getContext().getResources().getColor(R.color.c_caution)), 0, str.length(),
                                Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
                        last_msg_content.setText(ss);
                    }
                }
                if (type == 0) {
                    if (!TextUtils.isEmpty(item.lastMsg.content)) {
                        last_msg_content.setText(item.lastMsg.content);
                    } else {
                        last_msg_content.setText("");
                    }
                }
                last_msg_time.setVisibility(View.VISIBLE);
                last_msg_time.setText(holderSector.getTime(item.lastMsg.timeStp));
            }
        }


        if (item.unreadCount > 0) {
            unread_count.setVisibility(View.VISIBLE);
            mute_unread.setVisibility(View.VISIBLE);
            if (item.unreadCount > 99) {
                unread_count.setText("99+");
            } else {
                unread_count.setText(String.valueOf(item.unreadCount));
            }
        } else {
            unread_count.setVisibility(View.GONE);
            mute_unread.setVisibility(View.GONE);
        }

        if (item.isMute) {
            mute_area.setVisibility(View.VISIBLE);
            unread_count.setVisibility(View.GONE);
        } else {
//            if (item.cid.equals(IConversation.JOIN_GROUP_CID)) {
//                unread_count.setVisibility(View.GONE);
//                mute_icon.setVisibility(View.GONE);
//                if (item.unreadCount > 0) {
//                    mute_area.setVisibility(View.VISIBLE);
//                    mute_unread.setVisibility(View.VISIBLE);
//                } else {
//                    mute_area.setVisibility(View.GONE);
//                    mute_unread.setVisibility(View.GONE);
//                }
//            }/* else if (item.cid.equals(IConversation.FRIENDS_CID) && item.lastMsg != null && item.getLastMessage().getUnreadCount() > 0) {
//                mute_area.setVisibility(View.VISIBLE);
//                unread_count.setVisibility(View.GONE);
//                mute_icon.setVisibility(View.GONE);
//                mute_unread.setVisibility(View.VISIBLE);
//            }*/ else {
//                mute_area.setVisibility(View.GONE);
//            }
        }
    }





    public static class Item extends BasicHolder.Item{
        public String cid;
        public int type;

        public boolean isTop = false;
        public String chatName;
        public String draft;
        public LastMsg lastMsg;
        public int unreadCount;
        public boolean isMute;
        public long lastTime;
        public List<GroupUser> memberAvatars;
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
