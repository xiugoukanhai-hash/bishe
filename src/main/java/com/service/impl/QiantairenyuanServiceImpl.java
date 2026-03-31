package com.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.utils.PageUtils;
import com.utils.Query;
import com.dao.QiantairenyuanDao;
import com.entity.QiantairenyuanEntity;
import com.service.QiantairenyuanService;

/**
 * 前台人员服务实现类
 */
@Service("qiantairenyuanService")
public class QiantairenyuanServiceImpl extends ServiceImpl<QiantairenyuanDao, QiantairenyuanEntity> implements QiantairenyuanService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        Page<QiantairenyuanEntity> page = this.selectPage(
                new Query<QiantairenyuanEntity>(params).getPage(),
                new EntityWrapper<QiantairenyuanEntity>()
        );
        return new PageUtils(page);
    }

    @Override
    public PageUtils queryPage(Map<String, Object> params, Wrapper<QiantairenyuanEntity> wrapper) {
        Page<QiantairenyuanEntity> page = this.selectPage(
                new Query<QiantairenyuanEntity>(params).getPage(),
                wrapper
        );
        return new PageUtils(page);
    }

    @Override
    public QiantairenyuanEntity selectByZhanghao(String qiantaizhanghao) {
        if (StringUtils.isBlank(qiantaizhanghao)) {
            return null;
        }
        return baseMapper.selectByZhanghao(qiantaizhanghao);
    }

    @Override
    public QiantairenyuanEntity selectByShouji(String shouji) {
        if (StringUtils.isBlank(shouji)) {
            return null;
        }
        return baseMapper.selectByShouji(shouji);
    }

    @Override
    public QiantairenyuanEntity login(String username, String password) {
        if (StringUtils.isBlank(username) || StringUtils.isBlank(password)) {
            return null;
        }
        QiantairenyuanEntity user = baseMapper.selectByZhanghao(username);
        if (user != null && password.equals(user.getMima())) {
            return user;
        }
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updatePassword(Long id, String oldPassword, String newPassword) {
        if (id == null || StringUtils.isBlank(oldPassword) || StringUtils.isBlank(newPassword)) {
            return false;
        }
        QiantairenyuanEntity user = this.selectById(id);
        if (user == null || !oldPassword.equals(user.getMima())) {
            return false;
        }
        user.setMima(newPassword);
        return this.updateById(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean resetPassword(String zhanghao, String shouji, String newPassword) {
        if (StringUtils.isBlank(zhanghao) || StringUtils.isBlank(newPassword)) {
            return false;
        }
        QiantairenyuanEntity user = baseMapper.selectByZhanghao(zhanghao);
        if (user == null) {
            return false;
        }
        if (StringUtils.isNotBlank(shouji) && !shouji.equals(user.getShouji())) {
            return false;
        }
        user.setMima(newPassword);
        return this.updateById(user);
    }

    @Override
    public int countOnJob() {
        return baseMapper.countByZhuangtai("在职");
    }

    @Override
    public boolean existsByZhanghao(String qiantaizhanghao) {
        return selectByZhanghao(qiantaizhanghao) != null;
    }

    @Override
    public boolean existsByShouji(String shouji) {
        return selectByShouji(shouji) != null;
    }
}
