package com.jason.microstream.mapper;

import com.jason.microstream.core.im.im_mode.msg.BaseMsg;
import com.jason.microstream.core.im.im_mode.msg.ImMsgConfig;
import com.jason.microstream.core.im.im_mode.msg.TextMsg;
import com.jason.microstream.core.im.reqresp.data.bean.chat.ClientConversation;
import com.jason.microstream.core.im.reqresp.data.bean.chat.ReqCreateChat;
import com.jason.microstream.core.im.reqresp.data.bean.chat.RespCreateChat;
import com.jason.microstream.db.entity.ConversationEntity;
import com.jason.microstream.db.entity.MessageEntity;

import java.util.ArrayList;
import java.util.List;


public class MessageMapper2 {

    public static MessageMapper2 INSTANCE = new MessageMapper2();

    public static BaseMsg toConcreteMsg(BaseMsg baseMsg) {
        if (baseMsg.getMsgType() == ImMsgConfig.ImMsgType.TYPE_TEXT) {
            return new TextMsg(baseMsg);
        } else {

        }
        return baseMsg;
    }

    public MessageEntity toTextMessageDb(TextMsg textMsg) {
        BaseMsg baseMsg = new BaseMsg(textMsg);
        baseMsg.setContent(textMsg.text);
        MessageEntity messageEntity = MessageMapper.INSTANCE.toMessageDb(textMsg);
        messageEntity.setContent(textMsg.text);
        return messageEntity;
    }
//    public ArrayList<MessageEntity> toTextMessageDb(ArrayList<TextMsg> textMsgs) {
//        ArrayList<MessageEntity> messageEntities = MessageMapper.INSTANCE.toTextMessageDbPart(textMsgs);
//        for (int i = 0; i < textMsgs.size(); i++) {
//            messageEntities.get(i).setContent(textMsgs.get(i).text);
//        }
//        return messageEntities;
//    }

    public ArrayList<BaseMsg> toMessageConcrete(List<MessageEntity> messageEntities) {
        ArrayList<BaseMsg> baseMsgs = MessageMapper.INSTANCE.toMessage(messageEntities);
        ArrayList<BaseMsg> retMsgs = new ArrayList<>();
        for (BaseMsg baseMsg : baseMsgs) {
            retMsgs.add(toConcreteMsg(baseMsg));
        }
        return retMsgs;
    }

    public BaseMsg toMessageConcrete(MessageEntity messageEntity) {
        BaseMsg baseMsg = MessageMapper.INSTANCE.toMessage(messageEntity);
        baseMsg = toConcreteMsg(baseMsg);
        return baseMsg;
    }

    public ConversationEntity toConversationDb(ReqCreateChat reqCreateChat, RespCreateChat respCreateChat) {
        ConversationEntity entity = new ConversationEntity();
        entity.setCid(reqCreateChat.cid);
        entity.setCName(reqCreateChat.cName);
        entity.setCType(reqCreateChat.cType);
        entity.setCState(reqCreateChat.cState);
        entity.setWithUid(reqCreateChat.withUid);
        entity.setCreatorId(reqCreateChat.creatorId);
        entity.setCreateTime(reqCreateChat.createTime);
        entity.setLastMsgId(reqCreateChat.lastMsgId);
        entity.setLastMsgContent(reqCreateChat.lastMsgContent);
        entity.setLastMsgTime(reqCreateChat.lastMsgTime);
        entity.setLastMsgSenderId(reqCreateChat.lastMsgSenderId);
        entity.setLastMsgSenderName(reqCreateChat.lastMsgSenderName);
        entity.setMemberCount(reqCreateChat.memberCount);
        entity.setExtData(reqCreateChat.extData);
        entity.setUnreadCount(reqCreateChat.unreadCount);
        entity.setIsTop(reqCreateChat.isTop);
        entity.setIsMute(reqCreateChat.isMute);
        entity.setIsHidden(reqCreateChat.isHidden);
        entity.setLastReadMsgId(reqCreateChat.lastReadMsgId);
        entity.setLastReadMsgTime(reqCreateChat.lastReadMsgTime);
        entity.setNickName(reqCreateChat.nickName);
        entity.setRole(reqCreateChat.role);
        entity.setJoinTime(reqCreateChat.joinTime);
        return entity;
    }

//    public MessageEntity toNormalTextMessageDb(NormalTextMessage normalTextMessage) {
//        MessageEntity aiMsgEntity = new MessageEntity();
//        aiMsgEntity.setMid(normalTextMessage.mId);
//        aiMsgEntity.setCid(normalTextMessage.cid);
//        aiMsgEntity.setStatus(normalTextMessage.status);
//        aiMsgEntity.setSendTime(normalTextMessage.sendTime);
//        aiMsgEntity.setSeqId(normalTextMessage.converContentCode);
//        aiMsgEntity.setAssociateId(normalTextMessage.associateId);
//        aiMsgEntity.setSendId(normalTextMessage.sendId);
//        aiMsgEntity.setSendName(normalTextMessage.sendName);
//        aiMsgEntity.setType(normalTextMessage.type);
//        aiMsgEntity.setContent(normalTextMessage.content);
//        aiMsgEntity.setExtra("");
//        return aiMsgEntity;
//    }
//
//    public MessageEntity toAITextMessageDb(AITextMessage aiTextMessage) {
//        MessageEntity aiMsgEntity = new MessageEntity();
//        aiMsgEntity.setMid(aiTextMessage.mId);
//        aiMsgEntity.setCid(aiTextMessage.cid);
//        aiMsgEntity.setStatus(aiTextMessage.status);
//        aiMsgEntity.setSendTime(aiTextMessage.sendTime);
//        aiMsgEntity.setSeqId(aiTextMessage.converContentCode);
//        aiMsgEntity.setAssociateId(aiTextMessage.associateId);
//        aiMsgEntity.setSendId(aiTextMessage.sendId);
//        aiMsgEntity.setSendName(aiTextMessage.sendName);
//        aiMsgEntity.setType(aiTextMessage.type);
//        aiMsgEntity.setContent(aiTextMessage.content);
//        aiMsgEntity.setExtra(JsonUtil.toJson(aiTextMessage.aiText));
//        return aiMsgEntity;
//    }
//
//    public ArrayList<MessageEntity> toMessagesDb(ArrayList<ConversationContentAceRsp> contentAceRsps,DigitalStaff digitalStaff) {
//        ArrayList<MessageEntity> aiMsgEntities = new ArrayList<>();
//        for (ConversationContentAceRsp contentAceRsp : contentAceRsps) {
//            aiMsgEntities.addAll(toMessageDb(contentAceRsp,digitalStaff));
//        }
//        return aiMsgEntities;
//    }
//
//    public ArrayList<MessageEntity> toMessageDb(ConversationContentAceRsp contentAceRsp, DigitalStaff digitalStaff) {
//        MessageEntity aiMsgEntityFrom = new MessageEntity();
//        MessageEntity aiMsgEntityReply = new MessageEntity();
//
//
//        aiMsgEntityFrom.setMid(contentAceRsp.getUpdateTime() - 1);
//        aiMsgEntityFrom.setSendTime(contentAceRsp.getUpdateTime() - 1);
//
//        aiMsgEntityFrom.setSeqId(contentAceRsp.getCode());
//        aiMsgEntityFrom.setAssociateId(contentAceRsp.getCode());
//        //TODO:离线拉下来的消息，没区分是否是指令消息
//        aiMsgEntityFrom.setType(AIBaseMessage.Type.NORMAL_TEXT);
//
//        aiMsgEntityFrom.setSendId(AccountManager.getInstance().getUserId());
//        aiMsgEntityFrom.setSendName(AccountManager.getInstance().getName());
//
//        aiMsgEntityFrom.setContent(contentAceRsp.getFromContent());
//        aiMsgEntityFrom.setExtra("");
//        aiMsgEntityFrom.setCid(contentAceRsp.getConversationCode());
//        aiMsgEntityFrom.setStatus(AIBaseMessage.Status.SEND_SUCCESS);
//
//
//        if (TextUtils.isEmpty(contentAceRsp.getTargetContent())) {
//            return new ArrayList<>(Arrays.asList(aiMsgEntityFrom));
//        }
//        aiMsgEntityReply.setMid(contentAceRsp.getUpdateTime());
//        aiMsgEntityReply.setSendTime(contentAceRsp.getUpdateTime());
//
//        aiMsgEntityReply.setSeqId(contentAceRsp.getCode());
//        aiMsgEntityReply.setAssociateId("");
//        aiMsgEntityReply.setType(AIBaseMessage.Type.AI_TEXT);
//
//        aiMsgEntityReply.setSendId(digitalStaff.code);
//        aiMsgEntityReply.setSendName(digitalStaff.name);
//
//        aiMsgEntityReply.setContent(contentAceRsp.getTargetContent());
//        AITextMessage.AIText aiText = new AITextMessage.AIText();
//        aiText.likeState = contentAceRsp.getLikeStatus();
//        aiText.isEnd = true;
//        aiMsgEntityReply.setExtra(JsonUtil.toJson(aiText));
//        aiMsgEntityReply.setCid(contentAceRsp.getConversationCode());
//        aiMsgEntityReply.setStatus(AIBaseMessage.Status.GEN_SUCCESS);
//
//
//        return new ArrayList<>(Arrays.asList(aiMsgEntityFrom, aiMsgEntityReply));
//    }
//
//
//    public ArrayList<AIBaseMessage> toAIMessages(List<MessageEntity> msgEntities) {
//        ArrayList<AIBaseMessage> aiBaseMessages = new ArrayList<>();
//        for (MessageEntity msgEntity : msgEntities) {
//            AIBaseMessage aiBaseMessage = toAIMessage(msgEntity);
//            if (aiBaseMessage != null) {
//                aiBaseMessages.add(aiBaseMessage);
//            }
//        }
//        return aiBaseMessages;
//    }
//    public AIBaseMessage toAIMessage(MessageEntity msgEntity) {
//        AIBaseMessage aiBaseMessage = null;
//        if (msgEntity.getType() == AIBaseMessage.Type.AI_TEXT) {
//            aiBaseMessage = new AITextMessage();
//            aiBaseMessage.mId= msgEntity.getMid();
//            aiBaseMessage.converContentCode= msgEntity.getSeqId();
//            aiBaseMessage.associateId= msgEntity.getAssociateId();
//            aiBaseMessage.sendId= msgEntity.getSendId();
//            aiBaseMessage.sendName= msgEntity.getSendName();
//            aiBaseMessage.sendTime = msgEntity.getSendTime();
//            aiBaseMessage.type= msgEntity.getType();
//            aiBaseMessage.content= msgEntity.getContent();
//            aiBaseMessage.cid= msgEntity.getCid();
//            aiBaseMessage.status= msgEntity.getStatus();
//            aiBaseMessage.cmdCode= msgEntity.getCmdCode();
//            if (!TextUtils.isEmpty(msgEntity.getExtra())) {
//                ((AITextMessage) aiBaseMessage).aiText
//                        = JsonUtil.fromJson(msgEntity.getExtra(), AITextMessage.AIText.class);
//            }
//        } else if (msgEntity.getType() == AIBaseMessage.Type.NORMAL_TEXT
//                ||msgEntity.getType() == AIBaseMessage.Type.CMD_TEXT) {
//            aiBaseMessage = new NormalTextMessage();
//            aiBaseMessage.mId= msgEntity.getMid();
//            aiBaseMessage.converContentCode= msgEntity.getSeqId();
//            aiBaseMessage.associateId= msgEntity.getAssociateId();
//            aiBaseMessage.sendId= msgEntity.getSendId();
//            aiBaseMessage.sendName= msgEntity.getSendName();
//            aiBaseMessage.sendTime = msgEntity.getSendTime();
//            aiBaseMessage.type= msgEntity.getType();
//            aiBaseMessage.content= msgEntity.getContent();
//            aiBaseMessage.cid= msgEntity.getCid();
//            aiBaseMessage.status= msgEntity.getStatus();
//            aiBaseMessage.cmdCode= msgEntity.getCmdCode();
//
//        }
//        return aiBaseMessage;
//    }


}
