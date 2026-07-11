package com.veterinaria.ms_tratamientos.feign;

import com.veterinaria.ms_tratamientos.dto.MedicamentoDto;
import com.veterinaria.ms_tratamientos.dto.MovimientoRequestDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "ms-inventario")
public interface InventarioClient {

    @GetMapping("/inventario/nombre/{nombre}")
    MedicamentoDto getMedicamentoPorNombre(@PathVariable String nombre);

    @PostMapping("/inventario/movimientos")
    MedicamentoDto registrarMovimiento(@RequestBody MovimientoRequestDto dto);
}