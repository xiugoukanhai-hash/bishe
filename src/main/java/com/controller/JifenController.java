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

import com.entity.JifenEntity;
import com.service.JifenService;
import com.utils.PageUtils;
import com.utils.R;
import com.utils.MPUtil;

/**
 * 积分记录
 * 后端接口
 */
@RestController
@RequestMapping("/jifen")
public class JifenController {
    @Autowired
    private JifenService jifenService;

    /**
     * 后端列表
     */
    @RequestMapping("/page")
    public R page(@RequestParam Map<String, Object> params, JifenEntity jifen,
                  HttpServletRequest request) {
        EntityWrapper<JifenEntity> ew = new EntityWrapper<JifenEntity>();
        PageUtils page = jifenService.queryPage(params, MPUtil.sort(MPUtil.between(MPUtil.likeOrEq(ew, jifen), params), params));
        return R.ok().put("data", page);
    }

    /**
     * 获取我的积分记录
     */
    @RequestMapping("/mylist")
    public R mylist(@RequestParam Map<String, Object> params, HttpServletRequest request) {
        Long userId = (Long) request.getSession().getAttribute("userId");
        String tableName = (String) request.getSession().getAttribute("tableName");
        
        // 只有会员才有积分
        if (!"huiyuan".equals(tableName)) {
            return R.error("只有会员才能查看积分");
        }
        
        EntityWrapper<JifenEntity> ew = new EntityWrapper<JifenEntity>();
        ew.eq("huiyuanid", userId);
        ew.orderBy("addtime", false);
        
        PageUtils page = jifenService.queryPage(params, ew);
        return R.ok().put("data", page);
    }

    /**
     * 获取我的积分余额
     */
    @RequestMapping("/balance")
    public R balance(HttpServletRequest request) {
        Long userId = (Long) request.getSession().getAttribute("userId");
        String tableName = (String) request.getSession().getAttribute("tableName");
        
        if (!"huiyuan".equals(tableName)) {
            return R.error("只有会员才有积分");
        }
        
        Integer balance = jifenService.getBalance(userId);
        return R.ok().put("data", balance);
    }

    /**
     * 后端详情
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id) {
        JifenEntity jifen = jifenService.selectById(id);
        return R.ok().put("data", jifen);
    }

    /**
     * 后端保存（仅管理员可手动调整积分）
     */
    @RequestMapping("/save")
    public R save(@RequestBody JifenEntity jifen, HttpServletRequest request) {
        // 验证权限：只有管理员可以手动调整积分
        String tableName = (String) request.getSession().getAttribute("tableName");
        if (!"users".equals(tableName)) {
            return R.error("无权限操作，仅管理员可调整积分");
        }
        
        jifen.setId(new Date().getTime() + new Double(Math.floor(Math.random() * 1000)).longValue());
        jifen.setAddtime(new Date());
        
        String username = (String) request.getSession().getAttribute("username");
        jifen.setCaozuoren(username);
        
        if (jifen.getJifenshu() != null && jifen.getJifenshu() > 0) {
            // 增加积分
            jifenService.addPoints(jifen.getHuiyuanid(), jifen.getZhanghao(), 
                    jifen.getJifenshu(), jifen.getJifenleixing(), jifen.getShuoming(), jifen.getGuanliandingdan());
        } else if (jifen.getJifenshu() != null && jifen.getJifenshu() < 0) {
            // 扣减积分
            jifenService.deductPoints(jifen.getHuiyuanid(), jifen.getZhanghao(), 
                    Math.abs(jifen.getJifenshu()), jifen.getJifenleixing(), jifen.getShuoming(), jifen.getGuanliandingdan());
        } else {
            return R.error("积分数不能为0");
        }
        
        return R.ok();
    }

    /**
     * 修改（积分记录一般不允许修改，保留接口仅供特殊情况使用）
     */
    @RequestMapping("/update")
    public R update(@RequestBody JifenEntity jifen, HttpServletRequest request) {
        // 验证权限：只有管理员可以修改积分记录
        String tableName = (String) request.getSession().getAttribute("tableName");
        if (!"users".equals(tableName)) {
            return R.error("无权限操作，仅管理员可修改积分记录");
        }
        jifenService.updateById(jifen);
        return R.ok();
    }

    /**
     * 删除（仅管理员可操作）
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids, HttpServletRequest request) {
        // 验证权限：只有管理员可以删除积分记录
        String tableName = (String) request.getSession().getAttribute("tableName");
        if (!"users".equals(tableName)) {
            return R.error("无权限操作，仅管理员可删除积分记录");
        }
        jifenService.deleteBatchIds(Arrays.asList(ids));
        return R.ok();
    }
}
