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

import com.entity.QingsaofangjianEntity;
import com.entity.view.QingsaofangjianView;
import com.entity.KefangxinxiEntity;

import com.service.QingsaofangjianService;
import com.service.KefangxinxiService;
import com.service.TokenService;
import com.utils.PageUtils;
import com.utils.R;
import com.utils.MD5Util;
import com.utils.MPUtil;
import com.utils.CommonUtil;


/**
 * 清扫房间
 * 后端接口
 * @author 
 * @email 
 * @date 2021-04-30 10:31:55
 */
@RestController
@RequestMapping("/qingsaofangjian")
public class QingsaofangjianController {
    @Autowired
    private QingsaofangjianService qingsaofangjianService;
    
    @Autowired
    private KefangxinxiService kefangxinxiService;
    


    /**
     * 后端列表
     */
    @RequestMapping("/page")
    public R page(@RequestParam Map<String, Object> params,QingsaofangjianEntity qingsaofangjian,
		HttpServletRequest request){
		String tableName = request.getSession().getAttribute("tableName").toString();
		if(tableName.equals("qingjierenyuan")) {
			qingsaofangjian.setQingjiezhanghao((String)request.getSession().getAttribute("username"));
		}
        EntityWrapper<QingsaofangjianEntity> ew = new EntityWrapper<QingsaofangjianEntity>();
		PageUtils page = qingsaofangjianService.queryPage(params, MPUtil.sort(MPUtil.between(MPUtil.likeOrEq(ew, qingsaofangjian), params), params));

        return R.ok().put("data", page);
    }
    
    /**
     * 前端列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params,QingsaofangjianEntity qingsaofangjian, 
		HttpServletRequest request){
        EntityWrapper<QingsaofangjianEntity> ew = new EntityWrapper<QingsaofangjianEntity>();
		PageUtils page = qingsaofangjianService.queryPage(params, MPUtil.sort(MPUtil.between(MPUtil.likeOrEq(ew, qingsaofangjian), params), params));
        return R.ok().put("data", page);
    }

	/**
     * 列表
     */
    @RequestMapping("/lists")
    public R list( QingsaofangjianEntity qingsaofangjian){
       	EntityWrapper<QingsaofangjianEntity> ew = new EntityWrapper<QingsaofangjianEntity>();
      	ew.allEq(MPUtil.allEQMapPre( qingsaofangjian, "qingsaofangjian")); 
        return R.ok().put("data", qingsaofangjianService.selectListView(ew));
    }

	 /**
     * 查询
     */
    @RequestMapping("/query")
    public R query(QingsaofangjianEntity qingsaofangjian){
        EntityWrapper< QingsaofangjianEntity> ew = new EntityWrapper< QingsaofangjianEntity>();
 		ew.allEq(MPUtil.allEQMapPre( qingsaofangjian, "qingsaofangjian")); 
		QingsaofangjianView qingsaofangjianView =  qingsaofangjianService.selectView(ew);
		return R.ok("查询清扫房间成功").put("data", qingsaofangjianView);
    }
	
    /**
     * 后端详情
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
        QingsaofangjianEntity qingsaofangjian = qingsaofangjianService.selectById(id);
        return R.ok().put("data", qingsaofangjian);
    }

    /**
     * 前端详情
     */
    @RequestMapping("/detail/{id}")
    public R detail(@PathVariable("id") Long id){
        QingsaofangjianEntity qingsaofangjian = qingsaofangjianService.selectById(id);
        return R.ok().put("data", qingsaofangjian);
    }
    



    /**
     * 后端保存（管理员和清洁人员可操作）
     */
    @RequestMapping("/save")
    public R save(@RequestBody QingsaofangjianEntity qingsaofangjian, HttpServletRequest request){
        String tableName = (String) request.getSession().getAttribute("tableName");
        if (!"users".equals(tableName) && !"qingjierenyuan".equals(tableName)) {
            return R.error("无权限操作，仅管理员或清洁人员可添加清扫记录");
        }
    	qingsaofangjian.setId(new Date().getTime()+new Double(Math.floor(Math.random()*1000)).longValue());
        qingsaofangjianService.insert(qingsaofangjian);
        
        // 如果选择"是"已打扫，则更新房间清洁状态
        if ("是".equals(qingsaofangjian.getShifoudasao()) && qingsaofangjian.getKefanghao() != null) {
            KefangxinxiEntity kefang = kefangxinxiService.selectOne(
                new EntityWrapper<KefangxinxiEntity>().eq("kefanghao", qingsaofangjian.getKefanghao()));
            if (kefang != null) {
                kefang.setWeishengqingkuang("已清扫");
                // 如果房间当前是"待清扫"状态，更新为"空闲"
                if ("待清扫".equals(kefang.getKefangzhuangtai())) {
                    kefang.setKefangzhuangtai("空闲");
                }
                kefangxinxiService.updateById(kefang);
            }
        }
        return R.ok();
    }
    
    /**
     * 前端保存（禁用，清扫记录只能通过后台添加）
     */
    @RequestMapping("/add")
    public R add(@RequestBody QingsaofangjianEntity qingsaofangjian, HttpServletRequest request){
        return R.error("请通过后台管理系统添加清扫记录");
    }

    /**
     * 修改（仅管理员可操作）
     */
    @RequestMapping("/update")
    public R update(@RequestBody QingsaofangjianEntity qingsaofangjian, HttpServletRequest request){
        String tableName = (String) request.getSession().getAttribute("tableName");
        if (!"users".equals(tableName)) {
            return R.error("无权限操作，仅管理员可修改清扫记录");
        }
        qingsaofangjianService.updateById(qingsaofangjian);
        return R.ok();
    }
    

    /**
     * 删除（仅管理员可操作）
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids, HttpServletRequest request){
        String tableName = (String) request.getSession().getAttribute("tableName");
        if (!"users".equals(tableName)) {
            return R.error("无权限操作，仅管理员可删除清扫记录");
        }
        qingsaofangjianService.deleteBatchIds(Arrays.asList(ids));
        return R.ok();
    }
    
    /**
     * 提醒接口
     */
    /**
     * 获取待清扫房间列表（清洁人员专用）
     */
    @RequestMapping("/pendingClean")
    public R pendingClean(@RequestParam Map<String, Object> params, HttpServletRequest request) {
        EntityWrapper<KefangxinxiEntity> ew = new EntityWrapper<KefangxinxiEntity>();
        // 只查询待清扫状态的房间
        ew.eq("kefangzhuangtai", "待清扫")
          .or()
          .eq("weishengqingkuang", "待清扫");
        ew.orderBy("id", true);
        
        List<KefangxinxiEntity> list = kefangxinxiService.selectList(ew);
        return R.ok().put("data", list).put("total", list.size());
    }

    /**
     * 一键完成清扫（清洁人员专用）
     */
    @RequestMapping("/finishClean")
    public R finishClean(@RequestBody Map<String, Object> params, HttpServletRequest request) {
        String tableName = (String) request.getSession().getAttribute("tableName");
        if (!"qingjierenyuan".equals(tableName) && !"users".equals(tableName)) {
            return R.error("无权限操作");
        }
        
        String kefanghao = (String) params.get("kefanghao");
        if (StringUtils.isBlank(kefanghao)) {
            return R.error("客房号不能为空");
        }
        
        // 查找客房
        KefangxinxiEntity kefang = kefangxinxiService.selectOne(
            new EntityWrapper<KefangxinxiEntity>().eq("kefanghao", kefanghao));
        if (kefang == null) {
            return R.error("客房不存在");
        }
        
        // 更新客房清洁状态
        kefang.setWeishengqingkuang("已清扫");
        if ("待清扫".equals(kefang.getKefangzhuangtai())) {
            kefang.setKefangzhuangtai("空闲");
        }
        kefangxinxiService.updateById(kefang);
        
        // 创建清扫记录
        String username = (String) request.getSession().getAttribute("username");
        QingsaofangjianEntity qingsao = new QingsaofangjianEntity();
        qingsao.setId(new Date().getTime() + new Double(Math.floor(Math.random() * 1000)).longValue());
        qingsao.setAddtime(new Date());
        qingsao.setKefanghao(kefanghao);
        qingsao.setKefangleixing(kefang.getKefangleixing());
        qingsao.setSuoshujiudian(kefang.getSuoshujiudian());
        qingsao.setShifoudasao("是");
        qingsao.setDasaoshijian(new Date());
        qingsao.setQingjiezhanghao(username);
        // 获取清洁人员姓名
        if ("qingjierenyuan".equals(tableName)) {
            qingsao.setQingjiexingming(username);
        }
        qingsaofangjianService.insert(qingsao);
        
        return R.ok("清扫完成");
    }
    
	/**
     * 清洁人员工作统计
     */
    @RequestMapping("/statistics")
    public R statistics(@RequestParam(required = false) String qingjiehao,
                        @RequestParam(required = false) String startDate,
                        @RequestParam(required = false) String endDate,
                        HttpServletRequest request) {
        String tableName = (String) request.getSession().getAttribute("tableName");
        String username = (String) request.getSession().getAttribute("username");
        
        // 如果是清洁人员，只能查看自己的统计
        if ("qingjierenyuan".equals(tableName)) {
            qingjiehao = username;
        }
        
        EntityWrapper<QingsaofangjianEntity> ew = new EntityWrapper<>();
        if (StringUtils.isNotBlank(qingjiehao)) {
            ew.eq("qingjiezhanghao", qingjiehao);
        }
        ew.eq("shifoudasao", "是");
        
        if (StringUtils.isNotBlank(startDate)) {
            ew.ge("dasaoshijian", startDate + " 00:00:00");
        }
        if (StringUtils.isNotBlank(endDate)) {
            ew.le("dasaoshijian", endDate + " 23:59:59");
        }
        
        List<QingsaofangjianEntity> completedTasks = qingsaofangjianService.selectList(ew);
        
        Map<String, Object> result = new HashMap<>();
        result.put("totalTasks", completedTasks.size());
        result.put("tasks", completedTasks);
        
        // 查询清洁人员姓名
        if (StringUtils.isNotBlank(qingjiehao) && !completedTasks.isEmpty()) {
            result.put("cleanerName", completedTasks.get(0).getQingjiexingming());
        }
        
        // 日期范围
        if (StringUtils.isNotBlank(startDate) && StringUtils.isNotBlank(endDate)) {
            result.put("dateRange", startDate + " ~ " + endDate);
        } else if (StringUtils.isNotBlank(startDate)) {
            result.put("dateRange", startDate + " 至今");
        } else if (StringUtils.isNotBlank(endDate)) {
            result.put("dateRange", "截至 " + endDate);
        }
        
        // 按日期统计（转换为列表格式）
        Map<String, Integer> dailyStatsMap = new HashMap<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        for (QingsaofangjianEntity task : completedTasks) {
            if (task.getDasaoshijian() != null) {
                String dateKey = sdf.format(task.getDasaoshijian());
                dailyStatsMap.put(dateKey, dailyStatsMap.getOrDefault(dateKey, 0) + 1);
            }
        }
        
        // 转换为列表格式供前端表格展示
        List<Map<String, Object>> dailyStatsList = new ArrayList<>();
        dailyStatsMap.entrySet().stream()
            .sorted((a, b) -> b.getKey().compareTo(a.getKey()))
            .forEach(entry -> {
                Map<String, Object> item = new HashMap<>();
                item.put("date", entry.getKey());
                item.put("count", entry.getValue());
                dailyStatsList.add(item);
            });
        result.put("dailyStats", dailyStatsList);
        
        return R.ok().put("data", result);
    }
    
    /**
     * 获取所有清洁人员的工作排行
     */
    @RequestMapping("/ranking")
    public R ranking(@RequestParam(required = false) String startDate,
                     @RequestParam(required = false) String endDate,
                     HttpServletRequest request) {
        String tableName = (String) request.getSession().getAttribute("tableName");
        if (!"users".equals(tableName) && !"qiantairenyuan".equals(tableName)) {
            return R.error("无权限查看");
        }
        
        EntityWrapper<QingsaofangjianEntity> ew = new EntityWrapper<>();
        ew.eq("shifoudasao", "是");
        
        if (StringUtils.isNotBlank(startDate)) {
            ew.ge("dasaoshijian", startDate + " 00:00:00");
        }
        if (StringUtils.isNotBlank(endDate)) {
            ew.le("dasaoshijian", endDate + " 23:59:59");
        }
        
        List<QingsaofangjianEntity> completedTasks = qingsaofangjianService.selectList(ew);
        
        // 按清洁人员统计
        Map<String, Map<String, Object>> cleanerStats = new HashMap<>();
        for (QingsaofangjianEntity task : completedTasks) {
            String cleaner = task.getQingjiezhanghao();
            if (StringUtils.isNotBlank(cleaner)) {
                Map<String, Object> stat = cleanerStats.getOrDefault(cleaner, new HashMap<>());
                stat.put("qingjiehao", cleaner);
                stat.put("qingjiexingming", task.getQingjiexingming());
                stat.put("taskCount", ((Integer) stat.getOrDefault("taskCount", 0)) + 1);
                cleanerStats.put(cleaner, stat);
            }
        }
        
        // 转换为列表并排序
        List<Map<String, Object>> ranking = new ArrayList<>(cleanerStats.values());
        ranking.sort((a, b) -> ((Integer) b.get("taskCount")).compareTo((Integer) a.get("taskCount")));
        
        return R.ok().put("data", ranking);
    }

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
		
		Wrapper<QingsaofangjianEntity> wrapper = new EntityWrapper<QingsaofangjianEntity>();
		if(map.get("remindstart")!=null) {
			wrapper.ge(columnName, map.get("remindstart"));
		}
		if(map.get("remindend")!=null) {
			wrapper.le(columnName, map.get("remindend"));
		}

		String tableName = request.getSession().getAttribute("tableName").toString();
		if(tableName.equals("qingjierenyuan")) {
			wrapper.eq("qingjiezhanghao", (String)request.getSession().getAttribute("username"));
		}

		int count = qingsaofangjianService.selectCount(wrapper);
		return R.ok().put("count", count);
	}
	


}
