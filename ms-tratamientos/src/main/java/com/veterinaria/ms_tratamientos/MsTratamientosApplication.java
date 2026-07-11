package com.veterinaria.ms_tratamientos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class MsTratamientosApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsTratamientosApplication.class, args);
	}

}