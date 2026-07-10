package com.veterinaria.ms_consultas.feign;

import com.veterinaria.ms_consultas.dto.MascotaDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "ms-mascotas")
public interface MascotaClient {

    @GetMapping("/mascotas/{id}")
    MascotaDto getMascota(@PathVariable Long id);
}