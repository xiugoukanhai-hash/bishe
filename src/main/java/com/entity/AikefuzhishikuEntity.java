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
 * AI客服知识库
 * 存储常见问题和答案，用于智能客服自动回复
 */
@TableName("aikefuzhishiku")
public class AikefuzhishikuEntity<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    public AikefuzhishikuEntity() {
    }

    public AikefuzhishikuEntity(T t) {
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
     * 问题类型
     */
    private String wentileixing;

    /**
     * 关键词（多个用逗号分隔）
     */
    private String guanjianci;

    /**
     * 问题
     */
    private String wenti;

    /**
     * 答案
     */
    private String daan;

    /**
     * 排序（数字越小越靠前）
     */
    private Integer paixu;

    /**
     * 点击次数（用于统计热门问题）
     */
    private Integer clickcount;

    /**
     * 状态（启用/禁用）
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

    public String getWentileixing() {
        return wentileixing;
    }

    public void setWentileixing(String wentileixing) {
        this.wentileixing = wentileixing;
    }

    public String getGuanjianci() {
        return guanjianci;
    }

    public void setGuanjianci(String guanjianci) {
        this.guanjianci = guanjianci;
    }

    public String getWenti() {
        return wenti;
    }

    public void setWenti(String wenti) {
        this.wenti = wenti;
    }

    public String getDaan() {
        return daan;
    }

    public void setDaan(String daan) {
        this.daan = daan;
    }

    public Integer getPaixu() {
        return paixu;
    }

    public void setPaixu(Integer paixu) {
        this.paixu = paixu;
    }

    public Integer getClickcount() {
        return clickcount;
    }

    public void setClickcount(Integer clickcount) {
        this.clickcount = clickcount;
    }

    public String getZhuangtai() {
        return zhuangtai;
    }

    public void setZhuangtai(String zhuangtai) {
        this.zhuangtai = zhuangtai;
    }

    @Override
    public String toString() {
        return "AikefuzhishikuEntity{" +
                "id=" + id +
                ", wentileixing='" + wentileixing + '\'' +
                ", wenti='" + wenti + '\'' +
                ", paixu=" + paixu +
                ", zhuangtai='" + zhuangtai + '\'' +
                ", addtime=" + addtime +
                '}';
    }
}
