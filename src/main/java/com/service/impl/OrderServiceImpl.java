package com.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.entity.*;
import com.service.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 订单管理服务实现类
 */
@Service("orderService")
public class OrderServiceImpl implements OrderService {

    @Autowired
    private YonghuyuyueService yonghuyuyueService;

    @Autowired
    private HuiyuanyuyueService huiyuanyuyueService;

    @Autowired
    private YonghuruzhuService yonghuruzhuService;

    @Autowired
    private HuiyuanruzhuService huiyuanruzhuService;

    @Autowired
    private YonghutuifangService yonghutuifangService;

    @Autowired
    private HuiyuantuifangService huiyuantuifangService;

    @Autowired
    private BookingService bookingService;

    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private SimpleDateFormat dayFormat = new SimpleDateFormat("yyyy-MM-dd");

    @Override
    public Map<String, Object> queryOrders(Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> allOrders = new ArrayList<>();

        String type = (String) params.get("type");
        String status = (String) params.get("status");
        String keyword = (String) params.get("keyword");
        String startDate = (String) params.get("startDate");
        String endDate = (String) params.get("endDate");

        // 查询用户预约订单
        if (type == null || "yonghu".equals(type) || "all".equals(type)) {
            EntityWrapper<YonghuyuyueEntity> ew = new EntityWrapper<>();
            buildYonghuQueryWrapper(ew, status, keyword, startDate, endDate);
            ew.orderBy("addtime", false);
            List<YonghuyuyueEntity> yonghuOrders = yonghuyuyueService.selectList(ew);

            for (YonghuyuyueEntity order : yonghuOrders) {
                Map<String, Object> orderMap = convertYonghuOrderToMap(order);
                orderMap.put("userType", "用户");
                orderMap.put("userTypeCode", "yonghu");
                allOrders.add(orderMap);
            }
        }

        // 查询会员预约订单
        if (type == null || "huiyuan".equals(type) || "all".equals(type)) {
            EntityWrapper<HuiyuanyuyueEntity> ew = new EntityWrapper<>();
            buildHuiyuanQueryWrapper(ew, status, keyword, startDate, endDate);
            ew.orderBy("addtime", false);
            List<HuiyuanyuyueEntity> huiyuanOrders = huiyuanyuyueService.selectList(ew);

            for (HuiyuanyuyueEntity order : huiyuanOrders) {
                Map<String, Object> orderMap = convertHuiyuanOrderToMap(order);
                orderMap.put("userType", "会员");
                orderMap.put("userTypeCode", "huiyuan");
                allOrders.add(orderMap);
            }
        }

        // 按时间排序
        allOrders.sort((a, b) -> {
            Date dateA = (Date) a.get("addtime");
            Date dateB = (Date) b.get("addtime");
            if (dateA == null || dateB == null) return 0;
            return dateB.compareTo(dateA);
        });

        // 分页处理
        int page = params.get("page") != null ? Integer.parseInt(params.get("page").toString()) : 1;
        int limit = params.get("limit") != null ? Integer.parseInt(params.get("limit").toString()) : 10;
        int start = (page - 1) * limit;
        int end = Math.min(start + limit, allOrders.size());

        List<Map<String, Object>> pageData = start < allOrders.size() ? 
                allOrders.subList(start, end) : new ArrayList<>();

        result.put("list", pageData);
        result.put("total", allOrders.size());
        result.put("page", page);
        result.put("limit", limit);
        result.put("totalPage", (int) Math.ceil((double) allOrders.size() / limit));

        return result;
    }

    @Override
    public Map<String, Object> getOrderDetail(String orderNo, String type) {
        Map<String, Object> result = new HashMap<>();

        if ("yonghu".equals(type)) {
            EntityWrapper<YonghuyuyueEntity> ew = new EntityWrapper<>();
            ew.eq("yuyuebianhao", orderNo);
            YonghuyuyueEntity order = yonghuyuyueService.selectOne(ew);
            if (order != null) {
                result.put("order", convertYonghuOrderToMap(order));
                result.put("type", "yonghu");
                result.put("typeName", "用户订单");

                // 获取入住记录（使用dingdanbianhao字段关联）
                EntityWrapper<YonghuruzhuEntity> ruzhuEw = new EntityWrapper<>();
                ruzhuEw.eq("dingdanbianhao", orderNo);
                YonghuruzhuEntity ruzhu = yonghuruzhuService.selectOne(ruzhuEw);
                if (ruzhu != null) {
                    result.put("checkIn", convertRuzhuToMap(ruzhu));

                    // 获取退房记录
                    EntityWrapper<YonghutuifangEntity> tuifangEw = new EntityWrapper<>();
                    tuifangEw.eq("dingdanbianhao", ruzhu.getDingdanbianhao());
                    YonghutuifangEntity tuifang = yonghutuifangService.selectOne(tuifangEw);
                    if (tuifang != null) {
                        result.put("checkOut", convertTuifangToMap(tuifang));
                    }
                }
            }
        } else if ("huiyuan".equals(type)) {
            EntityWrapper<HuiyuanyuyueEntity> ew = new EntityWrapper<>();
            ew.eq("yuyuebianhao", orderNo);
            HuiyuanyuyueEntity order = huiyuanyuyueService.selectOne(ew);
            if (order != null) {
                result.put("order", convertHuiyuanOrderToMap(order));
                result.put("type", "huiyuan");
                result.put("typeName", "会员订单");

                // 获取入住记录
                EntityWrapper<HuiyuanruzhuEntity> ruzhuEw = new EntityWrapper<>();
                ruzhuEw.eq("dingdanbianhao", orderNo);
                HuiyuanruzhuEntity ruzhu = huiyuanruzhuService.selectOne(ruzhuEw);
                if (ruzhu != null) {
                    result.put("checkIn", convertHuiyuanRuzhuToMap(ruzhu));

                    // 获取退房记录
                    EntityWrapper<HuiyuantuifangEntity> tuifangEw = new EntityWrapper<>();
                    tuifangEw.eq("dingdanbianhao", ruzhu.getDingdanbianhao());
                    HuiyuantuifangEntity tuifang = huiyuantuifangService.selectOne(tuifangEw);
                    if (tuifang != null) {
                        result.put("checkOut", convertHuiyuanTuifangToMap(tuifang));
                    }
                }
            }
        }

        return result;
    }

    @Override
    public List<Map<String, Object>> getOrderStatusHistory(String orderNo) {
        List<Map<String, Object>> history = new ArrayList<>();

        // 先尝试查询用户订单
        EntityWrapper<YonghuyuyueEntity> ew1 = new EntityWrapper<>();
        ew1.eq("yuyuebianhao", orderNo);
        YonghuyuyueEntity yonghuOrder = yonghuyuyueService.selectOne(ew1);

        if (yonghuOrder != null) {
            buildYonghuStatusHistory(yonghuOrder, history);
        } else {
            // 查询会员订单
            EntityWrapper<HuiyuanyuyueEntity> ew2 = new EntityWrapper<>();
            ew2.eq("yuyuebianhao", orderNo);
            HuiyuanyuyueEntity huiyuanOrder = huiyuanyuyueService.selectOne(ew2);
            if (huiyuanOrder != null) {
                buildHuiyuanStatusHistory(huiyuanOrder, history);
            }
        }

        return history;
    }

    private void buildYonghuStatusHistory(YonghuyuyueEntity order, List<Map<String, Object>> history) {
        // 创建订单
        Map<String, Object> createStatus = new HashMap<>();
        createStatus.put("status", "已创建");
        createStatus.put("statusCode", "created");
        createStatus.put("time", order.getAddtime());
        createStatus.put("timeStr", order.getAddtime() != null ? dateFormat.format(order.getAddtime()) : "");
        createStatus.put("remark", "订单创建成功，等待审核");
        createStatus.put("icon", "el-icon-document-add");
        createStatus.put("color", "#409EFF");
        history.add(createStatus);

        // 审核状态
        if (order.getSfsh() != null && !"否".equals(order.getSfsh())) {
            Map<String, Object> auditStatus = new HashMap<>();
            boolean approved = "是".equals(order.getSfsh());
            auditStatus.put("status", approved ? "审核通过" : "审核拒绝");
            auditStatus.put("statusCode", approved ? "approved" : "rejected");
            auditStatus.put("time", order.getAddtime());
            auditStatus.put("timeStr", order.getAddtime() != null ? dateFormat.format(order.getAddtime()) : "");
            auditStatus.put("remark", StringUtils.isNotBlank(order.getShhf()) ? order.getShhf() : (approved ? "预约已通过审核" : "预约未通过审核"));
            auditStatus.put("icon", approved ? "el-icon-circle-check" : "el-icon-circle-close");
            auditStatus.put("color", approved ? "#67C23A" : "#F56C6C");
            history.add(auditStatus);
        }

        // 支付状态
        if ("已支付".equals(order.getIspay())) {
            Map<String, Object> payStatus = new HashMap<>();
            payStatus.put("status", "已支付");
            payStatus.put("statusCode", "paid");
            payStatus.put("time", order.getAddtime());
            payStatus.put("timeStr", order.getAddtime() != null ? dateFormat.format(order.getAddtime()) : "");
            payStatus.put("remark", "支付金额：¥" + (order.getJiage() != null ? order.getJiage() : "0"));
            payStatus.put("icon", "el-icon-wallet");
            payStatus.put("color", "#67C23A");
            history.add(payStatus);
        }

        // 入住记录
        EntityWrapper<YonghuruzhuEntity> ruzhuEw = new EntityWrapper<>();
        ruzhuEw.eq("dingdanbianhao", order.getYuyuebianhao());
        YonghuruzhuEntity ruzhu = yonghuruzhuService.selectOne(ruzhuEw);
        if (ruzhu != null) {
            Map<String, Object> checkInStatus = new HashMap<>();
            checkInStatus.put("status", "已入住");
            checkInStatus.put("statusCode", "checkedIn");
            checkInStatus.put("time", ruzhu.getRuzhushijian());
            checkInStatus.put("timeStr", ruzhu.getRuzhushijian() != null ? dateFormat.format(ruzhu.getRuzhushijian()) : "");
            checkInStatus.put("remark", "入住房间：" + ruzhu.getKefanghao());
            checkInStatus.put("icon", "el-icon-key");
            checkInStatus.put("color", "#E6A23C");
            history.add(checkInStatus);

            // 退房记录
            EntityWrapper<YonghutuifangEntity> tuifangEw = new EntityWrapper<>();
            tuifangEw.eq("dingdanbianhao", ruzhu.getDingdanbianhao());
            YonghutuifangEntity tuifang = yonghutuifangService.selectOne(tuifangEw);
            if (tuifang != null) {
                Map<String, Object> checkOutStatus = new HashMap<>();
                checkOutStatus.put("status", "已退房");
                checkOutStatus.put("statusCode", "checkedOut");
                checkOutStatus.put("time", tuifang.getTuifangshijian());
                checkOutStatus.put("timeStr", tuifang.getTuifangshijian() != null ? dateFormat.format(tuifang.getTuifangshijian()) : "");
                checkOutStatus.put("remark", "订单已完成");
                checkOutStatus.put("icon", "el-icon-circle-check");
                checkOutStatus.put("color", "#909399");
                history.add(checkOutStatus);
            }
        }
    }

    private void buildHuiyuanStatusHistory(HuiyuanyuyueEntity order, List<Map<String, Object>> history) {
        // 创建订单
        Map<String, Object> createStatus = new HashMap<>();
        createStatus.put("status", "已创建");
        createStatus.put("statusCode", "created");
        createStatus.put("time", order.getAddtime());
        createStatus.put("timeStr", order.getAddtime() != null ? dateFormat.format(order.getAddtime()) : "");
        createStatus.put("remark", "会员订单创建成功，等待审核");
        createStatus.put("icon", "el-icon-document-add");
        createStatus.put("color", "#409EFF");
        history.add(createStatus);

        // 审核状态
        if (order.getSfsh() != null && !"否".equals(order.getSfsh())) {
            Map<String, Object> auditStatus = new HashMap<>();
            boolean approved = "是".equals(order.getSfsh());
            auditStatus.put("status", approved ? "审核通过" : "审核拒绝");
            auditStatus.put("statusCode", approved ? "approved" : "rejected");
            auditStatus.put("time", order.getAddtime());
            auditStatus.put("timeStr", order.getAddtime() != null ? dateFormat.format(order.getAddtime()) : "");
            auditStatus.put("remark", StringUtils.isNotBlank(order.getShhf()) ? order.getShhf() : (approved ? "预约已通过审核" : "预约未通过审核"));
            auditStatus.put("icon", approved ? "el-icon-circle-check" : "el-icon-circle-close");
            auditStatus.put("color", approved ? "#67C23A" : "#F56C6C");
            history.add(auditStatus);
        }

        // 支付状态
        if ("已支付".equals(order.getIspay())) {
            Map<String, Object> payStatus = new HashMap<>();
            payStatus.put("status", "已支付");
            payStatus.put("statusCode", "paid");
            payStatus.put("time", order.getAddtime());
            payStatus.put("timeStr", order.getAddtime() != null ? dateFormat.format(order.getAddtime()) : "");
            payStatus.put("remark", "会员优惠价支付：¥" + (order.getJiage() != null ? order.getJiage() : "0"));
            payStatus.put("icon", "el-icon-wallet");
            payStatus.put("color", "#67C23A");
            history.add(payStatus);
        }

        // 入住记录
        EntityWrapper<HuiyuanruzhuEntity> ruzhuEw = new EntityWrapper<>();
        ruzhuEw.eq("dingdanbianhao", order.getYuyuebianhao());
        HuiyuanruzhuEntity ruzhu = huiyuanruzhuService.selectOne(ruzhuEw);
        if (ruzhu != null) {
            Map<String, Object> checkInStatus = new HashMap<>();
            checkInStatus.put("status", "已入住");
            checkInStatus.put("statusCode", "checkedIn");
            checkInStatus.put("time", ruzhu.getRuzhushijian());
            checkInStatus.put("timeStr", ruzhu.getRuzhushijian() != null ? dateFormat.format(ruzhu.getRuzhushijian()) : "");
            checkInStatus.put("remark", "入住房间：" + ruzhu.getKefanghao());
            checkInStatus.put("icon", "el-icon-key");
            checkInStatus.put("color", "#E6A23C");
            history.add(checkInStatus);

            // 退房记录
            EntityWrapper<HuiyuantuifangEntity> tuifangEw = new EntityWrapper<>();
            tuifangEw.eq("dingdanbianhao", ruzhu.getDingdanbianhao());
            HuiyuantuifangEntity tuifang = huiyuantuifangService.selectOne(tuifangEw);
            if (tuifang != null) {
                Map<String, Object> checkOutStatus = new HashMap<>();
                checkOutStatus.put("status", "已退房");
                checkOutStatus.put("statusCode", "checkedOut");
                checkOutStatus.put("time", tuifang.getTuifangshijian());
                checkOutStatus.put("timeStr", tuifang.getTuifangshijian() != null ? dateFormat.format(tuifang.getTuifangshijian()) : "");
                checkOutStatus.put("remark", "会员订单已完成");
                checkOutStatus.put("icon", "el-icon-circle-check");
                checkOutStatus.put("color", "#909399");
                history.add(checkOutStatus);
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> batchAuditOrders(List<Long> orderIds, String type, String sfsh, String shhf) {
        Map<String, Object> result = new HashMap<>();
        int successCount = 0;
        int failCount = 0;
        List<String> failReasons = new ArrayList<>();

        for (Long orderId : orderIds) {
            try {
                if ("yonghu".equals(type)) {
                    bookingService.auditYonghuBooking(orderId, sfsh, shhf);
                } else if ("huiyuan".equals(type)) {
                    bookingService.auditHuiyuanBooking(orderId, sfsh, shhf);
                }
                successCount++;
            } catch (Exception e) {
                failCount++;
                failReasons.add("订单ID " + orderId + ": " + e.getMessage());
            }
        }

        result.put("successCount", successCount);
        result.put("failCount", failCount);
        result.put("failReasons", failReasons);
        result.put("total", orderIds.size());

        return result;
    }

    @Override
    public List<Map<String, Object>> exportOrders(Map<String, Object> params) {
        params.put("page", 1);
        params.put("limit", 10000);
        Map<String, Object> queryResult = queryOrders(params);
        return (List<Map<String, Object>>) queryResult.get("list");
    }

    @Override
    public Map<String, Object> getOrderOverview() {
        Map<String, Object> result = new HashMap<>();
        String today = dayFormat.format(new Date());

        // 今日订单数
        EntityWrapper<YonghuyuyueEntity> ew1 = new EntityWrapper<>();
        ew1.ge("addtime", today + " 00:00:00");
        ew1.le("addtime", today + " 23:59:59");
        int todayYonghuOrders = yonghuyuyueService.selectCount(ew1);

        EntityWrapper<HuiyuanyuyueEntity> ew2 = new EntityWrapper<>();
        ew2.ge("addtime", today + " 00:00:00");
        ew2.le("addtime", today + " 23:59:59");
        int todayHuiyuanOrders = huiyuanyuyueService.selectCount(ew2);

        result.put("todayOrders", todayYonghuOrders + todayHuiyuanOrders);

        // 待审核订单数
        EntityWrapper<YonghuyuyueEntity> pendingEw1 = new EntityWrapper<>();
        pendingEw1.eq("sfsh", "否");
        int pendingYonghu = yonghuyuyueService.selectCount(pendingEw1);

        EntityWrapper<HuiyuanyuyueEntity> pendingEw2 = new EntityWrapper<>();
        pendingEw2.eq("sfsh", "否");
        int pendingHuiyuan = huiyuanyuyueService.selectCount(pendingEw2);

        result.put("pendingOrders", pendingYonghu + pendingHuiyuan);

        // 待支付订单数
        EntityWrapper<YonghuyuyueEntity> unpaidEw1 = new EntityWrapper<>();
        unpaidEw1.eq("sfsh", "是");
        unpaidEw1.eq("ispay", "未支付");
        int unpaidYonghu = yonghuyuyueService.selectCount(unpaidEw1);

        EntityWrapper<HuiyuanyuyueEntity> unpaidEw2 = new EntityWrapper<>();
        unpaidEw2.eq("sfsh", "是");
        unpaidEw2.eq("ispay", "未支付");
        int unpaidHuiyuan = huiyuanyuyueService.selectCount(unpaidEw2);

        result.put("unpaidOrders", unpaidYonghu + unpaidHuiyuan);

        // 总订单数
        int totalYonghu = yonghuyuyueService.selectCount(new EntityWrapper<>());
        int totalHuiyuan = huiyuanyuyueService.selectCount(new EntityWrapper<>());
        result.put("totalOrders", totalYonghu + totalHuiyuan);

        // 今日营收
        double todayRevenue = calculateTodayRevenue();
        result.put("todayRevenue", todayRevenue);

        return result;
    }

    @Override
    public Map<String, Object> getMyOrders(Long userId, String userType, Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> orders = new ArrayList<>();

        int page = params.get("page") != null ? Integer.parseInt(params.get("page").toString()) : 1;
        int limit = params.get("limit") != null ? Integer.parseInt(params.get("limit").toString()) : 10;

        if ("yonghu".equals(userType)) {
            EntityWrapper<YonghuyuyueEntity> ew = new EntityWrapper<>();
            ew.eq("yonghuid", userId);
            ew.orderBy("addtime", false);
            List<YonghuyuyueEntity> list = yonghuyuyueService.selectList(ew);
            for (YonghuyuyueEntity order : list) {
                orders.add(convertYonghuOrderToMap(order));
            }
        } else if ("huiyuan".equals(userType)) {
            EntityWrapper<HuiyuanyuyueEntity> ew = new EntityWrapper<>();
            ew.eq("huiyuanid", userId);
            ew.orderBy("addtime", false);
            List<HuiyuanyuyueEntity> list = huiyuanyuyueService.selectList(ew);
            for (HuiyuanyuyueEntity order : list) {
                orders.add(convertHuiyuanOrderToMap(order));
            }
        }

        int start = (page - 1) * limit;
        int end = Math.min(start + limit, orders.size());
        List<Map<String, Object>> pageData = start < orders.size() ? 
                orders.subList(start, end) : new ArrayList<>();

        result.put("list", pageData);
        result.put("total", orders.size());
        result.put("page", page);
        result.put("limit", limit);

        return result;
    }

    // ==================== 辅助方法 ====================

    private double calculateTodayRevenue() {
        String today = dayFormat.format(new Date());
        double revenue = 0;

        // 用户退房收入
        EntityWrapper<YonghutuifangEntity> ew1 = new EntityWrapper<>();
        ew1.ge("tuifangshijian", today + " 00:00:00");
        ew1.le("tuifangshijian", today + " 23:59:59");
        List<YonghutuifangEntity> yonghuTuifang = yonghutuifangService.selectList(ew1);
        for (YonghutuifangEntity tf : yonghuTuifang) {
            if (tf.getZongjine() != null) {
                revenue += tf.getZongjine();
            }
        }

        // 会员退房收入
        EntityWrapper<HuiyuantuifangEntity> ew2 = new EntityWrapper<>();
        ew2.ge("tuifangshijian", today + " 00:00:00");
        ew2.le("tuifangshijian", today + " 23:59:59");
        List<HuiyuantuifangEntity> huiyuanTuifang = huiyuantuifangService.selectList(ew2);
        for (HuiyuantuifangEntity tf : huiyuanTuifang) {
            if (tf.getZongjine() != null) {
                revenue += tf.getZongjine();
            }
        }

        return revenue;
    }

    private void buildYonghuQueryWrapper(EntityWrapper<YonghuyuyueEntity> ew, String status, 
                                         String keyword, String startDate, String endDate) {
        if (StringUtils.isNotBlank(status)) {
            switch (status) {
                case "pending":
                    ew.eq("sfsh", "否");
                    break;
                case "approved":
                    ew.eq("sfsh", "是");
                    break;
                case "paid":
                    ew.eq("ispay", "已支付");
                    break;
                case "unpaid":
                    ew.eq("sfsh", "是");
                    ew.eq("ispay", "未支付");
                    break;
                case "cancelled":
                    ew.eq("yuyuezhuangtai", "cancelled");
                    break;
            }
        }

        if (StringUtils.isNotBlank(keyword)) {
            ew.andNew()
              .like("yuyuebianhao", keyword)
              .or().like("kefanghao", keyword)
              .or().like("xingming", keyword)
              .or().like("shouji", keyword);
        }

        if (StringUtils.isNotBlank(startDate)) {
            ew.ge("addtime", startDate + " 00:00:00");
        }
        if (StringUtils.isNotBlank(endDate)) {
            ew.le("addtime", endDate + " 23:59:59");
        }
    }

    private void buildHuiyuanQueryWrapper(EntityWrapper<HuiyuanyuyueEntity> ew, String status, 
                                          String keyword, String startDate, String endDate) {
        if (StringUtils.isNotBlank(status)) {
            switch (status) {
                case "pending":
                    ew.eq("sfsh", "否");
                    break;
                case "approved":
                    ew.eq("sfsh", "是");
                    break;
                case "paid":
                    ew.eq("ispay", "已支付");
                    break;
                case "unpaid":
                    ew.eq("sfsh", "是");
                    ew.eq("ispay", "未支付");
                    break;
                case "cancelled":
                    ew.eq("yuyuezhuangtai", "cancelled");
                    break;
            }
        }

        if (StringUtils.isNotBlank(keyword)) {
            ew.andNew()
              .like("yuyuebianhao", keyword)
              .or().like("kefanghao", keyword)
              .or().like("xingming", keyword)
              .or().like("shouji", keyword);
        }

        if (StringUtils.isNotBlank(startDate)) {
            ew.ge("addtime", startDate + " 00:00:00");
        }
        if (StringUtils.isNotBlank(endDate)) {
            ew.le("addtime", endDate + " 23:59:59");
        }
    }

    private Map<String, Object> convertYonghuOrderToMap(YonghuyuyueEntity order) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", order.getId());
        map.put("yuyuebianhao", order.getYuyuebianhao());
        map.put("kefanghao", order.getKefanghao());
        map.put("kefangleixing", "");
        map.put("ruzhushijian", order.getRuzhushijian());
        map.put("ruzhushijianStr", order.getRuzhushijian() != null ? dateFormat.format(order.getRuzhushijian()) : "");
        map.put("tianshu", order.getTianshu());
        map.put("jiage", order.getJiage());
        map.put("zhanghao", order.getZhanghao());
        map.put("xingming", order.getXingming());
        map.put("shouji", order.getShouji());
        map.put("sfsh", order.getSfsh());
        map.put("shhf", order.getShhf());
        map.put("ispay", order.getIspay());
        map.put("yuyuezhuangtai", order.getYuyuezhuangtai());
        map.put("addtime", order.getAddtime());
        map.put("addtimeStr", order.getAddtime() != null ? dateFormat.format(order.getAddtime()) : "");
        map.put("orderType", "yonghu");
        
        // 计算订单状态
        String statusText = calculateOrderStatus(order.getSfsh(), order.getIspay(), order.getYuyuezhuangtai());
        map.put("statusText", statusText);
        
        return map;
    }

    private Map<String, Object> convertHuiyuanOrderToMap(HuiyuanyuyueEntity order) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", order.getId());
        map.put("yuyuebianhao", order.getYuyuebianhao());
        map.put("kefanghao", order.getKefanghao());
        map.put("kefangleixing", "");
        map.put("ruzhushijian", order.getRuzhushijian());
        map.put("ruzhushijianStr", order.getRuzhushijian() != null ? dateFormat.format(order.getRuzhushijian()) : "");
        map.put("tianshu", order.getTianshu());
        map.put("jiage", order.getJiage());
        map.put("zhanghao", order.getZhanghao());
        map.put("xingming", order.getXingming());
        map.put("shouji", order.getShouji());
        map.put("sfsh", order.getSfsh());
        map.put("shhf", order.getShhf());
        map.put("ispay", order.getIspay());
        map.put("yuyuezhuangtai", order.getYuyuezhuangtai());
        map.put("addtime", order.getAddtime());
        map.put("addtimeStr", order.getAddtime() != null ? dateFormat.format(order.getAddtime()) : "");
        map.put("orderType", "huiyuan");
        
        // 计算订单状态
        String statusText = calculateOrderStatus(order.getSfsh(), order.getIspay(), order.getYuyuezhuangtai());
        map.put("statusText", statusText);
        
        return map;
    }

    private String calculateOrderStatus(String sfsh, String ispay, String yuyuezhuangtai) {
        if ("cancelled".equals(yuyuezhuangtai)) {
            return "已取消";
        }
        if ("否".equals(sfsh)) {
            return "待审核";
        }
        if ("是".equals(sfsh) && "未支付".equals(ispay)) {
            return "待支付";
        }
        if ("是".equals(sfsh) && "已支付".equals(ispay)) {
            return "已支付";
        }
        return "未知";
    }

    private Map<String, Object> convertRuzhuToMap(YonghuruzhuEntity ruzhu) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", ruzhu.getId());
        map.put("dingdanbianhao", ruzhu.getDingdanbianhao());
        map.put("kefanghao", ruzhu.getKefanghao());
        map.put("ruzhushijian", ruzhu.getRuzhushijian());
        map.put("ruzhushijianStr", ruzhu.getRuzhushijian() != null ? dateFormat.format(ruzhu.getRuzhushijian()) : "");
        map.put("yulifangshijian", ruzhu.getYulifangshijian());
        map.put("yulifangshijianStr", ruzhu.getYulifangshijian() != null ? dateFormat.format(ruzhu.getYulifangshijian()) : "");
        map.put("kefangzhuangtai", ruzhu.getKefangzhuangtai());
        return map;
    }

    private Map<String, Object> convertHuiyuanRuzhuToMap(HuiyuanruzhuEntity ruzhu) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", ruzhu.getId());
        map.put("dingdanbianhao", ruzhu.getDingdanbianhao());
        map.put("kefanghao", ruzhu.getKefanghao());
        map.put("ruzhushijian", ruzhu.getRuzhushijian());
        map.put("ruzhushijianStr", ruzhu.getRuzhushijian() != null ? dateFormat.format(ruzhu.getRuzhushijian()) : "");
        map.put("yulifangshijian", ruzhu.getYulifangshijian());
        map.put("yulifangshijianStr", ruzhu.getYulifangshijian() != null ? dateFormat.format(ruzhu.getYulifangshijian()) : "");
        map.put("kefangzhuangtai", ruzhu.getKefangzhuangtai());
        return map;
    }

    private Map<String, Object> convertTuifangToMap(YonghutuifangEntity tuifang) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", tuifang.getId());
        map.put("tuifangshijian", tuifang.getTuifangshijian());
        map.put("tuifangshijianStr", tuifang.getTuifangshijian() != null ? dateFormat.format(tuifang.getTuifangshijian()) : "");
        map.put("zongjine", tuifang.getZongjine());
        return map;
    }

    private Map<String, Object> convertHuiyuanTuifangToMap(HuiyuantuifangEntity tuifang) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", tuifang.getId());
        map.put("tuifangshijian", tuifang.getTuifangshijian());
        map.put("tuifangshijianStr", tuifang.getTuifangshijian() != null ? dateFormat.format(tuifang.getTuifangshijian()) : "");
        map.put("zongjine", tuifang.getZongjine());
        return map;
    }
}
