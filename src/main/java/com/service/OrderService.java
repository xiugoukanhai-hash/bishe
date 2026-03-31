package com.service;

import java.util.List;
import java.util.Map;

/**
 * 订单管理服务接口
 * 统一管理用户和会员的预约、入住、退房订单
 */
public interface OrderService {

    /**
     * 综合查询订单列表
     * @param params 查询参数（type, status, keyword, startDate, endDate, page, limit）
     * @return 订单列表和分页信息
     */
    Map<String, Object> queryOrders(Map<String, Object> params);

    /**
     * 获取订单详情
     * @param orderNo 订单编号
     * @param type 订单类型（yonghu/huiyuan）
     * @return 订单详情（包含预约、入住、退房信息）
     */
    Map<String, Object> getOrderDetail(String orderNo, String type);

    /**
     * 获取订单状态流转历史
     * @param orderNo 订单编号
     * @return 状态历史列表
     */
    List<Map<String, Object>> getOrderStatusHistory(String orderNo);

    /**
     * 批量审核订单
     * @param orderIds 订单ID列表
     * @param type 订单类型
     * @param sfsh 审核结果（是/否）
     * @param shhf 审核回复
     * @return 处理结果（成功数、失败数、失败原因）
     */
    Map<String, Object> batchAuditOrders(List<Long> orderIds, String type, String sfsh, String shhf);

    /**
     * 导出订单数据
     * @param params 查询参数
     * @return 导出数据列表
     */
    List<Map<String, Object>> exportOrders(Map<String, Object> params);

    /**
     * 获取订单统计概览
     * @return 统计数据（今日订单、待审核、待支付、总订单）
     */
    Map<String, Object> getOrderOverview();

    /**
     * 获取用户订单列表（前端用户中心使用）
     * @param userId 用户ID
     * @param userType 用户类型（yonghu/huiyuan）
     * @param params 查询参数
     * @return 订单列表
     */
    Map<String, Object> getMyOrders(Long userId, String userType, Map<String, Object> params);
}
