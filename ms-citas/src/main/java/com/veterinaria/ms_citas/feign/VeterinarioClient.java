package com.veterinaria.ms_citas.feign;

import com.veterinaria.ms_citas.dto.VeterinarioDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "ms-veterinarios")
public interface VeterinarioClient {

    @GetMapping("/veterinarios/{id}")
    VeterinarioDto getVeterinario(@PathVariable Long id);

    @GetMapping("/veterinarios/{id}/disponible")
    Boolean isDisponible(@PathVariable Long id);
}
