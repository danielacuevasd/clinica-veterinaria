package com.veterinaria.ms_consultas.feign;

import com.veterinaria.ms_consultas.dto.VeterinarioDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "ms-veterinarios")
public interface VeterinarioClient {

    @GetMapping("/veterinarios/{id}")
    VeterinarioDto getVeterinario(@PathVariable Long id);
}