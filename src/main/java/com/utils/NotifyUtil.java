package com.utils;

import com.entity.TongzhiEntity;
import com.constant.CommonConstant;
import java.util.Date;

/**
 * 通知工具类
 * 提供创建各类通知的便捷方法
 */
public class NotifyUtil {

    private NotifyUtil() {
        throw new IllegalStateException("工具类不允许实例化");
    }

    /**
     * 创建通知实体
     * @param userid 接收用户ID
     * @param tablename 用户表名
     * @param title 通知标题
     * @param content 通知内容
     * @param type 通知类型
     * @param refid 关联ID
     * @param reftable 关联表名
     * @return 通知实体
     */
    public static TongzhiEntity createNotify(Long userid, String tablename, String title,
                                             String content, String type, Long refid, String reftable) {
        TongzhiEntity tongzhi = new TongzhiEntity();
        tongzhi.setId(OrderNumberUtil.generateNotifyId());
        tongzhi.setUserid(userid);
        tongzhi.setTablename(tablename);
        tongzhi.setTitle(title);
        tongzhi.setContent(content);
        tongzhi.setType(type);
        tongzhi.setRefid(refid);
        tongzhi.setReftable(reftable);
        tongzhi.setIsread(0);
        tongzhi.setAddtime(new Date());
        return tongzhi;
    }

    /**
     * 创建预约审核通过通知
     */
    public static TongzhiEntity createBookingApprovedNotify(Long userid, String tablename,
                                                            String kefanghao, Long bookingId, String bookingTable) {
        String title = "预约审核通过";
        String content = String.format("您预约的客房【%s】已审核通过，请按时前来办理入住。", kefanghao);
        return createNotify(userid, tablename, title, content,
                CommonConstant.NOTIFY_TYPE_BOOKING_AUDIT, bookingId, bookingTable);
    }

    /**
     * 创建预约审核拒绝通知
     */
    public static TongzhiEntity createBookingRejectedNotify(Long userid, String tablename,
                                                            String kefanghao, String reason, Long bookingId, String bookingTable) {
        String title = "预约审核未通过";
        String content = String.format("很抱歉，您预约的客房【%s】未能通过审核。原因：%s", kefanghao, reason);
        return createNotify(userid, tablename, title, content,
                CommonConstant.NOTIFY_TYPE_BOOKING_AUDIT, bookingId, bookingTable);
    }

    /**
     * 创建入住提醒通知
     */
    public static TongzhiEntity createCheckInRemindNotify(Long userid, String tablename,
                                                          String kefanghao, Date checkInDate, Long bookingId, String bookingTable) {
        String title = "入住提醒";
        String dateStr = new java.text.SimpleDateFormat("yyyy-MM-dd").format(checkInDate);
        String content = String.format("温馨提醒：您预约的客房【%s】将于【%s】入住，请提前准备好相关证件，按时到店办理入住手续。",
                kefanghao, dateStr);
        return createNotify(userid, tablename, title, content,
                CommonConstant.NOTIFY_TYPE_CHECKIN_REMIND, bookingId, bookingTable);
    }

    /**
     * 创建退房提醒通知
     */
    public static TongzhiEntity createCheckOutRemindNotify(Long userid, String tablename,
                                                           String kefanghao, Long orderId, String orderTable) {
        String title = "退房提醒";
        String content = String.format("温馨提醒：您入住的客房【%s】今日12:00前需要办理退房，请提前整理好个人物品。如需延时退房，请联系前台。",
                kefanghao);
        return createNotify(userid, tablename, title, content,
                CommonConstant.NOTIFY_TYPE_CHECKOUT_REMIND, orderId, orderTable);
    }

    /**
     * 创建客服回复通知
     */
    public static TongzhiEntity createChatReplyNotify(Long userid, String tablename, Long chatId) {
        String title = "客服回复";
        String content = "您的咨询已收到回复，请查看。";
        return createNotify(userid, tablename, title, content,
                CommonConstant.NOTIFY_TYPE_CHAT_REPLY, chatId, "chat");
    }

    /**
     * 创建清扫任务通知（发给清洁人员）
     */
    public static TongzhiEntity createCleanTaskNotify(Long userid, String kefanghao, Long taskId) {
        String title = "新的清扫任务";
        String content = String.format("您有新的清扫任务，客房号【%s】需要清扫，请尽快处理。", kefanghao);
        return createNotify(userid, CommonConstant.TABLE_QINGJIERENYUAN, title, content,
                CommonConstant.NOTIFY_TYPE_CLEAN_TASK, taskId, "qingsaofangjian");
    }

    /**
     * 创建积分变动通知
     */
    public static TongzhiEntity createPointsChangeNotify(Long userid, String tablename,
                                                         int points, int balance, String reason) {
        String title = "积分变动通知";
        String changeDesc = points > 0 ? "增加" + points : "减少" + Math.abs(points);
        String content = String.format("您的积分发生变动：%s积分，原因：%s。当前积分余额：%d",
                changeDesc, reason, balance);
        return createNotify(userid, tablename, title, content,
                CommonConstant.NOTIFY_TYPE_POINTS, null, null);
    }

    /**
     * 创建系统通知
     */
    public static TongzhiEntity createSystemNotify(Long userid, String tablename,
                                                   String title, String content) {
        return createNotify(userid, tablename, title, content,
                CommonConstant.NOTIFY_TYPE_SYSTEM, null, null);
    }

    /**
     * 创建支付成功通知
     */
    public static TongzhiEntity createPaymentSuccessNotify(Long userid, String tablename,
                                                           String orderNo, Double amount, Long orderId, String orderTable) {
        String title = "支付成功";
        String content = String.format("您的订单【%s】支付成功，支付金额：%.2f元。感谢您的信任与支持！",
                orderNo, amount);
        return createNotify(userid, tablename, title, content,
                CommonConstant.NOTIFY_TYPE_SYSTEM, orderId, orderTable);
    }

    /**
     * 创建取消申请通知
     */
    public static TongzhiEntity createCancelAuditNotify(Long userid, String tablename,
                                                        boolean approved, String kefanghao, String reason,
                                                        Long cancelId, String cancelTable) {
        String title = approved ? "取消申请已通过" : "取消申请未通过";
        String content;
        if (approved) {
            content = String.format("您对客房【%s】的取消预约申请已通过审核，相关费用将按规定处理。", kefanghao);
        } else {
            content = String.format("您对客房【%s】的取消预约申请未通过审核。原因：%s", kefanghao, reason);
        }
        return createNotify(userid, tablename, title, content,
                CommonConstant.NOTIFY_TYPE_CANCEL_AUDIT, cancelId, cancelTable);
    }
}
