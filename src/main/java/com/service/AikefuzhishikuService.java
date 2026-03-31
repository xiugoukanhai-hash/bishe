package com.service;

import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.service.IService;
import com.utils.PageUtils;
import com.entity.AikefuzhishikuEntity;
import java.util.List;
import java.util.Map;

/**
 * AI客服知识库
 */
public interface AikefuzhishikuService extends IService<AikefuzhishikuEntity> {

    PageUtils queryPage(Map<String, Object> params);
   	
   	PageUtils queryPage(Map<String, Object> params, Wrapper<AikefuzhishikuEntity> wrapper);
   	
   	/**
   	 * 智能回答问题
   	 * @param question 用户问题
   	 * @return 答案，如果没有匹配则返回默认回复
   	 */
   	String smartAnswer(String question);
   	
   	/**
   	 * 获取热门问题
   	 */
   	List<AikefuzhishikuEntity> getHotQuestions(Integer limit);
   	
   	/**
   	 * 增加点击次数
   	 */
   	void incrementClickCount(Long id);
}
