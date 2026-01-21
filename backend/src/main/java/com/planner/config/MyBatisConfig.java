package com.planner.config;

import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.LocalDateTimeTypeHandler;
import org.apache.ibatis.type.LocalDateTypeHandler;
import org.mybatis.spring.boot.autoconfigure.ConfigurationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MyBatis配置 - 日期类型处理
 */
@Configuration
public class MyBatisConfig {

    @Bean
    public ConfigurationCustomizer mybatisConfigurationCustomizer() {
        return configuration -> {
            configuration.getTypeHandlerRegistry().register(java.time.LocalDate.class, JdbcType.DATE,
                    LocalDateTypeHandler.class);
            configuration.getTypeHandlerRegistry().register(java.time.LocalDateTime.class, JdbcType.TIMESTAMP,
                    LocalDateTimeTypeHandler.class);
        };
    }
}
