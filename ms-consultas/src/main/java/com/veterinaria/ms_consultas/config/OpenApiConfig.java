package com.veterinaria.ms_consultas.config;

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
                        .title("ms-consultas API")
                        .version("1.0.0")
                        .description("Microservicio de registro de consultas medicas. "
                                + "Al guardar una consulta, publica un evento Kafka "
                                + "que consumen tratamientos, facturacion, inventario "
                                + "y notificaciones."));
    }
}