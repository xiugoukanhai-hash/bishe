package com.service;

import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.service.IService;
import com.utils.PageUtils;
import com.entity.QiantairenyuanEntity;
import java.util.List;
import java.util.Map;

/**
 * 前台人员服务接口
 */
public interface QiantairenyuanService extends IService<QiantairenyuanEntity> {

    /**
     * 分页查询前台人员列表
     */
    PageUtils queryPage(Map<String, Object> params);
   	
    /**
     * 分页查询前台人员列表（带条件）
     */
    PageUtils queryPage(Map<String, Object> params, Wrapper<QiantairenyuanEntity> wrapper);

    /**
     * 根据账号查询前台人员
     */
    QiantairenyuanEntity selectByZhanghao(String qiantaizhanghao);

    /**
     * 根据手机号查询前台人员
     */
    QiantairenyuanEntity selectByShouji(String shouji);

    /**
     * 前台人员登录
     */
    QiantairenyuanEntity login(String username, String password);

    /**
     * 修改密码
     */
    boolean updatePassword(Long id, String oldPassword, String newPassword);

    /**
     * 重置密码
     */
    boolean resetPassword(String zhanghao, String shouji, String newPassword);

    /**
     * 统计在职人员数量
     */
    int countOnJob();

    /**
     * 验证账号是否存在
     */
    boolean existsByZhanghao(String qiantaizhanghao);

    /**
     * 验证手机号是否存在
     */
    boolean existsByShouji(String shouji);
}
