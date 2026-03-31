package com.service;

import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.service.IService;
import com.utils.PageUtils;
import com.entity.TongzhiEntity;
import java.util.List;
import java.util.Map;

/**
 * 通知
 */
public interface TongzhiService extends IService<TongzhiEntity> {

    PageUtils queryPage(Map<String, Object> params);
   	
   	PageUtils queryPage(Map<String, Object> params, Wrapper<TongzhiEntity> wrapper);
   	
   	/**
   	 * 获取用户未读通知数量
   	 */
   	Integer getUnreadCount(Long userid, String tablename);
   	
   	/**
   	 * 标记通知为已读
   	 */
   	void markAsRead(Long id);
   	
   	/**
   	 * 标记用户所有通知为已读
   	 */
   	void markAllAsRead(Long userid, String tablename);
   	
   	/**
   	 * 发送通知
   	 */
   	void sendNotify(TongzhiEntity tongzhi);
}
