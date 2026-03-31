package com.enums;

/**
 * 通知类型枚举
 * 定义系统所有通知类型及其消息模板
 */
public enum NotifyTypeEnum {

    BOOKING_AUDIT("预约审核", "您的预约订单审核结果"),
    BOOKING_SUCCESS("预约成功", "您的预约已提交成功"),
    CANCEL_AUDIT("取消审核", "您的取消申请审核结果"),
    CHECKIN_REMIND("入住提醒", "入住时间提醒"),
    CHECKOUT_REMIND("退房提醒", "退房时间提醒"),
    CHAT_REPLY("客服回复", "客服已回复您的咨询"),
    MESSAGE_REPLY("留言回复", "您的留言已收到回复"),
    CLEAN_TASK("清扫任务", "有新的清扫任务待处理"),
    CLEAN_COMPLETE("清扫完成", "房间清扫已完成"),
    POINTS_CHANGE("积分变动", "您的积分发生变动"),
    SYSTEM("系统通知", "系统通知消息"),
    PAYMENT("支付通知", "支付结果通知"),
    REFUND("退款通知", "退款处理结果");

    private final String code;
    private final String template;

    NotifyTypeEnum(String code, String template) {
        this.code = code;
        this.template = template;
    }

    public String getCode() {
        return code;
    }

    public String getTemplate() {
        return template;
    }

    /**
     * 根据类型码获取枚举
     * @param code 类型码
     * @return 对应的枚举
     */
    public static NotifyTypeEnum getByCode(String code) {
        if (code == null || code.isEmpty()) {
            return null;
        }
        for (NotifyTypeEnum type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return null;
    }

    /**
     * 生成通知标题
     * @param type 通知类型
     * @param params 参数（如房间号等）
     * @return 格式化后的标题
     */
    public static String generateTitle(NotifyTypeEnum type, String... params) {
        if (type == null) {
            return "系统通知";
        }
        String title = type.getTemplate();
        if (params != null && params.length > 0) {
            for (int i = 0; i < params.length; i++) {
                title = title.replace("{" + i + "}", params[i]);
            }
        }
        return title;
    }

    /**
     * 判断是否为有效的通知类型
     * @param code 类型码
     * @return true-有效 false-无效
     */
    public static boolean isValidType(String code) {
        return getByCode(code) != null;
    }
}
