package com.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 密码加密工具类
 * 提供MD5加盐加密和密码验证功能
 */
public class PasswordUtil {

    /**
     * 盐值
     */
    private static final String SALT = "hotel_management_system_2026";

    /**
     * MD5加密（带盐值）
     * @param password 原始密码
     * @return 加密后的密码
     */
    public static String encrypt(String password) {
        if (password == null || password.isEmpty()) {
            return null;
        }
        return md5(password + SALT);
    }

    /**
     * 验证密码
     * @param inputPassword 输入的密码
     * @param encryptedPassword 数据库中的加密密码
     * @return true-匹配，false-不匹配
     */
    public static boolean verify(String inputPassword, String encryptedPassword) {
        if (inputPassword == null || encryptedPassword == null) {
            return false;
        }
        String encrypted = encrypt(inputPassword);
        return encrypted != null && encrypted.equals(encryptedPassword);
    }

    /**
     * 验证密码（兼容明文）
     * @param inputPassword 输入的密码
     * @param storedPassword 存储的密码（可能是明文或加密）
     * @return true-匹配，false-不匹配
     */
    public static boolean verifyWithPlaintext(String inputPassword, String storedPassword) {
        if (inputPassword == null || storedPassword == null) {
            return false;
        }
        // 先尝试明文匹配
        if (inputPassword.equals(storedPassword)) {
            return true;
        }
        // 再尝试加密匹配
        return verify(inputPassword, storedPassword);
    }

    /**
     * MD5加密
     */
    private static String md5(String str) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(str.getBytes());
            byte[] digest = md.digest();
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b & 0xff));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("MD5加密失败", e);
        }
    }

    /**
     * 生成随机密码
     * @param length 密码长度
     * @return 随机密码
     */
    public static String generateRandomPassword(int length) {
        if (length < 6) {
            length = 6;
        }
        String chars = "ABCDEFGHJKMNPQRSTUVWXYZabcdefghjkmnpqrstuvwxyz23456789";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int index = (int) (Math.random() * chars.length());
            sb.append(chars.charAt(index));
        }
        return sb.toString();
    }

    /**
     * 检查密码强度
     * @param password 密码
     * @return 0-弱，1-中，2-强
     */
    public static int checkPasswordStrength(String password) {
        if (password == null || password.length() < 6) {
            return 0;
        }

        int strength = 0;
        boolean hasLower = password.matches(".*[a-z].*");
        boolean hasUpper = password.matches(".*[A-Z].*");
        boolean hasDigit = password.matches(".*\\d.*");
        boolean hasSpecial = password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?].*");

        if (hasLower) strength++;
        if (hasUpper) strength++;
        if (hasDigit) strength++;
        if (hasSpecial) strength++;
        if (password.length() >= 10) strength++;

        if (strength <= 2) return 0;
        if (strength <= 3) return 1;
        return 2;
    }
}
