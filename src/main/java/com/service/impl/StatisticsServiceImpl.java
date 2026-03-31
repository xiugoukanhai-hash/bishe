package com.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.entity.*;
import com.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 统计报表服务实现类
 */
@Service("statisticsService")
public class StatisticsServiceImpl implements StatisticsService {

    @Autowired
    private KefangxinxiService kefangxinxiService;

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
    private YonghuService yonghuService;

    @Autowired
    private HuiyuanService huiyuanService;

    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    @Override
    public Map<String, Object> getTodayOverview() {
        Map<String, Object> result = new HashMap<>();
        String today = dateFormat.format(new Date());

        // 今日入住数
        int todayCheckIn = countTodayCheckIn(today);
        result.put("todayCheckIn", todayCheckIn);

        // 今日退房数
        int todayCheckOut = countTodayCheckOut(today);
        result.put("todayCheckOut", todayCheckOut);

        // 今日预约数
        int todayBooking = countTodayBooking(today);
        result.put("todayBooking", todayBooking);

        // 今日营收
        double todayRevenue = calculateTodayRevenue(today);
        result.put("todayRevenue", todayRevenue);

        // 当前入住率
        double occupancyRate = calculateCurrentOccupancyRate();
        result.put("occupancyRate", occupancyRate);

        // 待处理订单数（待审核的预约）
        int pendingOrders = countPendingOrders();
        result.put("pendingOrders", pendingOrders);

        // 总房间数
        int totalRooms = kefangxinxiService.selectCount(new EntityWrapper<>());
        result.put("totalRooms", totalRooms);

        // 空闲房间数
        EntityWrapper<KefangxinxiEntity> freeWrapper = new EntityWrapper<>();
        freeWrapper.eq("kefangzhuangtai", "空闲");
        int freeRooms = kefangxinxiService.selectCount(freeWrapper);
        result.put("freeRooms", freeRooms);

        // 已入住房间数
        EntityWrapper<KefangxinxiEntity> occupiedWrapper = new EntityWrapper<>();
        occupiedWrapper.eq("kefangzhuangtai", "已入住");
        int occupiedRooms = kefangxinxiService.selectCount(occupiedWrapper);
        result.put("occupiedRooms", occupiedRooms);

        // 待清扫房间数
        EntityWrapper<KefangxinxiEntity> cleanWrapper = new EntityWrapper<>();
        cleanWrapper.eq("kefangzhuangtai", "待清扫");
        int needCleanRooms = kefangxinxiService.selectCount(cleanWrapper);
        result.put("needCleanRooms", needCleanRooms);

        return result;
    }

    @Override
    public List<Map<String, Object>> getOccupancyStatistics(String startDate, String endDate, String dimension) {
        List<Map<String, Object>> result = new ArrayList<>();

        int totalRooms = kefangxinxiService.selectCount(new EntityWrapper<>());
        if (totalRooms == 0) {
            return result;
        }

        List<String> dateList = generateDateList(startDate, endDate, dimension);

        for (String date : dateList) {
            Map<String, Object> item = new HashMap<>();
            item.put("date", date);

            int occupiedRooms = countOccupiedRoomsByDate(date, dimension);
            double rate = (double) occupiedRooms / totalRooms * 100;

            item.put("occupiedRooms", occupiedRooms);
            item.put("totalRooms", totalRooms);
            item.put("occupancyRate", Math.round(rate * 100) / 100.0);

            result.add(item);
        }

        return result;
    }

    @Override
    public List<Map<String, Object>> getRevenueStatistics(String startDate, String endDate, String dimension) {
        List<Map<String, Object>> result = new ArrayList<>();

        List<String> dateList = generateDateList(startDate, endDate, dimension);

        for (String date : dateList) {
            Map<String, Object> item = new HashMap<>();
            item.put("date", date);

            double yonghuRevenue = calculateYonghuRevenueByDate(date, dimension);
            double huiyuanRevenue = calculateHuiyuanRevenueByDate(date, dimension);
            double totalRevenue = yonghuRevenue + huiyuanRevenue;

            item.put("revenue", totalRevenue);
            item.put("yonghuRevenue", yonghuRevenue);
            item.put("huiyuanRevenue", huiyuanRevenue);

            result.add(item);
        }

        return result;
    }

    @Override
    public List<Map<String, Object>> getOrderStatistics(String startDate, String endDate, String dimension) {
        List<Map<String, Object>> result = new ArrayList<>();

        List<String> dateList = generateDateList(startDate, endDate, dimension);

        for (String date : dateList) {
            Map<String, Object> item = new HashMap<>();
            item.put("date", date);

            int yonghuOrders = countYonghuOrdersByDate(date, dimension);
            int huiyuanOrders = countHuiyuanOrdersByDate(date, dimension);

            item.put("totalOrders", yonghuOrders + huiyuanOrders);
            item.put("yonghuOrders", yonghuOrders);
            item.put("huiyuanOrders", huiyuanOrders);

            result.add(item);
        }

        return result;
    }

    @Override
    public List<Map<String, Object>> getRoomTypeStatistics(String startDate, String endDate) {
        List<Map<String, Object>> result = new ArrayList<>();

        EntityWrapper<KefangxinxiEntity> ew = new EntityWrapper<>();
        ew.setSqlSelect("DISTINCT kefangleixing");
        List<KefangxinxiEntity> types = kefangxinxiService.selectList(ew);

        for (KefangxinxiEntity type : types) {
            if (type.getKefangleixing() == null) continue;

            Map<String, Object> item = new HashMap<>();
            item.put("roomType", type.getKefangleixing());

            EntityWrapper<KefangxinxiEntity> countWrapper = new EntityWrapper<>();
            countWrapper.eq("kefangleixing", type.getKefangleixing());
            int count = kefangxinxiService.selectCount(countWrapper);
            item.put("totalCount", count);

            int bookingCount = countBookingsByRoomType(type.getKefangleixing(), startDate, endDate);
            item.put("bookingCount", bookingCount);

            double revenue = calculateRevenueByRoomType(type.getKefangleixing(), startDate, endDate);
            item.put("revenue", revenue);

            result.add(item);
        }

        return result;
    }

    @Override
    public Map<String, Object> getUserStatistics(String startDate, String endDate) {
        Map<String, Object> result = new HashMap<>();

        int totalYonghu = yonghuService.selectCount(new EntityWrapper<>());
        int totalHuiyuan = huiyuanService.selectCount(new EntityWrapper<>());
        result.put("totalYonghu", totalYonghu);
        result.put("totalHuiyuan", totalHuiyuan);
        result.put("totalUsers", totalYonghu + totalHuiyuan);

        int newYonghu = countNewUsersByDate("yonghu", startDate, endDate);
        int newHuiyuan = countNewUsersByDate("huiyuan", startDate, endDate);
        result.put("newYonghu", newYonghu);
        result.put("newHuiyuan", newHuiyuan);
        result.put("newUsers", newYonghu + newHuiyuan);

        return result;
    }

    @Override
    public Map<String, Object> getRoomStatusStatistics() {
        Map<String, Object> result = new HashMap<>();

        String[] statuses = {"空闲", "已预约", "已入住", "待清扫"};
        int total = 0;

        for (String status : statuses) {
            EntityWrapper<KefangxinxiEntity> ew = new EntityWrapper<>();
            ew.eq("kefangzhuangtai", status);
            int count = kefangxinxiService.selectCount(ew);
            result.put(status, count);
            total += count;
        }

        result.put("total", total);

        int occupied = (Integer) result.getOrDefault("已入住", 0);
        double occupancyRate = total > 0 ? (double) occupied / total * 100 : 0;
        result.put("occupancyRate", Math.round(occupancyRate * 100) / 100.0);

        return result;
    }

    @Override
    public Map<String, Object> getMonthlyReport(int year, int month) {
        Map<String, Object> result = new HashMap<>();

        String startDate = String.format("%d-%02d-01", year, month);
        Calendar cal = Calendar.getInstance();
        cal.set(year, month - 1, 1);
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        String endDate = dateFormat.format(cal.getTime());

        result.put("year", year);
        result.put("month", month);
        result.put("startDate", startDate);
        result.put("endDate", endDate);

        double totalRevenue = calculateRevenueByDateRange(startDate, endDate);
        result.put("totalRevenue", totalRevenue);

        int totalOrders = countOrdersByDateRange(startDate, endDate);
        result.put("totalOrders", totalOrders);

        List<Map<String, Object>> occupancyList = getOccupancyStatistics(startDate, endDate, "day");
        double avgOccupancy = occupancyList.stream()
                .mapToDouble(m -> (Double) m.get("occupancyRate"))
                .average()
                .orElse(0);
        result.put("averageOccupancy", Math.round(avgOccupancy * 100) / 100.0);

        List<Map<String, Object>> roomTypeStats = getRoomTypeStatistics(startDate, endDate);
        result.put("roomTypeStatistics", roomTypeStats);

        List<Map<String, Object>> dailyRevenue = getRevenueStatistics(startDate, endDate, "day");
        result.put("dailyRevenue", dailyRevenue);

        return result;
    }

    @Override
    public Map<String, Object> getYearlyReport(int year) {
        Map<String, Object> result = new HashMap<>();

        String startDate = year + "-01-01";
        String endDate = year + "-12-31";

        result.put("year", year);

        double totalRevenue = calculateRevenueByDateRange(startDate, endDate);
        result.put("totalRevenue", totalRevenue);

        int totalOrders = countOrdersByDateRange(startDate, endDate);
        result.put("totalOrders", totalOrders);

        List<Map<String, Object>> monthlyRevenue = getRevenueStatistics(startDate, endDate, "month");
        result.put("monthlyRevenue", monthlyRevenue);

        List<Map<String, Object>> monthlyOccupancy = getOccupancyStatistics(startDate, endDate, "month");
        result.put("monthlyOccupancy", monthlyOccupancy);

        double avgOccupancy = monthlyOccupancy.stream()
                .mapToDouble(m -> (Double) m.get("occupancyRate"))
                .average()
                .orElse(0);
        result.put("averageOccupancy", Math.round(avgOccupancy * 100) / 100.0);

        return result;
    }

    // ==================== 辅助方法 ====================

    private int countTodayCheckIn(String today) {
        EntityWrapper<YonghuruzhuEntity> ew1 = new EntityWrapper<>();
        ew1.ge("ruzhushijian", today + " 00:00:00");
        ew1.le("ruzhushijian", today + " 23:59:59");
        int count1 = yonghuruzhuService.selectCount(ew1);

        EntityWrapper<HuiyuanruzhuEntity> ew2 = new EntityWrapper<>();
        ew2.ge("ruzhushijian", today + " 00:00:00");
        ew2.le("ruzhushijian", today + " 23:59:59");
        int count2 = huiyuanruzhuService.selectCount(ew2);

        return count1 + count2;
    }

    private int countTodayCheckOut(String today) {
        EntityWrapper<YonghutuifangEntity> ew1 = new EntityWrapper<>();
        ew1.ge("tuifangshijian", today + " 00:00:00");
        ew1.le("tuifangshijian", today + " 23:59:59");
        int count1 = yonghutuifangService.selectCount(ew1);

        EntityWrapper<HuiyuantuifangEntity> ew2 = new EntityWrapper<>();
        ew2.ge("tuifangshijian", today + " 00:00:00");
        ew2.le("tuifangshijian", today + " 23:59:59");
        int count2 = huiyuantuifangService.selectCount(ew2);

        return count1 + count2;
    }

    private int countTodayBooking(String today) {
        EntityWrapper<YonghuyuyueEntity> ew1 = new EntityWrapper<>();
        ew1.ge("addtime", today + " 00:00:00");
        ew1.le("addtime", today + " 23:59:59");
        int count1 = yonghuyuyueService.selectCount(ew1);

        EntityWrapper<HuiyuanyuyueEntity> ew2 = new EntityWrapper<>();
        ew2.ge("addtime", today + " 00:00:00");
        ew2.le("addtime", today + " 23:59:59");
        int count2 = huiyuanyuyueService.selectCount(ew2);

        return count1 + count2;
    }

    private double calculateTodayRevenue(String today) {
        return calculateRevenueByDateRange(today, today);
    }

    private double calculateCurrentOccupancyRate() {
        int totalRooms = kefangxinxiService.selectCount(new EntityWrapper<>());
        if (totalRooms == 0) return 0;

        EntityWrapper<KefangxinxiEntity> ew = new EntityWrapper<>();
        ew.eq("kefangzhuangtai", "已入住");
        int occupiedRooms = kefangxinxiService.selectCount(ew);

        return Math.round((double) occupiedRooms / totalRooms * 10000) / 100.0;
    }

    private int countPendingOrders() {
        EntityWrapper<YonghuyuyueEntity> ew1 = new EntityWrapper<>();
        ew1.eq("yuyuezhuangtai", "pending");
        int count1 = yonghuyuyueService.selectCount(ew1);

        EntityWrapper<HuiyuanyuyueEntity> ew2 = new EntityWrapper<>();
        ew2.eq("yuyuezhuangtai", "pending");
        int count2 = huiyuanyuyueService.selectCount(ew2);

        return count1 + count2;
    }

    private List<String> generateDateList(String startDate, String endDate, String dimension) {
        List<String> result = new ArrayList<>();
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date start = sdf.parse(startDate);
            Date end = sdf.parse(endDate);
            Calendar cal = Calendar.getInstance();
            cal.setTime(start);

            while (!cal.getTime().after(end)) {
                if ("day".equals(dimension)) {
                    result.add(sdf.format(cal.getTime()));
                    cal.add(Calendar.DAY_OF_MONTH, 1);
                } else if ("week".equals(dimension)) {
                    result.add(sdf.format(cal.getTime()));
                    cal.add(Calendar.WEEK_OF_YEAR, 1);
                } else if ("month".equals(dimension)) {
                    SimpleDateFormat monthFormat = new SimpleDateFormat("yyyy-MM");
                    result.add(monthFormat.format(cal.getTime()));
                    cal.add(Calendar.MONTH, 1);
                } else if ("year".equals(dimension)) {
                    SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy");
                    result.add(yearFormat.format(cal.getTime()));
                    cal.add(Calendar.YEAR, 1);
                } else {
                    result.add(sdf.format(cal.getTime()));
                    cal.add(Calendar.DAY_OF_MONTH, 1);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    private int countOccupiedRoomsByDate(String date, String dimension) {
        EntityWrapper<KefangxinxiEntity> ew = new EntityWrapper<>();
        ew.eq("kefangzhuangtai", "已入住");
        return kefangxinxiService.selectCount(ew);
    }

    private double calculateYonghuRevenueByDate(String date, String dimension) {
        String startTime, endTime;
        if ("month".equals(dimension)) {
            startTime = date + "-01 00:00:00";
            endTime = date + "-31 23:59:59";
        } else {
            startTime = date + " 00:00:00";
            endTime = date + " 23:59:59";
        }

        EntityWrapper<YonghutuifangEntity> ew = new EntityWrapper<>();
        ew.ge("tuifangshijian", startTime);
        ew.le("tuifangshijian", endTime);
        List<YonghutuifangEntity> list = yonghutuifangService.selectList(ew);

        return list.stream()
                .filter(t -> t.getJiage() != null)
                .mapToDouble(YonghutuifangEntity::getJiage)
                .sum();
    }

    private double calculateHuiyuanRevenueByDate(String date, String dimension) {
        String startTime, endTime;
        if ("month".equals(dimension)) {
            startTime = date + "-01 00:00:00";
            endTime = date + "-31 23:59:59";
        } else {
            startTime = date + " 00:00:00";
            endTime = date + " 23:59:59";
        }

        EntityWrapper<HuiyuantuifangEntity> ew = new EntityWrapper<>();
        ew.ge("tuifangshijian", startTime);
        ew.le("tuifangshijian", endTime);
        List<HuiyuantuifangEntity> list = huiyuantuifangService.selectList(ew);

        return list.stream()
                .filter(t -> t.getJiage() != null)
                .mapToDouble(HuiyuantuifangEntity::getJiage)
                .sum();
    }

    private double calculateRevenueByDateRange(String startDate, String endDate) {
        String startTime = startDate + " 00:00:00";
        String endTime = endDate + " 23:59:59";

        double yonghuRevenue = 0;
        EntityWrapper<YonghutuifangEntity> ew1 = new EntityWrapper<>();
        ew1.ge("tuifangshijian", startTime);
        ew1.le("tuifangshijian", endTime);
        List<YonghutuifangEntity> list1 = yonghutuifangService.selectList(ew1);
        yonghuRevenue = list1.stream()
                .filter(t -> t.getJiage() != null)
                .mapToDouble(YonghutuifangEntity::getJiage)
                .sum();

        double huiyuanRevenue = 0;
        EntityWrapper<HuiyuantuifangEntity> ew2 = new EntityWrapper<>();
        ew2.ge("tuifangshijian", startTime);
        ew2.le("tuifangshijian", endTime);
        List<HuiyuantuifangEntity> list2 = huiyuantuifangService.selectList(ew2);
        huiyuanRevenue = list2.stream()
                .filter(t -> t.getJiage() != null)
                .mapToDouble(HuiyuantuifangEntity::getJiage)
                .sum();

        return yonghuRevenue + huiyuanRevenue;
    }

    private int countYonghuOrdersByDate(String date, String dimension) {
        String startTime, endTime;
        if ("month".equals(dimension)) {
            startTime = date + "-01 00:00:00";
            endTime = date + "-31 23:59:59";
        } else {
            startTime = date + " 00:00:00";
            endTime = date + " 23:59:59";
        }

        EntityWrapper<YonghuyuyueEntity> ew = new EntityWrapper<>();
        ew.ge("addtime", startTime);
        ew.le("addtime", endTime);
        return yonghuyuyueService.selectCount(ew);
    }

    private int countHuiyuanOrdersByDate(String date, String dimension) {
        String startTime, endTime;
        if ("month".equals(dimension)) {
            startTime = date + "-01 00:00:00";
            endTime = date + "-31 23:59:59";
        } else {
            startTime = date + " 00:00:00";
            endTime = date + " 23:59:59";
        }

        EntityWrapper<HuiyuanyuyueEntity> ew = new EntityWrapper<>();
        ew.ge("addtime", startTime);
        ew.le("addtime", endTime);
        return huiyuanyuyueService.selectCount(ew);
    }

    private int countOrdersByDateRange(String startDate, String endDate) {
        String startTime = startDate + " 00:00:00";
        String endTime = endDate + " 23:59:59";

        EntityWrapper<YonghuyuyueEntity> ew1 = new EntityWrapper<>();
        ew1.ge("addtime", startTime);
        ew1.le("addtime", endTime);
        int count1 = yonghuyuyueService.selectCount(ew1);

        EntityWrapper<HuiyuanyuyueEntity> ew2 = new EntityWrapper<>();
        ew2.ge("addtime", startTime);
        ew2.le("addtime", endTime);
        int count2 = huiyuanyuyueService.selectCount(ew2);

        return count1 + count2;
    }

    private int countBookingsByRoomType(String roomType, String startDate, String endDate) {
        String startTime = startDate + " 00:00:00";
        String endTime = endDate + " 23:59:59";

        // 先获取该房型对应的所有客房号
        EntityWrapper<KefangxinxiEntity> roomWrapper = new EntityWrapper<>();
        roomWrapper.eq("kefangleixing", roomType);
        List<KefangxinxiEntity> rooms = kefangxinxiService.selectList(roomWrapper);
        
        if (rooms == null || rooms.isEmpty()) {
            return 0;
        }
        
        List<String> roomNumbers = new ArrayList<>();
        for (KefangxinxiEntity room : rooms) {
            if (room.getKefanghao() != null) {
                roomNumbers.add(room.getKefanghao());
            }
        }
        
        if (roomNumbers.isEmpty()) {
            return 0;
        }

        EntityWrapper<YonghuyuyueEntity> ew1 = new EntityWrapper<>();
        ew1.in("kefanghao", roomNumbers);
        ew1.ge("addtime", startTime);
        ew1.le("addtime", endTime);
        int count1 = yonghuyuyueService.selectCount(ew1);

        EntityWrapper<HuiyuanyuyueEntity> ew2 = new EntityWrapper<>();
        ew2.in("kefanghao", roomNumbers);
        ew2.ge("addtime", startTime);
        ew2.le("addtime", endTime);
        int count2 = huiyuanyuyueService.selectCount(ew2);

        return count1 + count2;
    }

    private double calculateRevenueByRoomType(String roomType, String startDate, String endDate) {
        String startTime = startDate + " 00:00:00";
        String endTime = endDate + " 23:59:59";

        double revenue = 0;

        EntityWrapper<YonghutuifangEntity> ew1 = new EntityWrapper<>();
        ew1.eq("kefangleixing", roomType);
        ew1.ge("tuifangshijian", startTime);
        ew1.le("tuifangshijian", endTime);
        List<YonghutuifangEntity> list1 = yonghutuifangService.selectList(ew1);
        revenue += list1.stream()
                .filter(t -> t.getJiage() != null)
                .mapToDouble(YonghutuifangEntity::getJiage)
                .sum();

        EntityWrapper<HuiyuantuifangEntity> ew2 = new EntityWrapper<>();
        ew2.eq("kefangleixing", roomType);
        ew2.ge("tuifangshijian", startTime);
        ew2.le("tuifangshijian", endTime);
        List<HuiyuantuifangEntity> list2 = huiyuantuifangService.selectList(ew2);
        revenue += list2.stream()
                .filter(t -> t.getJiage() != null)
                .mapToDouble(HuiyuantuifangEntity::getJiage)
                .sum();

        return revenue;
    }

    private int countNewUsersByDate(String type, String startDate, String endDate) {
        String startTime = startDate + " 00:00:00";
        String endTime = endDate + " 23:59:59";

        if ("yonghu".equals(type)) {
            EntityWrapper<YonghuEntity> ew = new EntityWrapper<>();
            ew.ge("addtime", startTime);
            ew.le("addtime", endTime);
            return yonghuService.selectCount(ew);
        } else {
            EntityWrapper<HuiyuanEntity> ew = new EntityWrapper<>();
            ew.ge("addtime", startTime);
            ew.le("addtime", endTime);
            return huiyuanService.selectCount(ew);
        }
    }

    @Override
    public List<Map<String, Object>> getRoomTypeRanking() {
        List<Map<String, Object>> result = new ArrayList<>();
        
        // 获取近30天的日期范围
        Calendar cal = Calendar.getInstance();
        String endDate = dateFormat.format(cal.getTime()).substring(0, 10);
        cal.add(Calendar.DAY_OF_MONTH, -30);
        String startDate = dateFormat.format(cal.getTime()).substring(0, 10);
        
        // 获取所有房型
        List<KefangxinxiEntity> rooms = kefangxinxiService.selectList(new EntityWrapper<>());
        Map<String, Integer> roomTypeCountMap = new HashMap<>();
        
        for (KefangxinxiEntity room : rooms) {
            String roomType = room.getKefangleixing();
            if (roomType != null && !roomType.isEmpty()) {
                roomTypeCountMap.putIfAbsent(roomType, 0);
            }
        }
        
        // 统计每个房型的预订数量
        for (String roomType : roomTypeCountMap.keySet()) {
            int count = countBookingsByRoomType(roomType, startDate, endDate);
            roomTypeCountMap.put(roomType, count);
        }
        
        // 按预订数量排序
        List<Map.Entry<String, Integer>> sortedList = new ArrayList<>(roomTypeCountMap.entrySet());
        sortedList.sort((a, b) -> b.getValue().compareTo(a.getValue()));
        
        // 转换为结果格式
        for (Map.Entry<String, Integer> entry : sortedList) {
            Map<String, Object> item = new HashMap<>();
            item.put("roomType", entry.getKey());
            item.put("kefangleixing", entry.getKey());
            item.put("count", entry.getValue());
            item.put("bookingCount", entry.getValue());
            result.add(item);
        }
        
        return result;
    }
}
