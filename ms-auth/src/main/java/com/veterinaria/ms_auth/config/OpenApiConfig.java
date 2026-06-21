package com.veterinaria.ms_auth.config;

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
                        .title("ms-auth API")
                        .version("1.0.0")
                        .description("Microservicio de autenticacion y autorizacion de la clinica veterinaria. "
                                + "Gestiona el registro de usuarios, inicio de sesion y emision de tokens JWT."));
    }
}
