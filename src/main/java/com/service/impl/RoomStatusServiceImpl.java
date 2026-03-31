package com.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.constant.CommonConstant;
import com.entity.KefangxinxiEntity;
import com.exception.CustomException;
import com.service.KefangxinxiService;
import com.service.RoomStatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 客房状态流转服务实现类
 */
@Service("roomStatusService")
public class RoomStatusServiceImpl implements RoomStatusService {

    @Autowired
    private KefangxinxiService kefangxinxiService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void setFree(String kefanghao) {
        updateStatus(kefanghao, CommonConstant.ROOM_STATUS_FREE);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void setBooked(String kefanghao) {
        updateStatus(kefanghao, CommonConstant.ROOM_STATUS_BOOKED);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void setOccupied(String kefanghao) {
        updateStatus(kefanghao, CommonConstant.ROOM_STATUS_OCCUPIED);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void setCleaning(String kefanghao) {
        updateStatus(kefanghao, CommonConstant.ROOM_STATUS_CLEANING);
        setCleanStatus(kefanghao, CommonConstant.CLEAN_STATUS_PENDING);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void setMaintenance(String kefanghao) {
        updateStatus(kefanghao, CommonConstant.ROOM_STATUS_MAINTENANCE);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void setCleanStatus(String kefanghao, String weishengqingkuang) {
        KefangxinxiEntity room = getByKefanghao(kefanghao);
        if (room == null) {
            throw new CustomException("客房不存在");
        }
        room.setWeishengqingkuang(weishengqingkuang);
        kefangxinxiService.updateById(room);
    }

    @Override
    public boolean canBook(String kefanghao) {
        KefangxinxiEntity room = getByKefanghao(kefanghao);
        if (room == null) {
            return false;
        }
        String status = room.getKefangzhuangtai();
        String cleanStatus = room.getWeishengqingkuang();
        // 房间状态必须是空闲或未入住，且清洁状态必须是已清扫
        boolean statusOk = CommonConstant.ROOM_STATUS_FREE.equals(status) || 
                          "空闲".equals(status) ||
                          "未入住".equals(status);
        boolean cleanOk = CommonConstant.CLEAN_STATUS_DONE.equals(cleanStatus) ||
                         "已清扫".equals(cleanStatus) ||
                         cleanStatus == null;
        return statusOk && cleanOk;
    }

    @Override
    public boolean canCheckIn(String kefanghao) {
        KefangxinxiEntity room = getByKefanghao(kefanghao);
        if (room == null) {
            return false;
        }
        String status = room.getKefangzhuangtai();
        String cleanStatus = room.getWeishengqingkuang();
        return (CommonConstant.ROOM_STATUS_BOOKED.equals(status) || 
                CommonConstant.ROOM_STATUS_FREE.equals(status) ||
                "已预约".equals(status) ||
                "未入住".equals(status)) &&
               (CommonConstant.CLEAN_STATUS_DONE.equals(cleanStatus) || 
                "已清扫".equals(cleanStatus) ||
                cleanStatus == null);
    }

    @Override
    public KefangxinxiEntity getByKefanghao(String kefanghao) {
        EntityWrapper<KefangxinxiEntity> wrapper = new EntityWrapper<>();
        wrapper.eq("kefanghao", kefanghao);
        return kefangxinxiService.selectOne(wrapper);
    }

    /**
     * 更新客房状态
     */
    private void updateStatus(String kefanghao, String status) {
        KefangxinxiEntity room = getByKefanghao(kefanghao);
        if (room == null) {
            throw new CustomException("客房不存在");
        }
        room.setKefangzhuangtai(status);
        kefangxinxiService.updateById(room);
    }
}
