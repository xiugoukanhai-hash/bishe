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

import com.utils.ValidatorUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.annotation.IgnoreAuth;

import com.entity.YonghuEntity;
import com.entity.view.YonghuView;

import com.service.YonghuService;
import com.service.TokenService;
import com.utils.PageUtils;
import com.utils.R;
import com.utils.MD5Util;
import com.utils.MPUtil;
import com.utils.CommonUtil;


/**
 * 用户
 * 后端接口
 * @author 
 * @email 
 * @date 2021-04-30 10:31:54
 */
@RestController
@RequestMapping("/yonghu")
public class YonghuController {
    @Autowired
    private YonghuService yonghuService;
    
	@Autowired
	private TokenService tokenService;
	
	/**
	 * 登录
	 */
	@IgnoreAuth
	@RequestMapping(value = "/login")
	public R login(String username, String password, String captcha, HttpServletRequest request) {
		YonghuEntity user = yonghuService.selectOne(new EntityWrapper<YonghuEntity>().eq("zhanghao", username));
		if(user==null || !user.getMima().equals(password)) {
			return R.error("账号或密码不正确");
		}
		
		String token = tokenService.generateToken(user.getId(), username,"yonghu",  "用户" );
		request.getSession().setAttribute("userId", user.getId());
		request.getSession().setAttribute("username", username);
		request.getSession().setAttribute("tableName", "yonghu");
		return R.ok().put("token", token).put("id", user.getId()).put("username", username);
	}
	
	/**
     * 注册
     */
	@IgnoreAuth
    @RequestMapping("/register")
    public R register(@RequestBody YonghuEntity yonghu){
        // 参数校验
        if (StringUtils.isBlank(yonghu.getZhanghao())) {
            return R.error("账号不能为空");
        }
        if (StringUtils.isBlank(yonghu.getMima())) {
            return R.error("密码不能为空");
        }
        if (yonghu.getMima().length() < 6 || yonghu.getMima().length() > 20) {
            return R.error("密码长度必须在6-20位之间");
        }
        if (StringUtils.isBlank(yonghu.getXingming())) {
            return R.error("姓名不能为空");
        }
        if (StringUtils.isBlank(yonghu.getShouji())) {
            return R.error("手机号不能为空");
        }
        if (!yonghu.getShouji().matches("^1[3-9]\\d{9}$")) {
            return R.error("手机号格式不正确");
        }
        if (StringUtils.isBlank(yonghu.getShenfenzheng())) {
            return R.error("身份证号不能为空");
        }
        if (!yonghu.getShenfenzheng().matches("^\\d{17}[\\dXx]$")) {
            return R.error("身份证号格式不正确");
        }
        
        // 检查账号是否已存在
    	YonghuEntity existUser = yonghuService.selectOne(new EntityWrapper<YonghuEntity>().eq("zhanghao", yonghu.getZhanghao()));
		if(existUser != null) {
			return R.error("账号已存在");
		}
        
        // 检查手机号是否已存在
        YonghuEntity existPhone = yonghuService.selectOne(new EntityWrapper<YonghuEntity>().eq("shouji", yonghu.getShouji()));
        if(existPhone != null) {
            return R.error("手机号已被注册");
        }
        
        // 设置ID和创建时间
		Long uId = new Date().getTime() + new Double(Math.floor(Math.random() * 1000)).longValue();
		yonghu.setId(uId);
        yonghu.setAddtime(new Date());
        
        yonghuService.insert(yonghu);
        return R.ok("注册成功");
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
    public R getCurrUser(HttpServletRequest request){
    	Long id = (Long)request.getSession().getAttribute("userId");
        YonghuEntity user = yonghuService.selectById(id);
        return R.ok().put("data", user);
    }
    
    /**
     * 密码重置（仅管理员可操作）
     */
	@RequestMapping(value = "/resetPass")
    public R resetPass(String username, HttpServletRequest request){
        String tableName = (String) request.getSession().getAttribute("tableName");
        if (!"users".equals(tableName)) {
            return R.error("无权限操作，仅管理员可重置密码");
        }
    	YonghuEntity user = yonghuService.selectOne(new EntityWrapper<YonghuEntity>().eq("zhanghao", username));
    	if(user==null) {
    		return R.error("账号不存在");
    	}
        user.setMima("123456");
        yonghuService.updateById(user);
        return R.ok("密码已重置为：123456");
    }


    /**
     * 后端列表
     */
    @RequestMapping("/page")
    public R page(@RequestParam Map<String, Object> params,YonghuEntity yonghu,
		HttpServletRequest request){
        EntityWrapper<YonghuEntity> ew = new EntityWrapper<YonghuEntity>();
		PageUtils page = yonghuService.queryPage(params, MPUtil.sort(MPUtil.between(MPUtil.likeOrEq(ew, yonghu), params), params));

        return R.ok().put("data", page);
    }
    
    /**
     * 前端列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params,YonghuEntity yonghu, 
		HttpServletRequest request){
        EntityWrapper<YonghuEntity> ew = new EntityWrapper<YonghuEntity>();
		PageUtils page = yonghuService.queryPage(params, MPUtil.sort(MPUtil.between(MPUtil.likeOrEq(ew, yonghu), params), params));
        return R.ok().put("data", page);
    }

	/**
     * 列表
     */
    @RequestMapping("/lists")
    public R list( YonghuEntity yonghu){
       	EntityWrapper<YonghuEntity> ew = new EntityWrapper<YonghuEntity>();
      	ew.allEq(MPUtil.allEQMapPre( yonghu, "yonghu")); 
        return R.ok().put("data", yonghuService.selectListView(ew));
    }

	 /**
     * 查询
     */
    @RequestMapping("/query")
    public R query(YonghuEntity yonghu){
        EntityWrapper< YonghuEntity> ew = new EntityWrapper< YonghuEntity>();
 		ew.allEq(MPUtil.allEQMapPre( yonghu, "yonghu")); 
		YonghuView yonghuView =  yonghuService.selectView(ew);
		return R.ok("查询用户成功").put("data", yonghuView);
    }
	
    /**
     * 后端详情
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
        YonghuEntity yonghu = yonghuService.selectById(id);
        return R.ok().put("data", yonghu);
    }

    /**
     * 前端详情
     */
    @RequestMapping("/detail/{id}")
    public R detail(@PathVariable("id") Long id){
        YonghuEntity yonghu = yonghuService.selectById(id);
        return R.ok().put("data", yonghu);
    }
    



    /**
     * 后端保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody YonghuEntity yonghu, HttpServletRequest request){
    	yonghu.setId(new Date().getTime()+new Double(Math.floor(Math.random()*1000)).longValue());
    	//ValidatorUtils.validateEntity(yonghu);
    	YonghuEntity user = yonghuService.selectOne(new EntityWrapper<YonghuEntity>().eq("zhanghao", yonghu.getZhanghao()));
		if(user!=null) {
			return R.error("用户已存在");
		}
		yonghu.setId(new Date().getTime());
        yonghuService.insert(yonghu);
        return R.ok();
    }
    
    /**
     * 前端保存
     */
    @RequestMapping("/add")
    public R add(@RequestBody YonghuEntity yonghu, HttpServletRequest request){
    	yonghu.setId(new Date().getTime()+new Double(Math.floor(Math.random()*1000)).longValue());
    	//ValidatorUtils.validateEntity(yonghu);
    	YonghuEntity user = yonghuService.selectOne(new EntityWrapper<YonghuEntity>().eq("zhanghao", yonghu.getZhanghao()));
		if(user!=null) {
			return R.error("用户已存在");
		}
		yonghu.setId(new Date().getTime());
        yonghuService.insert(yonghu);
        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody YonghuEntity yonghu, HttpServletRequest request){
        //ValidatorUtils.validateEntity(yonghu);
        yonghuService.updateById(yonghu);//全部更新
        return R.ok();
    }
    

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids){
        yonghuService.deleteBatchIds(Arrays.asList(ids));
        return R.ok();
    }
    
    /**
     * 修改密码
     */
    @RequestMapping("/updatePassword")
    public R updatePassword(@RequestBody Map<String, String> params, HttpServletRequest request) {
        Long userId = (Long) request.getSession().getAttribute("userId");
        String tableName = (String) request.getSession().getAttribute("tableName");
        
        if (!"yonghu".equals(tableName)) {
            return R.error("非用户无法修改密码");
        }
        
        String oldPassword = params.get("oldPassword");
        String newPassword = params.get("newPassword");
        
        if (StringUtils.isBlank(oldPassword)) {
            return R.error("原密码不能为空");
        }
        if (StringUtils.isBlank(newPassword)) {
            return R.error("新密码不能为空");
        }
        if (newPassword.length() < 6 || newPassword.length() > 20) {
            return R.error("新密码长度必须在6-20位之间");
        }
        
        YonghuEntity user = yonghuService.selectById(userId);
        if (user == null) {
            return R.error("用户不存在");
        }
        
        if (!user.getMima().equals(oldPassword)) {
            return R.error("原密码错误");
        }
        
        user.setMima(newPassword);
        yonghuService.updateById(user);
        return R.ok("密码修改成功");
    }

    /**
     * 提醒接口
     */
	@RequestMapping("/remind/{columnName}/{type}")
	public R remindCount(@PathVariable("columnName") String columnName, HttpServletRequest request, 
						 @PathVariable("type") String type,@RequestParam Map<String, Object> map) {
		map.put("column", columnName);
		map.put("type", type);
		
		if(type.equals("2")) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			Calendar c = Calendar.getInstance();
			Date remindStartDate = null;
			Date remindEndDate = null;
			if(map.get("remindstart")!=null) {
				Integer remindStart = Integer.parseInt(map.get("remindstart").toString());
				c.setTime(new Date()); 
				c.add(Calendar.DAY_OF_MONTH,remindStart);
				remindStartDate = c.getTime();
				map.put("remindstart", sdf.format(remindStartDate));
			}
			if(map.get("remindend")!=null) {
				Integer remindEnd = Integer.parseInt(map.get("remindend").toString());
				c.setTime(new Date());
				c.add(Calendar.DAY_OF_MONTH,remindEnd);
				remindEndDate = c.getTime();
				map.put("remindend", sdf.format(remindEndDate));
			}
		}
		
		Wrapper<YonghuEntity> wrapper = new EntityWrapper<YonghuEntity>();
		if(map.get("remindstart")!=null) {
			wrapper.ge(columnName, map.get("remindstart"));
		}
		if(map.get("remindend")!=null) {
			wrapper.le(columnName, map.get("remindend"));
		}


		int count = yonghuService.selectCount(wrapper);
		return R.ok().put("count", count);
	}
	
	/**
	 * 获取余额
	 */
	@RequestMapping("/balance")
	public R getBalance(HttpServletRequest request) {
		Long userId = (Long) request.getSession().getAttribute("userId");
		String tableName = (String) request.getSession().getAttribute("tableName");
		
		if (!"yonghu".equals(tableName)) {
			return R.error("非用户无法查询余额");
		}
		
		YonghuEntity user = yonghuService.selectById(userId);
		if (user == null) {
			return R.error("用户不存在");
		}
		
		Double balance = user.getYue();
		if (balance == null) {
			balance = 0.0;
		}
		
		return R.ok().put("balance", balance);
	}
	
	/**
	 * 充值（模拟充值，实际应接入支付渠道）
	 */
	@RequestMapping("/recharge")
	public R recharge(@RequestBody Map<String, Object> params, HttpServletRequest request) {
		Long userId = (Long) request.getSession().getAttribute("userId");
		String tableName = (String) request.getSession().getAttribute("tableName");
		
		if (!"yonghu".equals(tableName)) {
			return R.error("非用户无法充值");
		}
		
		Object amountObj = params.get("amount");
		if (amountObj == null) {
			return R.error("充值金额不能为空");
		}
		
		Double amount;
		try {
			amount = Double.parseDouble(amountObj.toString());
		} catch (NumberFormatException e) {
			return R.error("充值金额格式错误");
		}
		
		if (amount <= 0) {
			return R.error("充值金额必须大于0");
		}
		if (amount > 10000) {
			return R.error("单次充值金额不能超过10000元");
		}
		
		YonghuEntity user = yonghuService.selectById(userId);
		if (user == null) {
			return R.error("用户不存在");
		}
		
		Double currentBalance = user.getYue();
		if (currentBalance == null) {
			currentBalance = 0.0;
		}
		
		user.setYue(currentBalance + amount);
		yonghuService.updateById(user);
		
		return R.ok("充值成功").put("balance", user.getYue());
	}


}
