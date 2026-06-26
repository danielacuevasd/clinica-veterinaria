package com.veterinaria.ms_auth.service;

import com.veterinaria.ms_auth.dto.LoginRequestDto;
import com.veterinaria.ms_auth.dto.LoginResponseDto;
import com.veterinaria.ms_auth.dto.RegisterRequestDto;
import com.veterinaria.ms_auth.model.Rol;
import com.veterinaria.ms_auth.model.Usuario;
import com.veterinaria.ms_auth.repository.UsuarioRepository;
import com.veterinaria.ms_auth.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService - Pruebas Unitarias")
class AuthServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthService authService;

    private LoginRequestDto loginDtoEjemplo;
    private RegisterRequestDto registerDtoEjemplo;
    private Usuario usuarioEjemplo;

    @BeforeEach
    void setUp() {
        loginDtoEjemplo = new LoginRequestDto();
        loginDtoEjemplo.setUsername("admin");
        loginDtoEjemplo.setPassword("admin123");

        registerDtoEjemplo = new RegisterRequestDto();
        registerDtoEjemplo.setUsername("nuevoUsuario");
        registerDtoEjemplo.setPassword("clave123");
        registerDtoEjemplo.setRol(Rol.ADMIN);

        usuarioEjemplo = Usuario.builder()
                .id(1L)
                .username("admin")
                .password("hashEncriptado")
                .rol(Rol.ADMIN)
                .activo(true)
                .createdAt(LocalDateTime.now())
                .build();
    }


    // login() - autenticacion exitosa y casos de error
    @Test
    @DisplayName("login: deberia retornar token JWT cuando las credenciales son correctas")
    void login_credencialesCorrectas_retornaTokenJwt() {
        // Given
        when(usuarioRepository.findByUsername("admin"))
                .thenReturn(Optional.of(usuarioEjemplo));
        when(jwtService.generateToken(usuarioEjemplo))
                .thenReturn("token.jwt.simulado");

        // When
        LoginResponseDto resultado = authService.login(loginDtoEjemplo);

        // Then
        assertNotNull(resultado);
        assertEquals("token.jwt.simulado", resultado.getToken());
        assertEquals("admin", resultado.getUsername());
        assertEquals("ADMIN", resultado.getRol());
        verify(authenticationManager, times(1)).authenticate(any());
        verify(jwtService, times(1)).generateToken(usuarioEjemplo);
    }

    @Test
    @DisplayName("login: deberia lanzar excepcion si el usuario no existe en la BD")
    void login_usuarioNoExisteEnBD_lanzaExcepcion() {
        // Given: la autenticacion pasa (Spring Security la valida),
        // pero el usuario no se encuentra luego en el repositorio
        when(usuarioRepository.findByUsername("admin"))
                .thenReturn(Optional.empty());

        // When + Then
        assertThrows(UsernameNotFoundException.class,
                () -> authService.login(loginDtoEjemplo));
        verify(jwtService, never()).generateToken(any());
    }

    // register() - regla de negocio: username duplicado
    @Test
    @DisplayName("register: deberia registrar el usuario cuando el username no existe")
    void register_usernameNoExiste_registraUsuarioCorrectamente() {
        // Given
        when(usuarioRepository.existsByUsername("nuevoUsuario"))
                .thenReturn(false);
        when(passwordEncoder.encode("clave123"))
                .thenReturn("hashEncriptado");

        // When
        authService.register(registerDtoEjemplo);

        // Then
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
        verify(passwordEncoder, times(1)).encode("clave123");
    }

    @Test
    @DisplayName("register: deberia lanzar excepcion si el username ya existe")
    void register_usernameYaExiste_lanzaRuntimeException() {
        // Given
        when(usuarioRepository.existsByUsername("nuevoUsuario"))
                .thenReturn(true);

        // When + Then
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> authService.register(registerDtoEjemplo));

        assertEquals("El username ya está en uso: nuevoUsuario", ex.getMessage());
        // Si el username ya existe, NUNCA debe guardarse ni encriptarse la clave
        verify(usuarioRepository, never()).save(any(Usuario.class));
        verify(passwordEncoder, never()).encode(any());
    }
}