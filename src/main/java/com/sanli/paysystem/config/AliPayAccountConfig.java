package com.sanli.paysystem.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "alipay")
@Data
public class AliPayAccountConfig {
    private String appId;
    private String privateKey;
    private String aliPayPublicKey;
    private String notifyUrl;
    private String returnUrl;
}
