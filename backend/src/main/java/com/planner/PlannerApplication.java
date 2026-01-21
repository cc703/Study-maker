package com.planner;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 学习任务规划与执行系统 - 启动类
 */
@SpringBootApplication
@MapperScan("com.planner.mapper")
public class PlannerApplication {

    public static void main(String[] args) {
        SpringApplication.run(PlannerApplication.class, args);
        System.out.println("=================================");
        System.out.println("  Learning Planner 启动成功！");
        System.out.println("  访问地址: http://localhost:8080");
        System.out.println("=================================");
    }
}
