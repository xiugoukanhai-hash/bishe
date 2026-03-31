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

import com.entity.FuwujiluEntity;
import com.service.FuwujiluService;
import com.utils.PageUtils;
import com.utils.R;
import com.utils.MPUtil;
import com.utils.OrderNumberUtil;
import com.constant.CommonConstant;

/**
 * 服务记录
 * 后端接口
 */
@RestController
@RequestMapping("/fuwujilu")
public class FuwujiluController {
    @Autowired
    private FuwujiluService fuwujiluService;

    /**
     * 后端列表
     */
    @RequestMapping("/page")
    public R page(@RequestParam Map<String, Object> params, FuwujiluEntity fuwujilu,
                  HttpServletRequest request) {
        EntityWrapper<FuwujiluEntity> ew = new EntityWrapper<FuwujiluEntity>();
        PageUtils page = fuwujiluService.queryPage(params, MPUtil.sort(MPUtil.between(MPUtil.likeOrEq(ew, fuwujilu), params), params));
        return R.ok().put("data", page);
    }

    /**
     * 前端列表（需登录查看）
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params, FuwujiluEntity fuwujilu,
                  HttpServletRequest request) {
        String tableName = (String) request.getSession().getAttribute("tableName");
        if (tableName == null) {
            return R.error("请先登录");
        }
        EntityWrapper<FuwujiluEntity> ew = new EntityWrapper<FuwujiluEntity>();
        PageUtils page = fuwujiluService.queryPage(params, MPUtil.sort(MPUtil.between(MPUtil.likeOrEq(ew, fuwujilu), params), params));
        return R.ok().put("data", page);
    }

    /**
     * 根据客房号查询服务记录
     */
    @RequestMapping("/listByRoom")
    public R listByRoom(@RequestParam Map<String, Object> params, 
                        @RequestParam String kefanghao, HttpServletRequest request) {
        EntityWrapper<FuwujiluEntity> ew = new EntityWrapper<FuwujiluEntity>();
        ew.eq("kefanghao", kefanghao);
        ew.orderBy("addtime", false);
        PageUtils page = fuwujiluService.queryPage(params, ew);
        return R.ok().put("data", page);
    }

    /**
     * 后端详情
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id) {
        FuwujiluEntity fuwujilu = fuwujiluService.selectById(id);
        return R.ok().put("data", fuwujilu);
    }

    /**
     * 前端详情（需登录查看）
     */
    @RequestMapping("/detail/{id}")
    public R detail(@PathVariable("id") Long id, HttpServletRequest request) {
        String tableName = (String) request.getSession().getAttribute("tableName");
        if (tableName == null) {
            return R.error("请先登录");
        }
        FuwujiluEntity fuwujilu = fuwujiluService.selectById(id);
        return R.ok().put("data", fuwujilu);
    }

    /**
     * 后端保存（管理员、前台人员可操作）
     */
    @RequestMapping("/save")
    public R save(@RequestBody FuwujiluEntity fuwujilu, HttpServletRequest request) {
        // 验证权限：只有管理员和前台人员可以添加服务记录
        String tableName = (String) request.getSession().getAttribute("tableName");
        if (!"users".equals(tableName) && !"qiantairenyuan".equals(tableName)) {
            return R.error("无权限操作");
        }
        
        fuwujilu.setId(new Date().getTime() + new Double(Math.floor(Math.random() * 1000)).longValue());
        fuwujilu.setAddtime(new Date());
        fuwujilu.setDengjishijian(new Date());
        // 自动生成服务单号
        if (fuwujilu.getDingdanbianhao() == null || fuwujilu.getDingdanbianhao().isEmpty()) {
            fuwujilu.setDingdanbianhao("FW" + OrderNumberUtil.generateBookingNumber());
        }
        if (fuwujilu.getZhuangtai() == null) {
            fuwujilu.setZhuangtai(CommonConstant.SERVICE_STATUS_PENDING);
        }
        // 设置登记人
        String username = (String) request.getSession().getAttribute("username");
        if (username != null) {
            fuwujilu.setDengjirenzhanghao(username);
        }
        fuwujiluService.insert(fuwujilu);
        return R.ok();
    }

    /**
     * 前端保存（入住客户可申请服务）
     */
    @RequestMapping("/add")
    public R add(@RequestBody FuwujiluEntity fuwujilu, HttpServletRequest request) {
        String tableName = (String) request.getSession().getAttribute("tableName");
        String username = (String) request.getSession().getAttribute("username");
        if (tableName == null) {
            return R.error("请先登录");
        }
        fuwujilu.setId(new Date().getTime() + new Double(Math.floor(Math.random() * 1000)).longValue());
        fuwujilu.setAddtime(new Date());
        fuwujilu.setDengjishijian(new Date());
        fuwujilu.setZhuangtai(CommonConstant.SERVICE_STATUS_PENDING);
        fuwujilu.setDengjirenzhanghao(username);
        if (fuwujilu.getDingdanbianhao() == null || fuwujilu.getDingdanbianhao().isEmpty()) {
            fuwujilu.setDingdanbianhao("FW" + OrderNumberUtil.generateBookingNumber());
        }
        fuwujiluService.insert(fuwujilu);
        return R.ok();
    }

    /**
     * 修改（仅管理员和前台人员可修改）
     */
    @RequestMapping("/update")
    public R update(@RequestBody FuwujiluEntity fuwujilu, HttpServletRequest request) {
        String tableName = (String) request.getSession().getAttribute("tableName");
        if (!"users".equals(tableName) && !"qiantairenyuan".equals(tableName)) {
            return R.error("无权限修改服务记录");
        }
        fuwujiluService.updateById(fuwujilu);
        return R.ok();
    }

    /**
     * 处理服务（管理员、前台人员可操作）
     */
    @RequestMapping("/handle/{id}")
    public R handle(@PathVariable("id") Long id, HttpServletRequest request) {
        String tableName = (String) request.getSession().getAttribute("tableName");
        if (!"users".equals(tableName) && !"qiantairenyuan".equals(tableName)) {
            return R.error("无权限操作");
        }
        
        FuwujiluEntity fuwujilu = fuwujiluService.selectById(id);
        if (fuwujilu == null) {
            return R.error("服务记录不存在");
        }
        if (!CommonConstant.SERVICE_STATUS_PENDING.equals(fuwujilu.getZhuangtai())) {
            return R.error("只能处理待处理状态的服务");
        }
        
        String username = (String) request.getSession().getAttribute("username");
        fuwujilu.setChulirenzhanghao(username);
        fuwujilu.setChulishijian(new Date());
        fuwujilu.setZhuangtai(CommonConstant.SERVICE_STATUS_DONE);
        fuwujiluService.updateById(fuwujilu);
        return R.ok();
    }

    /**
     * 取消服务（管理员、前台人员、登记人可操作）
     */
    @RequestMapping("/cancel/{id}")
    public R cancel(@PathVariable("id") Long id, HttpServletRequest request) {
        String tableName = (String) request.getSession().getAttribute("tableName");
        String username = (String) request.getSession().getAttribute("username");
        
        FuwujiluEntity fuwujilu = fuwujiluService.selectById(id);
        if (fuwujilu == null) {
            return R.error("服务记录不存在");
        }
        if (!CommonConstant.SERVICE_STATUS_PENDING.equals(fuwujilu.getZhuangtai())) {
            return R.error("只能取消待处理状态的服务");
        }
        
        // 权限验证：管理员、前台人员或登记人可取消
        boolean canCancel = "users".equals(tableName) 
                || "qiantairenyuan".equals(tableName)
                || (username != null && username.equals(fuwujilu.getDengjirenzhanghao()));
        if (!canCancel) {
            return R.error("无权限取消此服务");
        }
        
        fuwujilu.setZhuangtai(CommonConstant.SERVICE_STATUS_CANCELLED);
        fuwujiluService.updateById(fuwujilu);
        return R.ok();
    }

    /**
     * 删除（仅管理员可操作）
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids, HttpServletRequest request) {
        // 验证权限：只有管理员可以删除服务记录
        String tableName = (String) request.getSession().getAttribute("tableName");
        if (!"users".equals(tableName)) {
            return R.error("无权限操作，仅管理员可删除服务记录");
        }
        fuwujiluService.deleteBatchIds(Arrays.asList(ids));
        return R.ok();
    }
}
