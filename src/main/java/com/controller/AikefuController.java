package com.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.annotation.IgnoreAuth;
import com.entity.AikefuzhishikuEntity;
import com.service.AikefuzhishikuService;
import com.utils.R;
import com.constant.AiKefuConstant;
import com.baomidou.mybatisplus.mapper.EntityWrapper;

/**
 * AI客服对话接口
 * 用于用户与AI客服进行智能问答交互
 */
@RestController
@RequestMapping("/aikefu")
public class AikefuController {

    private static final Logger logger = LoggerFactory.getLogger(AikefuController.class);

    @Autowired
    private AikefuzhishikuService aikefuzhishikuService;

    /**
     * AI客服问答接口
     * 接收用户问题，返回智能回答
     */
    @IgnoreAuth
    @RequestMapping(value = "/chat", method = RequestMethod.POST)
    public R chat(@RequestBody Map<String, String> params, HttpServletRequest request) {
        String question = params.get("question");
        if (question == null || question.trim().isEmpty()) {
            return R.error("请输入您的问题");
        }

        question = question.trim();
        logger.info("AI客服收到问题: {}", question);

        try {
            String answer = aikefuzhishikuService.smartAnswer(question);
            Map<String, Object> result = new HashMap<>();
            result.put("answer", answer);
            result.put("time", new Date());
            return R.ok(result);
        } catch (Exception e) {
            logger.error("AI客服回答异常: {}", e.getMessage());
            return R.ok().put("answer", AiKefuConstant.DEFAULT_REPLY);
        }
    }

    /**
     * 获取常见问题列表
     * 用于用户端展示热门问题
     */
    @IgnoreAuth
    @RequestMapping(value = "/hotQuestions", method = RequestMethod.GET)
    public R hotQuestions() {
        try {
            List<AikefuzhishikuEntity> list = aikefuzhishikuService.getHotQuestions(10);
            return R.ok().put("data", list);
        } catch (Exception e) {
            logger.error("获取热门问题异常: {}", e.getMessage());
            return R.ok().put("data", null);
        }
    }

    /**
     * 获取问题分类
     * 用于用户端展示问题分类导航
     */
    @IgnoreAuth
    @RequestMapping(value = "/categories", method = RequestMethod.GET)
    public R categories() {
        String[] categories = {"客房信息", "价格咨询", "服务咨询", "预订流程", "退订政策", "会员权益", "其他"};
        return R.ok().put("data", categories);
    }

    /**
     * 根据分类获取问题列表
     */
    @IgnoreAuth
    @RequestMapping(value = "/questionsByCategory", method = RequestMethod.GET)
    public R questionsByCategory(String category) {
        if (category == null || category.trim().isEmpty()) {
            return R.error("请选择问题分类");
        }
        List<AikefuzhishikuEntity> list = aikefuzhishikuService.selectList(
                new EntityWrapper<AikefuzhishikuEntity>()
                        .eq("wentileixing", category)
                        .eq("zhuangtai", AiKefuConstant.KB_STATUS_ENABLED)
                        .orderBy("paixu", true)
        );
        return R.ok().put("data", list);
    }

    /**
     * 获取欢迎语
     */
    @IgnoreAuth
    @RequestMapping(value = "/greeting", method = RequestMethod.GET)
    public R greeting() {
        String greeting = String.format(AiKefuConstant.GREETING_TEMPLATE, "酒店", "小智");
        return R.ok().put("greeting", greeting);
    }
}
