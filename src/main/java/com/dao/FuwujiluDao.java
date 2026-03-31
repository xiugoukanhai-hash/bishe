package com.dao;

import com.entity.FuwujiluEntity;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import java.util.List;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.pagination.Pagination;
import org.apache.ibatis.annotations.Param;

/**
 * 服务记录
 */
public interface FuwujiluDao extends BaseMapper<FuwujiluEntity> {
	
	List<FuwujiluEntity> selectListView(@Param("ew") Wrapper<FuwujiluEntity> wrapper);

	List<FuwujiluEntity> selectListView(Pagination page, @Param("ew") Wrapper<FuwujiluEntity> wrapper);
}
