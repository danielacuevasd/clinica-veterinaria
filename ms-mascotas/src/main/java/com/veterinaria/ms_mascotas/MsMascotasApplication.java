package com.veterinaria.ms_mascotas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class MsMascotasApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsMascotasApplication.class, args);
	}

}
