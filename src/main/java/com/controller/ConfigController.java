
package com.controller;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.annotation.IgnoreAuth;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.entity.ConfigEntity;
import com.service.ConfigService;
import com.utils.PageUtils;
import com.utils.R;
import com.utils.ValidatorUtils;

/**
 * 登录相关
 */
@RequestMapping("config")
@RestController
public class ConfigController{
	
	@Autowired
	private ConfigService configService;

	/**
     * 列表
     */
    @RequestMapping("/page")
    public R page(@RequestParam Map<String, Object> params,ConfigEntity config){
        EntityWrapper<ConfigEntity> ew = new EntityWrapper<ConfigEntity>();
    	PageUtils page = configService.queryPage(params);
        return R.ok().put("data", page);
    }
    
	/**
     * 列表
     */
    @IgnoreAuth
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params,ConfigEntity config){
        EntityWrapper<ConfigEntity> ew = new EntityWrapper<ConfigEntity>();
    	PageUtils page = configService.queryPage(params);
        return R.ok().put("data", page);
    }

    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") String id){
        ConfigEntity config = configService.selectById(id);
        return R.ok().put("data", config);
    }
    
    /**
     * 详情
     */
    @IgnoreAuth
    @RequestMapping("/detail/{id}")
    public R detail(@PathVariable("id") String id){
        ConfigEntity config = configService.selectById(id);
        return R.ok().put("data", config);
    }
    
    /**
     * 根据name获取信息
     */
    @RequestMapping("/info")
    public R infoByName(@RequestParam String name){
        ConfigEntity config = configService.selectOne(new EntityWrapper<ConfigEntity>().eq("name", "faceFile"));
        return R.ok().put("data", config);
    }
    
    /**
     * 保存
     */
    @PostMapping("/save")
    public R save(@RequestBody ConfigEntity config){
//    	ValidatorUtils.validateEntity(config);
    	configService.insert(config);
        return R.ok();
    }

    /**
     * 修改配置（仅管理员可操作）
     */
    @RequestMapping("/update")
    public R update(@RequestBody ConfigEntity config, HttpServletRequest request){
        String tableName = (String) request.getSession().getAttribute("tableName");
        if (!"users".equals(tableName)) {
            return R.error("无权限操作，仅管理员可修改配置");
        }
        configService.updateById(config);
        return R.ok("修改成功");
    }

    /**
     * 删除配置（仅管理员可操作）
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids, HttpServletRequest request){
        String tableName = (String) request.getSession().getAttribute("tableName");
        if (!"users".equals(tableName)) {
            return R.error("无权限操作，仅管理员可删除配置");
        }
    	configService.deleteBatchIds(Arrays.asList(ids));
        return R.ok("删除成功");
    }

    /**
     * 获取所有配置（以Map形式返回）
     */
    @IgnoreAuth
    @RequestMapping("/all")
    public R all() {
        List<ConfigEntity> list = configService.selectList(new EntityWrapper<>());
        Map<String, String> configMap = new HashMap<>();
        for (ConfigEntity config : list) {
            configMap.put(config.getName(), config.getValue());
        }
        return R.ok().put("data", configMap);
    }

    /**
     * 根据配置名称获取值
     */
    @IgnoreAuth
    @RequestMapping("/get/{name}")
    public R getByName(@PathVariable("name") String name) {
        EntityWrapper<ConfigEntity> ew = new EntityWrapper<>();
        ew.eq("name", name);
        ConfigEntity config = configService.selectOne(ew);
        return R.ok().put("value", config != null ? config.getValue() : null);
    }

    /**
     * 保存或更新单个配置
     */
    @RequestMapping("/saveOrUpdate")
    public R saveOrUpdate(@RequestBody Map<String, Object> params, HttpServletRequest request) {
        String tableName = (String) request.getSession().getAttribute("tableName");
        if (!"users".equals(tableName)) {
            return R.error("无权限操作，仅管理员可修改系统配置");
        }

        String name = (String) params.get("name");
        String value = (String) params.get("value");

        if (StringUtils.isBlank(name)) {
            return R.error("配置名称不能为空");
        }

        EntityWrapper<ConfigEntity> ew = new EntityWrapper<>();
        ew.eq("name", name);
        ConfigEntity existConfig = configService.selectOne(ew);

        if (existConfig != null) {
            existConfig.setValue(value);
            configService.updateById(existConfig);
        } else {
            ConfigEntity newConfig = new ConfigEntity();
            newConfig.setName(name);
            newConfig.setValue(value);
            configService.insert(newConfig);
        }

        return R.ok("保存成功");
    }

    /**
     * 批量保存配置
     */
    @RequestMapping("/batchSave")
    public R batchSave(@RequestBody Map<String, String> configs, HttpServletRequest request) {
        String tableName = (String) request.getSession().getAttribute("tableName");
        if (!"users".equals(tableName)) {
            return R.error("无权限操作，仅管理员可修改系统配置");
        }

        for (Map.Entry<String, String> entry : configs.entrySet()) {
            if (StringUtils.isBlank(entry.getKey())) {
                continue;
            }
            
            EntityWrapper<ConfigEntity> ew = new EntityWrapper<>();
            ew.eq("name", entry.getKey());
            ConfigEntity existConfig = configService.selectOne(ew);

            if (existConfig != null) {
                existConfig.setValue(entry.getValue());
                configService.updateById(existConfig);
            } else {
                ConfigEntity newConfig = new ConfigEntity();
                newConfig.setName(entry.getKey());
                newConfig.setValue(entry.getValue());
                configService.insert(newConfig);
            }
        }
        return R.ok("批量保存成功");
    }

    /**
     * 获取酒店信息配置
     */
    @IgnoreAuth
    @RequestMapping("/hotelInfo")
    public R hotelInfo() {
        Map<String, String> info = new HashMap<>();

        String[] configNames = {"hotelName", "hotelAddress", "hotelPhone", "hotelEmail", 
                "hotelDescription", "checkInTime", "checkOutTime", "hotelLogo"};

        for (String name : configNames) {
            EntityWrapper<ConfigEntity> ew = new EntityWrapper<>();
            ew.eq("name", name);
            ConfigEntity config = configService.selectOne(ew);
            info.put(name, config != null ? config.getValue() : "");
        }

        return R.ok().put("data", info);
    }

    /**
     * 保存酒店信息配置
     */
    @RequestMapping("/saveHotelInfo")
    public R saveHotelInfo(@RequestBody Map<String, String> hotelInfo, HttpServletRequest request) {
        String tableName = (String) request.getSession().getAttribute("tableName");
        if (!"users".equals(tableName)) {
            return R.error("无权限操作，仅管理员可修改酒店信息");
        }

        String[] allowedKeys = {"hotelName", "hotelAddress", "hotelPhone", "hotelEmail", 
                "hotelDescription", "checkInTime", "checkOutTime", "hotelLogo"};
        
        for (String key : allowedKeys) {
            if (hotelInfo.containsKey(key)) {
                EntityWrapper<ConfigEntity> ew = new EntityWrapper<>();
                ew.eq("name", key);
                ConfigEntity existConfig = configService.selectOne(ew);

                if (existConfig != null) {
                    existConfig.setValue(hotelInfo.get(key));
                    configService.updateById(existConfig);
                } else {
                    ConfigEntity newConfig = new ConfigEntity();
                    newConfig.setName(key);
                    newConfig.setValue(hotelInfo.get(key));
                    configService.insert(newConfig);
                }
            }
        }

        return R.ok("酒店信息保存成功");
    }

    /**
     * 获取系统参数配置
     */
    @RequestMapping("/systemParams")
    public R systemParams(HttpServletRequest request) {
        String tableName = (String) request.getSession().getAttribute("tableName");
        if (!"users".equals(tableName)) {
            return R.error("无权限查看系统参数");
        }

        Map<String, String> params = new HashMap<>();

        String[] configNames = {"bookingExpireHours", "overtimeRate", "memberDiscount", 
                "pointsRate", "cancelDeadlineHours", "maxBookingDays"};

        for (String name : configNames) {
            EntityWrapper<ConfigEntity> ew = new EntityWrapper<>();
            ew.eq("name", name);
            ConfigEntity config = configService.selectOne(ew);
            params.put(name, config != null ? config.getValue() : "");
        }

        return R.ok().put("data", params);
    }

    /**
     * 保存系统参数配置
     */
    @RequestMapping("/saveSystemParams")
    public R saveSystemParams(@RequestBody Map<String, String> systemParams, HttpServletRequest request) {
        String tableName = (String) request.getSession().getAttribute("tableName");
        if (!"users".equals(tableName)) {
            return R.error("无权限操作，仅管理员可修改系统参数");
        }

        String[] allowedKeys = {"bookingExpireHours", "overtimeRate", "memberDiscount", 
                "pointsRate", "cancelDeadlineHours", "maxBookingDays"};
        
        for (String key : allowedKeys) {
            if (systemParams.containsKey(key)) {
                EntityWrapper<ConfigEntity> ew = new EntityWrapper<>();
                ew.eq("name", key);
                ConfigEntity existConfig = configService.selectOne(ew);

                if (existConfig != null) {
                    existConfig.setValue(systemParams.get(key));
                    configService.updateById(existConfig);
                } else {
                    ConfigEntity newConfig = new ConfigEntity();
                    newConfig.setName(key);
                    newConfig.setValue(systemParams.get(key));
                    configService.insert(newConfig);
                }
            }
        }

        return R.ok("系统参数保存成功");
    }
}
