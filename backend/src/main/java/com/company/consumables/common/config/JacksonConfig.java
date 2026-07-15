package com.company.consumables.common.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import java.text.SimpleDateFormat;
import java.util.TimeZone;

/**
 * 类描述: Jackson 全局配置。按字段名原样输出 JSON key，不依赖 getter 推断属性名。
 *         解决 Lombok 生成的 getter（如 getSName）与 Jackson 默认命名策略不兼容的问题。
 *
 * @author honghui
 * @version 1.0
 * @date 2026/07/12
 */
@Configuration
public class JacksonConfig {

    /**
     * 功能描述: 配置 ObjectMapper：字段级序列化，忽略 getter 推断
     *
     * @return 自定义 ObjectMapper
     * @author honghui
     * @date 2026/07/12 14:00
     */
    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jacksonCustomizer() {
        return builder -> builder.postConfigurer(objectMapper -> {
            // 字段级可见性：直接按字段名序列化（不受 getter 命名影响）
            objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
            // 关闭 getter 自动检测，避免 getter 名推断覆盖字段名
            objectMapper.setVisibility(PropertyAccessor.GETTER, JsonAutoDetect.Visibility.NONE);
            objectMapper.setVisibility(PropertyAccessor.IS_GETTER, JsonAutoDetect.Visibility.NONE);
            // 保留 setter 用于反序列化（@RequestBody 需要）
            objectMapper.setVisibility(PropertyAccessor.SETTER, JsonAutoDetect.Visibility.ANY);
            // 全局日期格式：yyyy-MM-dd HH:mm:ss，时区 Asia/Shanghai
            objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
            objectMapper.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
            // 禁止把日期序列化为时间戳
            objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        });
    }
}
