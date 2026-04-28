package com.rvneto.broker.asset.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI brokerAssetOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Broker Asset API")
                        .description("Asset Catalog and Market Data Orchestration Service")
                        .version("v1.0.0")
                        .contact(new Contact()
                                .name("Roberto")
                                .email("rvneto.rs@gmail.com")));
    }
}
