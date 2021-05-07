package com.quan;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author: xiexinquan520@163.com
 * User: XieXinQuan
 * DATE:2021/4/29
 */
@Configuration
@EnableConfigurationProperties(GroundhogAutoConfigure.class)
public class GroundhogConfig {

    @Bean
    public JpaDynamicProxy jpaDynamicProxy(){
        return new JpaDynamicProxy();
    }
}
