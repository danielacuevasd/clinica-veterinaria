package com.veterinaria.ms_mascotas.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI apiInfo() {
        return new OpenAPI()
                .info(new Info()
                        .title("ms-mascotas API")
                        .version("1.0.0")
                        .description("Microservicio de gestion de mascotas y su "
                                + "historial medico en la clinica veterinaria."));
    }
}