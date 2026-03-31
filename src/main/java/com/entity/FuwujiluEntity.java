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
 * 服务记录
 * 记录前台为客人提供的各类服务
 */
@TableName("fuwujilu")
public class FuwujiluEntity<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    public FuwujiluEntity() {
    }

    public FuwujiluEntity(T t) {
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
     * 订单编号
     */
    private String dingdanbianhao;

    /**
     * 客房号
     */
    private String kefanghao;

    /**
     * 服务类型
     */
    private String fuwuleixing;

    /**
     * 服务详情
     */
    private String fuwuxiangqing;

    /**
     * 服务费用
     */
    private Double fuwufeiyong;

    /**
     * 备注
     */
    private String beizhu;

    /**
     * 登记人账号
     */
    private String dengjirenzhanghao;

    /**
     * 登记时间
     */
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat
    private Date dengjishijian;

    /**
     * 处理人账号
     */
    private String chulirenzhanghao;

    /**
     * 处理时间
     */
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat
    private Date chulishijian;

    /**
     * 状态（待处理/处理中/已完成/已取消）
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

    public String getDingdanbianhao() {
        return dingdanbianhao;
    }

    public void setDingdanbianhao(String dingdanbianhao) {
        this.dingdanbianhao = dingdanbianhao;
    }

    public String getKefanghao() {
        return kefanghao;
    }

    public void setKefanghao(String kefanghao) {
        this.kefanghao = kefanghao;
    }

    public String getFuwuleixing() {
        return fuwuleixing;
    }

    public void setFuwuleixing(String fuwuleixing) {
        this.fuwuleixing = fuwuleixing;
    }

    public String getFuwuxiangqing() {
        return fuwuxiangqing;
    }

    public void setFuwuxiangqing(String fuwuxiangqing) {
        this.fuwuxiangqing = fuwuxiangqing;
    }

    public Double getFuwufeiyong() {
        return fuwufeiyong;
    }

    public void setFuwufeiyong(Double fuwufeiyong) {
        this.fuwufeiyong = fuwufeiyong;
    }

    public String getBeizhu() {
        return beizhu;
    }

    public void setBeizhu(String beizhu) {
        this.beizhu = beizhu;
    }

    public String getDengjirenzhanghao() {
        return dengjirenzhanghao;
    }

    public void setDengjirenzhanghao(String dengjirenzhanghao) {
        this.dengjirenzhanghao = dengjirenzhanghao;
    }

    public Date getDengjishijian() {
        return dengjishijian;
    }

    public void setDengjishijian(Date dengjishijian) {
        this.dengjishijian = dengjishijian;
    }

    public String getChulirenzhanghao() {
        return chulirenzhanghao;
    }

    public void setChulirenzhanghao(String chulirenzhanghao) {
        this.chulirenzhanghao = chulirenzhanghao;
    }

    public Date getChulishijian() {
        return chulishijian;
    }

    public void setChulishijian(Date chulishijian) {
        this.chulishijian = chulishijian;
    }

    public String getZhuangtai() {
        return zhuangtai;
    }

    public void setZhuangtai(String zhuangtai) {
        this.zhuangtai = zhuangtai;
    }

    @Override
    public String toString() {
        return "FuwujiluEntity{" +
                "id=" + id +
                ", dingdanbianhao='" + dingdanbianhao + '\'' +
                ", kefanghao='" + kefanghao + '\'' +
                ", fuwuleixing='" + fuwuleixing + '\'' +
                ", zhuangtai='" + zhuangtai + '\'' +
                ", addtime=" + addtime +
                '}';
    }
}
