package com.company.consumables.common.error;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * 类描述: 错误码消息加载器，启动时从 error-code.properties 加载错误码到中文消息的映射
 *
 * @author honghui
 * @version 1.0
 * @date 2026/06/30
 */
@Slf4j
@Component
public class ErrorCodeMessageHolder {

    /** 错误码配置文件路径 */
    private static final String ERROR_CODE_FILE = "error-code.properties";

    /** 错误码 -> 中文消息 映射 */
    private final Map<String, String> messageMap = new HashMap<>();

    /**
     * 功能描述: 初始化加载错误码配置
     *
     * @author honghui
     * @date 2026/06/30 10:30
     */
    @PostConstruct
    public void init() {
        try {
            Properties properties = PropertiesLoaderUtils.loadAllProperties(ERROR_CODE_FILE);
            for (String key : properties.stringPropertyNames()) {
                messageMap.put(key, properties.getProperty(key));
            }
            log.info("错误码加载完成，共 {} 条", messageMap.size());
        } catch (Exception e) {
            log.error("错误码配置文件加载失败: {}", ERROR_CODE_FILE, e);
        }
    }

    /**
     * 功能描述: 根据错误码获取中文消息，找不到时返回错误码本身
     *
     * @param code 错误码
     * @return 中文消息
     * @author honghui
     * @date 2026/06/30 10:30
     */
    public String getMessage(String code) {
        return messageMap.getOrDefault(code, code);
    }
}
