package com.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 首页跳转控制器
 * 提供便捷的页面入口
 */
@Controller
public class IndexController {

    /**
     * 管理端入口
     */
    @RequestMapping("/")
    public String index() {
        return "redirect:/admin/index.html";
    }

    /**
     * 用户端入口
     */
    @RequestMapping("/user")
    public String user() {
        return "redirect:/front/index.html";
    }
}
