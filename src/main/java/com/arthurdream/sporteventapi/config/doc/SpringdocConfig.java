package com.arthurdream.sporteventapi.config.doc;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringdocConfig {

    @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
                             .group("Sport Event API")
                             .pathsToMatch("/**")
                             .build();
    }

    @Bean
    public OpenAPI customerOpenApi() {
        return new OpenAPI()
            .info(new Info()
                .title("Sport Event API")
                .description("API Documentation for Sport Event Application")
                .version("1.0.0"));
    }
}
