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

import com.entity.HuiyuantuifangEntity;
import com.entity.HuiyuanruzhuEntity;
import com.entity.KefangxinxiEntity;
import com.entity.view.HuiyuantuifangView;

import com.service.HuiyuantuifangService;
import com.service.HuiyuanruzhuService;
import com.service.KefangxinxiService;
import com.service.TokenService;
import com.utils.PageUtils;
import com.utils.R;
import com.utils.MD5Util;
import com.utils.MPUtil;
import com.utils.CommonUtil;


/**
 * 会员退房
 * 后端接口
 * @author 
 * @email 
 * @date 2021-04-30 10:31:55
 */
@RestController
@RequestMapping("/huiyuantuifang")
public class HuiyuantuifangController {
    @Autowired
    private HuiyuantuifangService huiyuantuifangService;
    
    @Autowired
    private HuiyuanruzhuService huiyuanruzhuService;
    
    @Autowired
    private KefangxinxiService kefangxinxiService;
    


    /**
     * 后端列表
     */
    @RequestMapping("/page")
    public R page(@RequestParam Map<String, Object> params,HuiyuantuifangEntity huiyuantuifang,
		HttpServletRequest request){
		String tableName = request.getSession().getAttribute("tableName").toString();
		if(tableName.equals("huiyuan")) {
			huiyuantuifang.setZhanghao((String)request.getSession().getAttribute("username"));
		}
        EntityWrapper<HuiyuantuifangEntity> ew = new EntityWrapper<HuiyuantuifangEntity>();
		PageUtils page = huiyuantuifangService.queryPage(params, MPUtil.sort(MPUtil.between(MPUtil.likeOrEq(ew, huiyuantuifang), params), params));

        return R.ok().put("data", page);
    }
    
    /**
     * 前端列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params,HuiyuantuifangEntity huiyuantuifang, 
		HttpServletRequest request){
        EntityWrapper<HuiyuantuifangEntity> ew = new EntityWrapper<HuiyuantuifangEntity>();
		PageUtils page = huiyuantuifangService.queryPage(params, MPUtil.sort(MPUtil.between(MPUtil.likeOrEq(ew, huiyuantuifang), params), params));
        return R.ok().put("data", page);
    }

	/**
     * 列表
     */
    @RequestMapping("/lists")
    public R list( HuiyuantuifangEntity huiyuantuifang){
       	EntityWrapper<HuiyuantuifangEntity> ew = new EntityWrapper<HuiyuantuifangEntity>();
      	ew.allEq(MPUtil.allEQMapPre( huiyuantuifang, "huiyuantuifang")); 
        return R.ok().put("data", huiyuantuifangService.selectListView(ew));
    }

	 /**
     * 查询
     */
    @RequestMapping("/query")
    public R query(HuiyuantuifangEntity huiyuantuifang){
        EntityWrapper< HuiyuantuifangEntity> ew = new EntityWrapper< HuiyuantuifangEntity>();
 		ew.allEq(MPUtil.allEQMapPre( huiyuantuifang, "huiyuantuifang")); 
		HuiyuantuifangView huiyuantuifangView =  huiyuantuifangService.selectView(ew);
		return R.ok("查询会员退房成功").put("data", huiyuantuifangView);
    }
	
    /**
     * 后端详情
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
        HuiyuantuifangEntity huiyuantuifang = huiyuantuifangService.selectById(id);
        return R.ok().put("data", huiyuantuifang);
    }

    /**
     * 前端详情
     */
    @RequestMapping("/detail/{id}")
    public R detail(@PathVariable("id") Long id){
        HuiyuantuifangEntity huiyuantuifang = huiyuantuifangService.selectById(id);
        return R.ok().put("data", huiyuantuifang);
    }
    



    /**
     * 后端保存（会员退房操作 - 仅管理员/前台可操作）
     */
    @RequestMapping("/save")
    public R save(@RequestBody HuiyuantuifangEntity huiyuantuifang, HttpServletRequest request){
        String tableName = (String) request.getSession().getAttribute("tableName");
        if (!"users".equals(tableName) && !"qiantairenyuan".equals(tableName)) {
            return R.error("无权限操作，仅管理员或前台人员可办理退房");
        }
        
    	huiyuantuifang.setId(new Date().getTime()+new Double(Math.floor(Math.random()*1000)).longValue());
    	huiyuantuifang.setAddtime(new Date());
    	if (huiyuantuifang.getTuifangshijian() == null) {
    	    huiyuantuifang.setTuifangshijian(new Date());
    	}
    	if (StringUtils.isBlank(huiyuantuifang.getIspay())) {
    	    huiyuantuifang.setIspay("未支付");
    	}
        huiyuantuifangService.insert(huiyuantuifang);
        
        // 退房后更新客房状态为"待清扫"
        if (StringUtils.isNotBlank(huiyuantuifang.getKefanghao())) {
            KefangxinxiEntity kefang = kefangxinxiService.selectOne(
                new EntityWrapper<KefangxinxiEntity>().eq("kefanghao", huiyuantuifang.getKefanghao()));
            if (kefang != null) {
                kefang.setKefangzhuangtai("待清扫");
                kefang.setWeishengqingkuang("待清扫");
                kefangxinxiService.updateById(kefang);
            }
        }
        
        // 更新对应的入住记录状态为"已退房"（保留历史记录）
        if (StringUtils.isNotBlank(huiyuantuifang.getDingdanbianhao())) {
            HuiyuanruzhuEntity ruzhu = huiyuanruzhuService.selectOne(
                new EntityWrapper<HuiyuanruzhuEntity>().eq("dingdanbianhao", huiyuantuifang.getDingdanbianhao()));
            if (ruzhu != null) {
                ruzhu.setKefangzhuangtai("已退房");
                huiyuanruzhuService.updateById(ruzhu);
            }
        }
        
        return R.ok("退房成功");
    }
    
    /**
     * 前端保存（禁用 - 退房只能通过后台办理）
     */
    @RequestMapping("/add")
    public R add(@RequestBody HuiyuantuifangEntity huiyuantuifang, HttpServletRequest request){
        return R.error("退房办理请联系前台工作人员");
    }

    /**
     * 修改（仅管理员/前台可操作）
     */
    @RequestMapping("/update")
    public R update(@RequestBody HuiyuantuifangEntity huiyuantuifang, HttpServletRequest request){
        String tableName = (String) request.getSession().getAttribute("tableName");
        if (!"users".equals(tableName) && !"qiantairenyuan".equals(tableName)) {
            return R.error("无权限操作");
        }
        huiyuantuifangService.updateById(huiyuantuifang);
        return R.ok();
    }
    
    /**
     * 支付退房费用（会员本人或管理员/前台可操作）
     */
    @RequestMapping("/pay/{id}")
    public R pay(@PathVariable("id") Long id, HttpServletRequest request){
        String tableName = (String) request.getSession().getAttribute("tableName");
        String username = (String) request.getSession().getAttribute("username");
        
        HuiyuantuifangEntity tuifang = huiyuantuifangService.selectById(id);
        if (tuifang == null) {
            return R.error("退房记录不存在");
        }
        
        // 权限验证：会员只能支付自己的退房费用
        if ("huiyuan".equals(tableName)) {
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
        huiyuantuifangService.updateById(tuifang);
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
        huiyuantuifangService.deleteBatchIds(Arrays.asList(ids));
        return R.ok();
    }
    
    /**
     * 基于入住记录办理退房（仅管理员/前台可操作）- 会员超时费用享受9折
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
        HuiyuanruzhuEntity ruzhu = huiyuanruzhuService.selectById(ruzhuId);
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
        HuiyuantuifangEntity tuifang = new HuiyuantuifangEntity();
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
        
        huiyuantuifangService.insert(tuifang);
        
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
        huiyuanruzhuService.updateById(ruzhu);
        
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
        HuiyuanruzhuEntity ruzhu = huiyuanruzhuService.selectById(ruzhuId);
        if (ruzhu == null) {
            return R.error("入住记录不存在");
        }
        
        Map<String, Object> feeInfo = calculateFee(ruzhu);
        return R.ok().put("data", feeInfo);
    }
    
    /**
     * 计算费用逻辑（会员超时费用9折）
     */
    private Map<String, Object> calculateFee(HuiyuanruzhuEntity ruzhu) {
        Map<String, Object> result = new HashMap<>();
        
        Double baseFee = ruzhu.getJiage() != null ? ruzhu.getJiage() : 0.0;
        Double overTimeFee = 0.0;
        long overTimeHours = 0;
        
        // 计算是否超时
        if (ruzhu.getYulifangshijian() != null) {
            Date now = new Date();
            if (now.after(ruzhu.getYulifangshijian())) {
                overTimeHours = (now.getTime() - ruzhu.getYulifangshijian().getTime()) / (1000 * 60 * 60);
                KefangxinxiEntity room = kefangxinxiService.selectOne(
                    new EntityWrapper<KefangxinxiEntity>().eq("kefanghao", ruzhu.getKefanghao()));
                if (room != null && overTimeHours > 0) {
                    // 会员超时费用享受9折
                    double hourlyRate = room.getJiage() / 12 * 0.9;
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
        result.put("discount", "会员超时费用享受9折");
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
		
		Wrapper<HuiyuantuifangEntity> wrapper = new EntityWrapper<HuiyuantuifangEntity>();
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

		int count = huiyuantuifangService.selectCount(wrapper);
		return R.ok().put("count", count);
	}
	


}
