
package com.service;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.service.IService;
import com.entity.TokenEntity;
import com.utils.PageUtils;


/**
 * Token服务接口
 * 提供Token生成、验证、刷新、删除等功能
 */
public interface TokenService extends IService<TokenEntity> {
    
    PageUtils queryPage(Map<String, Object> params);
    
    List<TokenEntity> selectListView(Wrapper<TokenEntity> wrapper);
    
    PageUtils queryPage(Map<String, Object> params, Wrapper<TokenEntity> wrapper);

    /**
     * 生成Token
     * @param userid 用户ID
     * @param username 用户名
     * @param tableName 表名
     * @param role 角色
     * @return token字符串
     */
    String generateToken(Long userid, String username, String tableName, String role);
    
    /**
     * 获取Token实体
     * @param token token字符串
     * @return Token实体
     */
    TokenEntity getTokenEntity(String token);

    /**
     * 验证Token是否有效
     * @param token token字符串
     * @return true-有效，false-无效
     */
    boolean validateToken(String token);

    /**
     * 刷新Token有效期
     * @param token token字符串
     * @return true-刷新成功，false-刷新失败
     */
    boolean refreshToken(String token);

    /**
     * 删除Token（退出登录）
     * @param token token字符串
     */
    void deleteToken(String token);

    /**
     * 根据用户ID和表名删除Token
     * @param userid 用户ID
     * @param tableName 表名
     */
    void deleteByUserAndTable(Long userid, String tableName);
}
