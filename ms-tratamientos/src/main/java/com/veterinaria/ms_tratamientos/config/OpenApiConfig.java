package com.veterinaria.ms_tratamientos.config;

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
                        .title("ms-tratamientos API")
                        .version("1.0.0")
                        .description("Microservicio de gestion de tratamientos medicos. "
                                + "Se crea automaticamente al consumir el evento Kafka "
                                + "consulta.registrada publicado por ms-consultas."));
    }
}