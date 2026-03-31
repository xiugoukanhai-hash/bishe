package com.entity;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.Date;
import org.springframework.format.annotation.DateTimeFormat;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.beanutils.BeanUtils;
import java.lang.reflect.InvocationTargetException;

/**
 * 前台人员
 * 数据库通用操作实体类（普通增删改查）
 */
@TableName("qiantairenyuan")
public class QiantairenyuanEntity<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    public QiantairenyuanEntity() {
    }

    public QiantairenyuanEntity(T t) {
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
     * 前台账号
     */
    @NotBlank(message = "前台账号不能为空")
    private String qiantaizhanghao;

    /**
     * 密码
     */
    @NotBlank(message = "密码不能为空")
    private String mima;

    /**
     * 前台姓名
     */
    private String qiantaixingming;

    /**
     * 性别
     */
    private String xingbie;

    /**
     * 手机
     */
    private String shouji;

    /**
     * 入职时间
     */
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat
    private Date ruzhishijian;

    /**
     * 照片
     */
    private String zhaopian;

    /**
     * 状态（在职/离职/停职）
     */
    private String zhuangtai;

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

    public String getQiantaizhanghao() {
        return qiantaizhanghao;
    }

    public void setQiantaizhanghao(String qiantaizhanghao) {
        this.qiantaizhanghao = qiantaizhanghao;
    }

    public String getMima() {
        return mima;
    }

    public void setMima(String mima) {
        this.mima = mima;
    }

    public String getQiantaixingming() {
        return qiantaixingming;
    }

    public void setQiantaixingming(String qiantaixingming) {
        this.qiantaixingming = qiantaixingming;
    }

    public String getXingbie() {
        return xingbie;
    }

    public void setXingbie(String xingbie) {
        this.xingbie = xingbie;
    }

    public String getShouji() {
        return shouji;
    }

    public void setShouji(String shouji) {
        this.shouji = shouji;
    }

    public Date getRuzhishijian() {
        return ruzhishijian;
    }

    public void setRuzhishijian(Date ruzhishijian) {
        this.ruzhishijian = ruzhishijian;
    }

    public String getZhaopian() {
        return zhaopian;
    }

    public void setZhaopian(String zhaopian) {
        this.zhaopian = zhaopian;
    }

    public String getZhuangtai() {
        return zhuangtai;
    }

    public void setZhuangtai(String zhuangtai) {
        this.zhuangtai = zhuangtai;
    }

    @Override
    public String toString() {
        return "QiantairenyuanEntity{" +
                "id=" + id +
                ", qiantaizhanghao='" + qiantaizhanghao + '\'' +
                ", qiantaixingming='" + qiantaixingming + '\'' +
                ", xingbie='" + xingbie + '\'' +
                ", shouji='" + shouji + '\'' +
                ", ruzhishijian=" + ruzhishijian +
                ", zhuangtai='" + zhuangtai + '\'' +
                ", addtime=" + addtime +
                '}';
    }
}
