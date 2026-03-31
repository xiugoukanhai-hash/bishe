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

import com.entity.HuiyuanyuyueEntity;
import com.entity.KefangxinxiEntity;
import com.entity.view.HuiyuanyuyueView;

import com.service.HuiyuanyuyueService;
import com.service.KefangxinxiService;
import com.service.BookingService;
import com.service.TokenService;
import com.utils.PageUtils;
import com.utils.R;
import com.utils.MD5Util;
import com.utils.MPUtil;
import com.utils.CommonUtil;


/**
 * 会员预约
 * 后端接口
 * @author 
 * @email 
 * @date 2021-04-30 10:31:54
 */
@RestController
@RequestMapping("/huiyuanyuyue")
public class HuiyuanyuyueController {
    @Autowired
    private HuiyuanyuyueService huiyuanyuyueService;
    
    @Autowired
    private KefangxinxiService kefangxinxiService;
    
    @Autowired
    private BookingService bookingService;
    


    /**
     * 后端列表
     */
    @RequestMapping("/page")
    public R page(@RequestParam Map<String, Object> params,HuiyuanyuyueEntity huiyuanyuyue,
		HttpServletRequest request){
		String tableName = request.getSession().getAttribute("tableName").toString();
		if(tableName.equals("huiyuan")) {
			huiyuanyuyue.setZhanghao((String)request.getSession().getAttribute("username"));
		}
        EntityWrapper<HuiyuanyuyueEntity> ew = new EntityWrapper<HuiyuanyuyueEntity>();
		PageUtils page = huiyuanyuyueService.queryPage(params, MPUtil.sort(MPUtil.between(MPUtil.likeOrEq(ew, huiyuanyuyue), params), params));

        return R.ok().put("data", page);
    }
    
    /**
     * 前端列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params,HuiyuanyuyueEntity huiyuanyuyue, 
		HttpServletRequest request){
        EntityWrapper<HuiyuanyuyueEntity> ew = new EntityWrapper<HuiyuanyuyueEntity>();
		PageUtils page = huiyuanyuyueService.queryPage(params, MPUtil.sort(MPUtil.between(MPUtil.likeOrEq(ew, huiyuanyuyue), params), params));
        return R.ok().put("data", page);
    }

	/**
     * 列表
     */
    @RequestMapping("/lists")
    public R list( HuiyuanyuyueEntity huiyuanyuyue){
       	EntityWrapper<HuiyuanyuyueEntity> ew = new EntityWrapper<HuiyuanyuyueEntity>();
      	ew.allEq(MPUtil.allEQMapPre( huiyuanyuyue, "huiyuanyuyue")); 
        return R.ok().put("data", huiyuanyuyueService.selectListView(ew));
    }

	 /**
     * 查询
     */
    @RequestMapping("/query")
    public R query(HuiyuanyuyueEntity huiyuanyuyue){
        EntityWrapper< HuiyuanyuyueEntity> ew = new EntityWrapper< HuiyuanyuyueEntity>();
 		ew.allEq(MPUtil.allEQMapPre( huiyuanyuyue, "huiyuanyuyue")); 
		HuiyuanyuyueView huiyuanyuyueView =  huiyuanyuyueService.selectView(ew);
		return R.ok("查询会员预约成功").put("data", huiyuanyuyueView);
    }
	
    /**
     * 后端详情
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
        HuiyuanyuyueEntity huiyuanyuyue = huiyuanyuyueService.selectById(id);
        return R.ok().put("data", huiyuanyuyue);
    }

    /**
     * 前端详情
     */
    @RequestMapping("/detail/{id}")
    public R detail(@PathVariable("id") Long id){
        HuiyuanyuyueEntity huiyuanyuyue = huiyuanyuyueService.selectById(id);
        return R.ok().put("data", huiyuanyuyue);
    }
    



    /**
     * 后端保存（会员预约操作 - 管理员/前台可操作）
     */
    @RequestMapping("/save")
    public R save(@RequestBody HuiyuanyuyueEntity huiyuanyuyue, HttpServletRequest request){
        String tableName = (String) request.getSession().getAttribute("tableName");
        if (!"users".equals(tableName) && !"qiantairenyuan".equals(tableName) && !"huiyuan".equals(tableName)) {
            return R.error("无权限操作");
        }
        
        // 如果是会员自己预约，验证账号匹配
        if ("huiyuan".equals(tableName)) {
            String username = (String) request.getSession().getAttribute("username");
            if (!username.equals(huiyuanyuyue.getZhanghao())) {
                return R.error("只能为自己预约");
            }
        }
        
    	huiyuanyuyue.setId(new Date().getTime()+new Double(Math.floor(Math.random()*1000)).longValue());
    	huiyuanyuyue.setAddtime(new Date());
    	if (huiyuanyuyue.getSfsh() == null) {
    	    huiyuanyuyue.setSfsh("待审核");
    	}
    	if (huiyuanyuyue.getIspay() == null) {
    	    huiyuanyuyue.setIspay("未支付");
    	}
        huiyuanyuyueService.insert(huiyuanyuyue);
        
        // 预约后更新客房状态为"已预约"
        if (StringUtils.isNotBlank(huiyuanyuyue.getKefanghao())) {
            KefangxinxiEntity kefang = kefangxinxiService.selectOne(
                new EntityWrapper<KefangxinxiEntity>().eq("kefanghao", huiyuanyuyue.getKefanghao()));
            if (kefang != null) {
                kefang.setKefangzhuangtai("已预约");
                kefangxinxiService.updateById(kefang);
            }
        }
        
        // 返回预约成功并包含订单ID
        return R.ok("预约成功").put("data", huiyuanyuyue.getId());
    }
    
    /**
     * 前端保存（会员预约操作）
     */
    @RequestMapping("/add")
    public R add(@RequestBody HuiyuanyuyueEntity huiyuanyuyue, HttpServletRequest request){
        // 验证客房是否可预约
        if (StringUtils.isNotBlank(huiyuanyuyue.getKefanghao())) {
            KefangxinxiEntity kefang = kefangxinxiService.selectOne(
                new EntityWrapper<KefangxinxiEntity>().eq("kefanghao", huiyuanyuyue.getKefanghao()));
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
        if (huiyuanyuyue.getRuzhushijian() == null) {
            return R.error("请选择入住时间");
        }
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Date today = cal.getTime();
        if (huiyuanyuyue.getRuzhushijian().before(today)) {
            return R.error("入住时间不能早于今天");
        }
        
        // 验证入住天数
        if (huiyuanyuyue.getTianshu() == null || huiyuanyuyue.getTianshu() <= 0) {
            return R.error("入住天数必须大于0");
        }
        if (huiyuanyuyue.getTianshu() > 30) {
            return R.error("单次入住最多30天");
        }
        
        // 验证联系人信息
        if (StringUtils.isBlank(huiyuanyuyue.getXingming())) {
            return R.error("请输入入住人姓名");
        }
        if (StringUtils.isBlank(huiyuanyuyue.getShouji())) {
            return R.error("请输入联系手机号");
        }
        if (StringUtils.isBlank(huiyuanyuyue.getShenfenzheng())) {
            return R.error("请输入身份证号");
        }
        
        // 设置预约时间和初始状态
        huiyuanyuyue.setYuyueshijian(new Date());
        huiyuanyuyue.setSfsh("待审核");
        huiyuanyuyue.setIspay("未支付");
        
        huiyuanyuyue.setId(new Date().getTime()+new Double(Math.floor(Math.random()*1000)).longValue());
        huiyuanyuyueService.insert(huiyuanyuyue);
        
        // 预约后更新客房状态为"已预约"
        KefangxinxiEntity kefang = kefangxinxiService.selectOne(
            new EntityWrapper<KefangxinxiEntity>().eq("kefanghao", huiyuanyuyue.getKefanghao()));
        if (kefang != null) {
            kefang.setKefangzhuangtai("已预约");
            kefangxinxiService.updateById(kefang);
        }
        
        // 返回预约成功并包含订单ID
        return R.ok("预约成功").put("data", huiyuanyuyue.getId());
    }

    /**
     * 修改（管理员/前台可修改任意，会员只能修改自己的待审核预约）
     */
    @RequestMapping("/update")
    public R update(@RequestBody HuiyuanyuyueEntity huiyuanyuyue, HttpServletRequest request){
        String tableName = (String) request.getSession().getAttribute("tableName");
        String username = (String) request.getSession().getAttribute("username");
        
        if (!"users".equals(tableName) && !"qiantairenyuan".equals(tableName) && !"huiyuan".equals(tableName)) {
            return R.error("无权限操作");
        }
        
        // 会员只能修改自己的待审核预约
        if ("huiyuan".equals(tableName)) {
            HuiyuanyuyueEntity existing = huiyuanyuyueService.selectById(huiyuanyuyue.getId());
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
        
        huiyuanyuyueService.updateById(huiyuanyuyue);
        return R.ok();
    }
    
    /**
     * 设置订单为已评价（专用接口，允许会员更新自己已完成订单的评价状态）
     */
    @RequestMapping("/setEvaluated/{id}")
    public R setEvaluated(@PathVariable("id") Long id, HttpServletRequest request) {
        String tableName = (String) request.getSession().getAttribute("tableName");
        String username = (String) request.getSession().getAttribute("username");
        
        HuiyuanyuyueEntity existing = huiyuanyuyueService.selectById(id);
        if (existing == null) {
            return R.error("订单不存在");
        }
        
        // 会员只能更新自己的订单
        if ("huiyuan".equals(tableName)) {
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
        huiyuanyuyueService.updateById(existing);
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
            HuiyuanyuyueEntity yuyue = huiyuanyuyueService.selectById(id);
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
        huiyuanyuyueService.deleteBatchIds(Arrays.asList(ids));
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
		
		Wrapper<HuiyuanyuyueEntity> wrapper = new EntityWrapper<HuiyuanyuyueEntity>();
		if(map.get("remindstart")!=null) {
			wrapper.ge(columnName, map.get("remindstart"));
		}
		if(map.get("remindend")!=null) {
			wrapper.le(columnName, map.get("remindend"));
		}

		String tableName = request.getSession().getAttribute("tableName").toString();
		if(tableName.equals("huiyuan")) {
			wrapper.eq("zhanghao", (String)request.getSession().getAttribute("username"));
		}

		int count = huiyuanyuyueService.selectCount(wrapper);
		return R.ok().put("count", count);
	}

    /**
     * 创建会员预约
     */
    @RequestMapping("/create")
    public R create(@RequestBody Map<String, Object> params, HttpServletRequest request) {
        Long userId = (Long) request.getSession().getAttribute("userId");
        String tableName = (String) request.getSession().getAttribute("tableName");

        if (!"huiyuan".equals(tableName)) {
            return R.error("仅会员可以进行此操作");
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

            String yuyuebianhao = bookingService.createHuiyuanBooking(userId, kefanghao, ruzhushijian, tianshu);
            return R.ok("预约成功，会员享受9折优惠，请等待审核").put("yuyuebianhao", yuyuebianhao);
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
            bookingService.auditHuiyuanBooking(id, sfsh, shhf);
            return R.ok("审核完成");
        } catch (Exception e) {
            return R.error("审核失败：" + e.getMessage());
        }
    }

    /**
     * 会员预约支付
     * @param payType 支付方式：wechat-微信支付，balance-余额支付
     */
    @RequestMapping("/pay/{id}")
    public R pay(@PathVariable("id") Long id, @RequestBody(required = false) Map<String, Object> params, HttpServletRequest request) {
        // 验证用户权限
        String tableName = (String) request.getSession().getAttribute("tableName");
        String username = (String) request.getSession().getAttribute("username");
        
        if (!"huiyuan".equals(tableName) && !"users".equals(tableName)) {
            return R.error("无权限操作");
        }
        
        // 验证预约所有权（会员只能支付自己的预约）
        if ("huiyuan".equals(tableName)) {
            HuiyuanyuyueEntity booking = huiyuanyuyueService.selectById(id);
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
            Map<String, Object> result = bookingService.payHuiyuanBooking(id, payType);
            return R.ok().put("data", result);
        } catch (Exception e) {
            return R.error("支付失败：" + e.getMessage());
        }
    }

    /**
     * 取消会员预约
     */
    @RequestMapping("/cancel/{id}")
    public R cancel(@PathVariable("id") Long id, @RequestBody Map<String, Object> params, HttpServletRequest request) {
        // 验证用户权限
        String tableName = (String) request.getSession().getAttribute("tableName");
        String username = (String) request.getSession().getAttribute("username");
        
        if (!"huiyuan".equals(tableName) && !"users".equals(tableName)) {
            return R.error("无权限操作");
        }
        
        // 验证预约所有权
        if ("huiyuan".equals(tableName)) {
            HuiyuanyuyueEntity booking = huiyuanyuyueService.selectById(id);
            if (booking == null) {
                return R.error("预约记录不存在");
            }
            if (!booking.getZhanghao().equals(username)) {
                return R.error("无权操作他人的预约");
            }
        }
        
        String reason = (String) params.get("reason");
        try {
            bookingService.cancelHuiyuanBooking(id, reason);
            return R.ok("取消成功");
        } catch (Exception e) {
            return R.error("取消失败：" + e.getMessage());
        }
    }

    /**
     * 计算会员预约价格（含折扣）
     */
    @RequestMapping("/calcPrice")
    public R calcPrice(@RequestBody Map<String, Object> params) {
        String kefanghao = (String) params.get("kefanghao");
        Object tianshuObj = params.get("tianshu");
        Integer tianshu = tianshuObj != null ? Integer.parseInt(tianshuObj.toString()) : 1;

        try {
            Double originalPrice = bookingService.calculatePrice(kefanghao, tianshu, false);
            Double memberPrice = bookingService.calculatePrice(kefanghao, tianshu, true);
            Double discount = originalPrice - memberPrice;

            Map<String, Object> result = new HashMap<>();
            result.put("originalPrice", originalPrice);
            result.put("memberPrice", memberPrice);
            result.put("discount", discount);

            return R.ok().put("data", result);
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
        
        EntityWrapper<HuiyuanyuyueEntity> ew = new EntityWrapper<HuiyuanyuyueEntity>();
        ew.eq("zhanghao", username);
        ew.orderBy("addtime", false);

        PageUtils page = huiyuanyuyueService.queryPage(params, ew);
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
        
        HuiyuanyuyueEntity booking = huiyuanyuyueService.selectById(id);
        if (booking == null) {
            return R.error("预约记录不存在");
        }
        if (!"申请退款".equals(booking.getYuyuezhuangtai())) {
            return R.error("该订单不是申请退款状态");
        }
        
        booking.setYuyuezhuangtai("已退款");
        booking.setIspay("已退款");
        huiyuanyuyueService.updateById(booking);
        
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
        
        // 查找所有已取消的会员预约
        EntityWrapper<HuiyuanyuyueEntity> ew = new EntityWrapper<>();
        ew.eq("yuyuezhuangtai", "已取消");
        List<HuiyuanyuyueEntity> cancelledBookings = huiyuanyuyueService.selectList(ew);
        
        int fixedCount = 0;
        for (HuiyuanyuyueEntity booking : cancelledBookings) {
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
