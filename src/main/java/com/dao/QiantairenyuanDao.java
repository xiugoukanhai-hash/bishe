package com.dao;

import com.entity.QiantairenyuanEntity;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import java.util.List;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.pagination.Pagination;
import org.apache.ibatis.annotations.Param;

/**
 * 前台人员数据访问接口
 */
public interface QiantairenyuanDao extends BaseMapper<QiantairenyuanEntity> {
	
	/**
	 * 分页查询前台人员列表视图
	 */
	List<QiantairenyuanEntity> selectListView(@Param("ew") Wrapper<QiantairenyuanEntity> wrapper);

	/**
	 * 分页查询前台人员列表视图
	 */
	List<QiantairenyuanEntity> selectListView(Pagination page, @Param("ew") Wrapper<QiantairenyuanEntity> wrapper);

	/**
	 * 根据账号查询前台人员
	 */
	QiantairenyuanEntity selectByZhanghao(@Param("qiantaizhanghao") String qiantaizhanghao);

	/**
	 * 根据手机号查询前台人员
	 */
	QiantairenyuanEntity selectByShouji(@Param("shouji") String shouji);

	/**
	 * 统计指定状态的人员数量
	 */
	int countByZhuangtai(@Param("zhuangtai") String zhuangtai);
}
