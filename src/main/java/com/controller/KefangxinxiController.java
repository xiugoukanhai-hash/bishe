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

import com.entity.KefangxinxiEntity;
import com.entity.view.KefangxinxiView;

import com.service.KefangxinxiService;
import com.service.RoomStatusService;
import com.service.TokenService;
import com.utils.PageUtils;
import com.utils.R;
import com.utils.MD5Util;
import com.utils.MPUtil;
import com.utils.CommonUtil;


/**
 * 客房信息
 * 后端接口
 * @author 
 * @email 
 * @date 2021-04-30 10:31:54
 */
@RestController
@RequestMapping("/kefangxinxi")
public class KefangxinxiController {
    @Autowired
    private KefangxinxiService kefangxinxiService;
    
    @Autowired
    private RoomStatusService roomStatusService;
    


    /**
     * 后端列表
     */
    @RequestMapping("/page")
    public R page(@RequestParam Map<String, Object> params,KefangxinxiEntity kefangxinxi,
		HttpServletRequest request){
        EntityWrapper<KefangxinxiEntity> ew = new EntityWrapper<KefangxinxiEntity>();
		PageUtils page = kefangxinxiService.queryPage(params, MPUtil.sort(MPUtil.between(MPUtil.likeOrEq(ew, kefangxinxi), params), params));

        return R.ok().put("data", page);
    }
    
    /**
     * 前端列表
     */
	@IgnoreAuth
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params,KefangxinxiEntity kefangxinxi, 
		HttpServletRequest request){
        EntityWrapper<KefangxinxiEntity> ew = new EntityWrapper<KefangxinxiEntity>();
		PageUtils page = kefangxinxiService.queryPage(params, MPUtil.sort(MPUtil.between(MPUtil.likeOrEq(ew, kefangxinxi), params), params));
        return R.ok().put("data", page);
    }

	/**
     * 列表
     */
    @RequestMapping("/lists")
    public R list( KefangxinxiEntity kefangxinxi){
       	EntityWrapper<KefangxinxiEntity> ew = new EntityWrapper<KefangxinxiEntity>();
      	ew.allEq(MPUtil.allEQMapPre( kefangxinxi, "kefangxinxi")); 
        return R.ok().put("data", kefangxinxiService.selectListView(ew));
    }

	 /**
     * 查询
     */
    @RequestMapping("/query")
    public R query(KefangxinxiEntity kefangxinxi){
        EntityWrapper< KefangxinxiEntity> ew = new EntityWrapper< KefangxinxiEntity>();
 		ew.allEq(MPUtil.allEQMapPre( kefangxinxi, "kefangxinxi")); 
		KefangxinxiView kefangxinxiView =  kefangxinxiService.selectView(ew);
		return R.ok("查询客房信息成功").put("data", kefangxinxiView);
    }
	
    /**
     * 后端详情
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
        KefangxinxiEntity kefangxinxi = kefangxinxiService.selectById(id);
		kefangxinxi.setClicknum(kefangxinxi.getClicknum()+1);
		kefangxinxi.setClicktime(new Date());
		kefangxinxiService.updateById(kefangxinxi);
        return R.ok().put("data", kefangxinxi);
    }

    /**
     * 前端详情
     */
	@IgnoreAuth
    @RequestMapping("/detail/{id}")
    public R detail(@PathVariable("id") Long id){
        KefangxinxiEntity kefangxinxi = kefangxinxiService.selectById(id);
		kefangxinxi.setClicknum(kefangxinxi.getClicknum()+1);
		kefangxinxi.setClicktime(new Date());
		kefangxinxiService.updateById(kefangxinxi);
        return R.ok().put("data", kefangxinxi);
    }
    



    /**
     * 后端保存（仅管理员可添加客房）
     */
    @RequestMapping("/save")
    public R save(@RequestBody KefangxinxiEntity kefangxinxi, HttpServletRequest request){
        String tableName = (String) request.getSession().getAttribute("tableName");
        if (!"users".equals(tableName)) {
            return R.error("无权限操作，仅管理员可添加客房");
        }
    	kefangxinxi.setId(new Date().getTime()+new Double(Math.floor(Math.random()*1000)).longValue());
    	kefangxinxi.setAddtime(new Date());
    	if (kefangxinxi.getClicknum() == null) {
    	    kefangxinxi.setClicknum(0);
    	}
        kefangxinxiService.insert(kefangxinxi);
        return R.ok();
    }
    
    /**
     * 前端保存（禁用，客房只能通过后台添加）
     */
    @RequestMapping("/add")
    public R add(@RequestBody KefangxinxiEntity kefangxinxi, HttpServletRequest request){
        return R.error("客房信息只能由管理员在后台添加");
    }

    /**
     * 修改（管理员和前台人员可修改）
     */
    @RequestMapping("/update")
    public R update(@RequestBody KefangxinxiEntity kefangxinxi, HttpServletRequest request){
        String tableName = (String) request.getSession().getAttribute("tableName");
        if (!"users".equals(tableName) && !"qiantairenyuan".equals(tableName)) {
            return R.error("无权限操作");
        }
        kefangxinxiService.updateById(kefangxinxi);
        return R.ok();
    }
    

    /**
     * 删除（仅管理员可删除）
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids, HttpServletRequest request){
        String tableName = (String) request.getSession().getAttribute("tableName");
        if (!"users".equals(tableName)) {
            return R.error("无权限操作，仅管理员可删除客房");
        }
        kefangxinxiService.deleteBatchIds(Arrays.asList(ids));
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
		
		Wrapper<KefangxinxiEntity> wrapper = new EntityWrapper<KefangxinxiEntity>();
		if(map.get("remindstart")!=null) {
			wrapper.ge(columnName, map.get("remindstart"));
		}
		if(map.get("remindend")!=null) {
			wrapper.le(columnName, map.get("remindend"));
		}


		int count = kefangxinxiService.selectCount(wrapper);
		return R.ok().put("count", count);
	}
	
	/**
     * 前端智能排序
     */
	@IgnoreAuth
    @RequestMapping("/autoSort")
    public R autoSort(@RequestParam Map<String, Object> params,KefangxinxiEntity kefangxinxi, HttpServletRequest request,String pre){
        EntityWrapper<KefangxinxiEntity> ew = new EntityWrapper<KefangxinxiEntity>();
        Map<String, Object> newMap = new HashMap<String, Object>();
        Map<String, Object> param = new HashMap<String, Object>();
		Iterator<Map.Entry<String, Object>> it = param.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, Object> entry = it.next();
			String key = entry.getKey();
			String newKey = entry.getKey();
			if (pre.endsWith(".")) {
				newMap.put(pre + newKey, entry.getValue());
			} else if (StringUtils.isEmpty(pre)) {
				newMap.put(newKey, entry.getValue());
			} else {
				newMap.put(pre + "." + newKey, entry.getValue());
			}
		}
		params.put("sort", "clicknum");
        params.put("order", "desc");
		PageUtils page = kefangxinxiService.queryPage(params, MPUtil.sort(MPUtil.between(MPUtil.likeOrEq(ew, kefangxinxi), params), params));
        return R.ok().put("data", page);
    }

    /**
     * 多条件筛选客房
     */
    @IgnoreAuth
    @RequestMapping("/search")
    public R search(@RequestParam Map<String, Object> params) {
        EntityWrapper<KefangxinxiEntity> ew = new EntityWrapper<KefangxinxiEntity>();

        String kefangleixing = (String) params.get("kefangleixing");
        if (StringUtils.isNotBlank(kefangleixing)) {
            ew.eq("kefangleixing", kefangleixing);
        }

        String minPrice = (String) params.get("minPrice");
        String maxPrice = (String) params.get("maxPrice");
        if (StringUtils.isNotBlank(minPrice)) {
            ew.ge("jiage", Double.parseDouble(minPrice));
        }
        if (StringUtils.isNotBlank(maxPrice)) {
            ew.le("jiage", Double.parseDouble(maxPrice));
        }

        String louceng = (String) params.get("louceng");
        if (StringUtils.isNotBlank(louceng)) {
            ew.eq("louceng", louceng);
        }

        String showAll = (String) params.get("showAll");
        if (!"true".equals(showAll)) {
            ew.in("kefangzhuangtai", new String[]{"空闲", "未入住"});
        }

        String orderBy = (String) params.get("orderBy");
        if ("price_asc".equals(orderBy)) {
            ew.orderBy("jiage", true);
        } else if ("price_desc".equals(orderBy)) {
            ew.orderBy("jiage", false);
        } else if ("click".equals(orderBy)) {
            ew.orderBy("clicknum", false);
        } else {
            ew.orderBy("id", false);
        }

        List<KefangxinxiEntity> list = kefangxinxiService.selectList(ew);
        return R.ok().put("data", list);
    }

    /**
     * 获取所有房型列表（用于筛选下拉框）
     */
    @IgnoreAuth
    @RequestMapping("/roomTypes")
    public R roomTypes() {
        EntityWrapper<KefangxinxiEntity> ew = new EntityWrapper<KefangxinxiEntity>();
        ew.setSqlSelect("DISTINCT kefangleixing");
        List<KefangxinxiEntity> list = kefangxinxiService.selectList(ew);
        List<String> types = new ArrayList<>();
        for (KefangxinxiEntity entity : list) {
            if (StringUtils.isNotBlank(entity.getKefangleixing())) {
                types.add(entity.getKefangleixing());
            }
        }
        return R.ok().put("data", types);
    }

    /**
     * 根据客房号获取详情
     */
    @RequestMapping("/info/kefanghao/{kefanghao}")
    public R infoByKefanghao(@PathVariable("kefanghao") String kefanghao) {
        KefangxinxiEntity kefangxinxi = roomStatusService.getByKefanghao(kefanghao);
        if (kefangxinxi == null) {
            return R.error("客房不存在");
        }
        return R.ok().put("data", kefangxinxi);
    }

    /**
     * 检查客房是否可预订
     */
    @RequestMapping("/canBook/{kefanghao}")
    public R canBook(@PathVariable("kefanghao") String kefanghao) {
        boolean canBook = roomStatusService.canBook(kefanghao);
        return R.ok().put("canBook", canBook);
    }

    /**
     * 修改客房状态（管理员和前台人员可操作）
     */
    @RequestMapping("/updateStatus")
    public R updateStatus(@RequestBody Map<String, Object> params, HttpServletRequest request) {
        String tableName = (String) request.getSession().getAttribute("tableName");
        if (!"users".equals(tableName) && !"qiantairenyuan".equals(tableName)) {
            return R.error("无权限操作");
        }

        String kefanghao = (String) params.get("kefanghao");
        String status = (String) params.get("status");

        if (StringUtils.isBlank(kefanghao) || StringUtils.isBlank(status)) {
            return R.error("参数不完整");
        }

        switch (status) {
            case "空闲":
            case "未入住":
                roomStatusService.setFree(kefanghao);
                break;
            case "已预订":
            case "已预约":
                roomStatusService.setBooked(kefanghao);
                break;
            case "已入住":
                roomStatusService.setOccupied(kefanghao);
                break;
            case "待清扫":
                roomStatusService.setCleaning(kefanghao);
                break;
            case "维修中":
                roomStatusService.setMaintenance(kefanghao);
                break;
            default:
                return R.error("无效的状态值");
        }

        return R.ok("状态更新成功");
    }

    /**
     * 修改卫生状态（管理员、前台、清洁人员可操作）
     */
    @RequestMapping("/updateCleanStatus")
    public R updateCleanStatus(@RequestBody Map<String, Object> params, HttpServletRequest request) {
        String tableName = (String) request.getSession().getAttribute("tableName");
        if (!"users".equals(tableName) && !"qiantairenyuan".equals(tableName) && !"qingjierenyuan".equals(tableName)) {
            return R.error("无权限操作");
        }

        String kefanghao = (String) params.get("kefanghao");
        String weishengqingkuang = (String) params.get("weishengqingkuang");

        if (StringUtils.isBlank(kefanghao) || StringUtils.isBlank(weishengqingkuang)) {
            return R.error("参数不完整");
        }

        roomStatusService.setCleanStatus(kefanghao, weishengqingkuang);
        return R.ok("卫生状态更新成功");
    }

    /**
     * 获取客房状态统计
     */
    @RequestMapping("/statusStatistics")
    public R statusStatistics() {
        Map<String, Integer> statistics = new HashMap<>();

        String[] statuses = {"空闲", "未入住", "已预订", "已预约", "已入住", "待清扫", "维修中"};
        for (String status : statuses) {
            EntityWrapper<KefangxinxiEntity> ew = new EntityWrapper<>();
            ew.eq("kefangzhuangtai", status);
            int count = kefangxinxiService.selectCount(ew);
            statistics.put(status, count);
        }

        int total = kefangxinxiService.selectCount(new EntityWrapper<>());
        statistics.put("total", total);

        return R.ok().put("data", statistics);
    }

}
