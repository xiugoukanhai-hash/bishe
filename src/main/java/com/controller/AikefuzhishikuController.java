package com.controller;

import java.util.Arrays;
import java.util.List;
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

import com.entity.AikefuzhishikuEntity;
import com.service.AikefuzhishikuService;
import com.utils.PageUtils;
import com.utils.R;
import com.utils.MPUtil;
import com.constant.AiKefuConstant;

/**
 * AI客服知识库
 * 后端接口
 */
@RestController
@RequestMapping("/aikefuzhishiku")
public class AikefuzhishikuController {
    @Autowired
    private AikefuzhishikuService aikefuzhishikuService;

    /**
     * 智能问答接口
     */
    @IgnoreAuth
    @RequestMapping("/ask")
    public R ask(@RequestBody Map<String, String> params) {
        String question = params.get("question");
        if (question == null || question.trim().isEmpty()) {
            return R.ok().put("data", AiKefuConstant.DEFAULT_REPLY);
        }
        String answer = aikefuzhishikuService.smartAnswer(question);
        return R.ok().put("data", answer);
    }

    /**
     * 获取热门问题
     */
    @IgnoreAuth
    @RequestMapping("/hotQuestions")
    public R hotQuestions(@RequestParam(value = "limit", defaultValue = "10") Integer limit) {
        List<AikefuzhishikuEntity> list = aikefuzhishikuService.getHotQuestions(limit);
        return R.ok().put("data", list);
    }

    /**
     * 获取问候语
     */
    @IgnoreAuth
    @RequestMapping("/greeting")
    public R greeting() {
        return R.ok().put("data", AiKefuConstant.GREETING_TEMPLATE);
    }

    /**
     * 后端列表（仅管理员可访问）
     */
    @RequestMapping("/page")
    public R page(@RequestParam Map<String, Object> params, AikefuzhishikuEntity aikefuzhishiku,
                  HttpServletRequest request) {
        // 验证权限：只有管理员可以访问后台知识库列表
        String tableName = (String) request.getSession().getAttribute("tableName");
        if (!"users".equals(tableName)) {
            return R.error("无权限访问知识库管理");
        }
        EntityWrapper<AikefuzhishikuEntity> ew = new EntityWrapper<AikefuzhishikuEntity>();
        PageUtils page = aikefuzhishikuService.queryPage(params, MPUtil.sort(MPUtil.between(MPUtil.likeOrEq(ew, aikefuzhishiku), params), params));
        return R.ok().put("data", page);
    }

    /**
     * 前端列表
     */
    @IgnoreAuth
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params, AikefuzhishikuEntity aikefuzhishiku,
                  HttpServletRequest request) {
        EntityWrapper<AikefuzhishikuEntity> ew = new EntityWrapper<AikefuzhishikuEntity>();
        ew.eq("zhuangtai", AiKefuConstant.KB_STATUS_ENABLED);
        PageUtils page = aikefuzhishikuService.queryPage(params, MPUtil.sort(MPUtil.between(MPUtil.likeOrEq(ew, aikefuzhishiku), params), params));
        return R.ok().put("data", page);
    }

    /**
     * 后端详情
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id) {
        AikefuzhishikuEntity aikefuzhishiku = aikefuzhishikuService.selectById(id);
        return R.ok().put("data", aikefuzhishiku);
    }

    /**
     * 前端详情
     */
    @IgnoreAuth
    @RequestMapping("/detail/{id}")
    public R detail(@PathVariable("id") Long id) {
        AikefuzhishikuEntity aikefuzhishiku = aikefuzhishikuService.selectById(id);
        // 增加点击次数
        if (aikefuzhishiku != null) {
            aikefuzhishikuService.incrementClickCount(id);
        }
        return R.ok().put("data", aikefuzhishiku);
    }

    /**
     * 后端保存（仅管理员可操作）
     */
    @RequestMapping("/save")
    public R save(@RequestBody AikefuzhishikuEntity aikefuzhishiku, HttpServletRequest request) {
        // 验证权限：只有管理员可以管理知识库
        String tableName = (String) request.getSession().getAttribute("tableName");
        if (!"users".equals(tableName)) {
            return R.error("无权限操作，仅管理员可管理知识库");
        }
        
        aikefuzhishiku.setId(new Date().getTime() + new Double(Math.floor(Math.random() * 1000)).longValue());
        aikefuzhishiku.setAddtime(new Date());
        if (aikefuzhishiku.getClickcount() == null) {
            aikefuzhishiku.setClickcount(0);
        }
        if (aikefuzhishiku.getPaixu() == null) {
            aikefuzhishiku.setPaixu(0);
        }
        if (aikefuzhishiku.getZhuangtai() == null) {
            aikefuzhishiku.setZhuangtai(AiKefuConstant.KB_STATUS_ENABLED);
        }
        aikefuzhishikuService.insert(aikefuzhishiku);
        return R.ok();
    }

    /**
     * 前端保存（禁用，知识库只能通过后台管理）
     */
    @RequestMapping("/add")
    public R add(@RequestBody AikefuzhishikuEntity aikefuzhishiku, HttpServletRequest request) {
        return R.error("请通过后台管理知识库");
    }

    /**
     * 修改（仅管理员可操作）
     */
    @RequestMapping("/update")
    public R update(@RequestBody AikefuzhishikuEntity aikefuzhishiku, HttpServletRequest request) {
        // 验证权限：只有管理员可以管理知识库
        String tableName = (String) request.getSession().getAttribute("tableName");
        if (!"users".equals(tableName)) {
            return R.error("无权限操作，仅管理员可管理知识库");
        }
        aikefuzhishikuService.updateById(aikefuzhishiku);
        return R.ok();
    }

    /**
     * 删除（仅管理员可操作）
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids, HttpServletRequest request) {
        // 验证权限：只有管理员可以管理知识库
        String tableName = (String) request.getSession().getAttribute("tableName");
        if (!"users".equals(tableName)) {
            return R.error("无权限操作，仅管理员可管理知识库");
        }
        aikefuzhishikuService.deleteBatchIds(Arrays.asList(ids));
        return R.ok();
    }
}
