package com.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;

import com.entity.KefangxinxiEntity;
import com.entity.QingsaofangjianEntity;
import com.entity.QingjierenyuanEntity;
import com.entity.view.KefangxinxiView;

import com.service.KefangxinxiService;
import com.service.QingsaofangjianService;
import com.service.QingjierenyuanService;
import com.utils.PageUtils;
import com.utils.R;
import com.utils.MPUtil;

/**
 * 待清扫房间
 * 专为清洁人员提供的待清扫房间列表接口
 */
@RestController
@RequestMapping("/daiqingsao")
public class DaiqingsaoController {
    
    @Autowired
    private KefangxinxiService kefangxinxiService;
    
    @Autowired
    private QingsaofangjianService qingsaofangjianService;
    
    @Autowired
    private QingjierenyuanService qingjierenyuanService;

    /**
     * 后端列表 - 获取待清扫状态的房间
     */
    @RequestMapping("/page")
    public R page(@RequestParam Map<String, Object> params, KefangxinxiEntity kefangxinxi,
            HttpServletRequest request){
        
        EntityWrapper<KefangxinxiEntity> ew = new EntityWrapper<KefangxinxiEntity>();
        
        // 筛选待清扫状态的房间
        ew.andNew().eq("kefangzhuangtai", "待清扫")
          .or().eq("weishengqingkuang", "待清扫");
        
        // 支持按客房号搜索
        if (kefangxinxi.getKefanghao() != null && !"".equals(kefangxinxi.getKefanghao())) {
            ew.like("kefanghao", kefangxinxi.getKefanghao());
        }
        
        // 支持按客房类型搜索
        if (kefangxinxi.getKefangleixing() != null && !"".equals(kefangxinxi.getKefangleixing())) {
            ew.like("kefangleixing", kefangxinxi.getKefangleixing());
        }
        
        // 支持按楼层搜索
        if (kefangxinxi.getLouceng() != null) {
            ew.eq("louceng", kefangxinxi.getLouceng());
        }
        
        PageUtils page = kefangxinxiService.queryPage(params, MPUtil.sort(ew, params));
        return R.ok().put("data", page);
    }

    /**
     * 后端详情
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
        KefangxinxiEntity kefangxinxi = kefangxinxiService.selectById(id);
        return R.ok().put("data", kefangxinxi);
    }
    
    /**
     * 执行清扫 - 创建清扫记录并更新房间状态
     */
    @RequestMapping("/clean/{id}")
    public R clean(@PathVariable("id") Long id, HttpServletRequest request){
        KefangxinxiEntity kefangxinxi = kefangxinxiService.selectById(id);
        if (kefangxinxi == null) {
            return R.error("客房不存在");
        }
        
        // 获取当前用户信息
        String username = (String) request.getSession().getAttribute("username");
        String tableName = (String) request.getSession().getAttribute("tableName");
        
        if (!"qingjierenyuan".equals(tableName) && !"users".equals(tableName)) {
            return R.error("只有清洁人员或管理员才能执行清扫操作");
        }
        
        // 获取清洁人员姓名（如果是管理员则显示为管理员操作）
        String qingjiexingming = "";
        if ("qingjierenyuan".equals(tableName)) {
            QingjierenyuanEntity qingjie = qingjierenyuanService.selectOne(
                new EntityWrapper<QingjierenyuanEntity>().eq("qingjiezhanghao", username)
            );
            if (qingjie != null) {
                qingjiexingming = qingjie.getQingjiexingming();
            }
        } else {
            qingjiexingming = "管理员(" + username + ")";
        }
        
        // 创建清扫记录
        QingsaofangjianEntity qingsao = new QingsaofangjianEntity();
        qingsao.setId(new Date().getTime() + new Double(Math.floor(Math.random() * 1000)).longValue());
        qingsao.setKefanghao(kefangxinxi.getKefanghao());
        qingsao.setKefangleixing(kefangxinxi.getKefangleixing());
        qingsao.setSuoshujiudian(kefangxinxi.getSuoshujiudian());
        qingsao.setShifoudasao("是");
        qingsao.setDasaoshijian(new Date());
        qingsao.setQingjiezhanghao(username);
        qingsao.setQingjiexingming(qingjiexingming);
        qingsao.setAddtime(new Date());
        
        qingsaofangjianService.insert(qingsao);
        
        // 更新房间状态
        kefangxinxi.setKefangzhuangtai("空闲");
        kefangxinxi.setWeishengqingkuang("已清扫");
        kefangxinxiService.updateById(kefangxinxi);
        
        return R.ok("清扫完成，房间已恢复可预约状态");
    }
    
    /**
     * 获取待清扫房间数量（用于首页统计）
     */
    @RequestMapping("/count")
    public R count(HttpServletRequest request){
        EntityWrapper<KefangxinxiEntity> ew = new EntityWrapper<KefangxinxiEntity>();
        ew.andNew().eq("kefangzhuangtai", "待清扫")
          .or().eq("weishengqingkuang", "待清扫");
        
        int count = kefangxinxiService.selectCount(ew);
        return R.ok().put("count", count);
    }
    
    /**
     * 按客房号执行清扫 - 用于退房页面的快捷清扫操作
     */
    @RequestMapping("/cleanByRoom")
    public R cleanByRoom(@RequestBody Map<String, Object> params, HttpServletRequest request){
        String kefanghao = (String) params.get("kefanghao");
        if (StringUtils.isBlank(kefanghao)) {
            return R.error("客房号不能为空");
        }
        
        KefangxinxiEntity kefangxinxi = kefangxinxiService.selectOne(
            new EntityWrapper<KefangxinxiEntity>().eq("kefanghao", kefanghao));
        if (kefangxinxi == null) {
            return R.error("客房不存在");
        }
        
        // 获取当前用户信息
        String username = (String) request.getSession().getAttribute("username");
        String tableName = (String) request.getSession().getAttribute("tableName");
        
        if (!"qingjierenyuan".equals(tableName) && !"users".equals(tableName)) {
            return R.error("只有清洁人员或管理员才能执行清扫操作");
        }
        
        // 获取清洁人员姓名
        String qingjiexingming = "";
        if ("qingjierenyuan".equals(tableName)) {
            QingjierenyuanEntity qingjie = qingjierenyuanService.selectOne(
                new EntityWrapper<QingjierenyuanEntity>().eq("qingjiezhanghao", username)
            );
            if (qingjie != null) {
                qingjiexingming = qingjie.getQingjiexingming();
            }
        } else {
            qingjiexingming = "管理员(" + username + ")";
        }
        
        // 创建清扫记录
        QingsaofangjianEntity qingsao = new QingsaofangjianEntity();
        qingsao.setId(new Date().getTime() + new Double(Math.floor(Math.random() * 1000)).longValue());
        qingsao.setKefanghao(kefangxinxi.getKefanghao());
        qingsao.setKefangleixing(kefangxinxi.getKefangleixing());
        qingsao.setSuoshujiudian(kefangxinxi.getSuoshujiudian());
        qingsao.setShifoudasao("是");
        qingsao.setDasaoshijian(new Date());
        qingsao.setQingjiezhanghao(username);
        qingsao.setQingjiexingming(qingjiexingming);
        qingsao.setAddtime(new Date());
        
        qingsaofangjianService.insert(qingsao);
        
        // 更新房间状态
        kefangxinxi.setKefangzhuangtai("空闲");
        kefangxinxi.setWeishengqingkuang("已清扫");
        kefangxinxiService.updateById(kefangxinxi);
        
        return R.ok("清扫完成，房间已恢复可预约状态");
    }
}
