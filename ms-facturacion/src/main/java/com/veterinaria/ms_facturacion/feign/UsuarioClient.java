package com.veterinaria.ms_facturacion.feign;

import com.veterinaria.ms_facturacion.dto.DuenoDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "ms-usuarios")
public interface UsuarioClient {

    @GetMapping("/usuarios/{id}")
    DuenoDto getDueno(@PathVariable Long id);
}