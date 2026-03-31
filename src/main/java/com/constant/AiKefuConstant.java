package com.constant;

/**
 * AI客服相关常量定义
 * 管理AI客服模块所需的常量
 */
public class AiKefuConstant {

    private AiKefuConstant() {
        throw new IllegalStateException("常量类不允许实例化");
    }

    /**
     * 问题类型
     */
    public static final String QUESTION_TYPE_BOOKING = "订房流程";
    public static final String QUESTION_TYPE_CHECKOUT = "退房规则";
    public static final String QUESTION_TYPE_INVOICE = "发票索取";
    public static final String QUESTION_TYPE_BREAKFAST = "早餐服务";
    public static final String QUESTION_TYPE_WIFI = "WiFi信息";
    public static final String QUESTION_TYPE_PARKING = "停车信息";
    public static final String QUESTION_TYPE_NEARBY = "周边推荐";
    public static final String QUESTION_TYPE_ROOM = "客房信息";
    public static final String QUESTION_TYPE_PRICE = "价格咨询";
    public static final String QUESTION_TYPE_SERVICE = "服务咨询";
    public static final String QUESTION_TYPE_OTHER = "其他";

    /**
     * 默认回复
     */
    public static final String DEFAULT_REPLY = "抱歉，我暂时无法回答您的问题。您可以选择转接人工客服获取帮助。";
    
    /**
     * 问候语模板
     */
    public static final String GREETING_TEMPLATE = "您好！欢迎使用智能客服，我是您的AI助手小智。" +
            "请问有什么可以帮助您的？\n\n您可以咨询以下问题：\n" +
            "1. 订房流程\n2. 退房规则\n3. 早餐服务\n4. WiFi信息\n5. 发票索取\n" +
            "或者直接输入您想咨询的问题。";

    /**
     * 知识库状态
     */
    public static final String KB_STATUS_ENABLED = "启用";
    public static final String KB_STATUS_DISABLED = "禁用";

    /**
     * 转人工关键词
     */
    public static final String[] TRANSFER_KEYWORDS = {
            "转人工", "人工客服", "人工服务", "找人工", "真人客服",
            "投诉", "不满意", "差评", "退款", "赔偿"
    };

    /**
     * 关键词匹配阈值
     */
    public static final double MATCH_THRESHOLD = 0.6;  // 相似度阈值
    public static final int MAX_RECOMMEND_COUNT = 5;   // 最大推荐问题数量
    public static final int HOT_QUESTION_COUNT = 10;   // 热门问题显示数量

    /**
     * 会话相关
     */
    public static final int SESSION_TIMEOUT = 30;  // 会话超时时间（分钟）
    public static final int MAX_HISTORY_COUNT = 50; // 最大历史记录数
}
