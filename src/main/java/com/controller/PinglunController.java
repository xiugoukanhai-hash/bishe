package com.controller;

import java.util.Arrays;
import java.util.Date;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.annotation.IgnoreAuth;

import com.entity.PinglunEntity;
import com.service.PinglunService;
import com.utils.PageUtils;
import com.utils.R;
import com.utils.MPUtil;

/**
 * 客户评价 后端接口
 */
@RestController
@RequestMapping("/pinglun")
public class PinglunController {

    @Autowired
    private PinglunService pinglunService;

    /**
     * 分页列表（管理员/前台）
     */
    @RequestMapping("/page")
    public R page(@RequestParam Map<String, Object> params, PinglunEntity pinglun, HttpServletRequest request) {
        EntityWrapper<PinglunEntity> ew = new EntityWrapper<>();
        PageUtils page = pinglunService.queryPage(params, MPUtil.sort(MPUtil.between(MPUtil.likeOrEq(ew, pinglun), params), params));
        return R.ok().put("data", page);
    }

    /**
     * 前端分页列表（用户查看评价）
     */
    @IgnoreAuth
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params, PinglunEntity pinglun) {
        EntityWrapper<PinglunEntity> ew = new EntityWrapper<>();
        PageUtils page = pinglunService.queryPage(params, MPUtil.sort(MPUtil.between(MPUtil.likeOrEq(ew, pinglun), params), params));
        return R.ok().put("data", page);
    }

    /**
     * 详情
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id) {
        PinglunEntity pinglun = pinglunService.selectById(id);
        return R.ok().put("data", pinglun);
    }

    /**
     * 提交评价（用户/会员）
     */
    @RequestMapping("/add")
    public R add(@RequestBody PinglunEntity pinglun, HttpServletRequest request) {
        Object userIdObj = request.getSession().getAttribute("userId");
        Object tableNameObj = request.getSession().getAttribute("tableName");
        Object usernameObj = request.getSession().getAttribute("username");

        if (userIdObj == null) {
            return R.error("请先登录");
        }

        pinglun.setId(new Date().getTime() + new Double(Math.floor(Math.random() * 1000)).longValue());
        pinglun.setUserid(Long.parseLong(userIdObj.toString()));
        pinglun.setTablename(tableNameObj != null ? tableNameObj.toString() : "");
        pinglun.setNickname(usernameObj != null ? usernameObj.toString() : "");
        if (pinglun.getPingfen() == null) {
            pinglun.setPingfen(5);
        }

        pinglunService.insert(pinglun);
        return R.ok("评价提交成功");
    }

    /**
     * 回复评价（管理员）
     */
    @RequestMapping("/reply")
    public R reply(@RequestBody PinglunEntity pinglun, HttpServletRequest request) {
        Object tableNameObj = request.getSession().getAttribute("tableName");
        if (tableNameObj == null || !"users".equals(tableNameObj.toString())) {
            return R.error("仅管理员可回复评价");
        }
        if (pinglun.getId() == null) {
            return R.error("评价ID不能为空");
        }
        PinglunEntity exist = pinglunService.selectById(pinglun.getId());
        if (exist == null) {
            return R.error("评价不存在");
        }
        exist.setReply(pinglun.getReply());
        exist.setReplytime(new Date());
        pinglunService.updateById(exist);
        return R.ok("回复成功");
    }

    /**
     * 删除评价（管理员）
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids, HttpServletRequest request) {
        Object tableNameObj = request.getSession().getAttribute("tableName");
        if (tableNameObj == null || !"users".equals(tableNameObj.toString())) {
            return R.error("仅管理员可删除评价");
        }
        pinglunService.deleteBatchIds(Arrays.asList(ids));
        return R.ok("删除成功");
    }
}
