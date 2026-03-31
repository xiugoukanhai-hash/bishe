package com.controller;

import java.util.*;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.service.OrderService;
import com.utils.R;

/**
 * 订单综合管理控制器
 * 提供订单查询、审核、导出等功能
 */
@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    /**
     * 综合查询订单列表
     * 支持按类型（用户/会员）、状态、关键词、日期范围筛选
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params, HttpServletRequest request) {
        // 权限验证：只有管理员和前台人员可以查看所有订单
        String tableName = (String) request.getSession().getAttribute("tableName");
        if (!"users".equals(tableName) && !"qiantairenyuan".equals(tableName)) {
            return R.error("无权限访问订单列表");
        }
        
        Map<String, Object> data = orderService.queryOrders(params);
        return R.ok().put("data", data);
    }

    /**
     * 获取订单详情
     */
    @RequestMapping("/detail")
    public R detail(@RequestParam String orderNo, @RequestParam String type, HttpServletRequest request) {
        String tableName = (String) request.getSession().getAttribute("tableName");
        if (!"users".equals(tableName) && !"qiantairenyuan".equals(tableName)) {
            return R.error("无权限查看订单详情");
        }
        
        Map<String, Object> data = orderService.getOrderDetail(orderNo, type);
        if (data.isEmpty()) {
            return R.error("订单不存在");
        }
        return R.ok().put("data", data);
    }

    /**
     * 获取订单状态流转历史
     */
    @RequestMapping("/statusHistory/{orderNo}")
    public R statusHistory(@PathVariable("orderNo") String orderNo, HttpServletRequest request) {
        String tableName = (String) request.getSession().getAttribute("tableName");
        if (!"users".equals(tableName) && !"qiantairenyuan".equals(tableName)) {
            return R.error("无权限查看订单历史");
        }
        
        List<Map<String, Object>> data = orderService.getOrderStatusHistory(orderNo);
        return R.ok().put("data", data);
    }

    /**
     * 批量审核订单
     */
    @RequestMapping("/batchAudit")
    public R batchAudit(@RequestBody Map<String, Object> params, HttpServletRequest request) {
        // 权限验证：只有管理员可以批量审核
        String tableName = (String) request.getSession().getAttribute("tableName");
        if (!"users".equals(tableName)) {
            return R.error("无权限执行批量审核，仅管理员可操作");
        }
        
        Object orderIdsObj = params.get("orderIds");
        List<Long> orderIds = new ArrayList<>();
        if (orderIdsObj instanceof List) {
            for (Object id : (List<?>) orderIdsObj) {
                if (id instanceof Number) {
                    orderIds.add(((Number) id).longValue());
                } else if (id instanceof String) {
                    orderIds.add(Long.parseLong((String) id));
                }
            }
        }
        
        if (orderIds.isEmpty()) {
            return R.error("请选择要审核的订单");
        }
        
        String type = (String) params.get("type");
        String sfsh = (String) params.get("sfsh");
        String shhf = (String) params.get("shhf");

        if (type == null || sfsh == null) {
            return R.error("参数不完整");
        }

        Map<String, Object> result = orderService.batchAuditOrders(orderIds, type, sfsh, shhf);
        return R.ok().put("data", result);
    }

    /**
     * 导出订单数据
     */
    @RequestMapping("/export")
    public R export(@RequestParam Map<String, Object> params, HttpServletRequest request) {
        String tableName = (String) request.getSession().getAttribute("tableName");
        if (!"users".equals(tableName)) {
            return R.error("无权限导出订单，仅管理员可操作");
        }
        
        List<Map<String, Object>> data = orderService.exportOrders(params);
        return R.ok().put("data", data);
    }

    /**
     * 获取订单统计概览
     */
    @RequestMapping("/overview")
    public R overview(HttpServletRequest request) {
        String tableName = (String) request.getSession().getAttribute("tableName");
        if (!"users".equals(tableName) && !"qiantairenyuan".equals(tableName)) {
            return R.error("无权限查看订单统计");
        }
        
        Map<String, Object> data = orderService.getOrderOverview();
        return R.ok().put("data", data);
    }

    /**
     * 获取我的订单（前端用户中心使用）
     */
    @RequestMapping("/myOrders")
    public R myOrders(@RequestParam Map<String, Object> params, HttpServletRequest request) {
        Long userId = (Long) request.getSession().getAttribute("userId");
        String tableName = (String) request.getSession().getAttribute("tableName");
        
        if (userId == null) {
            return R.error("请先登录");
        }
        
        // 只有用户和会员可以查看自己的订单
        if (!"yonghu".equals(tableName) && !"huiyuan".equals(tableName)) {
            return R.error("仅用户和会员可查看订单");
        }
        
        Map<String, Object> data = orderService.getMyOrders(userId, tableName, params);
        return R.ok().put("data", data);
    }

    /**
     * 获取订单统计数据（用于管理员工作台）
     */
    @RequestMapping("/stats")
    public R stats(HttpServletRequest request) {
        String tableName = (String) request.getSession().getAttribute("tableName");
        if (!"users".equals(tableName) && !"qiantairenyuan".equals(tableName)) {
            return R.error("无权限查看统计数据");
        }
        
        Map<String, Object> data = orderService.getOrderOverview();
        return R.ok().put("data", data);
    }
}
