package com.service;

import com.entity.KefangxinxiEntity;

/**
 * 客房状态流转服务接口
 */
public interface RoomStatusService {

    /**
     * 设置客房为空闲状态
     * @param kefanghao 客房号
     */
    void setFree(String kefanghao);

    /**
     * 设置客房为已预订状态
     * @param kefanghao 客房号
     */
    void setBooked(String kefanghao);

    /**
     * 设置客房为已入住状态
     * @param kefanghao 客房号
     */
    void setOccupied(String kefanghao);

    /**
     * 设置客房为待清扫状态
     * @param kefanghao 客房号
     */
    void setCleaning(String kefanghao);

    /**
     * 设置客房为维修中状态
     * @param kefanghao 客房号
     */
    void setMaintenance(String kefanghao);

    /**
     * 设置客房卫生状态
     * @param kefanghao 客房号
     * @param weishengqingkuang 卫生情况（已清扫/待清扫）
     */
    void setCleanStatus(String kefanghao, String weishengqingkuang);

    /**
     * 检查客房是否可预订
     * @param kefanghao 客房号
     * @return true-可预订，false-不可预订
     */
    boolean canBook(String kefanghao);

    /**
     * 检查客房是否可入住
     * @param kefanghao 客房号
     * @return true-可入住，false-不可入住
     */
    boolean canCheckIn(String kefanghao);

    /**
     * 根据客房号获取客房信息
     * @param kefanghao 客房号
     * @return 客房实体
     */
    KefangxinxiEntity getByKefanghao(String kefanghao);
}
