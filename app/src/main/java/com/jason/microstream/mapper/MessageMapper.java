package com.jason.microstream.mapper;


import com.jason.microstream.core.im.im_mode.msg.BaseMsg;
import com.jason.microstream.core.im.im_mode.msg.TextMsg;
import com.jason.microstream.core.im.reqresp.data.bean.chat.ClientConversation;
import com.jason.microstream.db.entity.ConversationEntity;
import com.jason.microstream.db.entity.MessageEntity;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.util.ArrayList;
import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MessageMapper {
    MessageMapper INSTANCE = Mappers.getMapper(MessageMapper.class);


    MessageEntity toMessageDb(BaseMsg baseMsg);
    ArrayList<MessageEntity> toMessageDb(List<BaseMsg> baseMsgs);
    ArrayList<BaseMsg> toMessage(List<MessageEntity> messageEntities);
    BaseMsg toMessage(MessageEntity messageEntity);

    ArrayList<ConversationEntity> toConversationsDb(List<ClientConversation> conversations);
    ConversationEntity toConversationDb(ClientConversation conversation);
    ClientConversation toClientConversation(ConversationEntity conversation);
    ArrayList<ClientConversation> toClientConversations(List<ConversationEntity> entities);


//    ArrayList<DigitalStaff> toStaffs(ArrayList<StaffAceRsp> staffAceRsp);
//    DigitalStaff toStaff(StaffAceRsp staffAceRsp);
//
//    ArrayList<DigitalStaffEntity> toStaffEntitys(ArrayList<StaffAceRsp> staffAceRsp);
//    DigitalStaffEntity toStaffEntity(StaffAceRsp staffAceRsp);
//
//    ArrayList<DigitalStaffKnowledgeEntity> toKnowledgeEntitys(ArrayList< TKnowledgeBaseAceDO > TKnowledgeBaseDOs);
//    DigitalStaffKnowledgeEntity toKnowledgeEntity(TKnowledgeBaseAceDO knowledgeBaseAceDO);
//
//
//    ArrayList<DigitalStaffEntity> toStaffEntitys(List<DigitalStaff> digitalStaffs);
//    DigitalStaffEntity toStaffEntity(DigitalStaff digitalStaff);
//
//    ArrayList<DigitalStaff> toStaffs(List<DigitalStaffEntity> staffEntities);
//    DigitalStaff toStaff(DigitalStaffEntity staffEntity);
//
//    ArrayList<DigitalStaffKnowledge> toDigitalStaffKnowledges(ArrayList< TKnowledgeBaseAceDO > TKnowledgeBaseDOs);
//    DigitalStaffKnowledge toDigitalStaffKnowledges(TKnowledgeBaseAceDO knowledgeBaseAceDO);
//
//
//    ArrayList<AIConversationEntity> toConversationEntities(ArrayList<ConversationAceRsp> conversationAceRsps);
//    AIConversationEntity toConversationEntity(ConversationAceRsp conversationAceRsp);
//
//    ArrayList<ConversationAceRsp> toConversations(ArrayList<AIConversationEntity> aiConversationEntities);
//    ConversationAceRsp toConversations(AIConversationEntity aiConversationEntity);
//
//
//    ArrayList<AICmdEntity> toAICmdEntities(ArrayList<InstructAceRsp> instructAceRsps);
//    AICmdEntity toAICmdEntity(InstructAceRsp instructAceRsp);
//
//    ArrayList<InstructAceRsp> toInstructAceRsps(ArrayList<AICmdEntity> aiCmdEntities);
//    InstructAceRsp toInstructAceRsp(AICmdEntity aiCmdEntity);







}
