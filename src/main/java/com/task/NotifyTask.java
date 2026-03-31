package com.task;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.entity.HuiyuanyuyueEntity;
import com.entity.YonghuyuyueEntity;
import com.entity.KefangxinxiEntity;
import com.service.HuiyuanyuyueService;
import com.service.YonghuyuyueService;
import com.service.KefangxinxiService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

/**
 * 通知定时任务
 */
@Component
public class NotifyTask {

    private static final Logger logger = LoggerFactory.getLogger(NotifyTask.class);

    @Autowired
    private YonghuyuyueService yonghuyuyueService;

    @Autowired
    private HuiyuanyuyueService huiyuanyuyueService;

    @Autowired
    private KefangxinxiService kefangxinxiService;

    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    /**
     * 入住提醒任务
     * 每天上午9点执行，提醒明天入住的客人
     */
    @Scheduled(cron = "0 0 9 * * ?")
    public void checkInReminder() {
        logger.info("开始执行入住提醒任务");
        
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, 1);
        String tomorrow = dateFormat.format(cal.getTime());

        EntityWrapper<YonghuyuyueEntity> ew1 = new EntityWrapper<>();
        ew1.eq("yuyuezhuangtai", "paid");
        ew1.ge("ruzhushijian", tomorrow + " 00:00:00");
        ew1.le("ruzhushijian", tomorrow + " 23:59:59");
        List<YonghuyuyueEntity> yonghuList = yonghuyuyueService.selectList(ew1);

        for (YonghuyuyueEntity yuyue : yonghuList) {
            logger.info("发送入住提醒给用户: {}, 房间: {}, 入住时间: {}", 
                    yuyue.getZhanghao(), yuyue.getKefanghao(), yuyue.getRuzhushijian());
        }

        EntityWrapper<HuiyuanyuyueEntity> ew2 = new EntityWrapper<>();
        ew2.eq("yuyuezhuangtai", "paid");
        ew2.ge("ruzhushijian", tomorrow + " 00:00:00");
        ew2.le("ruzhushijian", tomorrow + " 23:59:59");
        List<HuiyuanyuyueEntity> huiyuanList = huiyuanyuyueService.selectList(ew2);

        for (HuiyuanyuyueEntity yuyue : huiyuanList) {
            logger.info("发送入住提醒给会员: {}, 房间: {}, 入住时间: {}", 
                    yuyue.getZhanghao(), yuyue.getKefanghao(), yuyue.getRuzhushijian());
        }
        
        logger.info("入住提醒任务执行完成，共提醒{}位用户，{}位会员", 
                yonghuList.size(), huiyuanList.size());
    }

    /**
     * 预约过期检查任务
     * 每天凌晨1点执行，检查未支付的过期预约
     */
    @Scheduled(cron = "0 0 1 * * ?")
    public void checkExpiredBooking() {
        logger.info("开始执行预约过期检查任务");
        
        String today = dateFormat.format(Calendar.getInstance().getTime());

        EntityWrapper<YonghuyuyueEntity> ew1 = new EntityWrapper<>();
        ew1.eq("yuyuezhuangtai", "approved");
        ew1.lt("ruzhushijian", today + " 00:00:00");
        List<YonghuyuyueEntity> expiredYonghuList = yonghuyuyueService.selectList(ew1);

        for (YonghuyuyueEntity yuyue : expiredYonghuList) {
            yuyue.setYuyuezhuangtai("cancelled");
            yonghuyuyueService.updateById(yuyue);
            
            // 恢复房间状态为空闲
            restoreRoomStatus(yuyue.getKefanghao());
            logger.info("用户预约已过期取消: ID={}, 房间={}", yuyue.getId(), yuyue.getKefanghao());
        }

        EntityWrapper<HuiyuanyuyueEntity> ew2 = new EntityWrapper<>();
        ew2.eq("yuyuezhuangtai", "approved");
        ew2.lt("ruzhushijian", today + " 00:00:00");
        List<HuiyuanyuyueEntity> expiredHuiyuanList = huiyuanyuyueService.selectList(ew2);

        for (HuiyuanyuyueEntity yuyue : expiredHuiyuanList) {
            yuyue.setYuyuezhuangtai("cancelled");
            huiyuanyuyueService.updateById(yuyue);
            
            // 恢复房间状态为空闲
            restoreRoomStatus(yuyue.getKefanghao());
            logger.info("会员预约已过期取消: ID={}, 房间={}", yuyue.getId(), yuyue.getKefanghao());
        }
        
        logger.info("预约过期检查任务执行完成，共取消{}条用户预约，{}条会员预约", 
                expiredYonghuList.size(), expiredHuiyuanList.size());
    }

    /**
     * 房间状态检查任务
     * 每小时执行一次，检查已支付但未入住的预约
     */
    @Scheduled(cron = "0 0 * * * ?")
    public void checkRoomStatus() {
        logger.info("开始执行房间状态检查任务");
        
        String now = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime());

        EntityWrapper<YonghuyuyueEntity> ew1 = new EntityWrapper<>();
        ew1.eq("yuyuezhuangtai", "paid");
        ew1.lt("ruzhushijian", now);
        List<YonghuyuyueEntity> overdueList = yonghuyuyueService.selectList(ew1);

        if (!overdueList.isEmpty()) {
            logger.warn("发现{}条用户预约已过入住时间但未办理入住", overdueList.size());
        }

        EntityWrapper<HuiyuanyuyueEntity> ew2 = new EntityWrapper<>();
        ew2.eq("yuyuezhuangtai", "paid");
        ew2.lt("ruzhushijian", now);
        List<HuiyuanyuyueEntity> overdueHuiyuanList = huiyuanyuyueService.selectList(ew2);

        if (!overdueHuiyuanList.isEmpty()) {
            logger.warn("发现{}条会员预约已过入住时间但未办理入住", overdueHuiyuanList.size());
        }
        
        logger.info("房间状态检查任务执行完成");
    }

    /**
     * 恢复房间状态为空闲（当预约取消时调用）
     */
    private void restoreRoomStatus(String kefanghao) {
        if (StringUtils.isBlank(kefanghao)) return;
        
        KefangxinxiEntity kefang = kefangxinxiService.selectOne(
            new EntityWrapper<KefangxinxiEntity>().eq("kefanghao", kefanghao));
        if (kefang != null && "已预约".equals(kefang.getKefangzhuangtai())) {
            kefang.setKefangzhuangtai("空闲");
            kefangxinxiService.updateById(kefang);
            logger.info("房间{}状态已恢复为空闲", kefanghao);
        }
    }
}
