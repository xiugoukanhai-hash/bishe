package com.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 订单编号生成工具类
 * 提供多种格式的订单号生成方法
 */
public class OrderNumberUtil {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyyMMddHHmmss");
    private static final SimpleDateFormat DATE_FORMAT_SHORT = new SimpleDateFormat("yyyyMMdd");
    private static final Random RANDOM = new Random();
    private static final AtomicInteger SEQUENCE = new AtomicInteger(0);

    private OrderNumberUtil() {
        throw new IllegalStateException("工具类不允许实例化");
    }

    /**
     * 生成预约编号
     * 格式：YY + 年月日时分秒 + 6位随机数
     * 示例：YY20260228153045123456
     */
    public static String generateBookingNumber() {
        return "YY" + DATE_FORMAT.format(new Date()) + generateRandomNumber(6);
    }

    /**
     * 生成订单编号
     * 格式：DD + 时间戳
     * 示例：DD1709123456789
     */
    public static String generateOrderNumber() {
        return "DD" + System.currentTimeMillis();
    }

    /**
     * 生成入住编号
     * 格式：RZ + 年月日 + 4位序列号
     * 示例：RZ202602280001
     */
    public static String generateCheckInNumber() {
        String date = DATE_FORMAT_SHORT.format(new Date());
        int seq = SEQUENCE.incrementAndGet() % 10000;
        return "RZ" + date + String.format("%04d", seq);
    }

    /**
     * 生成退房编号
     * 格式：TF + 年月日 + 4位序列号
     * 示例：TF202602280001
     */
    public static String generateCheckOutNumber() {
        String date = DATE_FORMAT_SHORT.format(new Date());
        int seq = SEQUENCE.incrementAndGet() % 10000;
        return "TF" + date + String.format("%04d", seq);
    }

    /**
     * 生成服务记录编号
     * 格式：FW + 年月日时分秒 + 4位随机数
     * 示例：FW202602281530451234
     */
    public static String generateServiceNumber() {
        return "FW" + DATE_FORMAT.format(new Date()) + generateRandomNumber(4);
    }

    /**
     * 生成清扫任务编号
     * 格式：QS + 年月日 + 4位随机数
     * 示例：QS202602281234
     */
    public static String generateCleanTaskNumber() {
        return "QS" + DATE_FORMAT_SHORT.format(new Date()) + generateRandomNumber(4);
    }

    /**
     * 生成通知ID
     * 格式：时间戳 + 3位随机数
     */
    public static Long generateNotifyId() {
        return System.currentTimeMillis() * 1000 + RANDOM.nextInt(1000);
    }

    /**
     * 生成支付流水号
     * 格式：PAY + 年月日时分秒 + 6位随机数
     */
    public static String generatePaymentNumber() {
        return "PAY" + DATE_FORMAT.format(new Date()) + generateRandomNumber(6);
    }

    /**
     * 生成指定位数的随机数字字符串
     * @param length 位数
     * @return 随机数字字符串
     */
    public static String generateRandomNumber(int length) {
        if (length <= 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(RANDOM.nextInt(10));
        }
        return sb.toString();
    }

    /**
     * 生成带前缀的唯一编号
     * @param prefix 前缀
     * @return 带前缀的唯一编号
     */
    public static String generateWithPrefix(String prefix) {
        return prefix + DATE_FORMAT.format(new Date()) + generateRandomNumber(4);
    }

    /**
     * 解析订单编号中的时间
     * @param orderNumber 订单编号
     * @return 时间戳字符串，解析失败返回null
     */
    public static String parseTimeFromOrderNumber(String orderNumber) {
        if (orderNumber == null || orderNumber.length() < 16) {
            return null;
        }
        try {
            String timeStr = orderNumber.substring(2, 16);
            return timeStr;
        } catch (Exception e) {
            return null;
        }
    }
}
