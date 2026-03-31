package com.service.impl;

import org.springframework.stereotype.Service;
import java.util.Date;
import java.util.Map;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.utils.PageUtils;
import com.utils.Query;
import com.dao.TongzhiDao;
import com.entity.TongzhiEntity;
import com.service.TongzhiService;

/**
 * 通知 服务实现类
 */
@Service("tongzhiService")
public class TongzhiServiceImpl extends ServiceImpl<TongzhiDao, TongzhiEntity> implements TongzhiService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        Page<TongzhiEntity> page = this.selectPage(
                new Query<TongzhiEntity>(params).getPage(),
                new EntityWrapper<TongzhiEntity>()
        );
        return new PageUtils(page);
    }

    @Override
    public PageUtils queryPage(Map<String, Object> params, Wrapper<TongzhiEntity> wrapper) {
        Page<TongzhiEntity> page = this.selectPage(
                new Query<TongzhiEntity>(params).getPage(),
                wrapper
        );
        return new PageUtils(page);
    }

    @Override
    public Integer getUnreadCount(Long userid, String tablename) {
        return this.selectCount(new EntityWrapper<TongzhiEntity>()
                .eq("userid", userid)
                .eq("tablename", tablename)
                .eq("isread", 0));
    }

    @Override
    public void markAsRead(Long id) {
        TongzhiEntity tongzhi = this.selectById(id);
        if (tongzhi != null && tongzhi.getIsread() == 0) {
            tongzhi.setIsread(1);
            tongzhi.setReadtime(new Date());
            this.updateById(tongzhi);
        }
    }

    @Override
    public void markAllAsRead(Long userid, String tablename) {
        TongzhiEntity entity = new TongzhiEntity();
        entity.setIsread(1);
        entity.setReadtime(new Date());
        this.update(entity, new EntityWrapper<TongzhiEntity>()
                .eq("userid", userid)
                .eq("tablename", tablename)
                .eq("isread", 0));
    }

    @Override
    public void sendNotify(TongzhiEntity tongzhi) {
        if (tongzhi.getAddtime() == null) {
            tongzhi.setAddtime(new Date());
        }
        if (tongzhi.getIsread() == null) {
            tongzhi.setIsread(0);
        }
        this.insert(tongzhi);
    }
}
