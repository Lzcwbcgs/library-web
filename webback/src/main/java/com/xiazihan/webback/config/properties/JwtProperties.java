package com.xiazihan.webback.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {
    /**
     * JWT加解密使用的密钥
     */
    private String secret;

    /**
     * JWT的超期限时间(单位：毫秒)
     */
    private Long expiration;
}