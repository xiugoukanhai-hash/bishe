package com.enums;

/**
 * 服务类型枚举
 * 定义前台可提供的各类服务
 */
public enum ServiceTypeEnum {

    BED("加床服务", "提供额外床铺服务", true),
    BREAKFAST("早餐预订", "预订早餐服务", true),
    LATE_CHECKOUT("延时退房", "延迟退房时间", true),
    WAKEUP("叫醒服务", "电话叫醒服务", false),
    BORROW("物品借用", "借用酒店物品", false),
    REPAIR("维修服务", "房间设施维修", false),
    LAUNDRY("洗衣服务", "衣物清洗服务", true),
    CLEANING("额外清洁", "房间额外清洁", false),
    LUGGAGE("行李寄存", "行李寄存服务", false),
    OTHER("其他", "其他服务需求", false);

    private final String code;
    private final String desc;
    private final boolean needCharge;  // 是否需要额外收费

    ServiceTypeEnum(String code, String desc, boolean needCharge) {
        this.code = code;
        this.desc = desc;
        this.needCharge = needCharge;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public boolean isNeedCharge() {
        return needCharge;
    }

    /**
     * 根据类型码获取枚举
     * @param code 类型码
     * @return 对应的枚举
     */
    public static ServiceTypeEnum getByCode(String code) {
        if (code == null || code.isEmpty()) {
            return null;
        }
        for (ServiceTypeEnum type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return null;
    }

    /**
     * 获取所有服务类型的描述列表
     * @return 服务类型描述数组
     */
    public static String[] getAllTypeCodes() {
        ServiceTypeEnum[] enums = values();
        String[] codes = new String[enums.length];
        for (int i = 0; i < enums.length; i++) {
            codes[i] = enums[i].getCode();
        }
        return codes;
    }

    /**
     * 判断服务是否需要收费
     * @param code 服务类型码
     * @return true-需要收费 false-免费
     */
    public static boolean isChargeableService(String code) {
        ServiceTypeEnum type = getByCode(code);
        return type != null && type.isNeedCharge();
    }

    /**
     * 判断是否为有效的服务类型
     * @param code 类型码
     * @return true-有效 false-无效
     */
    public static boolean isValidType(String code) {
        return getByCode(code) != null;
    }
}
