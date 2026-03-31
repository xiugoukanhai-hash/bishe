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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

import com.entity.ChatEntity;
import com.entity.view.ChatView;

import com.service.ChatService;
import com.service.TokenService;
import com.service.AikefuzhishikuService;
import com.utils.PageUtils;
import com.utils.R;
import com.utils.MD5Util;
import com.utils.MPUtil;
import com.utils.CommonUtil;


/**
 * 在线客服
 * 后端接口
 * 支持人工客服和AI智能客服自动回复
 */
@RestController
@RequestMapping("/chat")
public class ChatController {
    
    private static final Logger logger = LoggerFactory.getLogger(ChatController.class);
    
    @Autowired
    private ChatService chatService;
    
    @Autowired
    private AikefuzhishikuService aikefuzhishikuService;
    


    /**
     * 后端列表（管理员和前台人员可查看全部，其他用户只能查看自己的）
     */
    @RequestMapping("/page")
    public R page(@RequestParam Map<String, Object> params,ChatEntity chat,
		HttpServletRequest request){
    	String tableName = (String) request.getSession().getAttribute("tableName");
    	// 管理员和前台人员可以查看所有消息
    	if (!"users".equals(tableName) && !"qiantairenyuan".equals(tableName)) {
    		chat.setUserid((Long)request.getSession().getAttribute("userId"));
    	}
        EntityWrapper<ChatEntity> ew = new EntityWrapper<ChatEntity>();
		PageUtils page = chatService.queryPage(params, MPUtil.sort(MPUtil.between(MPUtil.likeOrEq(ew, chat), params), params));

        return R.ok().put("data", page);
    }
    
    /**
     * 前端列表（用户只能看自己的聊天记录）
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params,ChatEntity chat, 
		HttpServletRequest request){
    	String tableName = (String) request.getSession().getAttribute("tableName");
    	// 管理员和前台人员可以查看所有消息
    	if (!"users".equals(tableName) && !"qiantairenyuan".equals(tableName)) {
    		chat.setUserid((Long)request.getSession().getAttribute("userId"));
    	}
        EntityWrapper<ChatEntity> ew = new EntityWrapper<ChatEntity>();
		PageUtils page = chatService.queryPage(params, MPUtil.sort(MPUtil.between(MPUtil.likeOrEq(ew, chat), params), params));
        return R.ok().put("data", page);
    }

	/**
     * 列表
     */
    @RequestMapping("/lists")
    public R list( ChatEntity chat){
       	EntityWrapper<ChatEntity> ew = new EntityWrapper<ChatEntity>();
      	ew.allEq(MPUtil.allEQMapPre( chat, "chat")); 
        return R.ok().put("data", chatService.selectListView(ew));
    }

	 /**
     * 查询
     */
    @RequestMapping("/query")
    public R query(ChatEntity chat){
        EntityWrapper< ChatEntity> ew = new EntityWrapper< ChatEntity>();
 		ew.allEq(MPUtil.allEQMapPre( chat, "chat")); 
		ChatView chatView =  chatService.selectView(ew);
		return R.ok("查询在线客服成功").put("data", chatView);
    }
	
    /**
     * 后端详情
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
        ChatEntity chat = chatService.selectById(id);
        return R.ok().put("data", chat);
    }

    /**
     * 前端详情
     */
    @RequestMapping("/detail/{id}")
    public R detail(@PathVariable("id") Long id){
        ChatEntity chat = chatService.selectById(id);
        return R.ok().put("data", chat);
    }
    



    /**
     * 后端保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody ChatEntity chat, HttpServletRequest request){
    	chat.setId(new Date().getTime()+new Double(Math.floor(Math.random()*1000)).longValue());
    	//ValidatorUtils.validateEntity(chat);
    	if(StringUtils.isNotBlank(chat.getAsk())) {
			chatService.updateForSet("isreply=0", new EntityWrapper<ChatEntity>().eq("userid", request.getSession().getAttribute("userId")));
    		chat.setUserid((Long)request.getSession().getAttribute("userId"));
    		chat.setIsreply(1);
    	}
    	if(StringUtils.isNotBlank(chat.getReply())) {
    		chatService.updateForSet("isreply=0", new EntityWrapper<ChatEntity>().eq("userid", chat.getUserid()));
    		chat.setAdminid((Long)request.getSession().getAttribute("userId"));
    	}
        chatService.insert(chat);
        return R.ok();
    }
    
    /**
     * 前端保存（支持AI自动回复）
     */
    @RequestMapping("/add")
    public R add(@RequestBody ChatEntity chat, HttpServletRequest request){
    	chat.setId(new Date().getTime()+new Double(Math.floor(Math.random()*1000)).longValue());
    	chat.setUserid((Long)request.getSession().getAttribute("userId"));
    	
    	if(StringUtils.isNotBlank(chat.getAsk())) {
			chatService.updateForSet("isreply=0", new EntityWrapper<ChatEntity>().eq("userid", request.getSession().getAttribute("userId")));
    		chat.setUserid((Long)request.getSession().getAttribute("userId"));
    		
    		// 尝试AI自动回复
    		try {
    		    String aiAnswer = aikefuzhishikuService.smartAnswer(chat.getAsk());
    		    if (aiAnswer != null && !aiAnswer.isEmpty()) {
    		        chat.setReply("[AI小智] " + aiAnswer);
    		        chat.setIsreply(1);
    		        logger.info("AI自动回复成功: question={}", chat.getAsk());
    		    } else {
    		        chat.setIsreply(0);
    		    }
    		} catch (Exception e) {
    		    logger.error("AI自动回复失败: {}", e.getMessage());
    		    chat.setIsreply(0);
    		}
    	}
    	if(StringUtils.isNotBlank(chat.getReply()) && !chat.getReply().startsWith("[AI小智]")) {
    		chatService.updateForSet("isreply=0", new EntityWrapper<ChatEntity>().eq("userid", chat.getUserid()));
    		chat.setAdminid((Long)request.getSession().getAttribute("userId"));
    	}
        chatService.insert(chat);
        return R.ok();
    }
    
    /**
     * AI客服智能问答接口（支持实时AI回复）
     */
    @IgnoreAuth
    @RequestMapping("/aiChat")
    public R aiChat(@RequestBody Map<String, Object> params, HttpServletRequest request) {
        String question = (String) params.get("question");
        if (StringUtils.isBlank(question)) {
            return R.error("请输入您的问题");
        }
        
        try {
            String answer = aikefuzhishikuService.smartAnswer(question);
            Map<String, Object> result = new HashMap<>();
            result.put("answer", answer);
            result.put("time", new Date());
            result.put("isAiReply", true);
            return R.ok().put("data", result);
        } catch (Exception e) {
            logger.error("AI问答异常: {}", e.getMessage());
            return R.error("AI客服暂时无法回答，请稍后再试或联系人工客服");
        }
    }
    
    /**
     * 获取待回复消息列表（客服使用）
     */
    @RequestMapping("/pendingList")
    public R pendingList(@RequestParam Map<String, Object> params, HttpServletRequest request) {
        // 验证权限：只有管理员和前台人员可以查看待回复消息
        String tableName = (String) request.getSession().getAttribute("tableName");
        if (!"users".equals(tableName) && !"qiantairenyuan".equals(tableName)) {
            return R.error("无权限查看待回复消息");
        }
        
        EntityWrapper<ChatEntity> ew = new EntityWrapper<>();
        ew.eq("isreply", 0);
        ew.isNull("reply");
        ew.orderBy("addtime", true);
        
        PageUtils page = chatService.queryPage(params, ew);
        return R.ok().put("data", page);
    }
    
    /**
     * 客服回复消息
     */
    @RequestMapping("/reply/{chatId}")
    public R reply(@PathVariable("chatId") Long chatId, @RequestBody Map<String, Object> params, HttpServletRequest request) {
        // 验证权限：只有管理员和前台人员可以回复
        String tableName = (String) request.getSession().getAttribute("tableName");
        if (!"users".equals(tableName) && !"qiantairenyuan".equals(tableName)) {
            return R.error("无权限回复消息");
        }
        
        String reply = (String) params.get("reply");
        if (StringUtils.isBlank(reply)) {
            return R.error("回复内容不能为空");
        }
        
        ChatEntity chat = chatService.selectById(chatId);
        if (chat == null) {
            return R.error("消息不存在");
        }
        
        chat.setReply(reply);
        chat.setIsreply(1);
        chat.setAdminid((Long) request.getSession().getAttribute("userId"));
        chatService.updateById(chat);
        
        return R.ok("回复成功");
    }
    
    /**
     * 转人工客服
     */
    @RequestMapping("/transferHuman")
    public R transferHuman(@RequestBody Map<String, Object> params, HttpServletRequest request) {
        Long userId = (Long) request.getSession().getAttribute("userId");
        String content = (String) params.get("content");
        
        ChatEntity chat = new ChatEntity();
        chat.setId(new Date().getTime() + new Double(Math.floor(Math.random() * 1000)).longValue());
        chat.setAddtime(new Date());
        chat.setUserid(userId);
        chat.setAsk("[转人工] " + (StringUtils.isNotBlank(content) ? content : "用户请求转人工客服"));
        chat.setIsreply(0);
        
        chatService.insert(chat);
        return R.ok("已转接人工客服，请稍候");
    }

    /**
     * 修改（仅管理员和前台人员可操作）
     */
    @RequestMapping("/update")
    public R update(@RequestBody ChatEntity chat, HttpServletRequest request){
        // 验证权限：只有管理员和前台人员可以修改
        String tableName = (String) request.getSession().getAttribute("tableName");
        if (!"users".equals(tableName) && !"qiantairenyuan".equals(tableName)) {
            return R.error("无权限修改聊天记录");
        }
        chatService.updateById(chat);
        return R.ok();
    }
    

    /**
     * 删除（仅管理员可操作）
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids, HttpServletRequest request){
        // 验证权限：只有管理员可以删除聊天记录
        String tableName = (String) request.getSession().getAttribute("tableName");
        if (!"users".equals(tableName)) {
            return R.error("无权限删除聊天记录");
        }
        chatService.deleteBatchIds(Arrays.asList(ids));
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
		
		Wrapper<ChatEntity> wrapper = new EntityWrapper<ChatEntity>();
		if(map.get("remindstart")!=null) {
			wrapper.ge(columnName, map.get("remindstart"));
		}
		if(map.get("remindend")!=null) {
			wrapper.le(columnName, map.get("remindend"));
		}


		int count = chatService.selectCount(wrapper);
		return R.ok().put("count", count);
	}
	


}
