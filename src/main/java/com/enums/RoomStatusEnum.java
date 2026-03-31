package com.enums;

/**
 * 客房状态枚举
 * 定义客房所有可能的状态及其描述
 */
public enum RoomStatusEnum {

    FREE("空闲", "客房空闲可预订"),
    BOOKED("已预订", "已被预订待入住"),
    OCCUPIED("已入住", "客人正在住宿"),
    CLEANING("待清扫", "等待清洁打扫"),
    MAINTENANCE("维修中", "正在维修暂不可用"),
    FULL("已客满", "房间已满不可预订");

    private final String code;
    private final String desc;

    RoomStatusEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    /**
     * 根据状态码获取枚举
     * @param code 状态码
     * @return 对应的枚举，未找到返回null
     */
    public static RoomStatusEnum getByCode(String code) {
        if (code == null || code.isEmpty()) {
            return null;
        }
        for (RoomStatusEnum status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return null;
    }

    /**
     * 判断客房是否可预订
     * @param statusCode 状态码
     * @return true-可预订 false-不可预订
     */
    public static boolean canBook(String statusCode) {
        RoomStatusEnum status = getByCode(statusCode);
        return status != null && status == FREE;
    }

    /**
     * 判断客房是否可入住
     * @param statusCode 状态码
     * @return true-可入住 false-不可入住
     */
    public static boolean canCheckIn(String statusCode) {
        RoomStatusEnum status = getByCode(statusCode);
        return status != null && (status == FREE || status == BOOKED);
    }

    /**
     * 判断客房是否可退房
     * @param statusCode 状态码
     * @return true-可退房 false-不可退房
     */
    public static boolean canCheckOut(String statusCode) {
        RoomStatusEnum status = getByCode(statusCode);
        return status != null && status == OCCUPIED;
    }

    /**
     * 判断是否为有效状态码
     * @param statusCode 状态码
     * @return true-有效 false-无效
     */
    public static boolean isValidStatus(String statusCode) {
        return getByCode(statusCode) != null;
    }
}
