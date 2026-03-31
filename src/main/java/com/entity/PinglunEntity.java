package com.entity;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import java.lang.reflect.InvocationTargetException;
import java.io.Serializable;
import java.util.Date;
import org.springframework.format.annotation.DateTimeFormat;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.beanutils.BeanUtils;

/**
 * 客户评价表
 */
@TableName("pinglun")
public class PinglunEntity<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    public PinglunEntity() {}

    public PinglunEntity(T t) {
        try {
            BeanUtils.copyProperties(this, t);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    @TableId
    private Long id;
    private Long refid;
    private Long userid;
    private String tablename;
    private String nickname;
    private String content;
    private Integer pingfen;
    private String dingdanbianhao;
    private String reply;
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat
    private Date replytime;
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat
    private Date addtime;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
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
