package com.jason.microstream.ui.conversation.mapper;//package com.shinemo.openim.uikit.main.conversation.mapper;
//
//import com.shinemo.openim.uikit.main.conversation.holder.ConversationHolder;
//
//import org.mapstruct.Mapper;
//import org.mapstruct.Mapping;
//import org.mapstruct.MappingTarget;
//import org.mapstruct.ReportingPolicy;
//import org.mapstruct.factory.Mappers;
//
//import java.util.ArrayList;
//
//@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
//public interface ChatMapper {
//    ChatMapper INSTANCE = Mappers.getMapper(ChatMapper.class);
//
//    @Mapping(source = "cyConversation.top", target = "isTop")
//    @Mapping(source = "cyConversation.name", target = "chatName")
//    @Mapping(source = "cyConversation.lastMessage", target = "lastMsg")
//    @Mapping(source = "cyConversation.unreadCount", target = "unreadCount")
//    @Mapping(source = "cyConversation.mute", target = "isMute")
//    ConversationHolder.Item toConverItem(CYConversation cyConversation);
//
//    ArrayList<ConversationHolder.Item> toConverItems(ArrayList<CYConversation> cyConversations);
//
//
//    @Mapping(source = "cyMessage.lastTime", target = "sendTime")
//    @Mapping(source = "cyMessage.message", target = "content")
//    ConversationHolder.Item.LastMsg toLasMsgItem(CYMessage cyMessage);
//
//    @Mapping(source = "cyMessage.lastTime", target = "sendTime")
//    @Mapping(source = "cyMessage.message", target = "content")
//    void updateLasMsgItem(ConversationHolder.Item.LastMsg lastMsg,CYMessage cyMessage);
//
//    ;
//
//
//    @Mapping(source = "cyConversation.top", target = "isTop")
//    @Mapping(source = "cyConversation.name", target = "chatName")
//    @Mapping(source = "cyConversation.lastMessage", target = "lastMsg")
//    @Mapping(source = "cyConversation.unreadCount", target = "unreadCount")
//    @Mapping(source = "cyConversation.mute", target = "isMute")
//    void updateConverItem(ConversationHolder.Item item, @MappingTarget CYConversation cyConversation);
//
//
//}
