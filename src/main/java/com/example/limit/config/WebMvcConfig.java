package com.example.limit.config;

import com.example.limit.interceptor.TokenBucketInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author yzp
 * @version 1.0
 * @date 2022/7/19 - 22:12
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private TokenBucketInterceptor tokenBucketInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(tokenBucketInterceptor)
                .addPathPatterns("/**");
    }

}
