package com.veterinaria.ms_auth.controller;

import com.veterinaria.ms_auth.dto.LoginRequestDto;
import com.veterinaria.ms_auth.dto.LoginResponseDto;
import com.veterinaria.ms_auth.dto.RegisterRequestDto;
import com.veterinaria.ms_auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
@RequiredArgsConstructor
@Slf4j
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(
            @Valid @RequestBody LoginRequestDto dto) {
        log.info("Peticion de login recibida para: {}", dto.getUsername());
        return ResponseEntity.ok(authService.login(dto));
    }

    @PostMapping("/register")
    public ResponseEntity<Void> register(
            @Valid @RequestBody RegisterRequestDto dto) {
        authService.register(dto);
        log.info("Usuario registrado: {}", dto.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
