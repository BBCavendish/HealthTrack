package org.healthtrack.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan("org.healthtrack.mapper")
public class MyBatisConfig {
    // 配置类为空即可，@MapperScan注解会扫描mapper包
}