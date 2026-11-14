package com.example.utils;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@ConfigurationProperties(prefix = "aliyun.oss")
@Component
public class AliyunOSSProperties {

    private String endpoint;
    private String bucketName;
    private String region;

}
