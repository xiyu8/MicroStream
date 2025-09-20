//package com.jason.microstream.ui.conversation.mapper;
//
//import com.shinemo.openim.uikit.main.conversation.holder.ConversationHolder;
//import com.shinemo.protocol.groupstruct.GroupUser;
//import com.shinemo.qoffice.biz.im.data.IConversation;
//import com.shinemo.qoffice.biz.im.data.impl.ConversationImpl;
//import com.shinemo.qoffice.biz.im.model.MessageVo;
//
//import java.util.ArrayList;
//
//public class ChatConvert {
//    public static ArrayList<ConversationHolder.Item> toConverItems(ArrayList<ConversationImpl> conversations) {
//        if (conversations == null) {
//            return null;
//        }
//        ArrayList<ConversationHolder.Item> items = new ArrayList<>();
//        for (ConversationImpl conversation : conversations) {
//            ConversationHolder.Item item = toConverItem(conversation);
//            items.add(item);
//        }
//        return items;
//    }
//
//    private static ConversationHolder.Item toConverItem(ConversationImpl conversation) {
//        if (conversation == null) {
//            return null;
//        }
//        ConversationHolder.Item item = new ConversationHolder.Item();
//
//        item.cid = conversation.getCid();
//        item.type = conversation.getConversationType();
//        item.isTop = conversation.isTop();
//        item.chatName = conversation.getName();
//        item.draft = conversation.getDraft();
//        item.lastMsg = toLasMsgItem(conversation.getLastMessage());
//        item.unreadCount = conversation.getUnreadCount();
//        item.isMute = !conversation.isNotification();
//        item.lastTime = conversation.getLastModifyTime();
//        item.unreadMsgType = conversation.getMessageType();
//        item.isAt = conversation.isAt();
//        if (conversation.getConversationType() == IConversation.TYPE_GROUP
//                && conversation.getGroupAvatars() != null) {
//            item.memberAvatars = new ArrayList<>();
//            for (GroupUser groupAvatar : conversation.getGroupAvatars()) {
//                item.memberAvatars.add(groupAvatar);
//            }
//        }
//
//        return item;
//
//    }
//
//    public static ConversationHolder.Item.LastMsg toLasMsgItem(MessageVo messageVo) {
//        if (messageVo == null) {
//            return null;
//        }
//        ConversationHolder.Item.LastMsg lastMsg = new ConversationHolder.Item.LastMsg();
//        lastMsg.timeStp = messageVo.getSendTime();
//        lastMsg.sendId = messageVo.getSendId();
//        lastMsg.sendName = messageVo.getName();
//        lastMsg.content = messageVo.getContent();
//        lastMsg.type = messageVo.getType();
//        lastMsg.status = messageVo.getStatus();
//        return lastMsg;
//    }
//
//
////    public static ConversationHolder.Item toConverItem(CYConversation cyConversation){
////        if (cyConversation == null) {
////
////            return null;
////        }
////        ConversationHolder.Item item = new ConversationHolder.Item();
////
////        item. cid= cyConversation.getCid();
////        item. type= cyConversation.getConversationType();
////        item. isTop= cyConversation.isTop();
////        item.chatName = cyConversation.getName();
////        item.draft = cyConversation.getDraft();
////        item.lastMsg = toLasMsgItem(cyConversation.getLastMessage());
////        item. unreadCount= cyConversation.getUnreadCount();
////        item.isMute = cyConversation.isMute();
////        item.lastTime = cyConversation.getLastTime();
////        if (cyConversation.getConversationType() == CYConversation.CYConversationType.GroupChat
////                && cyConversation.getGroupAvatars() != null) {
////            item.memberAvatars = new ArrayList<>();
////            for (CYGroupMember groupAvatar : cyConversation.getGroupAvatars()) {
////                for (CYGroupMember avatar : cyConversation.getGroupAvatars()) {
////                    item.memberAvatars.add(avatar);
////                }
////            }
////        }
////
////        return item;
////
////    }
////
////    public static ArrayList<ConversationHolder.Item> toConverItems(ArrayList<CYConversation> cyConversations){
////        if (cyConversations == null) {
////            return null;
////        }
////        ArrayList<ConversationHolder.Item> items = new ArrayList<>();
////        for (CYConversation cyConversation : cyConversations) {
////            ConversationHolder.Item item = toConverItem(cyConversation);
////            items.add(item);
////        }
////        return items;
////    }
////
////    public static ConversationHolder.Item.LastMsg toLasMsgItem(CYMessage cyMessage){
////        if (cyMessage == null) {
////            return null;
////        }
////        ConversationHolder.Item.LastMsg lastMsg = new ConversationHolder.Item.LastMsg();
////        lastMsg.timeStp= cyMessage.getSendTime();
////        lastMsg.sendId= cyMessage.getSendId();
////        lastMsg.sendName= cyMessage.getSendName();
////        lastMsg.content= cyMessage.getMessage();
////        lastMsg.type= cyMessage.getType();
////        lastMsg.status= cyMessage.getStatus();
////        return lastMsg;
////    }
//
//}
