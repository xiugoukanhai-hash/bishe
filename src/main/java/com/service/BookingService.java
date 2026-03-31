package com.service;

import java.util.Date;
import java.util.Map;

/**
 * 预约业务服务接口（统一处理用户和会员预约）
 */
public interface BookingService {

    /**
     * 创建用户预约
     * @param userId 用户ID
     * @param kefanghao 客房号
     * @param ruzhushijian 入住时间
     * @param tianshu 入住天数
     * @return 预约编号
     */
    String createYonghuBooking(Long userId, String kefanghao, Date ruzhushijian, Integer tianshu);

    /**
     * 创建会员预约
     * @param huiyuanId 会员ID
     * @param kefanghao 客房号
     * @param ruzhushijian 入住时间
     * @param tianshu 入住天数
     * @return 预约编号
     */
    String createHuiyuanBooking(Long huiyuanId, String kefanghao, Date ruzhushijian, Integer tianshu);

    /**
     * 审核用户预约
     * @param bookingId 预约ID
     * @param sfsh 审核结果（是/否）
     * @param shhf 审核回复
     */
    void auditYonghuBooking(Long bookingId, String sfsh, String shhf);

    /**
     * 审核会员预约
     * @param bookingId 预约ID
     * @param sfsh 审核结果（是/否）
     * @param shhf 审核回复
     */
    void auditHuiyuanBooking(Long bookingId, String sfsh, String shhf);

    /**
     * 用户预约支付
     * @param bookingId 预约ID
     * @return 支付结果
     */
    Map<String, Object> payYonghuBooking(Long bookingId);
    
    /**
     * 用户预约支付（支持多种支付方式）
     * @param bookingId 预约ID
     * @param payType 支付方式：wechat-微信支付，balance-余额支付
     * @return 支付结果
     */
    Map<String, Object> payYonghuBooking(Long bookingId, String payType);

    /**
     * 会员预约支付
     * @param bookingId 预约ID
     * @return 支付结果
     */
    Map<String, Object> payHuiyuanBooking(Long bookingId);
    
    /**
     * 会员预约支付（支持多种支付方式）
     * @param bookingId 预约ID
     * @param payType 支付方式：wechat-微信支付，balance-余额支付
     * @return 支付结果
     */
    Map<String, Object> payHuiyuanBooking(Long bookingId, String payType);

    /**
     * 取消用户预约
     * @param bookingId 预约ID
     * @param reason 取消原因
     */
    void cancelYonghuBooking(Long bookingId, String reason);

    /**
     * 取消会员预约
     * @param bookingId 预约ID
     * @param reason 取消原因
     */
    void cancelHuiyuanBooking(Long bookingId, String reason);

    /**
     * 计算预约总价
     * @param kefanghao 客房号
     * @param tianshu 入住天数
     * @param isHuiyuan 是否会员
     * @return 总价
     */
    Double calculatePrice(String kefanghao, Integer tianshu, boolean isHuiyuan);
}
