package com.service;

import java.util.List;
import java.util.Map;

/**
 * 统计报表服务接口
 */
public interface StatisticsService {

    /**
     * 获取今日概览数据
     * @return 概览数据
     */
    Map<String, Object> getTodayOverview();

    /**
     * 获取入住率统计
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param dimension 维度（day/week/month/year）
     * @return 统计数据
     */
    List<Map<String, Object>> getOccupancyStatistics(String startDate, String endDate, String dimension);

    /**
     * 获取营收统计
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param dimension 维度
     * @return 统计数据
     */
    List<Map<String, Object>> getRevenueStatistics(String startDate, String endDate, String dimension);

    /**
     * 获取订单统计
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param dimension 维度
     * @return 统计数据
     */
    List<Map<String, Object>> getOrderStatistics(String startDate, String endDate, String dimension);

    /**
     * 获取房型统计
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 统计数据
     */
    List<Map<String, Object>> getRoomTypeStatistics(String startDate, String endDate);

    /**
     * 获取用户统计
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 统计数据
     */
    Map<String, Object> getUserStatistics(String startDate, String endDate);

    /**
     * 获取客房状态统计
     * @return 统计数据
     */
    Map<String, Object> getRoomStatusStatistics();

    /**
     * 获取月度报表数据
     * @param year 年份
     * @param month 月份
     * @return 报表数据
     */
    Map<String, Object> getMonthlyReport(int year, int month);

    /**
     * 获取年度报表数据
     * @param year 年份
     * @return 报表数据
     */
    Map<String, Object> getYearlyReport(int year);

    /**
     * 获取房型预订排行榜（近30天）
     * @return 排行数据
     */
    List<Map<String, Object>> getRoomTypeRanking();
}
