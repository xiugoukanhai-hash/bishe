package com.service;

import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.service.IService;
import com.utils.PageUtils;
import com.entity.FuwujiluEntity;
import java.util.List;
import java.util.Map;

/**
 * 服务记录
 */
public interface FuwujiluService extends IService<FuwujiluEntity> {

    PageUtils queryPage(Map<String, Object> params);
   	
   	PageUtils queryPage(Map<String, Object> params, Wrapper<FuwujiluEntity> wrapper);
}
