package com.constant;

/**
 * 系统通用常量定义
 * 统一管理系统中的常量值，避免硬编码
 */
public class CommonConstant {

    private CommonConstant() {
        throw new IllegalStateException("常量类不允许实例化");
    }

    /**
     * 客房状态常量
     */
    public static final String ROOM_STATUS_FREE = "空闲";
    public static final String ROOM_STATUS_BOOKED = "已预订";
    public static final String ROOM_STATUS_OCCUPIED = "已入住";
    public static final String ROOM_STATUS_CLEANING = "待清扫";
    public static final String ROOM_STATUS_MAINTENANCE = "维修中";
    public static final String ROOM_STATUS_FULL = "已客满";

    /**
     * 卫生状态常量
     */
    public static final String CLEAN_STATUS_DONE = "已清扫";
    public static final String CLEAN_STATUS_PENDING = "待清扫";
    public static final String CLEAN_STATUS_CLEANING = "清扫中";

    /**
     * 审核状态常量
     */
    public static final String AUDIT_STATUS_PENDING = "待审核";
    public static final String AUDIT_STATUS_NO = "否";
    public static final String AUDIT_STATUS_YES = "是";

    /**
     * 支付状态常量
     */
    public static final String PAY_STATUS_UNPAID = "未支付";
    public static final String PAY_STATUS_PAID = "已支付";
    public static final String PAY_STATUS_REFUNDED = "已退款";

    /**
     * 通知类型常量
     */
    public static final String NOTIFY_TYPE_BOOKING_AUDIT = "预约审核";
    public static final String NOTIFY_TYPE_CANCEL_AUDIT = "取消审核";
    public static final String NOTIFY_TYPE_CHECKIN_REMIND = "入住提醒";
    public static final String NOTIFY_TYPE_CHECKOUT_REMIND = "退房提醒";
    public static final String NOTIFY_TYPE_CHAT_REPLY = "客服回复";
    public static final String NOTIFY_TYPE_MESSAGE_REPLY = "留言回复";
    public static final String NOTIFY_TYPE_CLEAN_TASK = "清扫任务";
    public static final String NOTIFY_TYPE_SYSTEM = "系统通知";
    public static final String NOTIFY_TYPE_POINTS = "积分变动";

    /**
     * 服务类型常量
     */
    public static final String SERVICE_TYPE_BED = "加床服务";
    public static final String SERVICE_TYPE_BREAKFAST = "早餐预订";
    public static final String SERVICE_TYPE_LATE_CHECKOUT = "延时退房";
    public static final String SERVICE_TYPE_WAKEUP = "叫醒服务";
    public static final String SERVICE_TYPE_BORROW = "物品借用";
    public static final String SERVICE_TYPE_REPAIR = "维修服务";
    public static final String SERVICE_TYPE_OTHER = "其他";

    /**
     * 服务状态常量
     */
    public static final String SERVICE_STATUS_PENDING = "待处理";
    public static final String SERVICE_STATUS_PROCESSING = "处理中";
    public static final String SERVICE_STATUS_DONE = "已完成";
    public static final String SERVICE_STATUS_CANCELLED = "已取消";

    /**
     * 用户角色常量
     */
    public static final String ROLE_ADMIN = "管理员";
    public static final String ROLE_RECEPTIONIST = "前台人员";
    public static final String ROLE_USER = "用户";
    public static final String ROLE_MEMBER = "会员";
    public static final String ROLE_CLEANER = "清洁人员";

    /**
     * 用户表名常量
     */
    public static final String TABLE_USERS = "users";
    public static final String TABLE_QIANTAIRENYUAN = "qiantairenyuan";
    public static final String TABLE_YONGHU = "yonghu";
    public static final String TABLE_HUIYUAN = "huiyuan";
    public static final String TABLE_QINGJIERENYUAN = "qingjierenyuan";

    /**
     * 员工状态常量
     */
    public static final String STAFF_STATUS_ACTIVE = "在职";
    public static final String STAFF_STATUS_LEAVE = "离职";
    public static final String STAFF_STATUS_SUSPEND = "停职";

    /**
     * 分页默认值
     */
    public static final int DEFAULT_PAGE = 1;
    public static final int DEFAULT_LIMIT = 10;
    public static final int MAX_LIMIT = 100;

    /**
     * Token相关
     */
    public static final long TOKEN_EXPIRE_TIME = 12 * 3600 * 1000L;  // 12小时
    public static final long TOKEN_RENEWAL_TIME = 2 * 3600 * 1000L;  // 2小时内自动续期

    /**
     * 积分规则
     */
    public static final String POINTS_TYPE_BOOKING = "预订积分";
    public static final String POINTS_TYPE_CHECKIN = "入住积分";
    public static final String POINTS_TYPE_CONSUME = "消费积分";
    public static final String POINTS_TYPE_EXCHANGE = "积分兑换";
    public static final String POINTS_TYPE_GIFT = "赠送积分";
    public static final String POINTS_TYPE_DEDUCT = "积分扣减";
    
    public static final int POINTS_PER_YUAN = 1;  // 每消费1元获得1积分

    /**
     * 退房时间常量
     */
    public static final int CHECKOUT_HOUR = 12;  // 默认退房时间12点
    public static final int LATE_CHECKOUT_FEE_RATE = 50;  // 超时每小时加收50%日租金

    /**
     * 订单状态
     */
    public static final String ORDER_STATUS_PENDING = "待处理";
    public static final String ORDER_STATUS_CONFIRMED = "已确认";
    public static final String ORDER_STATUS_CHECKIN = "已入住";
    public static final String ORDER_STATUS_CHECKOUT = "已退房";
    public static final String ORDER_STATUS_CANCELLED = "已取消";
    public static final String ORDER_STATUS_COMPLETED = "已完成";
}
