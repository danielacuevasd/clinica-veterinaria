package com.veterinaria.ms_auth.service;

import com.veterinaria.ms_auth.dto.LoginRequestDto;
import com.veterinaria.ms_auth.dto.LoginResponseDto;
import com.veterinaria.ms_auth.dto.RegisterRequestDto;
import com.veterinaria.ms_auth.model.Usuario;
import com.veterinaria.ms_auth.repository.UsuarioRepository;
import com.veterinaria.ms_auth.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public LoginResponseDto login(LoginRequestDto dto) {
        log.info("Intento de login para usuario: {}", dto.getUsername());

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        dto.getUsername(), dto.getPassword()));

        Usuario usuario = usuarioRepository.findByUsername(dto.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Usuario no encontrado"));

        String token = jwtService.generateToken(usuario);
        log.info("Login exitoso para usuario: {}", dto.getUsername());

        return LoginResponseDto.builder()
                .token(token)
                .username(usuario.getUsername())
                .rol(usuario.getRol().name())
                .build();
    }

    public void register(RegisterRequestDto dto) {
        log.info("Registrando nuevo usuario: {}", dto.getUsername());

        if (usuarioRepository.existsByUsername(dto.getUsername())) {
            throw new RuntimeException(
                    "El username ya está en uso: " + dto.getUsername());
        }

        Usuario usuario = Usuario.builder()
                .username(dto.getUsername())
                .password(passwordEncoder.encode(dto.getPassword()))
                .rol(dto.getRol())
                .activo(true)
                .createdAt(LocalDateTime.now())
                .build();

        usuarioRepository.save(usuario);
        log.info("Usuario registrado exitosamente: {}", dto.getUsername());
    }

}
