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

import com.entity.YonghuruzhuEntity;
import com.entity.YonghuyuyueEntity;
import com.entity.KefangxinxiEntity;
import com.entity.view.YonghuruzhuView;

import com.service.YonghuruzhuService;
import com.service.YonghuyuyueService;
import com.service.KefangxinxiService;
import com.service.TokenService;
import com.utils.PageUtils;
import com.utils.R;
import com.utils.MD5Util;
import com.utils.MPUtil;
import com.utils.CommonUtil;


/**
 * 用户入住
 * 后端接口
 * @author 
 * @email 
 * @date 2021-04-30 10:31:55
 */
@RestController
@RequestMapping("/yonghuruzhu")
public class YonghuruzhuController {
    @Autowired
    private YonghuruzhuService yonghuruzhuService;
    
    @Autowired
    private YonghuyuyueService yonghuyuyueService;
    
    @Autowired
    private KefangxinxiService kefangxinxiService;
    


    /**
     * 后端列表
     */
    @RequestMapping("/page")
    public R page(@RequestParam Map<String, Object> params,YonghuruzhuEntity yonghuruzhu,
		HttpServletRequest request){
		String tableName = request.getSession().getAttribute("tableName").toString();
		if(tableName.equals("yonghu")) {
			yonghuruzhu.setZhanghao((String)request.getSession().getAttribute("username"));
		}
        EntityWrapper<YonghuruzhuEntity> ew = new EntityWrapper<YonghuruzhuEntity>();
		PageUtils page = yonghuruzhuService.queryPage(params, MPUtil.sort(MPUtil.between(MPUtil.likeOrEq(ew, yonghuruzhu), params), params));

        return R.ok().put("data", page);
    }
    
    /**
     * 前端列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params,YonghuruzhuEntity yonghuruzhu, 
		HttpServletRequest request){
        EntityWrapper<YonghuruzhuEntity> ew = new EntityWrapper<YonghuruzhuEntity>();
		PageUtils page = yonghuruzhuService.queryPage(params, MPUtil.sort(MPUtil.between(MPUtil.likeOrEq(ew, yonghuruzhu), params), params));
        return R.ok().put("data", page);
    }

	/**
     * 列表
     */
    @RequestMapping("/lists")
    public R list( YonghuruzhuEntity yonghuruzhu){
       	EntityWrapper<YonghuruzhuEntity> ew = new EntityWrapper<YonghuruzhuEntity>();
      	ew.allEq(MPUtil.allEQMapPre( yonghuruzhu, "yonghuruzhu")); 
        return R.ok().put("data", yonghuruzhuService.selectListView(ew));
    }

	 /**
     * 查询
     */
    @RequestMapping("/query")
    public R query(YonghuruzhuEntity yonghuruzhu){
        EntityWrapper< YonghuruzhuEntity> ew = new EntityWrapper< YonghuruzhuEntity>();
 		ew.allEq(MPUtil.allEQMapPre( yonghuruzhu, "yonghuruzhu")); 
		YonghuruzhuView yonghuruzhuView =  yonghuruzhuService.selectView(ew);
		return R.ok("查询用户入住成功").put("data", yonghuruzhuView);
    }
	
    /**
     * 后端详情
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
        YonghuruzhuEntity yonghuruzhu = yonghuruzhuService.selectById(id);
        return R.ok().put("data", yonghuruzhu);
    }

    /**
     * 前端详情
     */
    @RequestMapping("/detail/{id}")
    public R detail(@PathVariable("id") Long id){
        YonghuruzhuEntity yonghuruzhu = yonghuruzhuService.selectById(id);
        return R.ok().put("data", yonghuruzhu);
    }
    



    /**
     * 后端保存（用户入住操作 - 仅管理员/前台可操作）
     */
    @RequestMapping("/save")
    public R save(@RequestBody YonghuruzhuEntity yonghuruzhu, HttpServletRequest request){
        String tableName = (String) request.getSession().getAttribute("tableName");
        if (!"users".equals(tableName) && !"qiantairenyuan".equals(tableName)) {
            return R.error("无权限操作，仅管理员或前台人员可办理入住");
        }
        
    	yonghuruzhu.setId(new Date().getTime()+new Double(Math.floor(Math.random()*1000)).longValue());
    	yonghuruzhu.setAddtime(new Date());
    	if (yonghuruzhu.getRuzhushijian() == null) {
    	    yonghuruzhu.setRuzhushijian(new Date());
    	}
    	if (yonghuruzhu.getIspay() == null) {
    	    yonghuruzhu.setIspay("未支付");
    	}
        yonghuruzhuService.insert(yonghuruzhu);
        
        // 入住后更新客房状态为"已入住"
        if (StringUtils.isNotBlank(yonghuruzhu.getKefanghao())) {
            KefangxinxiEntity kefang = kefangxinxiService.selectOne(
                new EntityWrapper<KefangxinxiEntity>().eq("kefanghao", yonghuruzhu.getKefanghao()));
            if (kefang != null) {
                kefang.setKefangzhuangtai("已入住");
                kefangxinxiService.updateById(kefang);
            }
        }
        
        // 如果是从预约转入住，更新预约状态为已入住
        if (StringUtils.isNotBlank(yonghuruzhu.getDingdanbianhao())) {
            YonghuyuyueEntity yuyue = yonghuyuyueService.selectOne(
                new EntityWrapper<YonghuyuyueEntity>().eq("yuyuebianhao", yonghuruzhu.getDingdanbianhao()));
            if (yuyue != null) {
                yuyue.setYuyuezhuangtai("checkedin");
                yonghuyuyueService.updateById(yuyue);
            }
        }
        
        return R.ok("入住成功");
    }
    
    /**
     * 前端保存（禁用 - 入住只能通过后台办理）
     */
    @RequestMapping("/add")
    public R add(@RequestBody YonghuruzhuEntity yonghuruzhu, HttpServletRequest request){
        return R.error("入住办理请联系前台工作人员");
    }

    /**
     * 修改（仅管理员/前台可操作）
     */
    @RequestMapping("/update")
    public R update(@RequestBody YonghuruzhuEntity yonghuruzhu, HttpServletRequest request){
        String tableName = (String) request.getSession().getAttribute("tableName");
        if (!"users".equals(tableName) && !"qiantairenyuan".equals(tableName)) {
            return R.error("无权限操作");
        }
        yonghuruzhuService.updateById(yonghuruzhu);
        return R.ok();
    }
    
    /**
     * 支付入住费用（用户本人或管理员/前台可操作）
     */
    @RequestMapping("/pay/{id}")
    public R pay(@PathVariable("id") Long id, HttpServletRequest request){
        String tableName = (String) request.getSession().getAttribute("tableName");
        String username = (String) request.getSession().getAttribute("username");
        
        YonghuruzhuEntity ruzhu = yonghuruzhuService.selectById(id);
        if (ruzhu == null) {
            return R.error("入住记录不存在");
        }
        
        // 权限验证：用户只能支付自己的入住费用
        if ("yonghu".equals(tableName)) {
            if (!username.equals(ruzhu.getZhanghao())) {
                return R.error("无权操作他人的入住记录");
            }
        } else if (!"users".equals(tableName) && !"qiantairenyuan".equals(tableName)) {
            return R.error("无权限操作");
        }
        
        if ("已支付".equals(ruzhu.getIspay())) {
            return R.error("该入住记录已支付");
        }
        ruzhu.setIspay("已支付");
        yonghuruzhuService.updateById(ruzhu);
        return R.ok("支付成功");
    }

    /**
     * 删除入住记录（仅管理员可操作，同时恢复客房状态）
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids, HttpServletRequest request){
        String tableName = (String) request.getSession().getAttribute("tableName");
        if (!"users".equals(tableName)) {
            return R.error("无权限操作，仅管理员可删除入住记录");
        }
        
        // 删除前恢复客房状态
        for (Long id : ids) {
            YonghuruzhuEntity ruzhu = yonghuruzhuService.selectById(id);
            if (ruzhu != null && StringUtils.isNotBlank(ruzhu.getKefanghao())) {
                KefangxinxiEntity kefang = kefangxinxiService.selectOne(
                    new EntityWrapper<KefangxinxiEntity>().eq("kefanghao", ruzhu.getKefanghao()));
                if (kefang != null && "已入住".equals(kefang.getKefangzhuangtai())) {
                    kefang.setKefangzhuangtai("空闲");
                    kefang.setWeishengqingkuang("已清扫");
                    kefangxinxiService.updateById(kefang);
                }
            }
        }
        yonghuruzhuService.deleteBatchIds(Arrays.asList(ids));
        return R.ok();
    }
    
    /**
     * 基于预约办理入住（仅管理员/前台可操作）
     */
    @RequestMapping("/checkInByYuyue/{yuyueId}")
    public R checkInByYuyue(@PathVariable("yuyueId") Long yuyueId, HttpServletRequest request){
        String tableName = (String) request.getSession().getAttribute("tableName");
        if (!"users".equals(tableName) && !"qiantairenyuan".equals(tableName)) {
            return R.error("无权限操作，仅管理员或前台人员可办理入住");
        }
        
        // 获取预约信息
        YonghuyuyueEntity yuyue = yonghuyuyueService.selectById(yuyueId);
        if (yuyue == null) {
            return R.error("预约记录不存在");
        }
        
        // 验证预约状态
        if (!"是".equals(yuyue.getSfsh())) {
            return R.error("预约尚未审核通过，无法办理入住");
        }
        if (!"已支付".equals(yuyue.getIspay())) {
            return R.error("预约尚未支付，请先完成支付");
        }
        
        // 检查房间是否可入住（是否已清扫）
        KefangxinxiEntity kefang = kefangxinxiService.selectOne(
            new EntityWrapper<KefangxinxiEntity>().eq("kefanghao", yuyue.getKefanghao()));
        if (kefang == null) {
            return R.error("客房信息不存在");
        }
        if ("待清扫".equals(kefang.getWeishengqingkuang())) {
            return R.error("房间尚未清扫完成，暂不能入住");
        }
        if ("已入住".equals(kefang.getKefangzhuangtai())) {
            return R.error("该房间已有客人入住");
        }
        
        // 检查是否已经办理过入住
        YonghuruzhuEntity existRuzhu = yonghuruzhuService.selectOne(
            new EntityWrapper<YonghuruzhuEntity>().eq("dingdanbianhao", yuyue.getYuyuebianhao()));
        if (existRuzhu != null) {
            return R.error("该预约已办理过入住");
        }
        
        // 创建入住记录
        YonghuruzhuEntity ruzhu = new YonghuruzhuEntity();
        ruzhu.setId(new Date().getTime() + new Double(Math.floor(Math.random() * 1000)).longValue());
        ruzhu.setAddtime(new Date());
        ruzhu.setDingdanbianhao(yuyue.getYuyuebianhao());
        ruzhu.setKefanghao(yuyue.getKefanghao());
        ruzhu.setKefangleixing(kefang.getKefangleixing());
        ruzhu.setSuoshujiudian(kefang.getSuoshujiudian());
        ruzhu.setZhanghao(yuyue.getZhanghao());
        ruzhu.setXingming(yuyue.getXingming());
        ruzhu.setShouji(yuyue.getShouji());
        ruzhu.setShenfenzheng(yuyue.getShenfenzheng());
        ruzhu.setRuzhushijian(new Date());
        
        // 计算预离时间（入住时间 + 预约天数）
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.DAY_OF_MONTH, yuyue.getTianshu() != null ? yuyue.getTianshu() : 1);
        cal.set(Calendar.HOUR_OF_DAY, 12);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        ruzhu.setYulifangshijian(cal.getTime());
        
        ruzhu.setJiage(yuyue.getZongjia() != null ? Double.parseDouble(yuyue.getZongjia()) : (yuyue.getJiage() != null ? Double.parseDouble(yuyue.getJiage()) : 0.0));
        ruzhu.setIspay("已支付");
        
        yonghuruzhuService.insert(ruzhu);
        
        // 更新客房状态为"已入住"
        kefang.setKefangzhuangtai("已入住");
        kefangxinxiService.updateById(kefang);
        
        // 更新预约状态为"已入住"
        yuyue.setYuyuezhuangtai("checkedin");
        yonghuyuyueService.updateById(yuyue);
        
        return R.ok("入住办理成功").put("ruzhuId", ruzhu.getId());
    }
    
    /**
     * 验证预约是否可入住
     */
    @RequestMapping("/validateCheckIn/{yuyueId}")
    public R validateCheckIn(@PathVariable("yuyueId") Long yuyueId){
        Map<String, Object> result = new HashMap<>();
        result.put("valid", true);
        result.put("message", "可以办理入住");
        
        YonghuyuyueEntity yuyue = yonghuyuyueService.selectById(yuyueId);
        if (yuyue == null) {
            result.put("valid", false);
            result.put("message", "预约记录不存在");
            return R.ok().put("data", result);
        }
        
        if (!"是".equals(yuyue.getSfsh())) {
            result.put("valid", false);
            result.put("message", "预约尚未审核通过");
            return R.ok().put("data", result);
        }
        
        if (!"已支付".equals(yuyue.getIspay())) {
            result.put("valid", false);
            result.put("message", "预约尚未支付");
            return R.ok().put("data", result);
        }
        
        KefangxinxiEntity kefang = kefangxinxiService.selectOne(
            new EntityWrapper<KefangxinxiEntity>().eq("kefanghao", yuyue.getKefanghao()));
        if (kefang != null && "待清扫".equals(kefang.getWeishengqingkuang())) {
            result.put("valid", false);
            result.put("message", "房间尚未清扫完成");
            return R.ok().put("data", result);
        }
        
        result.put("yuyue", yuyue);
        return R.ok().put("data", result);
    }
    
    /**
     * 查询当前在住客人列表
     */
    @RequestMapping("/currentGuests")
    public R currentGuests(@RequestParam Map<String, Object> params, HttpServletRequest request) {
        String tableName = (String) request.getSession().getAttribute("tableName");
        if (!"users".equals(tableName) && !"qiantairenyuan".equals(tableName)) {
            return R.error("无权限查看");
        }
        
        EntityWrapper<YonghuruzhuEntity> ew = new EntityWrapper<>();
        ew.isNotNull("ruzhushijian");
        ew.orderBy("ruzhushijian", false);
        
        PageUtils page = yonghuruzhuService.queryPage(params, ew);
        return R.ok().put("data", page);
    }
    
    /**
     * 根据客房号查询在住客人
     */
    @RequestMapping("/guestByRoom/{kefanghao}")
    public R guestByRoom(@PathVariable("kefanghao") String kefanghao) {
        EntityWrapper<YonghuruzhuEntity> ew = new EntityWrapper<>();
        ew.eq("kefanghao", kefanghao);
        ew.orderBy("ruzhushijian", false);
        List<YonghuruzhuEntity> list = yonghuruzhuService.selectList(ew);
        return R.ok().put("data", list.isEmpty() ? null : list.get(0));
    }
    
    /**
     * 现场入住（散客入住，无预约直接入住）
     * 仅管理员/前台可操作
     */
    @RequestMapping("/walkIn")
    public R walkIn(@RequestBody Map<String, Object> params, HttpServletRequest request){
        String tableName = (String) request.getSession().getAttribute("tableName");
        if (!"users".equals(tableName) && !"qiantairenyuan".equals(tableName)) {
            return R.error("无权限操作，仅管理员或前台人员可办理入住");
        }
        
        String kefanghao = (String) params.get("kefanghao");
        String xingming = (String) params.get("xingming");
        String shouji = (String) params.get("shouji");
        String shenfenzheng = (String) params.get("shenfenzheng");
        Integer tianshu = params.get("tianshu") != null ? Integer.valueOf(params.get("tianshu").toString()) : 1;
        
        if (StringUtils.isBlank(kefanghao)) {
            return R.error("请选择房间");
        }
        if (StringUtils.isBlank(xingming)) {
            return R.error("请输入入住人姓名");
        }
        if (StringUtils.isBlank(shouji)) {
            return R.error("请输入手机号");
        }
        if (StringUtils.isBlank(shenfenzheng)) {
            return R.error("请输入身份证号");
        }
        
        // 验证房间是否可入住
        KefangxinxiEntity kefang = kefangxinxiService.selectOne(
            new EntityWrapper<KefangxinxiEntity>().eq("kefanghao", kefanghao));
        if (kefang == null) {
            return R.error("客房信息不存在");
        }
        if ("已入住".equals(kefang.getKefangzhuangtai())) {
            return R.error("该房间已有客人入住");
        }
        if ("待清扫".equals(kefang.getWeishengqingkuang())) {
            return R.error("房间尚未清扫完成，暂不能入住");
        }
        if ("已预约".equals(kefang.getKefangzhuangtai())) {
            return R.error("该房间已被预约，请选择其他房间");
        }
        
        // 创建入住记录
        YonghuruzhuEntity ruzhu = new YonghuruzhuEntity();
        ruzhu.setId(new Date().getTime() + new Double(Math.floor(Math.random() * 1000)).longValue());
        ruzhu.setAddtime(new Date());
        ruzhu.setDingdanbianhao("WI" + System.currentTimeMillis());
        ruzhu.setKefanghao(kefanghao);
        ruzhu.setKefangleixing(kefang.getKefangleixing());
        ruzhu.setSuoshujiudian(kefang.getSuoshujiudian());
        ruzhu.setZhanghao("散客");
        ruzhu.setXingming(xingming);
        ruzhu.setShouji(shouji);
        ruzhu.setShenfenzheng(shenfenzheng);
        ruzhu.setRuzhushijian(new Date());
        ruzhu.setKefangzhuangtai("已入住");
        
        // 计算预离时间
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.DAY_OF_MONTH, tianshu);
        cal.set(Calendar.HOUR_OF_DAY, 12);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        ruzhu.setYulifangshijian(cal.getTime());
        
        // 计算费用
        Double jiage = kefang.getJiage() != null ? kefang.getJiage() * tianshu : 0.0;
        ruzhu.setJiage(jiage);
        ruzhu.setIspay("未支付");
        
        yonghuruzhuService.insert(ruzhu);
        
        // 更新客房状态
        kefang.setKefangzhuangtai("已入住");
        kefangxinxiService.updateById(kefang);
        
        return R.ok("入住办理成功，应付金额：¥" + jiage).put("ruzhuId", ruzhu.getId()).put("jiage", jiage);
    }
    
    /**
     * 获取可入住的空闲房间列表
     */
    @RequestMapping("/freeRooms")
    public R getFreeRooms(HttpServletRequest request) {
        EntityWrapper<KefangxinxiEntity> ew = new EntityWrapper<>();
        ew.eq("kefangzhuangtai", "空闲");
        ew.eq("weishengqingkuang", "已清扫");
        List<KefangxinxiEntity> list = kefangxinxiService.selectList(ew);
        return R.ok().put("data", list);
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
		
		Wrapper<YonghuruzhuEntity> wrapper = new EntityWrapper<YonghuruzhuEntity>();
		if(map.get("remindstart")!=null) {
			wrapper.ge(columnName, map.get("remindstart"));
		}
		if(map.get("remindend")!=null) {
			wrapper.le(columnName, map.get("remindend"));
		}

		String tableName = request.getSession().getAttribute("tableName").toString();
		if(tableName.equals("yonghu")) {
			wrapper.eq("zhanghao", (String)request.getSession().getAttribute("username"));
		}

		int count = yonghuruzhuService.selectCount(wrapper);
		return R.ok().put("count", count);
	}
	


}
