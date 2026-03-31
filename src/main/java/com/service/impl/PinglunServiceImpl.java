package com.service.impl;

import org.springframework.stereotype.Service;
import java.util.Map;
import java.util.List;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.utils.PageUtils;
import com.utils.Query;
import com.dao.PinglunDao;
import com.entity.PinglunEntity;
import com.service.PinglunService;
import com.entity.vo.PinglunVO;
import com.entity.view.PinglunView;

@Service("pinglunService")
public class PinglunServiceImpl extends ServiceImpl<PinglunDao, PinglunEntity> implements PinglunService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        Page<PinglunEntity> page = this.selectPage(
                new Query<PinglunEntity>(params).getPage(),
                new EntityWrapper<PinglunEntity>()
        );
        return new PageUtils(page);
    }

    @Override
    public PageUtils queryPage(Map<String, Object> params, Wrapper<PinglunEntity> wrapper) {
        Page<PinglunView> page = new Query<PinglunView>(params).getPage();
        page.setRecords(baseMapper.selectListView(page, wrapper));
        return new PageUtils(page);
    }

    @Override
    public List<PinglunVO> selectListVO(Wrapper<PinglunEntity> wrapper) {
        return baseMapper.selectListVO(wrapper);
    }

    @Override
    public PinglunVO selectVO(Wrapper<PinglunEntity> wrapper) {
        return baseMapper.selectVO(wrapper);
    }

    @Override
    public List<PinglunView> selectListView(Wrapper<PinglunEntity> wrapper) {
        return baseMapper.selectListView(wrapper);
    }

    @Override
    public PinglunView selectView(Wrapper<PinglunEntity> wrapper) {
        return baseMapper.selectView(wrapper);
    }
}
