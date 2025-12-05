package com.ProgWebII.biotrack.controller;

import com.ProgWebII.biotrack.dto.request.MeasureRequest;
import com.ProgWebII.biotrack.dto.response.MedidaResponse;
import com.ProgWebII.biotrack.service.MeasureService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Testes de Controller para MeasureController.
 */
@WebMvcTest(MeasureController.class)
@DisplayName("Testes de Controller - MeasureController")
class MeasureControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private MeasureService measureService;

    private MeasureRequest measureRequest;
    private MedidaResponse medidaResponse;

    @BeforeEach
    void setUp() {
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

        medidaResponse = new MedidaResponse(
                1L,
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
    @DisplayName("POST /medidas/{userId} - Deve criar medida com sucesso")
    void deveCriarMedidaComSucesso() throws Exception {
        // Given
        doNothing().when(measureService).CreateMeasure(any(MeasureRequest.class), eq(1L));

        // When & Then
        mockMvc.perform(post("/medidas/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(measureRequest)))
                .andExpect(status().isOk())
                .andExpect(content().string("Medida criada com sucesso!"));

        verify(measureService, times(1)).CreateMeasure(any(MeasureRequest.class), eq(1L));
    }

    @Test
    @DisplayName("POST /medidas/{userId} - Deve retornar erro com peso inválido")
    void deveRetornarErroComPesoInvalido() throws Exception {
        // Given - Peso negativo (inválido)
        MeasureRequest requestInvalido = new MeasureRequest(
                LocalDateTime.of(2024, 1, 15, 10, 30),
                -75.5,  // peso negativo
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

        // When & Then
        mockMvc.perform(post("/medidas/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestInvalido)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /medidas/{userId} - Deve validar data obrigatória")
    void deveValidarDataObrigatoria() throws Exception {
        // Given - Sem data de medição
        MeasureRequest requestSemData = new MeasureRequest(
                null,  // data nula
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

        // When & Then
        mockMvc.perform(post("/medidas/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestSemData)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /medidas/{userId} - Deve validar peso obrigatório")
    void deveValidarPesoObrigatorio() throws Exception {
        // Given - Sem peso
        MeasureRequest requestSemPeso = new MeasureRequest(
                LocalDateTime.of(2024, 1, 15, 10, 30),
                null,  // peso nulo
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

        // When & Then
        mockMvc.perform(post("/medidas/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestSemPeso)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /medidas/{usuarioId}/medidas - Deve listar todas as medidas")
    void deveListarTodasMedidas() throws Exception {
        // Given
        when(measureService.listarTodasAsMedidasDeUmUsuario(1L))
                .thenReturn(Arrays.asList(medidaResponse));

        // When & Then
        mockMvc.perform(get("/medidas/1/medidas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].weightKg").value(75.5))
                .andExpect(jsonPath("$[0].heightCm").value(175.0));

        verify(measureService, times(1)).listarTodasAsMedidasDeUmUsuario(1L);
    }

    @Test
    @DisplayName("GET /medidas/{usuarioId}/medidas - Deve retornar lista vazia quando não há medidas")
    void deveRetornarListaVaziaQuandoNaoHaMedidas() throws Exception {
        // Given
        when(measureService.listarTodasAsMedidasDeUmUsuario(1L))
                .thenReturn(Collections.emptyList());

        // When & Then
        mockMvc.perform(get("/medidas/1/medidas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    @DisplayName("GET /medidas/{usuarioId}/medidas/{medidaId} - Deve buscar medida específica")
    void deveBuscarMedidaEspecifica() throws Exception {
        // Given
        when(measureService.buscarMedidaPorId(1L, 1L)).thenReturn(medidaResponse);

        // When & Then
        mockMvc.perform(get("/medidas/1/medidas/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.weightKg").value(75.5))
                .andExpect(jsonPath("$.heightCm").value(175.0));

        verify(measureService, times(1)).buscarMedidaPorId(1L, 1L);
    }

    @Test
    @DisplayName("GET /medidas/{usuarioId}/medidas/{medidaId} - Deve retornar 404 para medida inexistente")
    void deveRetornar404ParaMedidaInexistente() throws Exception {
        // Given
        when(measureService.buscarMedidaPorId(1L, 999L))
                .thenThrow(new EntityNotFoundException("Medida não encontrada"));

        // When & Then
        mockMvc.perform(get("/medidas/1/medidas/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("PUT /medidas/{medidaId} - Deve atualizar medida com sucesso")
    void deveAtualizarMedidaComSucesso() throws Exception {
        // Given
        doNothing().when(measureService).atualizarMedida(eq(1L), any(MeasureRequest.class));

        // When & Then
        mockMvc.perform(put("/medidas/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(measureRequest)))
                .andExpect(status().isOk())
                .andExpect(content().string("Medida atualizada com sucesso!"));

        verify(measureService, times(1)).atualizarMedida(eq(1L), any(MeasureRequest.class));
    }

    @Test
    @DisplayName("PUT /medidas/{medidaId} - Deve retornar erro ao atualizar com dados inválidos")
    void deveRetornarErroAoAtualizarComDadosInvalidos() throws Exception {
        // Given - Altura negativa
        MeasureRequest requestInvalido = new MeasureRequest(
                LocalDateTime.of(2024, 1, 15, 10, 30),
                75.5,
                -175.0,  // altura negativa
                85.0,
                95.0,
                100.0,
                32.0,
                31.5,
                58.0,
                57.5,
                18.5
        );

        // When & Then
        mockMvc.perform(put("/medidas/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestInvalido)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("DELETE /medidas/{medidaId} - Deve remover medida com sucesso")
    void deveRemoverMedidaComSucesso() throws Exception {
        // Given
        doNothing().when(measureService).removerMedida(1L);

        // When & Then
        mockMvc.perform(delete("/medidas/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Medida removida com sucesso!"));

        verify(measureService, times(1)).removerMedida(1L);
    }

    @Test
    @DisplayName("DELETE /medidas/{medidaId} - Deve retornar erro ao remover medida inexistente")
    void deveRetornarErroAoRemoverMedidaInexistente() throws Exception {
        // Given
        doThrow(new RuntimeException("Medida não encontrada"))
                .when(measureService).removerMedida(999L);

        // When & Then
        mockMvc.perform(delete("/medidas/999"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @DisplayName("POST /medidas/{userId} - Deve aceitar medida com campos opcionais nulos")
    void deveAceitarMedidaComCamposOpcionaisNulos() throws Exception {
        // Given - Apenas campos obrigatórios
        MeasureRequest requestMinimo = new MeasureRequest(
                LocalDateTime.of(2024, 1, 15, 10, 30),
                75.5,
                175.0,
                null,  // campos opcionais nulos
                null,
                null,
                null,
                null,
                null,
                null,
                null
        );

        doNothing().when(measureService).CreateMeasure(any(MeasureRequest.class), eq(1L));

        // When & Then
        mockMvc.perform(post("/medidas/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestMinimo)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /medidas/{usuarioId}/medidas - Deve retornar erro para usuário inexistente")
    void deveRetornarErroParaUsuarioInexistente() throws Exception {
        // Given
        when(measureService.listarTodasAsMedidasDeUmUsuario(999L))
                .thenThrow(new EntityNotFoundException("Usuário não encontrado"));

        // When & Then
        mockMvc.perform(get("/medidas/999/medidas"))
                .andExpect(status().isNotFound());
    }
}