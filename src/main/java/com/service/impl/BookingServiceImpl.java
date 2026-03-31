package com.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.constant.CommonConstant;
import com.entity.*;
import com.exception.CustomException;
import com.service.*;
import com.utils.OrderNumberUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 预约业务服务实现类
 */
@Service("bookingService")
public class BookingServiceImpl implements BookingService {

    @Autowired
    private YonghuyuyueService yonghuyuyueService;

    @Autowired
    private HuiyuanyuyueService huiyuanyuyueService;

    @Autowired
    private YonghuService yonghuService;

    @Autowired
    private HuiyuanService huiyuanService;

    @Autowired
    private RoomStatusService roomStatusService;

    @Autowired
    private TongzhiService tongzhiService;

    @Autowired
    private KefangxinxiService kefangxinxiService;

    private static final double MEMBER_DISCOUNT = 0.9;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String createYonghuBooking(Long userId, String kefanghao, Date ruzhushijian, Integer tianshu) {
        if (!roomStatusService.canBook(kefanghao)) {
            throw new CustomException("该房间当前不可预订");
        }

        YonghuEntity yonghu = yonghuService.selectById(userId);
        if (yonghu == null) {
            throw new CustomException("用户不存在");
        }

        KefangxinxiEntity room = roomStatusService.getByKefanghao(kefanghao);
        if (room == null) {
            throw new CustomException("客房不存在");
        }

        Double totalPrice = calculatePrice(kefanghao, tianshu, false);
        String yuyuebianhao = OrderNumberUtil.generateBookingNumber();

        YonghuyuyueEntity booking = new YonghuyuyueEntity();
        booking.setId(new Date().getTime() + new Double(Math.floor(Math.random() * 1000)).longValue());
        booking.setAddtime(new Date());
        booking.setYuyuebianhao(yuyuebianhao);
        booking.setKefanghao(kefanghao);
        booking.setRuzhushijian(ruzhushijian);
        booking.setTianshu(tianshu);
        booking.setJiage(String.valueOf(room.getJiage()));
        booking.setZongjia(String.valueOf(totalPrice));
        booking.setYuyueshijian(new Date());
        booking.setZhanghao(yonghu.getZhanghao());
        booking.setXingming(yonghu.getXingming());
        booking.setShouji(yonghu.getShouji());
        booking.setShenfenzheng(yonghu.getShenfenzheng());
        booking.setSfsh("是");  // 小程序预订自动审核通过，用户可直接支付
        booking.setIspay("未支付");
        booking.setYuyuezhuangtai("待支付");  // 设置初始状态为待支付

        yonghuyuyueService.insert(booking);
        roomStatusService.setBooked(kefanghao);

        return yuyuebianhao;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String createHuiyuanBooking(Long huiyuanId, String kefanghao, Date ruzhushijian, Integer tianshu) {
        if (!roomStatusService.canBook(kefanghao)) {
            throw new CustomException("该房间当前不可预订");
        }

        HuiyuanEntity huiyuan = huiyuanService.selectById(huiyuanId);
        if (huiyuan == null) {
            throw new CustomException("会员不存在");
        }

        KefangxinxiEntity room = roomStatusService.getByKefanghao(kefanghao);
        if (room == null) {
            throw new CustomException("客房不存在");
        }

        Double totalPrice = calculatePrice(kefanghao, tianshu, true);
        String yuyuebianhao = OrderNumberUtil.generateBookingNumber();

        HuiyuanyuyueEntity booking = new HuiyuanyuyueEntity();
        booking.setId(new Date().getTime() + new Double(Math.floor(Math.random() * 1000)).longValue());
        booking.setAddtime(new Date());
        booking.setYuyuebianhao(yuyuebianhao);
        booking.setKefanghao(kefanghao);
        booking.setRuzhushijian(ruzhushijian);
        booking.setTianshu(tianshu);
        booking.setJiage(String.valueOf(room.getJiage()));
        booking.setZongjia(String.valueOf(totalPrice));
        booking.setYuyueshijian(new Date());
        booking.setZhanghao(huiyuan.getZhanghao());
        booking.setXingming(huiyuan.getXingming());
        booking.setShouji(huiyuan.getShouji());
        booking.setShenfenzheng(huiyuan.getShenfenzheng());
        booking.setSfsh("是");  // 小程序预订自动审核通过，用户可直接支付
        booking.setIspay("未支付");
        booking.setYuyuezhuangtai("待支付");  // 设置初始状态为待支付

        huiyuanyuyueService.insert(booking);
        roomStatusService.setBooked(kefanghao);

        return yuyuebianhao;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void auditYonghuBooking(Long bookingId, String sfsh, String shhf) {
        YonghuyuyueEntity booking = yonghuyuyueService.selectById(bookingId);
        if (booking == null) {
            throw new CustomException("预约记录不存在");
        }
        
        // 检查预约状态，已入住的不能再审核
        if ("checkedin".equals(booking.getYuyuezhuangtai())) {
            throw new CustomException("该预约已办理入住，无法再次审核");
        }

        booking.setSfsh(sfsh);
        booking.setShhf(shhf);
        
        // 更新预约状态
        if ("是".equals(sfsh)) {
            booking.setYuyuezhuangtai("待支付");
        } else if ("否".equals(sfsh)) {
            booking.setYuyuezhuangtai("已拒绝");
            roomStatusService.setFree(booking.getKefanghao());
        }
        
        yonghuyuyueService.updateById(booking);

        sendAuditNotify(booking.getZhanghao(), "yonghu", sfsh, shhf, bookingId, "yonghuyuyue");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void auditHuiyuanBooking(Long bookingId, String sfsh, String shhf) {
        HuiyuanyuyueEntity booking = huiyuanyuyueService.selectById(bookingId);
        if (booking == null) {
            throw new CustomException("预约记录不存在");
        }
        
        // 检查预约状态，已入住的不能再审核
        if ("checkedin".equals(booking.getYuyuezhuangtai())) {
            throw new CustomException("该预约已办理入住，无法再次审核");
        }

        booking.setSfsh(sfsh);
        booking.setShhf(shhf);
        
        // 更新预约状态
        if ("是".equals(sfsh)) {
            booking.setYuyuezhuangtai("待支付");
        } else if ("否".equals(sfsh)) {
            booking.setYuyuezhuangtai("已拒绝");
            roomStatusService.setFree(booking.getKefanghao());
        }
        
        huiyuanyuyueService.updateById(booking);

        sendAuditNotify(booking.getZhanghao(), "huiyuan", sfsh, shhf, bookingId, "huiyuanyuyue");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> payYonghuBooking(Long bookingId) {
        return payYonghuBooking(bookingId, "wechat");  // 默认微信支付
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> payYonghuBooking(Long bookingId, String payType) {
        YonghuyuyueEntity booking = yonghuyuyueService.selectById(bookingId);
        if (booking == null) {
            throw new CustomException("预约记录不存在");
        }

        String sfsh = booking.getSfsh();
        if (!"是".equals(sfsh) && !"待审核".equals(sfsh)) {
            throw new CustomException("预约审核未通过，无法支付");
        }

        if ("已支付".equals(booking.getIspay())) {
            throw new CustomException("该订单已支付");
        }
        
        Double amount = 0.0;
        try {
            amount = Double.parseDouble(booking.getZongjia());
        } catch (Exception e) {
            amount = 0.0;
        }
        
        // 余额支付处理
        if ("balance".equals(payType)) {
            YonghuEntity user = yonghuService.selectOne(
                new EntityWrapper<YonghuEntity>().eq("zhanghao", booking.getZhanghao())
            );
            if (user == null) {
                throw new CustomException("用户不存在");
            }
            
            Double balance = user.getYue();
            if (balance == null) balance = 0.0;
            
            if (balance < amount) {
                throw new CustomException("余额不足，当前余额：" + String.format("%.2f", balance) + "元");
            }
            
            // 扣除余额
            user.setYue(balance - amount);
            yonghuService.updateById(user);
        }

        booking.setSfsh("是");
        booking.setIspay("已支付");
        booking.setYuyuezhuangtai("待入住");
        yonghuyuyueService.updateById(booking);

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "支付成功");
        result.put("orderNo", booking.getYuyuebianhao());
        result.put("amount", booking.getZongjia());
        result.put("payType", payType);
        result.put("payTime", new Date());

        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> payHuiyuanBooking(Long bookingId) {
        return payHuiyuanBooking(bookingId, "wechat");  // 默认微信支付
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> payHuiyuanBooking(Long bookingId, String payType) {
        HuiyuanyuyueEntity booking = huiyuanyuyueService.selectById(bookingId);
        if (booking == null) {
            throw new CustomException("预约记录不存在");
        }

        String sfsh = booking.getSfsh();
        if (!"是".equals(sfsh) && !"待审核".equals(sfsh)) {
            throw new CustomException("预约审核未通过，无法支付");
        }

        if ("已支付".equals(booking.getIspay())) {
            throw new CustomException("该订单已支付");
        }
        
        Double amount = 0.0;
        try {
            amount = Double.parseDouble(booking.getZongjia());
        } catch (Exception e) {
            amount = 0.0;
        }
        
        // 余额支付处理
        if ("balance".equals(payType)) {
            HuiyuanEntity member = huiyuanService.selectOne(
                new EntityWrapper<HuiyuanEntity>().eq("zhanghao", booking.getZhanghao())
            );
            if (member == null) {
                throw new CustomException("会员不存在");
            }
            
            Double balance = member.getYue();
            if (balance == null) balance = 0.0;
            
            if (balance < amount) {
                throw new CustomException("余额不足，当前余额：" + String.format("%.2f", balance) + "元");
            }
            
            // 扣除余额
            member.setYue(balance - amount);
            huiyuanService.updateById(member);
        }

        booking.setSfsh("是");
        booking.setIspay("已支付");
        booking.setYuyuezhuangtai("待入住");
        huiyuanyuyueService.updateById(booking);

        // 支付成功赠送积分
        int points = 0;
        try {
            points = (int) amount.doubleValue();
        } catch (Exception e) {
            points = 0;
        }
        addMemberPoints(booking.getZhanghao(), points);

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "支付成功");
        result.put("orderNo", booking.getYuyuebianhao());
        result.put("amount", booking.getZongjia());
        result.put("payType", payType);
        result.put("payTime", new Date());
        result.put("pointsEarned", points);

        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelYonghuBooking(Long bookingId, String reason) {
        YonghuyuyueEntity booking = yonghuyuyueService.selectById(bookingId);
        if (booking == null) {
            throw new CustomException("预约记录不存在");
        }

        // 如果已经是取消状态，直接恢复房间状态
        if ("已取消".equals(booking.getYuyuezhuangtai()) || "申请退款".equals(booking.getYuyuezhuangtai())) {
            roomStatusService.setFree(booking.getKefanghao());
            return;
        }

        // 已支付订单：状态改为"申请退款"，等待客服处理
        if ("已支付".equals(booking.getIspay()) || "已支付".equals(booking.getYuyuezhuangtai())) {
            booking.setYuyuezhuangtai("申请退款");
            yonghuyuyueService.updateById(booking);
            roomStatusService.setFree(booking.getKefanghao());
            return;
        }

        // 未支付订单：直接取消
        booking.setYuyuezhuangtai("已取消");
        yonghuyuyueService.updateById(booking);
        
        // 恢复房间状态为空闲
        roomStatusService.setFree(booking.getKefanghao());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelHuiyuanBooking(Long bookingId, String reason) {
        HuiyuanyuyueEntity booking = huiyuanyuyueService.selectById(bookingId);
        if (booking == null) {
            throw new CustomException("预约记录不存在");
        }

        // 如果已经是取消状态，直接恢复房间状态
        if ("已取消".equals(booking.getYuyuezhuangtai()) || "申请退款".equals(booking.getYuyuezhuangtai())) {
            roomStatusService.setFree(booking.getKefanghao());
            return;
        }

        // 已支付订单：状态改为"申请退款"，等待客服处理
        if ("已支付".equals(booking.getIspay()) || "已支付".equals(booking.getYuyuezhuangtai())) {
            booking.setYuyuezhuangtai("申请退款");
            huiyuanyuyueService.updateById(booking);
            roomStatusService.setFree(booking.getKefanghao());
            return;
        }

        // 未支付订单：直接取消
        booking.setYuyuezhuangtai("已取消");
        huiyuanyuyueService.updateById(booking);
        
        // 恢复房间状态为空闲
        roomStatusService.setFree(booking.getKefanghao());
    }

    @Override
    public Double calculatePrice(String kefanghao, Integer tianshu, boolean isHuiyuan) {
        KefangxinxiEntity room = roomStatusService.getByKefanghao(kefanghao);
        if (room == null) {
            throw new CustomException("客房不存在");
        }

        double unitPrice = room.getJiage() != null ? room.getJiage().doubleValue() : 0.0;
        double totalPrice = unitPrice * tianshu;

        if (isHuiyuan) {
            totalPrice = totalPrice * MEMBER_DISCOUNT;
        }

        return Math.round(totalPrice * 100) / 100.0;
    }

    private void sendAuditNotify(String zhanghao, String tablename, String sfsh, 
                                 String shhf, Long refid, String reftable) {
        Long userid = getUserIdByZhanghao(zhanghao, tablename);
        if (userid == null) {
            return;
        }

        String title = "是".equals(sfsh) ? "预约审核通过" : "预约审核未通过";
        String content = "是".equals(sfsh) ? 
                "您的预约已审核通过，请尽快完成支付。" : 
                "您的预约审核未通过，原因：" + shhf;

        TongzhiEntity tongzhi = new TongzhiEntity();
        tongzhi.setId(new Date().getTime() + new Double(Math.floor(Math.random() * 1000)).longValue());
        tongzhi.setAddtime(new Date());
        tongzhi.setUserid(userid);
        tongzhi.setTablename(tablename);
        tongzhi.setTitle(title);
        tongzhi.setContent(content);
        tongzhi.setType(CommonConstant.NOTIFY_TYPE_BOOKING_AUDIT);
        tongzhi.setRefid(refid);
        tongzhi.setReftable(reftable);
        tongzhi.setIsread(0);

        tongzhiService.sendNotify(tongzhi);
    }

    private Long getUserIdByZhanghao(String zhanghao, String tablename) {
        if ("yonghu".equals(tablename)) {
            EntityWrapper<YonghuEntity> ew = new EntityWrapper<>();
            ew.eq("zhanghao", zhanghao);
            YonghuEntity user = yonghuService.selectOne(ew);
            return user != null ? user.getId() : null;
        } else if ("huiyuan".equals(tablename)) {
            EntityWrapper<HuiyuanEntity> ew = new EntityWrapper<>();
            ew.eq("zhanghao", zhanghao);
            HuiyuanEntity user = huiyuanService.selectOne(ew);
            return user != null ? user.getId() : null;
        }
        return null;
    }

    private void addMemberPoints(String zhanghao, int points) {
        EntityWrapper<HuiyuanEntity> ew = new EntityWrapper<>();
        ew.eq("zhanghao", zhanghao);
        HuiyuanEntity huiyuan = huiyuanService.selectOne(ew);
        if (huiyuan != null) {
            int newPoints = (huiyuan.getJifen() != null ? huiyuan.getJifen() : 0) + points;
            huiyuan.setJifen(newPoints);
            huiyuanService.updateById(huiyuan);
        }
    }
}
