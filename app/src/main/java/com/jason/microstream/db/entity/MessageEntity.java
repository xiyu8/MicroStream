package com.jason.microstream.db.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.DaoException;
import com.jason.microstream.db.entity.generator.DaoSession;
import com.jason.microstream.db.entity.generator.MessageEntityDao;

@Entity(active = true, indexes = {
        @Index(value = "seqId, stubId", unique = true),
        @Index(value = "stubId", unique = true),
        @Index(value = "seqId", unique = true)
})
public class MessageEntity {

    @Id
//    @Property(nameInDb = "id")
    private Long id;

    private String seqId;
    private String stubId;

    private Integer msgType;
    private String content;
    private String contentAbstract;
    private String extra;

    private int state;
    private String cid;
    private int cType;

    private String fromId;
    private String fromName;
    private String toId;
    private String toName;
    private long sendTime;

/** Used to resolve relations */
@Generated(hash = 2040040024)
private transient DaoSession daoSession;

/** Used for active entity operations. */
@Generated(hash = 499759967)
private transient MessageEntityDao myDao;
@Generated(hash = 2095284860)
public MessageEntity(Long id, String seqId, String stubId, Integer msgType,
        String content, String contentAbstract, String extra, int state,
        String cid, int cType, String fromId, String fromName, String toId,
        String toName, long sendTime) {
    this.id = id;
    this.seqId = seqId;
    this.stubId = stubId;
    this.msgType = msgType;
    this.content = content;
    this.contentAbstract = contentAbstract;
    this.extra = extra;
    this.state = state;
    this.cid = cid;
    this.cType = cType;
    this.fromId = fromId;
    this.fromName = fromName;
    this.toId = toId;
    this.toName = toName;
    this.sendTime = sendTime;
}
@Generated(hash = 1797882234)
public MessageEntity() {
}
public Long getId() {
    return this.id;
}
public void setId(Long id) {
    this.id = id;
}
public String getSeqId() {
    return this.seqId;
}
public void setSeqId(String seqId) {
    this.seqId = seqId;
}
public String getStubId() {
    return this.stubId;
}
public void setStubId(String stubId) {
    this.stubId = stubId;
}
public Integer getMsgType() {
    return this.msgType;
}
public void setMsgType(Integer msgType) {
    this.msgType = msgType;
}
public String getContent() {
    return this.content;
}
public void setContent(String content) {
    this.content = content;
}
public String getExtra() {
    return this.extra;
}
public void setExtra(String extra) {
    this.extra = extra;
}
public int getState() {
    return this.state;
}
public void setState(int state) {
    this.state = state;
}
public String getCid() {
    return this.cid;
}
public void setCid(String cid) {
    this.cid = cid;
}
public String getFromId() {
    return this.fromId;
}
public void setFromId(String fromId) {
    this.fromId = fromId;
}
public String getFromName() {
    return this.fromName;
}
public void setFromName(String fromName) {
    this.fromName = fromName;
}
public String getToId() {
    return this.toId;
}
public void setToId(String toId) {
    this.toId = toId;
}
public String getToName() {
    return this.toName;
}
public void setToName(String toName) {
    this.toName = toName;
}
public long getSendTime() {
    return this.sendTime;
}
public void setSendTime(long sendTime) {
    this.sendTime = sendTime;
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
@Generated(hash = 83651317)
public void __setDaoSession(DaoSession daoSession) {
    this.daoSession = daoSession;
    myDao = daoSession != null ? daoSession.getMessageEntityDao() : null;
}
public String getContentAbstract() {
    return this.contentAbstract;
}
public void setContentAbstract(String contentAbstract) {
    this.contentAbstract = contentAbstract;
}
public int getCType() {
    return this.cType;
}
public void setCType(int cType) {
    this.cType = cType;
}



}
