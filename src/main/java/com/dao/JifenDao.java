package com.dao;

import com.entity.JifenEntity;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import java.util.List;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.pagination.Pagination;
import org.apache.ibatis.annotations.Param;

/**
 * 积分记录
 */
public interface JifenDao extends BaseMapper<JifenEntity> {
	
	List<JifenEntity> selectListView(@Param("ew") Wrapper<JifenEntity> wrapper);

	List<JifenEntity> selectListView(Pagination page, @Param("ew") Wrapper<JifenEntity> wrapper);
	
	/**
	 * 统计会员总积分
	 */
	Integer sumByHuiyuanId(@Param("huiyuanid") Long huiyuanid);
}
