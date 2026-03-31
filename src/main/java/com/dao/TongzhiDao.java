package com.dao;

import com.entity.TongzhiEntity;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import java.util.List;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.pagination.Pagination;
import org.apache.ibatis.annotations.Param;

/**
 * 通知
 */
public interface TongzhiDao extends BaseMapper<TongzhiEntity> {
	
	List<TongzhiEntity> selectListView(@Param("ew") Wrapper<TongzhiEntity> wrapper);

	List<TongzhiEntity> selectListView(Pagination page, @Param("ew") Wrapper<TongzhiEntity> wrapper);
	
	/**
	 * 获取用户未读通知数量
	 */
	Integer selectUnreadCount(@Param("userid") Long userid, @Param("tablename") String tablename);
}
