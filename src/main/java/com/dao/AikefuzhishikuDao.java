package com.dao;

import com.entity.AikefuzhishikuEntity;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import java.util.List;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.pagination.Pagination;
import org.apache.ibatis.annotations.Param;

/**
 * AI客服知识库
 */
public interface AikefuzhishikuDao extends BaseMapper<AikefuzhishikuEntity> {
	
	List<AikefuzhishikuEntity> selectListView(@Param("ew") Wrapper<AikefuzhishikuEntity> wrapper);

	List<AikefuzhishikuEntity> selectListView(Pagination page, @Param("ew") Wrapper<AikefuzhishikuEntity> wrapper);
	
	/**
	 * 根据关键词搜索知识库
	 */
	List<AikefuzhishikuEntity> searchByKeyword(@Param("keyword") String keyword);
	
	/**
	 * 获取热门问题
	 */
	List<AikefuzhishikuEntity> selectHotQuestions(@Param("limit") Integer limit);
}
