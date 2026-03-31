package com.entity.vo;

import com.entity.PinglunEntity;
import com.baomidou.mybatisplus.annotations.TableName;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.io.Serializable;

/**
 * 客户评价 VO
 */
public class PinglunVO implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long refid;
    private Long userid;
    private String tablename;
    private String nickname;
    private String content;
    private Integer pingfen;
    private String dingdanbianhao;
    private String reply;
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date replytime;
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date addtime;

    public Long getRefid() { return refid; }
    public void setRefid(Long refid) { this.refid = refid; }
    public Long getUserid() { return userid; }
    public void setUserid(Long userid) { this.userid = userid; }
    public String getTablename() { return tablename; }
    public void setTablename(String tablename) { this.tablename = tablename; }
    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public Integer getPingfen() { return pingfen; }
    public void setPingfen(Integer pingfen) { this.pingfen = pingfen; }
    public String getDingdanbianhao() { return dingdanbianhao; }
    public void setDingdanbianhao(String dingdanbianhao) { this.dingdanbianhao = dingdanbianhao; }
    public String getReply() { return reply; }
    public void setReply(String reply) { this.reply = reply; }
    public Date getReplytime() { return replytime; }
    public void setReplytime(Date replytime) { this.replytime = replytime; }
    public Date getAddtime() { return addtime; }
    public void setAddtime(Date addtime) { this.addtime = addtime; }
}
