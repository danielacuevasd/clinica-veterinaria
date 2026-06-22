package com.veterinaria.ms_inventario.config;

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
                        .title("ms-inventario API")
                        .version("1.0.0")
                        .description("Microservicio de gestion de inventario de "
                                + "medicamentos. Controla el stock y registra "
                                + "movimientos al consumir eventos de consultas."));
    }
}
