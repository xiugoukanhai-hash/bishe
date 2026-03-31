package com.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.Date;
import java.util.Map;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.utils.PageUtils;
import com.utils.Query;
import com.dao.JifenDao;
import com.dao.HuiyuanDao;
import com.entity.JifenEntity;
import com.entity.HuiyuanEntity;
import com.service.JifenService;

/**
 * 积分记录 服务实现类
 */
@Service("jifenService")
public class JifenServiceImpl extends ServiceImpl<JifenDao, JifenEntity> implements JifenService {

    @Autowired
    private HuiyuanDao huiyuanDao;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        Page<JifenEntity> page = this.selectPage(
                new Query<JifenEntity>(params).getPage(),
                new EntityWrapper<JifenEntity>()
        );
        return new PageUtils(page);
    }

    @Override
    public PageUtils queryPage(Map<String, Object> params, Wrapper<JifenEntity> wrapper) {
        Page<JifenEntity> page = this.selectPage(
                new Query<JifenEntity>(params).getPage(),
                wrapper
        );
        return new PageUtils(page);
    }

    @Override
    public void addPoints(Long huiyuanid, String zhanghao, int points, String type, String remark, String orderNo) {
        if (points <= 0) {
            return;
        }
        
        // 获取当前余额
        Integer currentBalance = this.getBalance(huiyuanid);
        int newBalance = (currentBalance == null ? 0 : currentBalance) + points;
        
        // 创建积分记录
        JifenEntity jifen = new JifenEntity();
        jifen.setHuiyuanid(huiyuanid);
        jifen.setZhanghao(zhanghao);
        jifen.setJifenleixing(type);
        jifen.setJifenshu(points);
        jifen.setYue(newBalance);
        jifen.setShuoming(remark);
        jifen.setGuanliandingdan(orderNo);
        jifen.setAddtime(new Date());
        this.insert(jifen);
        
        // 更新会员积分
        HuiyuanEntity huiyuan = huiyuanDao.selectById(huiyuanid);
        if (huiyuan != null) {
            huiyuan.setJifen(newBalance);
            huiyuanDao.updateById(huiyuan);
        }
    }

    @Override
    public boolean deductPoints(Long huiyuanid, String zhanghao, int points, String type, String remark, String orderNo) {
        if (points <= 0) {
            return false;
        }
        
        // 获取当前余额
        Integer currentBalance = this.getBalance(huiyuanid);
        if (currentBalance == null || currentBalance < points) {
            return false; // 余额不足
        }
        
        int newBalance = currentBalance - points;
        
        // 创建积分记录（负数）
        JifenEntity jifen = new JifenEntity();
        jifen.setHuiyuanid(huiyuanid);
        jifen.setZhanghao(zhanghao);
        jifen.setJifenleixing(type);
        jifen.setJifenshu(-points);  // 存储为负数
        jifen.setYue(newBalance);
        jifen.setShuoming(remark);
        jifen.setGuanliandingdan(orderNo);
        jifen.setAddtime(new Date());
        this.insert(jifen);
        
        // 更新会员积分
        HuiyuanEntity huiyuan = huiyuanDao.selectById(huiyuanid);
        if (huiyuan != null) {
            huiyuan.setJifen(newBalance);
            huiyuanDao.updateById(huiyuan);
        }
        
        return true;
    }

    @Override
    public Integer getBalance(Long huiyuanid) {
        HuiyuanEntity huiyuan = huiyuanDao.selectById(huiyuanid);
        if (huiyuan != null && huiyuan.getJifen() != null) {
            return huiyuan.getJifen();
        }
        return 0;
    }
}
