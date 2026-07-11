package com.veterinaria.ms_facturacion;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class MsFacturacionApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsFacturacionApplication.class, args);
	}

}