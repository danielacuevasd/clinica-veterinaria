package com.veterinaria.ms_auth.service;

import com.veterinaria.ms_auth.model.Rol;
import com.veterinaria.ms_auth.model.Usuario;
import com.veterinaria.ms_auth.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UsuarioDetailsService - Pruebas Unitarias")
class UsuarioDetailsServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private UsuarioDetailsService usuarioDetailsService;

    private Usuario usuarioEjemplo;

    @BeforeEach
    void setUp() {
        usuarioEjemplo = Usuario.builder()
                .id(1L)
                .username("dcuevas")
                .password("$2a$10$hashDeEjemplo")
                .rol(Rol.ADMIN)
                .activo(true)
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("loadUserByUsername: deberia retornar el UserDetails cuando el username existe")
    void loadUserByUsername_cuandoExiste_retornaUserDetails() {
        // Given
        when(usuarioRepository.findByUsername("dcuevas"))
                .thenReturn(Optional.of(usuarioEjemplo));

        // When
        UserDetails resultado = usuarioDetailsService.loadUserByUsername("dcuevas");

        // Then
        assertNotNull(resultado);
        assertEquals("dcuevas", resultado.getUsername());
        assertTrue(resultado.isEnabled());
        verify(usuarioRepository, times(1)).findByUsername("dcuevas");
    }

    @Test
    @DisplayName("loadUserByUsername: deberia lanzar UsernameNotFoundException cuando el username no existe")
    void loadUserByUsername_cuandoNoExiste_lanzaUsernameNotFoundException() {
        // Given
        when(usuarioRepository.findByUsername("noexiste"))
                .thenReturn(Optional.empty());

        // When + Then
        UsernameNotFoundException ex = assertThrows(UsernameNotFoundException.class,
                () -> usuarioDetailsService.loadUserByUsername("noexiste"));

        assertEquals("Usuario no encontrado: noexiste", ex.getMessage());
    }
}