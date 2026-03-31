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

import com.entity.YonghutuifangEntity;
import com.entity.YonghuruzhuEntity;
import com.entity.KefangxinxiEntity;
import com.entity.view.YonghutuifangView;

import com.service.YonghutuifangService;
import com.service.YonghuruzhuService;
import com.service.KefangxinxiService;
import com.service.TokenService;
import com.utils.PageUtils;
import com.utils.R;
import com.utils.MD5Util;
import com.utils.MPUtil;
import com.utils.CommonUtil;


/**
 * 用户退房
 * 后端接口
 * @author 
 * @email 
 * @date 2021-04-30 10:31:55
 */
@RestController
@RequestMapping("/yonghutuifang")
public class YonghutuifangController {
    @Autowired
    private YonghutuifangService yonghutuifangService;
    
    @Autowired
    private YonghuruzhuService yonghuruzhuService;
    
    @Autowired
    private KefangxinxiService kefangxinxiService;
    


    /**
     * 后端列表
     */
    @RequestMapping("/page")
    public R page(@RequestParam Map<String, Object> params,YonghutuifangEntity yonghutuifang,
		HttpServletRequest request){
		String tableName = request.getSession().getAttribute("tableName").toString();
		if(tableName.equals("yonghu")) {
			yonghutuifang.setZhanghao((String)request.getSession().getAttribute("username"));
		}
        EntityWrapper<YonghutuifangEntity> ew = new EntityWrapper<YonghutuifangEntity>();
		PageUtils page = yonghutuifangService.queryPage(params, MPUtil.sort(MPUtil.between(MPUtil.likeOrEq(ew, yonghutuifang), params), params));

        return R.ok().put("data", page);
    }
    
    /**
     * 前端列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params,YonghutuifangEntity yonghutuifang, 
		HttpServletRequest request){
        EntityWrapper<YonghutuifangEntity> ew = new EntityWrapper<YonghutuifangEntity>();
		PageUtils page = yonghutuifangService.queryPage(params, MPUtil.sort(MPUtil.between(MPUtil.likeOrEq(ew, yonghutuifang), params), params));
        return R.ok().put("data", page);
    }

	/**
     * 列表
     */
    @RequestMapping("/lists")
    public R list( YonghutuifangEntity yonghutuifang){
       	EntityWrapper<YonghutuifangEntity> ew = new EntityWrapper<YonghutuifangEntity>();
      	ew.allEq(MPUtil.allEQMapPre( yonghutuifang, "yonghutuifang")); 
        return R.ok().put("data", yonghutuifangService.selectListView(ew));
    }

	 /**
     * 查询
     */
    @RequestMapping("/query")
    public R query(YonghutuifangEntity yonghutuifang){
        EntityWrapper< YonghutuifangEntity> ew = new EntityWrapper< YonghutuifangEntity>();
 		ew.allEq(MPUtil.allEQMapPre( yonghutuifang, "yonghutuifang")); 
		YonghutuifangView yonghutuifangView =  yonghutuifangService.selectView(ew);
		return R.ok("查询用户退房成功").put("data", yonghutuifangView);
    }
	
    /**
     * 后端详情
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
        YonghutuifangEntity yonghutuifang = yonghutuifangService.selectById(id);
        return R.ok().put("data", yonghutuifang);
    }

    /**
     * 前端详情
     */
    @RequestMapping("/detail/{id}")
    public R detail(@PathVariable("id") Long id){
        YonghutuifangEntity yonghutuifang = yonghutuifangService.selectById(id);
        return R.ok().put("data", yonghutuifang);
    }
    



    /**
     * 后端保存（退房操作 - 仅管理员/前台可操作）
     */
    @RequestMapping("/save")
    public R save(@RequestBody YonghutuifangEntity yonghutuifang, HttpServletRequest request){
        String tableName = (String) request.getSession().getAttribute("tableName");
        if (!"users".equals(tableName) && !"qiantairenyuan".equals(tableName)) {
            return R.error("无权限操作，仅管理员或前台人员可办理退房");
        }
        
    	yonghutuifang.setId(new Date().getTime()+new Double(Math.floor(Math.random()*1000)).longValue());
    	yonghutuifang.setAddtime(new Date());
    	if (yonghutuifang.getTuifangshijian() == null) {
    	    yonghutuifang.setTuifangshijian(new Date());
    	}
    	if (StringUtils.isBlank(yonghutuifang.getIspay())) {
    	    yonghutuifang.setIspay("未支付");
    	}
        yonghutuifangService.insert(yonghutuifang);
        
        // 退房后更新客房状态为"待清扫"
        if (StringUtils.isNotBlank(yonghutuifang.getKefanghao())) {
            KefangxinxiEntity kefang = kefangxinxiService.selectOne(
                new EntityWrapper<KefangxinxiEntity>().eq("kefanghao", yonghutuifang.getKefanghao()));
            if (kefang != null) {
                kefang.setKefangzhuangtai("待清扫");
                kefang.setWeishengqingkuang("待清扫");
                kefangxinxiService.updateById(kefang);
            }
        }
        
        // 更新对应的入住记录状态为"已退房"（保留历史记录）
        if (StringUtils.isNotBlank(yonghutuifang.getDingdanbianhao())) {
            YonghuruzhuEntity ruzhu = yonghuruzhuService.selectOne(
                new EntityWrapper<YonghuruzhuEntity>().eq("dingdanbianhao", yonghutuifang.getDingdanbianhao()));
            if (ruzhu != null) {
                ruzhu.setKefangzhuangtai("已退房");
                yonghuruzhuService.updateById(ruzhu);
            }
        }
        
        return R.ok("退房成功");
    }
    
    /**
     * 前端保存（禁用 - 退房只能通过后台办理）
     */
    @RequestMapping("/add")
    public R add(@RequestBody YonghutuifangEntity yonghutuifang, HttpServletRequest request){
        return R.error("退房办理请联系前台工作人员");
    }

    /**
     * 修改（仅管理员/前台可操作）
     */
    @RequestMapping("/update")
    public R update(@RequestBody YonghutuifangEntity yonghutuifang, HttpServletRequest request){
        String tableName = (String) request.getSession().getAttribute("tableName");
        if (!"users".equals(tableName) && !"qiantairenyuan".equals(tableName)) {
            return R.error("无权限操作");
        }
        yonghutuifangService.updateById(yonghutuifang);
        return R.ok();
    }
    
    /**
     * 支付退房费用（用户本人或管理员/前台可操作）
     */
    @RequestMapping("/pay/{id}")
    public R pay(@PathVariable("id") Long id, HttpServletRequest request){
        String tableName = (String) request.getSession().getAttribute("tableName");
        String username = (String) request.getSession().getAttribute("username");
        
        YonghutuifangEntity tuifang = yonghutuifangService.selectById(id);
        if (tuifang == null) {
            return R.error("退房记录不存在");
        }
        
        // 权限验证：用户只能支付自己的退房费用
        if ("yonghu".equals(tableName)) {
            if (!username.equals(tuifang.getZhanghao())) {
                return R.error("无权操作他人的退房记录");
            }
        } else if (!"users".equals(tableName) && !"qiantairenyuan".equals(tableName)) {
            return R.error("无权限操作");
        }
        
        if ("已支付".equals(tuifang.getIspay())) {
            return R.error("该退房记录已支付");
        }
        tuifang.setIspay("已支付");
        yonghutuifangService.updateById(tuifang);
        return R.ok("支付成功");
    }
    

    /**
     * 删除（仅管理员可操作）
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids, HttpServletRequest request){
        String tableName = (String) request.getSession().getAttribute("tableName");
        if (!"users".equals(tableName)) {
            return R.error("无权限操作，仅管理员可删除退房记录");
        }
        yonghutuifangService.deleteBatchIds(Arrays.asList(ids));
        return R.ok();
    }
    
    /**
     * 基于入住记录办理退房（仅管理员/前台可操作）
     */
    @RequestMapping("/checkOut/{ruzhuId}")
    public R checkOut(@PathVariable("ruzhuId") Long ruzhuId, 
                      @RequestBody(required = false) Map<String, Object> params,
                      HttpServletRequest request){
        String tableName = (String) request.getSession().getAttribute("tableName");
        if (!"users".equals(tableName) && !"qiantairenyuan".equals(tableName)) {
            return R.error("无权限操作，仅管理员或前台人员可办理退房");
        }
        
        // 获取入住记录
        YonghuruzhuEntity ruzhu = yonghuruzhuService.selectById(ruzhuId);
        if (ruzhu == null) {
            return R.error("入住记录不存在");
        }
        
        // 计算费用
        Map<String, Object> feeInfo = calculateFee(ruzhu);
        Double totalFee = (Double) feeInfo.get("overTimeFee");
        
        // 额外费用
        Double extraFee = 0.0;
        String remark = "";
        if (params != null) {
            if (params.get("extraFee") != null) {
                extraFee = Double.parseDouble(params.get("extraFee").toString());
            }
            remark = (String) params.get("remark");
        }
        totalFee += extraFee;
        
        // 创建退房记录
        YonghutuifangEntity tuifang = new YonghutuifangEntity();
        tuifang.setId(new Date().getTime() + new Double(Math.floor(Math.random() * 1000)).longValue());
        tuifang.setAddtime(new Date());
        tuifang.setDingdanbianhao(ruzhu.getDingdanbianhao());
        tuifang.setKefanghao(ruzhu.getKefanghao());
        tuifang.setKefangleixing(ruzhu.getKefangleixing());
        tuifang.setSuoshujiudian(ruzhu.getSuoshujiudian());
        tuifang.setZhanghao(ruzhu.getZhanghao());
        tuifang.setXingming(ruzhu.getXingming());
        tuifang.setShouji(ruzhu.getShouji());
        tuifang.setShenfenzheng(ruzhu.getShenfenzheng());
        tuifang.setTuifangshijian(new Date());
        tuifang.setJiage(totalFee);
        tuifang.setIspay(totalFee > 0 ? "未支付" : "已支付");
        
        yonghutuifangService.insert(tuifang);
        
        // 更新客房状态为"待清扫"
        KefangxinxiEntity kefang = kefangxinxiService.selectOne(
            new EntityWrapper<KefangxinxiEntity>().eq("kefanghao", ruzhu.getKefanghao()));
        if (kefang != null) {
            kefang.setKefangzhuangtai("待清扫");
            kefang.setWeishengqingkuang("待清扫");
            kefangxinxiService.updateById(kefang);
        }
        
        // 更新入住记录状态为"已退房"（保留历史记录）
        ruzhu.setKefangzhuangtai("已退房");
        yonghuruzhuService.updateById(ruzhu);
        
        Map<String, Object> result = new HashMap<>();
        result.put("tuifangId", tuifang.getId());
        result.put("overTimeFee", feeInfo.get("overTimeFee"));
        result.put("extraFee", extraFee);
        result.put("totalFee", totalFee);
        
        return R.ok("退房办理成功").put("data", result);
    }
    
    /**
     * 计算退房费用
     */
    @RequestMapping("/calcFee/{ruzhuId}")
    public R calcFee(@PathVariable("ruzhuId") Long ruzhuId){
        YonghuruzhuEntity ruzhu = yonghuruzhuService.selectById(ruzhuId);
        if (ruzhu == null) {
            return R.error("入住记录不存在");
        }
        
        Map<String, Object> feeInfo = calculateFee(ruzhu);
        return R.ok().put("data", feeInfo);
    }
    
    /**
     * 计算费用逻辑
     */
    private Map<String, Object> calculateFee(YonghuruzhuEntity ruzhu) {
        Map<String, Object> result = new HashMap<>();
        
        Double baseFee = ruzhu.getJiage() != null ? ruzhu.getJiage() : 0.0;
        Double overTimeFee = 0.0;
        long overTimeHours = 0;
        
        // 计算是否超时（超过预计退房时间）
        if (ruzhu.getYulifangshijian() != null) {
            Date now = new Date();
            if (now.after(ruzhu.getYulifangshijian())) {
                overTimeHours = (now.getTime() - ruzhu.getYulifangshijian().getTime()) / (1000 * 60 * 60);
                // 获取房间单价
                KefangxinxiEntity room = kefangxinxiService.selectOne(
                    new EntityWrapper<KefangxinxiEntity>().eq("kefanghao", ruzhu.getKefanghao()));
                if (room != null && overTimeHours > 0) {
                    // 超时费用：每小时收取日租金的1/12（约8%）
                    double hourlyRate = room.getJiage() / 12;
                    overTimeFee = hourlyRate * overTimeHours;
                    overTimeFee = Math.round(overTimeFee * 100) / 100.0;
                }
            }
        }
        
        result.put("baseFee", baseFee);
        result.put("overTimeFee", overTimeFee);
        result.put("overTimeHours", overTimeHours);
        result.put("totalFee", overTimeFee);
        result.put("paidFee", baseFee);
        result.put("unpaidFee", overTimeFee);
        result.put("ruzhushijian", ruzhu.getRuzhushijian());
        result.put("yulifangshijian", ruzhu.getYulifangshijian());
        
        return result;
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
		
		Wrapper<YonghutuifangEntity> wrapper = new EntityWrapper<YonghutuifangEntity>();
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

		int count = yonghutuifangService.selectCount(wrapper);
		return R.ok().put("count", count);
	}
	


}
