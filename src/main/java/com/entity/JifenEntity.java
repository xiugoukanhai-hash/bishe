package com.entity;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import java.io.Serializable;
import java.util.Date;
import org.springframework.format.annotation.DateTimeFormat;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.beanutils.BeanUtils;
import java.lang.reflect.InvocationTargetException;

/**
 * 积分记录
 * 记录会员积分的增减变动
 */
@TableName("jifen")
public class JifenEntity<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    public JifenEntity() {
    }

    public JifenEntity(T t) {
        try {
            BeanUtils.copyProperties(this, t);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    /**
     * 主键id
     */
    @TableId
    private Long id;

    /**
     * 会员ID
     */
    private Long huiyuanid;

    /**
     * 账号
     */
    private String zhanghao;

    /**
     * 积分类型（预订积分/入住积分/消费积分/积分兑换/赠送积分/积分扣减）
     */
    private String jifenleixing;

    /**
     * 积分数（正数为增加，负数为减少）
     */
    private Integer jifenshu;

    /**
     * 变动后余额
     */
    private Integer yue;

    /**
     * 说明
     */
    private String shuoming;

    /**
     * 关联订单号
     */
    private String guanliandingdan;

    /**
     * 操作人
     */
    private String caozuoren;

    /**
     * 创建时间
     */
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat
    private Date addtime;

    public Date getAddtime() {
        return addtime;
    }

    public void setAddtime(Date addtime) {
        this.addtime = addtime;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getHuiyuanid() {
        return huiyuanid;
    }

    public void setHuiyuanid(Long huiyuanid) {
        this.huiyuanid = huiyuanid;
    }

    public String getZhanghao() {
        return zhanghao;
    }

    public void setZhanghao(String zhanghao) {
        this.zhanghao = zhanghao;
    }

    public String getJifenleixing() {
        return jifenleixing;
    }

    public void setJifenleixing(String jifenleixing) {
        this.jifenleixing = jifenleixing;
    }

    public Integer getJifenshu() {
        return jifenshu;
    }

    public void setJifenshu(Integer jifenshu) {
        this.jifenshu = jifenshu;
    }

    public Integer getYue() {
        return yue;
    }

    public void setYue(Integer yue) {
        this.yue = yue;
    }

    public String getShuoming() {
        return shuoming;
    }

    public void setShuoming(String shuoming) {
        this.shuoming = shuoming;
    }

    public String getGuanliandingdan() {
        return guanliandingdan;
    }

    public void setGuanliandingdan(String guanliandingdan) {
        this.guanliandingdan = guanliandingdan;
    }

    public String getCaozuoren() {
        return caozuoren;
    }

    public void setCaozuoren(String caozuoren) {
        this.caozuoren = caozuoren;
    }

    @Override
    public String toString() {
        return "JifenEntity{" +
                "id=" + id +
                ", huiyuanid=" + huiyuanid +
                ", zhanghao='" + zhanghao + '\'' +
                ", jifenleixing='" + jifenleixing + '\'' +
                ", jifenshu=" + jifenshu +
                ", yue=" + yue +
                ", addtime=" + addtime +
                '}';
    }
}
