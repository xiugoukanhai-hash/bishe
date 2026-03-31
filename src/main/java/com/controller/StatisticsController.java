package com.controller;

import java.util.*;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.service.StatisticsService;
import com.utils.R;

/**
 * 统计报表控制器
 * 权限说明：仅管理员和前台人员可访问统计报表
 */
@RestController
@RequestMapping("/statistics")
public class StatisticsController {

    @Autowired
    private StatisticsService statisticsService;

    /**
     * 验证是否有权限访问统计报表
     */
    private boolean hasPermission(HttpServletRequest request) {
        Object tableNameObj = request.getSession().getAttribute("tableName");
        if (tableNameObj == null) return false;
        String tableName = tableNameObj.toString();
        return "users".equals(tableName) || "qiantairenyuan".equals(tableName);
    }

    /**
     * 获取今日概览数据
     */
    @RequestMapping("/todayOverview")
    public R todayOverview(HttpServletRequest request) {
        if (!hasPermission(request)) {
            return R.error("无权限访问统计数据");
        }
        Map<String, Object> data = statisticsService.getTodayOverview();
        return R.ok().put("data", data);
    }

    /**
     * 获取入住率统计
     */
    @RequestMapping("/occupancy")
    public R occupancy(@RequestParam(required = false) String startDate,
                       @RequestParam(required = false) String endDate,
                       @RequestParam(required = false) Integer days,
                       @RequestParam(defaultValue = "day") String dimension,
                       HttpServletRequest request) {
        if (!hasPermission(request)) {
            return R.error("无权限访问统计数据");
        }
        
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
        java.util.Date now = new java.util.Date();
        
        if (startDate == null || endDate == null) {
            int d = (days != null && days > 0) ? days : 7;
            endDate = sdf.format(now);
            java.util.Calendar cal = java.util.Calendar.getInstance();
            cal.setTime(now);
            cal.add(java.util.Calendar.DAY_OF_MONTH, -(d - 1));
            startDate = sdf.format(cal.getTime());
        }
        
        List<Map<String, Object>> data = statisticsService.getOccupancyStatistics(startDate, endDate, dimension);
        return R.ok().put("data", data);
    }

    /**
     * 获取营收统计
     * 支持两种调用方式：
     * 1. 传递 startDate 和 endDate
     * 2. 传递 days 参数（默认7天）
     */
    @RequestMapping("/revenue")
    public R revenue(@RequestParam(required = false) String startDate,
                     @RequestParam(required = false) String endDate,
                     @RequestParam(required = false) Integer days,
                     @RequestParam(defaultValue = "day") String dimension,
                     HttpServletRequest request) {
        if (!hasPermission(request)) {
            return R.error("无权限访问统计数据");
        }
        
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
        java.util.Date now = new java.util.Date();
        
        if (startDate == null || endDate == null) {
            int d = (days != null && days > 0) ? days : 7;
            endDate = sdf.format(now);
            java.util.Calendar cal = java.util.Calendar.getInstance();
            cal.setTime(now);
            cal.add(java.util.Calendar.DAY_OF_MONTH, -(d - 1));
            startDate = sdf.format(cal.getTime());
        }
        
        List<Map<String, Object>> data = statisticsService.getRevenueStatistics(startDate, endDate, dimension);
        return R.ok().put("data", data);
    }

    /**
     * 获取订单统计
     */
    @RequestMapping("/orders")
    public R orders(@RequestParam(required = false) String startDate,
                    @RequestParam(required = false) String endDate,
                    @RequestParam(required = false) Integer days,
                    @RequestParam(defaultValue = "day") String dimension,
                    HttpServletRequest request) {
        if (!hasPermission(request)) {
            return R.error("无权限访问统计数据");
        }
        
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
        java.util.Date now = new java.util.Date();
        
        if (startDate == null || endDate == null) {
            int d = (days != null && days > 0) ? days : 7;
            endDate = sdf.format(now);
            java.util.Calendar cal = java.util.Calendar.getInstance();
            cal.setTime(now);
            cal.add(java.util.Calendar.DAY_OF_MONTH, -(d - 1));
            startDate = sdf.format(cal.getTime());
        }
        
        List<Map<String, Object>> data = statisticsService.getOrderStatistics(startDate, endDate, dimension);
        return R.ok().put("data", data);
    }

    /**
     * 获取房型统计
     */
    @RequestMapping("/roomType")
    public R roomType(@RequestParam(required = false) String startDate,
                      @RequestParam(required = false) String endDate,
                      @RequestParam(required = false) Integer days,
                      HttpServletRequest request) {
        if (!hasPermission(request)) {
            return R.error("无权限访问统计数据");
        }
        
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
        java.util.Date now = new java.util.Date();
        
        if (startDate == null || endDate == null) {
            int d = (days != null && days > 0) ? days : 30;
            endDate = sdf.format(now);
            java.util.Calendar cal = java.util.Calendar.getInstance();
            cal.setTime(now);
            cal.add(java.util.Calendar.DAY_OF_MONTH, -(d - 1));
            startDate = sdf.format(cal.getTime());
        }
        
        List<Map<String, Object>> data = statisticsService.getRoomTypeStatistics(startDate, endDate);
        return R.ok().put("data", data);
    }

    /**
     * 获取用户统计
     */
    @RequestMapping("/users")
    public R users(@RequestParam(required = false) String startDate,
                   @RequestParam(required = false) String endDate,
                   @RequestParam(required = false) Integer days,
                   HttpServletRequest request) {
        if (!hasPermission(request)) {
            return R.error("无权限访问统计数据");
        }
        
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
        java.util.Date now = new java.util.Date();
        
        if (startDate == null || endDate == null) {
            int d = (days != null && days > 0) ? days : 30;
            endDate = sdf.format(now);
            java.util.Calendar cal = java.util.Calendar.getInstance();
            cal.setTime(now);
            cal.add(java.util.Calendar.DAY_OF_MONTH, -(d - 1));
            startDate = sdf.format(cal.getTime());
        }
        
        Map<String, Object> data = statisticsService.getUserStatistics(startDate, endDate);
        return R.ok().put("data", data);
    }

    /**
     * 获取客房状态统计
     */
    @RequestMapping("/roomStatus")
    public R roomStatus(HttpServletRequest request) {
        if (!hasPermission(request)) {
            return R.error("无权限访问统计数据");
        }
        Map<String, Object> data = statisticsService.getRoomStatusStatistics();
        return R.ok().put("data", data);
    }

    /**
     * 获取房型预订排行榜
     */
    @RequestMapping("/roomTypeRanking")
    public R roomTypeRanking(HttpServletRequest request) {
        if (!hasPermission(request)) {
            return R.error("无权限访问统计数据");
        }
        List<Map<String, Object>> data = statisticsService.getRoomTypeRanking();
        return R.ok().put("data", data);
    }

    /**
     * 获取月度报表（仅管理员可访问）
     */
    @RequestMapping("/monthlyReport")
    public R monthlyReport(@RequestParam int year, @RequestParam int month, HttpServletRequest request) {
        Object tableNameObj = request.getSession().getAttribute("tableName");
        if (tableNameObj == null || !"users".equals(tableNameObj.toString())) {
            return R.error("无权限访问报表数据，仅管理员可访问");
        }
        Map<String, Object> data = statisticsService.getMonthlyReport(year, month);
        return R.ok().put("data", data);
    }

    /**
     * 获取年度报表（仅管理员可访问）
     */
    @RequestMapping("/yearlyReport")
    public R yearlyReport(@RequestParam int year, HttpServletRequest request) {
        Object tableNameObj = request.getSession().getAttribute("tableName");
        if (tableNameObj == null || !"users".equals(tableNameObj.toString())) {
            return R.error("无权限访问报表数据，仅管理员可访问");
        }
        Map<String, Object> data = statisticsService.getYearlyReport(year);
        return R.ok().put("data", data);
    }
}
