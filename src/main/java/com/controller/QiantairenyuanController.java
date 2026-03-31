package com.controller;

import java.util.Arrays;
import java.util.Date;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.annotation.IgnoreAuth;

import com.entity.QiantairenyuanEntity;
import com.service.QiantairenyuanService;
import com.service.TokenService;
import com.utils.PageUtils;
import com.utils.R;
import com.utils.MPUtil;

/**
 * 前台人员
 * 后端接口
 */
@RestController
@RequestMapping("/qiantairenyuan")
public class QiantairenyuanController {
    @Autowired
    private QiantairenyuanService qiantairenyuanService;
    
    @Autowired
    private TokenService tokenService;

    /**
     * 登录
     */
    @IgnoreAuth
    @RequestMapping(value = "/login")
    public R login(String username, String password, String captcha, HttpServletRequest request) {
        QiantairenyuanEntity user = qiantairenyuanService.selectOne(
                new EntityWrapper<QiantairenyuanEntity>().eq("qiantaizhanghao", username));
        if(user == null || !user.getMima().equals(password)) {
            return R.error("账号或密码不正确");
        }
        // 状态验证：只有在职状态才能登录
        String status = user.getZhuangtai();
        if("离职".equals(status)) {
            return R.error("该账号已离职，无法登录");
        }
        if("请假".equals(status)) {
            return R.error("该账号当前请假中，如需登录请联系管理员");
        }
        if(!"在职".equals(status)) {
            return R.error("账号状态异常，请联系管理员");
        }
        String token = tokenService.generateToken(user.getId(), username, "qiantairenyuan", "前台人员");
        request.getSession().setAttribute("userId", user.getId());
        request.getSession().setAttribute("username", username);
        request.getSession().setAttribute("tableName", "qiantairenyuan");
        return R.ok().put("token", token).put("id", user.getId()).put("username", username);
    }

    /**
     * 注册（仅管理员可添加前台人员，此接口保留供系统内部使用）
     */
    @RequestMapping("/register")
    public R register(@RequestBody QiantairenyuanEntity qiantairenyuan, HttpServletRequest request) {
        // 验证操作者是否为管理员
        String tableName = (String) request.getSession().getAttribute("tableName");
        if (!"users".equals(tableName)) {
            return R.error("无权限操作，仅管理员可添加前台人员");
        }
        
        QiantairenyuanEntity user = qiantairenyuanService.selectOne(
                new EntityWrapper<QiantairenyuanEntity>().eq("qiantaizhanghao", qiantairenyuan.getQiantaizhanghao()));
        if(user != null) {
            return R.error("该账号已存在");
        }
        Long uId = new Date().getTime();
        qiantairenyuan.setId(uId);
        qiantairenyuan.setZhuangtai("在职");
        qiantairenyuan.setAddtime(new Date());
        qiantairenyuanService.insert(qiantairenyuan);
        return R.ok();
    }

    /**
     * 退出
     */
    @RequestMapping("/logout")
    public R logout(HttpServletRequest request) {
        request.getSession().invalidate();
        return R.ok("退出成功");
    }

    /**
     * 获取用户的session用户信息
     */
    @RequestMapping("/session")
    public R getCurrUser(HttpServletRequest request) {
        Long id = (Long) request.getSession().getAttribute("userId");
        QiantairenyuanEntity user = qiantairenyuanService.selectById(id);
        return R.ok().put("data", user);
    }

    /**
     * 密码重置（仅管理员可操作）
     */
    @RequestMapping(value = "/resetPass")
    public R resetPass(String username, HttpServletRequest request) {
        // 验证操作者是否为管理员
        String tableName = (String) request.getSession().getAttribute("tableName");
        if (!"users".equals(tableName)) {
            return R.error("无权限操作，仅管理员可重置密码");
        }
        
        QiantairenyuanEntity user = qiantairenyuanService.selectOne(
                new EntityWrapper<QiantairenyuanEntity>().eq("qiantaizhanghao", username));
        if(user == null) {
            return R.error("账号不存在");
        }
        user.setMima("123456");
        qiantairenyuanService.updateById(user);
        return R.ok("密码已重置为：123456");
    }

    /**
     * 后端列表
     */
    @RequestMapping("/page")
    public R page(@RequestParam Map<String, Object> params, QiantairenyuanEntity qiantairenyuan,
                  HttpServletRequest request) {
        EntityWrapper<QiantairenyuanEntity> ew = new EntityWrapper<QiantairenyuanEntity>();
        PageUtils page = qiantairenyuanService.queryPage(params, MPUtil.sort(MPUtil.between(MPUtil.likeOrEq(ew, qiantairenyuan), params), params));
        return R.ok().put("data", page);
    }

    /**
     * 前端列表（仅管理员可查看员工列表）
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params, QiantairenyuanEntity qiantairenyuan,
                  HttpServletRequest request) {
        String tableName = (String) request.getSession().getAttribute("tableName");
        if (!"users".equals(tableName)) {
            return R.error("无权限查看前台人员列表");
        }
        EntityWrapper<QiantairenyuanEntity> ew = new EntityWrapper<QiantairenyuanEntity>();
        PageUtils page = qiantairenyuanService.queryPage(params, MPUtil.sort(MPUtil.between(MPUtil.likeOrEq(ew, qiantairenyuan), params), params));
        return R.ok().put("data", page);
    }

    /**
     * 查询
     */
    @RequestMapping("/query")
    public R query(QiantairenyuanEntity qiantairenyuan) {
        EntityWrapper<QiantairenyuanEntity> ew = new EntityWrapper<QiantairenyuanEntity>();
        ew.allEq(MPUtil.allEQMapPre(qiantairenyuan, "qiantairenyuan"));
        QiantairenyuanEntity entity = qiantairenyuanService.selectOne(ew);
        return R.ok().put("data", entity);
    }

    /**
     * 后端详情
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id) {
        QiantairenyuanEntity qiantairenyuan = qiantairenyuanService.selectById(id);
        return R.ok().put("data", qiantairenyuan);
    }

    /**
     * 前端详情（仅管理员可查看）
     */
    @RequestMapping("/detail/{id}")
    public R detail(@PathVariable("id") Long id, HttpServletRequest request) {
        String tableName = (String) request.getSession().getAttribute("tableName");
        if (!"users".equals(tableName)) {
            return R.error("无权限查看前台人员详情");
        }
        QiantairenyuanEntity qiantairenyuan = qiantairenyuanService.selectById(id);
        return R.ok().put("data", qiantairenyuan);
    }

    /**
     * 后端保存（仅管理员可添加前台人员）
     */
    @RequestMapping("/save")
    public R save(@RequestBody QiantairenyuanEntity qiantairenyuan, HttpServletRequest request) {
        String tableName = (String) request.getSession().getAttribute("tableName");
        if (!"users".equals(tableName)) {
            return R.error("无权限操作，仅管理员可添加前台人员");
        }
        qiantairenyuan.setId(new Date().getTime() + new Double(Math.floor(Math.random() * 1000)).longValue());
        QiantairenyuanEntity user = qiantairenyuanService.selectOne(
                new EntityWrapper<QiantairenyuanEntity>().eq("qiantaizhanghao", qiantairenyuan.getQiantaizhanghao()));
        if(user != null) {
            return R.error("该账号已存在");
        }
        if (qiantairenyuan.getMima() == null || qiantairenyuan.getMima().isEmpty()) {
            qiantairenyuan.setMima("123456");
        }
        if (qiantairenyuan.getZhuangtai() == null || qiantairenyuan.getZhuangtai().isEmpty()) {
            qiantairenyuan.setZhuangtai("在职");
        }
        qiantairenyuan.setAddtime(new Date());
        qiantairenyuanService.insert(qiantairenyuan);
        return R.ok();
    }

    /**
     * 前端保存（禁用，前台人员只能由管理员添加）
     */
    @RequestMapping("/add")
    public R add(@RequestBody QiantairenyuanEntity qiantairenyuan, HttpServletRequest request) {
        return R.error("前台人员只能由管理员在后台添加");
    }

    /**
     * 修改（管理员可修改任意人员，前台人员只能修改自己的基本信息）
     */
    @RequestMapping("/update")
    public R update(@RequestBody QiantairenyuanEntity qiantairenyuan, HttpServletRequest request) {
        String tableName = (String) request.getSession().getAttribute("tableName");
        Long userId = (Long) request.getSession().getAttribute("userId");
        
        if ("users".equals(tableName)) {
            qiantairenyuanService.updateById(qiantairenyuan);
            return R.ok();
        } else if ("qiantairenyuan".equals(tableName)) {
            if (!userId.equals(qiantairenyuan.getId())) {
                return R.error("只能修改自己的信息");
            }
            QiantairenyuanEntity updateEntity = new QiantairenyuanEntity();
            updateEntity.setId(qiantairenyuan.getId());
            updateEntity.setQiantaixingming(qiantairenyuan.getQiantaixingming());
            updateEntity.setShouji(qiantairenyuan.getShouji());
            updateEntity.setZhaopian(qiantairenyuan.getZhaopian());
            qiantairenyuanService.updateById(updateEntity);
            return R.ok();
        }
        return R.error("无权限修改前台人员信息");
    }

    /**
     * 删除（仅管理员可操作）
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids, HttpServletRequest request) {
        // 验证操作者是否为管理员
        String tableName = (String) request.getSession().getAttribute("tableName");
        if (!"users".equals(tableName)) {
            return R.error("无权限操作，仅管理员可删除前台人员");
        }
        qiantairenyuanService.deleteBatchIds(Arrays.asList(ids));
        return R.ok();
    }
    
    /**
     * 修改密码
     */
    @RequestMapping("/updatePassword")
    public R updatePassword(@RequestBody Map<String, String> params, HttpServletRequest request) {
        Long userId = (Long) request.getSession().getAttribute("userId");
        String tableName = (String) request.getSession().getAttribute("tableName");
        
        if (!"qiantairenyuan".equals(tableName)) {
            return R.error("非前台人员无法修改密码");
        }
        
        String oldPassword = params.get("oldPassword");
        String newPassword = params.get("newPassword");
        
        if (oldPassword == null || newPassword == null) {
            return R.error("参数不完整");
        }
        
        QiantairenyuanEntity user = qiantairenyuanService.selectById(userId);
        if (user == null) {
            return R.error("用户不存在");
        }
        
        if (!user.getMima().equals(oldPassword)) {
            return R.error("原密码错误");
        }
        
        user.setMima(newPassword);
        qiantairenyuanService.updateById(user);
        return R.ok("密码修改成功");
    }
}
