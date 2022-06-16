package com.lianzheng.core.wechat.db.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 存储企业微信的原始会话内容（解密过的）
 * @TableName wechat_session
 */
@Data
@TableName("wechat_session")
public class WechatSession implements Serializable {
    /**
     * 消息id，消息的唯一标识，企业可以使用此字段进行消息去重。String类型。msgid以_external结尾的消息，表明该消息是一条外部消息。
     */
    @TableId
    private String msgid;

    /**
     * 消息的seq值，标识消息的序号。再次拉取需要带上上次回包中最大的seq。Uint64类型，范围0-pow(2,64)-1
     */
    private Long seq;

    /**
     * 文本消息为：text;图片消息为：image,撤回消息为：revoke,同意消息为：agree，不同意消息为：disagree,语音消息为：voice,视频消息为：video,名片消息为：card,位置消息为：location,表情消息为：emotion,文件消息为：file,链接消息为：link,小程序消息为：weapp,会话记录消息为：chatrecord,待办消息:todo,投票消息:vote,填表消息:collect,红包消息:redpacket,会议邀请消息:meeting,在线文档消息:docmsg,MarkDown格式消息:markdown,图文消息:news,日程消息:calendar,混合消息:mixed,音频存档消息:meeting_voice_call,音频共享文档消息:voip_doc_share,互通红包消息:external_redpacket,视频号消息:sphfeed
     */
    private String msgtype;

    /**
     * 消息发送方id。同一企业内容为userid，非相同企业为external_userid。消息如果是机器人发出，也为external_userid。String类型
     */
    private String from;

    /**
     * 当from是external user时，需要获取它的unionid
     */
    private String unionid;

    /**
     * 消息接收方列表，可能是多个，同一个企业内容为userid，非相同企业为external_userid。数组，内容为string类型
     */
    private String tolist;

    /**
     * 消息内容。String类型
     */
    private String content;

    /**
     * 消息动作，目前有send(发送消息)/recall(撤回消息)/switch(切换企业日志)三种类型。String类型
     */
    private String action;

    /**
     * 群聊消息的群id。如果是单聊则为空。String类型
     */
    private String roomid;

    /**
     * 消息发送时间戳，utc时间，ms单位。
     */
    private Date msgtime;

    /**
     * 相关的存证id
     */
    private String evidenceid;

    /**
     * 
     */
    private Date createdtime;

    private static final long serialVersionUID = 1L;

    /**
     * 消息id，消息的唯一标识，企业可以使用此字段进行消息去重。String类型。msgid以_external结尾的消息，表明该消息是一条外部消息。
     */
    public String getMsgid() {
        return msgid;
    }

    /**
     * 消息id，消息的唯一标识，企业可以使用此字段进行消息去重。String类型。msgid以_external结尾的消息，表明该消息是一条外部消息。
     */
    public void setMsgid(String msgid) {
        this.msgid = msgid;
    }

    /**
     * 消息的seq值，标识消息的序号。再次拉取需要带上上次回包中最大的seq。Uint64类型，范围0-pow(2,64)-1
     */
    public Long getSeq() {
        return seq;
    }

    /**
     * 消息的seq值，标识消息的序号。再次拉取需要带上上次回包中最大的seq。Uint64类型，范围0-pow(2,64)-1
     */
    public void setSeq(Long seq) {
        this.seq = seq;
    }

    /**
     * 文本消息为：text;图片消息为：image,撤回消息为：revoke,同意消息为：agree，不同意消息为：disagree,语音消息为：voice,视频消息为：video,名片消息为：card,位置消息为：location,表情消息为：emotion,文件消息为：file,链接消息为：link,小程序消息为：weapp,会话记录消息为：chatrecord,待办消息:todo,投票消息:vote,填表消息:collect,红包消息:redpacket,会议邀请消息:meeting,在线文档消息:docmsg,MarkDown格式消息:markdown,图文消息:news,日程消息:calendar,混合消息:mixed,音频存档消息:meeting_voice_call,音频共享文档消息:voip_doc_share,互通红包消息:external_redpacket,视频号消息:sphfeed
     */
    public String getMsgtype() {
        return msgtype;
    }

    /**
     * 文本消息为：text;图片消息为：image,撤回消息为：revoke,同意消息为：agree，不同意消息为：disagree,语音消息为：voice,视频消息为：video,名片消息为：card,位置消息为：location,表情消息为：emotion,文件消息为：file,链接消息为：link,小程序消息为：weapp,会话记录消息为：chatrecord,待办消息:todo,投票消息:vote,填表消息:collect,红包消息:redpacket,会议邀请消息:meeting,在线文档消息:docmsg,MarkDown格式消息:markdown,图文消息:news,日程消息:calendar,混合消息:mixed,音频存档消息:meeting_voice_call,音频共享文档消息:voip_doc_share,互通红包消息:external_redpacket,视频号消息:sphfeed
     */
    public void setMsgtype(String msgtype) {
        this.msgtype = msgtype;
    }

    /**
     * 消息发送方id。同一企业内容为userid，非相同企业为external_userid。消息如果是机器人发出，也为external_userid。String类型
     */
    public String getFrom() {
        return from;
    }

    /**
     * 消息发送方id。同一企业内容为userid，非相同企业为external_userid。消息如果是机器人发出，也为external_userid。String类型
     */
    public void setFrom(String from) {
        this.from = from;
    }

    /**
     * 当from是external user时，需要获取它的unionid
     */
    public String getUnionid() {
        return unionid;
    }

    /**
     * 当from是external user时，需要获取它的unionid
     */
    public void setUnionid(String unionid) {
        this.unionid = unionid;
    }

    /**
     * 消息接收方列表，可能是多个，同一个企业内容为userid，非相同企业为external_userid。数组，内容为string类型
     */
    public String getTolist() {
        return tolist;
    }

    /**
     * 消息接收方列表，可能是多个，同一个企业内容为userid，非相同企业为external_userid。数组，内容为string类型
     */
    public void setTolist(String tolist) {
        this.tolist = tolist;
    }

    /**
     * 消息内容。String类型
     */
    public String getContent() {
        return content;
    }

    /**
     * 消息内容。String类型
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * 消息动作，目前有send(发送消息)/recall(撤回消息)/switch(切换企业日志)三种类型。String类型
     */
    public String getAction() {
        return action;
    }

    /**
     * 消息动作，目前有send(发送消息)/recall(撤回消息)/switch(切换企业日志)三种类型。String类型
     */
    public void setAction(String action) {
        this.action = action;
    }

    /**
     * 群聊消息的群id。如果是单聊则为空。String类型
     */
    public String getRoomid() {
        return roomid;
    }

    /**
     * 群聊消息的群id。如果是单聊则为空。String类型
     */
    public void setRoomid(String roomid) {
        this.roomid = roomid;
    }

    /**
     * 消息发送时间戳，utc时间，ms单位。
     */
    public Date getMsgtime() {
        return msgtime;
    }

    /**
     * 消息发送时间戳，utc时间，ms单位。
     */
    public void setMsgtime(Date msgtime) {
        this.msgtime = msgtime;
    }

    /**
     * 相关的存证id
     */
    public String getEvidenceid() {
        return evidenceid;
    }

    /**
     * 相关的存证id
     */
    public void setEvidenceid(String evidenceid) {
        this.evidenceid = evidenceid;
    }

    /**
     * 
     */
    public Date getCreatedtime() {
        return createdtime;
    }

    /**
     * 
     */
    public void setCreatedtime(Date createdtime) {
        this.createdtime = createdtime;
    }

    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that == null) {
            return false;
        }
        if (getClass() != that.getClass()) {
            return false;
        }
        WechatSession other = (WechatSession) that;
        return (this.getMsgid() == null ? other.getMsgid() == null : this.getMsgid().equals(other.getMsgid()))
            && (this.getSeq() == null ? other.getSeq() == null : this.getSeq().equals(other.getSeq()))
            && (this.getMsgtype() == null ? other.getMsgtype() == null : this.getMsgtype().equals(other.getMsgtype()))
            && (this.getFrom() == null ? other.getFrom() == null : this.getFrom().equals(other.getFrom()))
            && (this.getUnionid() == null ? other.getUnionid() == null : this.getUnionid().equals(other.getUnionid()))
            && (this.getTolist() == null ? other.getTolist() == null : this.getTolist().equals(other.getTolist()))
            && (this.getContent() == null ? other.getContent() == null : this.getContent().equals(other.getContent()))
            && (this.getAction() == null ? other.getAction() == null : this.getAction().equals(other.getAction()))
            && (this.getRoomid() == null ? other.getRoomid() == null : this.getRoomid().equals(other.getRoomid()))
            && (this.getMsgtime() == null ? other.getMsgtime() == null : this.getMsgtime().equals(other.getMsgtime()))
            && (this.getEvidenceid() == null ? other.getEvidenceid() == null : this.getEvidenceid().equals(other.getEvidenceid()))
            && (this.getCreatedtime() == null ? other.getCreatedtime() == null : this.getCreatedtime().equals(other.getCreatedtime()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getMsgid() == null) ? 0 : getMsgid().hashCode());
        result = prime * result + ((getSeq() == null) ? 0 : getSeq().hashCode());
        result = prime * result + ((getMsgtype() == null) ? 0 : getMsgtype().hashCode());
        result = prime * result + ((getFrom() == null) ? 0 : getFrom().hashCode());
        result = prime * result + ((getUnionid() == null) ? 0 : getUnionid().hashCode());
        result = prime * result + ((getTolist() == null) ? 0 : getTolist().hashCode());
        result = prime * result + ((getContent() == null) ? 0 : getContent().hashCode());
        result = prime * result + ((getAction() == null) ? 0 : getAction().hashCode());
        result = prime * result + ((getRoomid() == null) ? 0 : getRoomid().hashCode());
        result = prime * result + ((getMsgtime() == null) ? 0 : getMsgtime().hashCode());
        result = prime * result + ((getEvidenceid() == null) ? 0 : getEvidenceid().hashCode());
        result = prime * result + ((getCreatedtime() == null) ? 0 : getCreatedtime().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", msgid=").append(msgid);
        sb.append(", seq=").append(seq);
        sb.append(", msgtype=").append(msgtype);
        sb.append(", from=").append(from);
        sb.append(", unionid=").append(unionid);
        sb.append(", tolist=").append(tolist);
        sb.append(", content=").append(content);
        sb.append(", action=").append(action);
        sb.append(", roomid=").append(roomid);
        sb.append(", msgtime=").append(msgtime);
        sb.append(", evidenceid=").append(evidenceid);
        sb.append(", createdtime=").append(createdtime);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}