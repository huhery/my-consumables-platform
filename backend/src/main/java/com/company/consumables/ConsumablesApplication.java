package com.company.consumables;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 类描述: 超市耗材供应商系统后端启动类
 *
 * @author honghui
 * @version 1.0
 * @date 2026/06/30
 */
@SpringBootApplication
@MapperScan("com.company.consumables.**.mapper")
public class ConsumablesApplication {

    /**
     * 功能描述: 应用启动入口
     *
     * @param args 启动参数
     * @author honghui
     * @date 2026/06/30 10:00
     */
    public static void main(String[] args) {
        SpringApplication.run(ConsumablesApplication.class, args);
    }
}
