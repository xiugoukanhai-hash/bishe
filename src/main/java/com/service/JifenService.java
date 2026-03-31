package com.service;

import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.service.IService;
import com.utils.PageUtils;
import com.entity.JifenEntity;
import java.util.List;
import java.util.Map;

/**
 * 积分记录
 */
public interface JifenService extends IService<JifenEntity> {

    PageUtils queryPage(Map<String, Object> params);
   	
   	PageUtils queryPage(Map<String, Object> params, Wrapper<JifenEntity> wrapper);
   	
   	/**
   	 * 增加积分
   	 * @param huiyuanid 会员ID
   	 * @param zhanghao 账号
   	 * @param points 积分数（正数）
   	 * @param type 积分类型
   	 * @param remark 备注说明
   	 * @param orderNo 关联订单号
   	 */
   	void addPoints(Long huiyuanid, String zhanghao, int points, String type, String remark, String orderNo);
   	
   	/**
   	 * 扣减积分
   	 * @param huiyuanid 会员ID
   	 * @param zhanghao 账号
   	 * @param points 积分数（正数，会自动转为负数）
   	 * @param type 积分类型
   	 * @param remark 备注说明
   	 * @param orderNo 关联订单号
   	 * @return 是否扣减成功
   	 */
   	boolean deductPoints(Long huiyuanid, String zhanghao, int points, String type, String remark, String orderNo);
   	
   	/**
   	 * 获取会员当前积分余额
   	 */
   	Integer getBalance(Long huiyuanid);
}
