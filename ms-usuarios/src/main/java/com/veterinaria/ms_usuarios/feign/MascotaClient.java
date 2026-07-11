package com.veterinaria.ms_usuarios.feign;

import com.veterinaria.ms_usuarios.dto.MascotaDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "ms-mascotas")
public interface MascotaClient {

    @GetMapping("/mascotas/dueno/{idDueno}")
    List<MascotaDto> getMascotasByDueno(@PathVariable Long idDueno);
}