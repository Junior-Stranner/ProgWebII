package com.ProgWebII.biotrack.service;

import com.ProgWebII.biotrack.dto.request.MeasureRequest;
import com.ProgWebII.biotrack.dto.response.MedidaResponse;
import com.ProgWebII.biotrack.model.Measure;
import com.ProgWebII.biotrack.model.User;
import com.ProgWebII.biotrack.repository.MeasureRepository;
import com.ProgWebII.biotrack.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Testes unitários para MeasureService.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Testes Unitários - MeasureService")
class MeasureServiceTest {

    @Mock
    private MeasureRepository measureRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private MeasureService measureService;

    private User usuarioTeste;
    private Measure medidaTeste;
    private MeasureRequest measureRequest;

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

        medidaTeste = Measure.builder()
                .id(1L)
                .measurementDate(LocalDateTime.of(2024, 1, 15, 10, 30))
                .weightKg(75.5)
                .heightCm(175.0)
                .waistCm(85.0)
                .hipCm(95.0)
                .chestCm(100.0)
                .armRightCm(32.0)
                .armLeftCm(31.5)
                .thighRightCm(58.0)
                .thighLeftCm(57.5)
                .bodyFatPercentage(18.5)
                .user(usuarioTeste)
                .build();

        measureRequest = new MeasureRequest(
                LocalDateTime.of(2024, 1, 15, 10, 30),
                75.5,
                175.0,
                85.0,
                95.0,
                100.0,
                32.0,
                31.5,
                58.0,
                57.5,
                18.5
        );
    }

    @Test
    @DisplayName("Deve criar medida com sucesso")
    void deveCriarMedidaComSucesso() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(usuarioTeste));
        when(measureRepository.save(any(Measure.class))).thenReturn(medidaTeste);

        // When
        measureService.CreateMeasure(measureRequest, 1L);

        // Then
        verify(userRepository, times(1)).findById(1L);
        verify(measureRepository, times(1)).save(any(Measure.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao criar medida para usuário inexistente")
    void deveLancarExcecaoAoCriarMedidaParaUsuarioInexistente() {
        // Given
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> measureService.CreateMeasure(measureRequest, 999L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Usuário não encontrado");
    }

    @Test
    @DisplayName("Deve listar todas as medidas de um usuário")
    void deveListarTodasMedidasDeUsuario() {
        // Given
        Measure medida2 = Measure.builder()
                .id(2L)
                .measurementDate(LocalDateTime.of(2024, 1, 20, 10, 0))
                .weightKg(76.0)
                .heightCm(175.0)
                .build();

        usuarioTeste.setMeasures(Arrays.asList(medidaTeste, medida2));
        when(userRepository.findById(1L)).thenReturn(Optional.of(usuarioTeste));

        // When
        List<MedidaResponse> resultado = measureService.listarTodasAsMedidasDeUmUsuario(1L);

        // Then
        assertThat(resultado).hasSize(2);
        assertThat(resultado.get(0).getWeightKg()).isEqualTo(75.5);
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Deve lançar exceção ao listar medidas de usuário inexistente")
    void deveLancarExcecaoAoListarMedidasDeUsuarioInexistente() {
        // Given
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> measureService.listarTodasAsMedidasDeUmUsuario(999L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Usuário não encontrado");
    }

    @Test
    @DisplayName("Deve buscar medida específica por ID")
    void deveBuscarMedidaEspecificaPorId() {
        // Given
        usuarioTeste.setMeasures(Arrays.asList(medidaTeste));
        when(userRepository.findById(1L)).thenReturn(Optional.of(usuarioTeste));

        // When
        MedidaResponse resultado = measureService.buscarMedidaPorId(1L, 1L);

        // Then
        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo(1L);
        assertThat(resultado.getWeightKg()).isEqualTo(75.5);
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar medida inexistente")
    void deveLancarExcecaoAoBuscarMedidaInexistente() {
        // Given
        usuarioTeste.setMeasures(Arrays.asList(medidaTeste));
        when(userRepository.findById(1L)).thenReturn(Optional.of(usuarioTeste));

        // When & Then
        assertThatThrownBy(() -> measureService.buscarMedidaPorId(1L, 999L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Medida não encontrada para este usuário");
    }

    @Test
    @DisplayName("Deve atualizar medida com sucesso")
    void deveAtualizarMedidaComSucesso() {
        // Given
        MeasureRequest novasMedidas = new MeasureRequest(
                LocalDateTime.of(2024, 2, 15, 10, 30),
                80.0,
                175.0,
                90.0,
                100.0,
                105.0,
                35.0,
                34.5,
                60.0,
                59.5,
                20.0
        );

        when(measureRepository.findById(1L)).thenReturn(Optional.of(medidaTeste));
        when(measureRepository.save(any(Measure.class))).thenReturn(medidaTeste);

        // When
        measureService.atualizarMedida(1L, novasMedidas);

        // Then
        verify(measureRepository, times(1)).findById(1L);
        verify(measureRepository, times(1)).save(any(Measure.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar medida inexistente")
    void deveLancarExcecaoAoAtualizarMedidaInexistente() {
        // Given
        when(measureRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> measureService.atualizarMedida(999L, measureRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Medida não encontrada com o ID: 999");
    }

    @Test
    @DisplayName("Deve remover medida com sucesso")
    void deveRemoverMedidaComSucesso() {
        // Given
        when(measureRepository.existsById(1L)).thenReturn(true);
        doNothing().when(measureRepository).deleteById(1L);

        // When
        measureService.removerMedida(1L);

        // Then
        verify(measureRepository, times(1)).existsById(1L);
        verify(measureRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Deve lançar exceção ao remover medida inexistente")
    void deveLancarExcecaoAoRemoverMedidaInexistente() {
        // Given
        when(measureRepository.existsById(999L)).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> measureService.removerMedida(999L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Medida não encontrada com o ID: 999");
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando usuário não tem medidas")
    void deveRetornarListaVaziaQuandoUsuarioNaoTemMedidas() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(usuarioTeste));

        // When
        List<MedidaResponse> resultado = measureService.listarTodasAsMedidasDeUmUsuario(1L);

        // Then
        assertThat(resultado).isEmpty();
    }
}