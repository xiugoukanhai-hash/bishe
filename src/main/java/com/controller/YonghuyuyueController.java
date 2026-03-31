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

import com.entity.YonghuyuyueEntity;
import com.entity.KefangxinxiEntity;
import com.entity.view.YonghuyuyueView;

import com.service.YonghuyuyueService;
import com.service.KefangxinxiService;
import com.service.BookingService;
import com.service.TokenService;
import com.utils.PageUtils;
import com.utils.R;
import com.utils.MD5Util;
import com.utils.MPUtil;
import com.utils.CommonUtil;


/**
 * 用户预约
 * 后端接口
 * @author 
 * @email 
 * @date 2021-04-30 10:31:54
 */
@RestController
@RequestMapping("/yonghuyuyue")
public class YonghuyuyueController {
    @Autowired
    private YonghuyuyueService yonghuyuyueService;
    
    @Autowired
    private KefangxinxiService kefangxinxiService;
    
    @Autowired
    private BookingService bookingService;
    


    /**
     * 后端列表
     */
    @RequestMapping("/page")
    public R page(@RequestParam Map<String, Object> params,YonghuyuyueEntity yonghuyuyue,
		HttpServletRequest request){
		String tableName = request.getSession().getAttribute("tableName").toString();
		if(tableName.equals("yonghu")) {
			yonghuyuyue.setZhanghao((String)request.getSession().getAttribute("username"));
		}
        EntityWrapper<YonghuyuyueEntity> ew = new EntityWrapper<YonghuyuyueEntity>();
		PageUtils page = yonghuyuyueService.queryPage(params, MPUtil.sort(MPUtil.between(MPUtil.likeOrEq(ew, yonghuyuyue), params), params));

        return R.ok().put("data", page);
    }
    
    /**
     * 前端列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params,YonghuyuyueEntity yonghuyuyue, 
		HttpServletRequest request){
        EntityWrapper<YonghuyuyueEntity> ew = new EntityWrapper<YonghuyuyueEntity>();
		PageUtils page = yonghuyuyueService.queryPage(params, MPUtil.sort(MPUtil.between(MPUtil.likeOrEq(ew, yonghuyuyue), params), params));
        return R.ok().put("data", page);
    }

	/**
     * 列表
     */
    @RequestMapping("/lists")
    public R list( YonghuyuyueEntity yonghuyuyue){
       	EntityWrapper<YonghuyuyueEntity> ew = new EntityWrapper<YonghuyuyueEntity>();
      	ew.allEq(MPUtil.allEQMapPre( yonghuyuyue, "yonghuyuyue")); 
        return R.ok().put("data", yonghuyuyueService.selectListView(ew));
    }

	 /**
     * 查询
     */
    @RequestMapping("/query")
    public R query(YonghuyuyueEntity yonghuyuyue){
        EntityWrapper< YonghuyuyueEntity> ew = new EntityWrapper< YonghuyuyueEntity>();
 		ew.allEq(MPUtil.allEQMapPre( yonghuyuyue, "yonghuyuyue")); 
		YonghuyuyueView yonghuyuyueView =  yonghuyuyueService.selectView(ew);
		return R.ok("查询用户预约成功").put("data", yonghuyuyueView);
    }
	
    /**
     * 后端详情
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
        YonghuyuyueEntity yonghuyuyue = yonghuyuyueService.selectById(id);
        return R.ok().put("data", yonghuyuyue);
    }

    /**
     * 前端详情
     */
    @RequestMapping("/detail/{id}")
    public R detail(@PathVariable("id") Long id){
        YonghuyuyueEntity yonghuyuyue = yonghuyuyueService.selectById(id);
        return R.ok().put("data", yonghuyuyue);
    }
    



    /**
     * 后端保存（用户预约操作 - 管理员/前台可操作）
     */
    @RequestMapping("/save")
    public R save(@RequestBody YonghuyuyueEntity yonghuyuyue, HttpServletRequest request){
        String tableName = (String) request.getSession().getAttribute("tableName");
        if (!"users".equals(tableName) && !"qiantairenyuan".equals(tableName) && !"yonghu".equals(tableName)) {
            return R.error("无权限操作");
        }
        
        // 如果是用户自己预约，验证账号匹配
        if ("yonghu".equals(tableName)) {
            String username = (String) request.getSession().getAttribute("username");
            if (!username.equals(yonghuyuyue.getZhanghao())) {
                return R.error("只能为自己预约");
            }
        }
        
    	yonghuyuyue.setId(new Date().getTime()+new Double(Math.floor(Math.random()*1000)).longValue());
    	yonghuyuyue.setAddtime(new Date());
    	if (yonghuyuyue.getSfsh() == null) {
    	    yonghuyuyue.setSfsh("待审核");
    	}
    	if (yonghuyuyue.getIspay() == null) {
    	    yonghuyuyue.setIspay("未支付");
    	}
        yonghuyuyueService.insert(yonghuyuyue);
        
        // 预约后更新客房状态为"已预约"
        if (StringUtils.isNotBlank(yonghuyuyue.getKefanghao())) {
            KefangxinxiEntity kefang = kefangxinxiService.selectOne(
                new EntityWrapper<KefangxinxiEntity>().eq("kefanghao", yonghuyuyue.getKefanghao()));
            if (kefang != null) {
                kefang.setKefangzhuangtai("已预约");
                kefangxinxiService.updateById(kefang);
            }
        }
        
        // 返回预约成功并包含订单ID
        return R.ok("预约成功").put("data", yonghuyuyue.getId());
    }
    
    /**
     * 前端保存（用户预约操作）
     */
    @RequestMapping("/add")
    public R add(@RequestBody YonghuyuyueEntity yonghuyuyue, HttpServletRequest request){
        // 验证客房是否可预约
        if (StringUtils.isNotBlank(yonghuyuyue.getKefanghao())) {
            KefangxinxiEntity kefang = kefangxinxiService.selectOne(
                new EntityWrapper<KefangxinxiEntity>().eq("kefanghao", yonghuyuyue.getKefanghao()));
            if (kefang == null) {
                return R.error("客房不存在");
            }
            String status = kefang.getKefangzhuangtai();
            if (!"空闲".equals(status) && !"未入住".equals(status)) {
                return R.error("该客房当前状态为\"" + status + "\"，暂不可预约");
            }
            String cleanStatus = kefang.getWeishengqingkuang();
            if (cleanStatus != null && !"已清扫".equals(cleanStatus)) {
                return R.error("该客房卫生状况为\"" + cleanStatus + "\"，暂不可预约");
            }
        } else {
            return R.error("客房号不能为空");
        }
        
        // 验证入住时间
        if (yonghuyuyue.getRuzhushijian() == null) {
            return R.error("请选择入住时间");
        }
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Date today = cal.getTime();
        if (yonghuyuyue.getRuzhushijian().before(today)) {
            return R.error("入住时间不能早于今天");
        }
        
        // 验证入住天数
        if (yonghuyuyue.getTianshu() == null || yonghuyuyue.getTianshu() <= 0) {
            return R.error("入住天数必须大于0");
        }
        if (yonghuyuyue.getTianshu() > 30) {
            return R.error("单次入住最多30天");
        }
        
        // 验证联系人信息
        if (StringUtils.isBlank(yonghuyuyue.getXingming())) {
            return R.error("请输入入住人姓名");
        }
        if (StringUtils.isBlank(yonghuyuyue.getShouji())) {
            return R.error("请输入联系手机号");
        }
        if (StringUtils.isBlank(yonghuyuyue.getShenfenzheng())) {
            return R.error("请输入身份证号");
        }
        
        // 设置预约时间和初始状态
        yonghuyuyue.setYuyueshijian(new Date());
        yonghuyuyue.setSfsh("待审核");
        yonghuyuyue.setIspay("未支付");
        
        yonghuyuyue.setId(new Date().getTime()+new Double(Math.floor(Math.random()*1000)).longValue());
        yonghuyuyueService.insert(yonghuyuyue);
        
        // 预约后更新客房状态为"已预约"
        KefangxinxiEntity kefang = kefangxinxiService.selectOne(
            new EntityWrapper<KefangxinxiEntity>().eq("kefanghao", yonghuyuyue.getKefanghao()));
        if (kefang != null) {
            kefang.setKefangzhuangtai("已预约");
            kefangxinxiService.updateById(kefang);
        }
        
        // 返回预约成功并包含订单ID
        return R.ok("预约成功").put("data", yonghuyuyue.getId());
    }

    /**
     * 修改（管理员/前台可修改任意，用户只能修改自己的待审核预约）
     */
    @RequestMapping("/update")
    public R update(@RequestBody YonghuyuyueEntity yonghuyuyue, HttpServletRequest request){
        String tableName = (String) request.getSession().getAttribute("tableName");
        String username = (String) request.getSession().getAttribute("username");
        
        if (!"users".equals(tableName) && !"qiantairenyuan".equals(tableName) && !"yonghu".equals(tableName)) {
            return R.error("无权限操作");
        }
        
        // 用户只能修改自己的待审核预约
        if ("yonghu".equals(tableName)) {
            YonghuyuyueEntity existing = yonghuyuyueService.selectById(yonghuyuyue.getId());
            if (existing == null) {
                return R.error("预约记录不存在");
            }
            if (!username.equals(existing.getZhanghao())) {
                return R.error("无权修改他人的预约");
            }
            if (!"待审核".equals(existing.getSfsh())) {
                return R.error("只能修改待审核状态的预约");
            }
        }
        
        yonghuyuyueService.updateById(yonghuyuyue);
        return R.ok();
    }
    
    /**
     * 设置订单为已评价（专用接口，允许用户更新自己已完成订单的评价状态）
     */
    @RequestMapping("/setEvaluated/{id}")
    public R setEvaluated(@PathVariable("id") Long id, HttpServletRequest request) {
        String tableName = (String) request.getSession().getAttribute("tableName");
        String username = (String) request.getSession().getAttribute("username");
        
        YonghuyuyueEntity existing = yonghuyuyueService.selectById(id);
        if (existing == null) {
            return R.error("订单不存在");
        }
        
        // 用户只能更新自己的订单
        if ("yonghu".equals(tableName)) {
            if (!username.equals(existing.getZhanghao())) {
                return R.error("无权操作他人的订单");
            }
        }
        
        // 只有已完成的订单才能评价
        String status = existing.getYuyuezhuangtai();
        if (!"已完成".equals(status) && !"已入住".equals(status) && !"已支付".equals(status) && !"待入住".equals(status)) {
            return R.error("只有已完成或已入住的订单可以评价");
        }
        
        existing.setIspingjia("是");
        yonghuyuyueService.updateById(existing);
        return R.ok("评价状态已更新");
    }

    /**
     * 删除预约记录（同时恢复客房状态）
     * 权限：管理员可删除任意记录
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids, HttpServletRequest request){
        String tableName = (String) request.getSession().getAttribute("tableName");
        
        // 只有管理员可以删除预约记录
        if (!"users".equals(tableName)) {
            return R.error("无权限操作，仅管理员可删除预约记录");
        }
        
        // 删除前恢复客房状态
        for (Long id : ids) {
            YonghuyuyueEntity yuyue = yonghuyuyueService.selectById(id);
            if (yuyue != null && StringUtils.isNotBlank(yuyue.getKefanghao())) {
                KefangxinxiEntity kefang = kefangxinxiService.selectOne(
                    new EntityWrapper<KefangxinxiEntity>().eq("kefanghao", yuyue.getKefanghao()));
                if (kefang != null && "已预约".equals(kefang.getKefangzhuangtai())) {
                    kefang.setKefangzhuangtai("空闲");
                    kefang.setWeishengqingkuang("已清扫");
                    kefangxinxiService.updateById(kefang);
                }
            }
        }
        yonghuyuyueService.deleteBatchIds(Arrays.asList(ids));
        return R.ok();
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
		
		Wrapper<YonghuyuyueEntity> wrapper = new EntityWrapper<YonghuyuyueEntity>();
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

		int count = yonghuyuyueService.selectCount(wrapper);
		return R.ok().put("count", count);
	}

    /**
     * 创建预约
     */
    @RequestMapping("/create")
    public R create(@RequestBody Map<String, Object> params, HttpServletRequest request) {
        Long userId = (Long) request.getSession().getAttribute("userId");
        String tableName = (String) request.getSession().getAttribute("tableName");

        if (!"yonghu".equals(tableName)) {
            return R.error("仅用户可以进行此操作");
        }

        String kefanghao = (String) params.get("kefanghao");
        String ruzhushijianStr = (String) params.get("ruzhushijian");
        Object tianshuObj = params.get("tianshu");
        Integer tianshu = tianshuObj != null ? Integer.parseInt(tianshuObj.toString()) : 1;

        if (StringUtils.isBlank(kefanghao)) {
            return R.error("请选择客房");
        }
        if (StringUtils.isBlank(ruzhushijianStr)) {
            return R.error("请选择入住时间");
        }
        if (tianshu < 1) {
            return R.error("入住天数不能小于1天");
        }

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date ruzhushijian = sdf.parse(ruzhushijianStr);

            String yuyuebianhao = bookingService.createYonghuBooking(userId, kefanghao, ruzhushijian, tianshu);
            return R.ok("预约成功，请等待审核").put("yuyuebianhao", yuyuebianhao);
        } catch (Exception e) {
            return R.error("预约失败：" + e.getMessage());
        }
    }

    /**
     * 审核预约（前台/管理员）
     */
    @RequestMapping("/audit/{id}")
    public R audit(@PathVariable("id") Long id, @RequestBody Map<String, Object> params, HttpServletRequest request) {
        String tableName = (String) request.getSession().getAttribute("tableName");
        if (!"users".equals(tableName) && !"qiantairenyuan".equals(tableName)) {
            return R.error("无权限操作");
        }

        String sfsh = (String) params.get("sfsh");
        String shhf = (String) params.get("shhf");

        if (StringUtils.isBlank(sfsh)) {
            return R.error("请选择审核结果");
        }

        try {
            bookingService.auditYonghuBooking(id, sfsh, shhf);
            return R.ok("审核完成");
        } catch (Exception e) {
            return R.error("审核失败：" + e.getMessage());
        }
    }

    /**
     * 支付预约
     * @param payType 支付方式：wechat-微信支付，balance-余额支付
     */
    @RequestMapping("/pay/{id}")
    public R pay(@PathVariable("id") Long id, @RequestBody(required = false) Map<String, Object> params, HttpServletRequest request) {
        // 验证用户权限
        String tableName = (String) request.getSession().getAttribute("tableName");
        String username = (String) request.getSession().getAttribute("username");
        
        if (!"yonghu".equals(tableName) && !"users".equals(tableName)) {
            return R.error("无权限操作");
        }
        
        // 验证预约所有权（普通用户只能支付自己的预约）
        if ("yonghu".equals(tableName)) {
            YonghuyuyueEntity booking = yonghuyuyueService.selectById(id);
            if (booking == null) {
                return R.error("预约记录不存在");
            }
            if (!booking.getZhanghao().equals(username)) {
                return R.error("无权操作他人的预约");
            }
        }
        
        // 获取支付方式，默认微信支付
        String payType = "wechat";
        if (params != null && params.get("payType") != null) {
            payType = params.get("payType").toString();
        }
        
        try {
            Map<String, Object> result = bookingService.payYonghuBooking(id, payType);
            return R.ok().put("data", result);
        } catch (Exception e) {
            return R.error("支付失败：" + e.getMessage());
        }
    }

    /**
     * 取消预约
     */
    @RequestMapping("/cancel/{id}")
    public R cancel(@PathVariable("id") Long id, @RequestBody Map<String, Object> params, HttpServletRequest request) {
        // 验证用户权限
        String tableName = (String) request.getSession().getAttribute("tableName");
        String username = (String) request.getSession().getAttribute("username");
        
        if (!"yonghu".equals(tableName) && !"users".equals(tableName)) {
            return R.error("无权限操作");
        }
        
        // 验证预约所有权
        if ("yonghu".equals(tableName)) {
            YonghuyuyueEntity booking = yonghuyuyueService.selectById(id);
            if (booking == null) {
                return R.error("预约记录不存在");
            }
            if (!booking.getZhanghao().equals(username)) {
                return R.error("无权操作他人的预约");
            }
        }
        
        String reason = (String) params.get("reason");
        try {
            bookingService.cancelYonghuBooking(id, reason);
            return R.ok("取消成功");
        } catch (Exception e) {
            return R.error("取消失败：" + e.getMessage());
        }
    }

    /**
     * 计算预约价格
     */
    @RequestMapping("/calcPrice")
    public R calcPrice(@RequestBody Map<String, Object> params) {
        String kefanghao = (String) params.get("kefanghao");
        Object tianshuObj = params.get("tianshu");
        Integer tianshu = tianshuObj != null ? Integer.parseInt(tianshuObj.toString()) : 1;

        try {
            Double price = bookingService.calculatePrice(kefanghao, tianshu, false);
            return R.ok().put("price", price);
        } catch (Exception e) {
            return R.error("计算失败：" + e.getMessage());
        }
    }

    /**
     * 查询我的预约列表
     */
    @RequestMapping("/mylist")
    public R mylist(@RequestParam Map<String, Object> params, HttpServletRequest request) {
        String username = (String) request.getSession().getAttribute("username");
        
        EntityWrapper<YonghuyuyueEntity> ew = new EntityWrapper<YonghuyuyueEntity>();
        ew.eq("zhanghao", username);
        ew.orderBy("addtime", false);

        PageUtils page = yonghuyuyueService.queryPage(params, ew);
        return R.ok().put("data", page);
    }

    /**
     * 确认退款（管理员/前台可操作）
     * 将"申请退款"状态的订单改为"已退款"
     */
    @RequestMapping("/confirmRefund/{id}")
    public R confirmRefund(@PathVariable("id") Long id, HttpServletRequest request) {
        String tableName = (String) request.getSession().getAttribute("tableName");
        if (!"users".equals(tableName) && !"qiantairenyuan".equals(tableName)) {
            return R.error("无权限操作");
        }
        
        YonghuyuyueEntity booking = yonghuyuyueService.selectById(id);
        if (booking == null) {
            return R.error("预约记录不存在");
        }
        if (!"申请退款".equals(booking.getYuyuezhuangtai())) {
            return R.error("该订单不是申请退款状态");
        }
        
        booking.setYuyuezhuangtai("已退款");
        booking.setIspay("已退款");
        yonghuyuyueService.updateById(booking);
        
        return R.ok("退款已确认");
    }

    /**
     * 修复已取消订单的房间状态（管理员/前台可操作）
     */
    @RequestMapping("/fixRoomStatus")
    public R fixRoomStatus(HttpServletRequest request) {
        String tableName = (String) request.getSession().getAttribute("tableName");
        if (!"users".equals(tableName) && !"qiantairenyuan".equals(tableName)) {
            return R.error("无权限操作");
        }
        
        // 查找所有已取消的预约
        EntityWrapper<YonghuyuyueEntity> ew = new EntityWrapper<>();
        ew.eq("yuyuezhuangtai", "已取消");
        List<YonghuyuyueEntity> cancelledBookings = yonghuyuyueService.selectList(ew);
        
        int fixedCount = 0;
        for (YonghuyuyueEntity booking : cancelledBookings) {
            if (StringUtils.isNotBlank(booking.getKefanghao())) {
                KefangxinxiEntity room = kefangxinxiService.selectOne(
                    new EntityWrapper<KefangxinxiEntity>().eq("kefanghao", booking.getKefanghao())
                );
                if (room != null && "已预约".equals(room.getKefangzhuangtai())) {
                    room.setKefangzhuangtai("空闲");
                    kefangxinxiService.updateById(room);
                    fixedCount++;
                }
            }
        }
        
        return R.ok("修复完成，共修复 " + fixedCount + " 个房间状态");
    }
}
