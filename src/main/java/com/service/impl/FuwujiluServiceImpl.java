package com.service.impl;

import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.utils.PageUtils;
import com.utils.Query;
import com.dao.FuwujiluDao;
import com.entity.FuwujiluEntity;
import com.service.FuwujiluService;

/**
 * 服务记录 服务实现类
 */
@Service("fuwujiluService")
public class FuwujiluServiceImpl extends ServiceImpl<FuwujiluDao, FuwujiluEntity> implements FuwujiluService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        Page<FuwujiluEntity> page = this.selectPage(
                new Query<FuwujiluEntity>(params).getPage(),
                new EntityWrapper<FuwujiluEntity>()
        );
        return new PageUtils(page);
    }

    @Override
    public PageUtils queryPage(Map<String, Object> params, Wrapper<FuwujiluEntity> wrapper) {
        Page<FuwujiluEntity> page = this.selectPage(
                new Query<FuwujiluEntity>(params).getPage(),
                wrapper
        );
        return new PageUtils(page);
    }
}
