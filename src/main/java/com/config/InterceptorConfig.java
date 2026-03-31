package com.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import com.interceptor.AuthorizationInterceptor;

@Configuration
public class InterceptorConfig extends WebMvcConfigurationSupport{
	
	@Bean
    public AuthorizationInterceptor getAuthorizationInterceptor() {
        return new AuthorizationInterceptor();
    }
	
	@Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(getAuthorizationInterceptor())
                .addPathPatterns("/**")
                .excludePathPatterns("/")
                .excludePathPatterns("/user")
                .excludePathPatterns("/static/**")
                .excludePathPatterns("/admin/**")
                .excludePathPatterns("/front/**")
                .excludePathPatterns("/file/**")
                .excludePathPatterns("/upload/**")
                .excludePathPatterns("/*.html")
                .excludePathPatterns("/*.js")
                .excludePathPatterns("/*.css")
                .excludePathPatterns("/*.ico")
                .excludePathPatterns("/error");
        super.addInterceptors(registry);
	}
	
	/**
	 * springboot 2.0配置WebMvcConfigurationSupport之后，会导致默认配置被覆盖，要访问静态资源需要重写addResourceHandlers方法
	 */
	@Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
		// 管理端静态资源 - 访问/admin/xxx 映射到 classpath:/admin/admin/dist/xxx
		registry.addResourceHandler("/admin/**")
				.addResourceLocations("classpath:/admin/admin/dist/");
		
		// 用户端静态资源 - 访问/front/xxx 映射到 classpath:/front/front/xxx
		registry.addResourceHandler("/front/**")
				.addResourceLocations("classpath:/front/front/");
		
		// 上传文件 - 支持多个位置
		registry.addResourceHandler("/upload/**")
				.addResourceLocations("file:upload/")
				.addResourceLocations("classpath:/static/upload/");
		
		// 其他静态资源
		registry.addResourceHandler("/**")
				.addResourceLocations("classpath:/resources/")
				.addResourceLocations("classpath:/static/")
				.addResourceLocations("classpath:/public/");
		
		super.addResourceHandlers(registry);
    }
}
