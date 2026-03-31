package com.controller;

import java.util.Arrays;
import java.util.Map;
import java.util.Date;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.annotation.IgnoreAuth;

import com.entity.TongzhiEntity;
import com.service.TongzhiService;
import com.utils.PageUtils;
import com.utils.R;
import com.utils.MPUtil;

/**
 * 通知
 * 后端接口
 */
@RestController
@RequestMapping("/tongzhi")
public class TongzhiController {
    @Autowired
    private TongzhiService tongzhiService;

    /**
     * 后端列表
     */
    @RequestMapping("/page")
    public R page(@RequestParam Map<String, Object> params, TongzhiEntity tongzhi,
                  HttpServletRequest request) {
        EntityWrapper<TongzhiEntity> ew = new EntityWrapper<TongzhiEntity>();
        PageUtils page = tongzhiService.queryPage(params, MPUtil.sort(MPUtil.between(MPUtil.likeOrEq(ew, tongzhi), params), params));
        return R.ok().put("data", page);
    }

    /**
     * 获取我的通知列表
     */
    @RequestMapping("/mylist")
    public R mylist(@RequestParam Map<String, Object> params, HttpServletRequest request) {
        Long userId = (Long) request.getSession().getAttribute("userId");
        String tableName = (String) request.getSession().getAttribute("tableName");
        
        EntityWrapper<TongzhiEntity> ew = new EntityWrapper<TongzhiEntity>();
        ew.eq("userid", userId);
        ew.eq("tablename", tableName);
        ew.orderBy("addtime", false);  // 按时间倒序
        
        PageUtils page = tongzhiService.queryPage(params, ew);
        return R.ok().put("data", page);
    }

    /**
     * 获取未读通知数量
     */
    @RequestMapping("/unreadCount")
    public R unreadCount(HttpServletRequest request) {
        Long userId = (Long) request.getSession().getAttribute("userId");
        String tableName = (String) request.getSession().getAttribute("tableName");
        
        Integer count = tongzhiService.getUnreadCount(userId, tableName);
        return R.ok().put("data", count);
    }

    /**
     * 标记单条通知已读
     */
    @RequestMapping("/read/{id}")
    public R read(@PathVariable("id") Long id, HttpServletRequest request) {
        tongzhiService.markAsRead(id);
        return R.ok();
    }

    /**
     * 标记所有通知已读
     */
    @RequestMapping("/readAll")
    public R readAll(HttpServletRequest request) {
        Long userId = (Long) request.getSession().getAttribute("userId");
        String tableName = (String) request.getSession().getAttribute("tableName");
        
        tongzhiService.markAllAsRead(userId, tableName);
        return R.ok();
    }

    /**
     * 后端详情
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id) {
        TongzhiEntity tongzhi = tongzhiService.selectById(id);
        // 如果是未读，标记为已读
        if (tongzhi != null && tongzhi.getIsread() == 0) {
            tongzhiService.markAsRead(id);
        }
        return R.ok().put("data", tongzhi);
    }

    /**
     * 后端保存（仅管理员可发送系统通知）
     */
    @RequestMapping("/save")
    public R save(@RequestBody TongzhiEntity tongzhi, HttpServletRequest request) {
        // 验证权限：只有管理员可以发送系统通知
        String tableName = (String) request.getSession().getAttribute("tableName");
        if (!"users".equals(tableName)) {
            return R.error("无权限操作，仅管理员可发送通知");
        }
        
        tongzhi.setId(new Date().getTime() + new Double(Math.floor(Math.random() * 1000)).longValue());
        tongzhi.setAddtime(new Date());
        tongzhi.setIsread(0);
        tongzhiService.insert(tongzhi);
        return R.ok();
    }

    /**
     * 修改（一般不允许修改通知内容）
     */
    @RequestMapping("/update")
    public R update(@RequestBody TongzhiEntity tongzhi, HttpServletRequest request) {
        // 验证权限：只有管理员可以修改通知
        String tableName = (String) request.getSession().getAttribute("tableName");
        if (!"users".equals(tableName)) {
            return R.error("无权限操作");
        }
        tongzhiService.updateById(tongzhi);
        return R.ok();
    }

    /**
     * 删除（仅管理员可批量删除）
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids, HttpServletRequest request) {
        // 验证权限：只有管理员可以批量删除通知
        String tableName = (String) request.getSession().getAttribute("tableName");
        if (!"users".equals(tableName)) {
            return R.error("无权限操作，仅管理员可删除通知");
        }
        tongzhiService.deleteBatchIds(Arrays.asList(ids));
        return R.ok();
    }

    /**
     * 删除我的通知
     */
    @RequestMapping("/deleteMyNotify")
    public R deleteMyNotify(@RequestBody Long[] ids, HttpServletRequest request) {
        Long userId = (Long) request.getSession().getAttribute("userId");
        String tableName = (String) request.getSession().getAttribute("tableName");
        
        // 只删除属于当前用户的通知
        for (Long id : ids) {
            TongzhiEntity tongzhi = tongzhiService.selectById(id);
            if (tongzhi != null && tongzhi.getUserid().equals(userId) 
                    && tongzhi.getTablename().equals(tableName)) {
                tongzhiService.deleteById(id);
            }
        }
        return R.ok();
    }
}
