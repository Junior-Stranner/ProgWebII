package com.ProgWebII.biotrack.service;

import com.ProgWebII.biotrack.dto.request.UserPatchRequest;
import com.ProgWebII.biotrack.dto.request.UserRequest;
import com.ProgWebII.biotrack.dto.response.*;
import com.ProgWebII.biotrack.model.Measure;
import com.ProgWebII.biotrack.model.User;
import com.ProgWebII.biotrack.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Testes unitários para UserService.
 * Usa @ExtendWith(MockitoExtension.class) para inicializar mocks.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Testes Unitários - UserService")
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User usuarioTeste;
    private UserRequest userRequest;

    @BeforeEach
    void setUp() {
        usuarioTeste = User.builder()
                .id(1L)
                .name("João Silva")
                .birthDate(LocalDate.of(1990, 5, 15))
                .zipCode("12345-678")
                .email("joao.silva@email.com")
                .password("senhaHasheada123")
                .measures(new ArrayList<>())
                .build();

        userRequest = new UserRequest(
                "João Silva",
                LocalDate.of(1990, 5, 15),
                "12345-678",
                "joao.silva@email.com",
                "senha123"
        );
    }

    @Test
    @DisplayName("Deve criar usuário com sucesso")
    void deveCriarUsuarioComSucesso() {
        // Given
        when(passwordEncoder.encode(anyString())).thenReturn("senhaHasheada123");
        when(userRepository.save(any(User.class))).thenReturn(usuarioTeste);

        // When
        userService.createUser(userRequest);

        // Then
        verify(passwordEncoder, times(1)).encode("senha123");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao criar usuário com erro")
    void deveLancarExcecaoAoCriarUsuarioComErro() {
        // Given
        when(passwordEncoder.encode(anyString())).thenReturn("senhaHasheada123");
        when(userRepository.save(any(User.class))).thenThrow(new RuntimeException("Erro no banco"));

        // When & Then
        assertThatThrownBy(() -> userService.createUser(userRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Falha ao processar a criação do usuário");
    }

    @Test
    @DisplayName("Deve listar todos os usuários")
    void deveListarTodosUsuarios() {
        // Given
        User usuario2 = User.builder()
                .id(2L)
                .name("Maria Santos")
                .birthDate(LocalDate.of(1992, 8, 20))
                .zipCode("98765-432")
                .email("maria.santos@email.com")
                .password("senhaHasheada456")
                .build();

        when(userRepository.findAll()).thenReturn(Arrays.asList(usuarioTeste, usuario2));

        // When
        List<ListarTodosUsuariosResponse> resultado = userService.listarTodos();

        // Then
        assertThat(resultado).hasSize(2);
        assertThat(resultado.get(0).name()).isEqualTo("João Silva");
        assertThat(resultado.get(1).name()).isEqualTo("Maria Santos");
        verify(userRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Deve lançar exceção ao listar usuários quando lista está vazia")
    void deveLancarExcecaoAoListarUsuariosQuandoListaVazia() {
        // Given
        when(userRepository.findAll()).thenReturn(new ArrayList<>());

        // When & Then
        assertThatThrownBy(() -> userService.listarTodos())
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Nenhum usuário encontrado");
    }

    @Test
    @DisplayName("Deve buscar usuário por ID")
    void deveBuscarUsuarioPorId() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(usuarioTeste));

        // When
        BuscarUsuarioPorIdResponse resultado = userService.buscarPorId(1L);

        // Then
        assertThat(resultado).isNotNull();
        assertThat(resultado.name()).isEqualTo("João Silva");
        assertThat(resultado.email()).isEqualTo("joao.silva@email.com");
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar usuário inexistente")
    void deveLancarExcecaoAoBuscarUsuarioInexistente() {
        // Given
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> userService.buscarPorId(999L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Usuário não encontrado com o ID: 999");
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar com ID nulo")
    void deveLancarExcecaoAoBuscarComIdNulo() {
        // When & Then
        assertThatThrownBy(() -> userService.buscarPorId(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("ID do usuário deve ser um número positivo");
    }

    @Test
    @DisplayName("Deve listar usuários sem medidas")
    void deveListarUsuariosSemMedidas() {
        // Given
        when(userRepository.findUsersWithoutMeasures()).thenReturn(Arrays.asList(usuarioTeste));

        // When
        List<UsuarioSemMedidasResponse> resultado = userService.listarUsuariosSemMedidas();

        // Then
        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).name()).isEqualTo("João Silva");
        verify(userRepository, times(1)).findUsersWithoutMeasures();
    }

    @Test
    @DisplayName("Deve trazer usuário com todas as medidas")
    void deveTrazerUsuarioComTodasMedidas() {
        // Given
        Measure medida1 = Measure.builder()
                .id(1L)
                .measurementDate(LocalDateTime.of(2024, 1, 10, 10, 0))
                .weightKg(75.0)
                .heightCm(175.0)
                .build();

        Measure medida2 = Measure.builder()
                .id(2L)
                .measurementDate(LocalDateTime.of(2024, 1, 20, 10, 0))
                .weightKg(76.0)
                .heightCm(175.0)
                .build();

        usuarioTeste.setMeasures(Arrays.asList(medida1, medida2));
        when(userRepository.findById(1L)).thenReturn(Optional.of(usuarioTeste));

        // When
        UsuarioResponse resultado = userService.trazerUsuarioPorIdComTodasAsMedidas(1L);

        // Then
        assertThat(resultado).isNotNull();
        assertThat(resultado.medidas()).hasSize(2);
        assertThat(resultado.name()).isEqualTo("João Silva");
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar usuário sem medidas para trazer todas")
    void deveLancarExcecaoAoBuscarUsuarioSemMedidasParaTrazerTodas() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(usuarioTeste));

        // When & Then
        assertThatThrownBy(() -> userService.trazerUsuarioPorIdComTodasAsMedidas(1L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Nenhuma medida encontrada");
    }

    @Test
    @DisplayName("Deve trazer usuário com última medida")
    void deveTrazerUsuarioComUltimaMedida() {
        // Given
        Measure medidaAntiga = Measure.builder()
                .id(1L)
                .measurementDate(LocalDateTime.of(2024, 1, 10, 10, 0))
                .weightKg(75.0)
                .heightCm(175.0)
                .build();

        Measure medidaRecente = Measure.builder()
                .id(2L)
                .measurementDate(LocalDateTime.of(2024, 1, 20, 10, 0))
                .weightKg(76.0)
                .heightCm(175.0)
                .build();

        usuarioTeste.setMeasures(Arrays.asList(medidaAntiga, medidaRecente));
        when(userRepository.findById(1L)).thenReturn(Optional.of(usuarioTeste));

        // When
        UsuarioResponse resultado = userService.trazerUsuarioPorIdComUltimaMedida(1L);

        // Then
        assertThat(resultado).isNotNull();
        assertThat(resultado.medidas()).hasSize(1);
        assertThat(resultado.medidas().get(0).getWeightKg()).isEqualTo(76.0);
    }

    @Test
    @DisplayName("Deve atualizar usuário completamente")
    void deveAtualizarUsuarioCompletamente() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(usuarioTeste));
        when(passwordEncoder.encode(anyString())).thenReturn("novaSenhaHasheada");
        when(userRepository.save(any(User.class))).thenReturn(usuarioTeste);

        // When
        userService.atualizarUsuario(1L, userRequest);

        // Then
        verify(userRepository, times(1)).findById(1L);
        verify(passwordEncoder, times(1)).encode("senha123");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Deve atualizar usuário parcialmente")
    void deveAtualizarUsuarioParcialmente() {
        // Given
        UserPatchRequest patchRequest = new UserPatchRequest(
                "João Silva Atualizado",
                null,
                null,
                "novo.email@email.com",
                null
        );

        when(userRepository.findById(1L)).thenReturn(Optional.of(usuarioTeste));
        when(userRepository.save(any(User.class))).thenReturn(usuarioTeste);

        // When
        userService.atualizarParcialUsuario(1L, patchRequest);

        // Then
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Deve remover usuário")
    void deveRemoverUsuario() {
        // Given
        when(userRepository.existsById(1L)).thenReturn(true);
        doNothing().when(userRepository).deleteById(1L);

        // When
        userService.removerUsuario(1L);

        // Then
        verify(userRepository, times(1)).existsById(1L);
        verify(userRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Deve lançar exceção ao remover usuário inexistente")
    void deveLancarExcecaoAoRemoverUsuarioInexistente() {
        // Given
        when(userRepository.existsById(999L)).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> userService.removerUsuario(999L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Usuário não encontrado com o ID: 999");
    }
}