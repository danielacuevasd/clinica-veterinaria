package com.veterinaria.ms_auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequestDto {

    @NotBlank(message = "El username es obligatorio")
    private String username;

    @NotBlank(message = "La password es obligatoria")
    private String password;
}
