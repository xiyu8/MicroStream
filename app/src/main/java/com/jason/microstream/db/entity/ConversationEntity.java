package com.jason.microstream.db.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.DaoException;
import com.jason.microstream.db.entity.generator.DaoSession;
import com.jason.microstream.db.entity.generator.ConversationEntityDao;

@Entity(active = true, indexes = {
        @Index(value = "cid", unique = true),
        @Index(value = "lastMsgTime"),
})
public class ConversationEntity {
    @Id
    private Long id;

    private String cid;
    private String cName;
    private int cType;
    private int cState;
    private String withUid;
    private String creatorId;
    private long createTime;

    private long lastMsgId;
    private String lastMsgContent;
    private long lastMsgTime;
    private String lastMsgSenderId;
    private String lastMsgSenderName;

    private int memberCount;

    private String extData;

    /////////////////////////////////////////////////
    private int unreadCount;
    private boolean isTop;
    private boolean isMute;
    private boolean isHidden;

    private long lastReadMsgId;
    private long lastReadMsgTime;

    private String nickName;
    private int role;
    private long joinTime;

/** Used to resolve relations */
@Generated(hash = 2040040024)
private transient DaoSession daoSession;

/** Used for active entity operations. */
@Generated(hash = 2058273996)
private transient ConversationEntityDao myDao;
@Generated(hash = 1689591994)
public ConversationEntity(Long id, String cid, String cName, int cType,
        int cState, String withUid, String creatorId, long createTime,
        long lastMsgId, String lastMsgContent, long lastMsgTime,
        String lastMsgSenderId, String lastMsgSenderName, int memberCount,
        String extData, int unreadCount, boolean isTop, boolean isMute,
        boolean isHidden, long lastReadMsgId, long lastReadMsgTime,
        String nickName, int role, long joinTime) {
    this.id = id;
    this.cid = cid;
    this.cName = cName;
    this.cType = cType;
    this.cState = cState;
    this.withUid = withUid;
    this.creatorId = creatorId;
    this.createTime = createTime;
    this.lastMsgId = lastMsgId;
    this.lastMsgContent = lastMsgContent;
    this.lastMsgTime = lastMsgTime;
    this.lastMsgSenderId = lastMsgSenderId;
    this.lastMsgSenderName = lastMsgSenderName;
    this.memberCount = memberCount;
    this.extData = extData;
    this.unreadCount = unreadCount;
    this.isTop = isTop;
    this.isMute = isMute;
    this.isHidden = isHidden;
    this.lastReadMsgId = lastReadMsgId;
    this.lastReadMsgTime = lastReadMsgTime;
    this.nickName = nickName;
    this.role = role;
    this.joinTime = joinTime;
}
@Generated(hash = 2044044276)
public ConversationEntity() {
}
public Long getId() {
    return this.id;
}
public void setId(Long id) {
    this.id = id;
}
public String getCid() {
    return this.cid;
}
public void setCid(String cid) {
    this.cid = cid;
}
public String getCName() {
    return this.cName;
}
public void setCName(String cName) {
    this.cName = cName;
}
public int getCType() {
    return this.cType;
}
public void setCType(int cType) {
    this.cType = cType;
}
public int getCState() {
    return this.cState;
}
public void setCState(int cState) {
    this.cState = cState;
}
public String getWithUid() {
    return this.withUid;
}
public void setWithUid(String withUid) {
    this.withUid = withUid;
}
public String getCreatorId() {
    return this.creatorId;
}
public void setCreatorId(String creatorId) {
    this.creatorId = creatorId;
}
public long getCreateTime() {
    return this.createTime;
}
public void setCreateTime(long createTime) {
    this.createTime = createTime;
}
public long getLastMsgId() {
    return this.lastMsgId;
}
public void setLastMsgId(long lastMsgId) {
    this.lastMsgId = lastMsgId;
}
public String getLastMsgContent() {
    return this.lastMsgContent;
}
public void setLastMsgContent(String lastMsgContent) {
    this.lastMsgContent = lastMsgContent;
}
public long getLastMsgTime() {
    return this.lastMsgTime;
}
public void setLastMsgTime(long lastMsgTime) {
    this.lastMsgTime = lastMsgTime;
}
public String getLastMsgSenderId() {
    return this.lastMsgSenderId;
}
public void setLastMsgSenderId(String lastMsgSenderId) {
    this.lastMsgSenderId = lastMsgSenderId;
}
public String getLastMsgSenderName() {
    return this.lastMsgSenderName;
}
public void setLastMsgSenderName(String lastMsgSenderName) {
    this.lastMsgSenderName = lastMsgSenderName;
}
public int getMemberCount() {
    return this.memberCount;
}
public void setMemberCount(int memberCount) {
    this.memberCount = memberCount;
}
public String getExtData() {
    return this.extData;
}
public void setExtData(String extData) {
    this.extData = extData;
}
public int getUnreadCount() {
    return this.unreadCount;
}
public void setUnreadCount(int unreadCount) {
    this.unreadCount = unreadCount;
}
public boolean getIsTop() {
    return this.isTop;
}
public void setIsTop(boolean isTop) {
    this.isTop = isTop;
}
public boolean getIsMute() {
    return this.isMute;
}
public void setIsMute(boolean isMute) {
    this.isMute = isMute;
}
public boolean getIsHidden() {
    return this.isHidden;
}
public void setIsHidden(boolean isHidden) {
    this.isHidden = isHidden;
}
public long getLastReadMsgId() {
    return this.lastReadMsgId;
}
public void setLastReadMsgId(long lastReadMsgId) {
    this.lastReadMsgId = lastReadMsgId;
}
public long getLastReadMsgTime() {
    return this.lastReadMsgTime;
}
public void setLastReadMsgTime(long lastReadMsgTime) {
    this.lastReadMsgTime = lastReadMsgTime;
}
public String getNickName() {
    return this.nickName;
}
public void setNickName(String nickName) {
    this.nickName = nickName;
}
public int getRole() {
    return this.role;
}
public void setRole(int role) {
    this.role = role;
}
public long getJoinTime() {
    return this.joinTime;
}
public void setJoinTime(long joinTime) {
    this.joinTime = joinTime;
}
/**
 * Convenient call for {@link org.greenrobot.greendao.AbstractDao#delete(Object)}.
 * Entity must attached to an entity context.
 */
@Generated(hash = 128553479)
public void delete() {
    if (myDao == null) {
        throw new DaoException("Entity is detached from DAO context");
    }
    myDao.delete(this);
}
/**
 * Convenient call for {@link org.greenrobot.greendao.AbstractDao#refresh(Object)}.
 * Entity must attached to an entity context.
 */
@Generated(hash = 1942392019)
public void refresh() {
    if (myDao == null) {
        throw new DaoException("Entity is detached from DAO context");
    }
    myDao.refresh(this);
}
/**
 * Convenient call for {@link org.greenrobot.greendao.AbstractDao#update(Object)}.
 * Entity must attached to an entity context.
 */
@Generated(hash = 713229351)
public void update() {
    if (myDao == null) {
        throw new DaoException("Entity is detached from DAO context");
    }
    myDao.update(this);
}
/** called by internal mechanisms, do not call yourself. */
@Generated(hash = 1872900634)
public void __setDaoSession(DaoSession daoSession) {
    this.daoSession = daoSession;
    myDao = daoSession != null ? daoSession.getConversationEntityDao() : null;
}


}
