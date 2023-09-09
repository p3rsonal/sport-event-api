package com.arthurdream.sporteventapi.config.caching;

import jdk.jfr.Description;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "app.cache")
@Getter
@Setter
public class CacheProperties {

    @Description("Whether caching is enabled or not")
    private boolean enabled;
}

