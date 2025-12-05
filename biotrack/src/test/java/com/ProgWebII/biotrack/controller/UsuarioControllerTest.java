package com.ProgWebII.biotrack.controller;

import com.ProgWebII.biotrack.dto.request.UserPatchRequest;
import com.ProgWebII.biotrack.dto.request.UserRequest;
import com.ProgWebII.biotrack.dto.response.*;
import com.ProgWebII.biotrack.mapper.UsuarioMapper;
import com.ProgWebII.biotrack.model.Imc;
import com.ProgWebII.biotrack.repository.UserRepository;
import com.ProgWebII.biotrack.service.UserService;
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

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Testes de Controller para UsuarioController.
 * Usa @WebMvcTest para testar apenas a camada web.
 */
@WebMvcTest(UsuarioController.class)
@DisplayName("Testes de Controller - UsuarioController")
class UsuarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private Imc imc;

    @MockBean
    private UsuarioMapper usuarioMapper;

    private UserRequest userRequest;
    private ListarTodosUsuariosResponse usuarioResponse;

    @BeforeEach
    void setUp() {
        userRequest = new UserRequest(
                "João Silva",
                LocalDate.of(1990, 5, 15),
                "12345-678",
                "joao.silva@email.com",
                "Senha123"
        );

        usuarioResponse = new ListarTodosUsuariosResponse(
                1L,
                "João Silva",
                LocalDate.of(1990, 5, 15),
                "12345-678",
                "joao.silva@email.com"
        );
    }

    @Test
    @DisplayName("POST /usuarios - Deve criar usuário com sucesso")
    void deveCriarUsuarioComSucesso() throws Exception {
        // Given
        doNothing().when(userService).createUser(any(UserRequest.class));

        // When & Then
        mockMvc.perform(post("/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequest)))
                .andExpect(status().isOk())
                .andExpect(content().string("Usuário criado com sucesso!"));

        verify(userService, times(1)).createUser(any(UserRequest.class));
    }

    @Test
    @DisplayName("POST /usuarios - Deve retornar erro com dados inválidos")
    void deveRetornarErroComDadosInvalidos() throws Exception {
        // Given - Request sem nome (campo obrigatório)
        UserRequest requestInvalido = new UserRequest(
                "",  // nome vazio
                LocalDate.of(1990, 5, 15),
                "12345-678",
                "joao.silva@email.com",
                "Senha123"
        );

        // When & Then
        mockMvc.perform(post("/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestInvalido)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /usuarios - Deve listar todos os usuários")
    void deveListarTodosUsuarios() throws Exception {
        // Given
        when(userService.listarTodos())
                .thenReturn(Arrays.asList(usuarioResponse));

        // When & Then
        mockMvc.perform(get("/usuarios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("João Silva"))
                .andExpect(jsonPath("$[0].email").value("joao.silva@email.com"));

        verify(userService, times(1)).listarTodos();
    }

    @Test
    @DisplayName("GET /usuarios/{id} - Deve buscar usuário por ID")
    void deveBuscarUsuarioPorId() throws Exception {
        // Given
        BuscarUsuarioPorIdResponse response = new BuscarUsuarioPorIdResponse(
                1L,
                "João Silva",
                LocalDate.of(1990, 5, 15),
                "12345-678",
                "joao.silva@email.com"
        );
        when(userService.buscarPorId(1L)).thenReturn(response);

        // When & Then
        mockMvc.perform(get("/usuarios/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("João Silva"))
                .andExpect(jsonPath("$.email").value("joao.silva@email.com"));

        verify(userService, times(1)).buscarPorId(1L);
    }

    @Test
    @DisplayName("GET /usuarios/{id} - Deve retornar 404 para usuário inexistente")
    void deveRetornar404ParaUsuarioInexistente() throws Exception {
        // Given
        when(userService.buscarPorId(999L))
                .thenThrow(new EntityNotFoundException("Usuário não encontrado"));

        // When & Then
        mockMvc.perform(get("/usuarios/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /usuarios/sem-medidas - Deve listar usuários sem medidas")
    void deveListarUsuariosSemMedidas() throws Exception {
        // Given
        UsuarioSemMedidasResponse response = new UsuarioSemMedidasResponse(
                1L,
                "João Silva",
                LocalDate.of(1990, 5, 15),
                "12345-678",
                "joao.silva@email.com"
        );
        when(userService.listarUsuariosSemMedidas())
                .thenReturn(Arrays.asList(response));

        // When & Then
        mockMvc.perform(get("/usuarios/sem-medidas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("João Silva"));

        verify(userService, times(1)).listarUsuariosSemMedidas();
    }

    @Test
    @DisplayName("GET /usuarios/{id}/todas-medidas - Deve buscar usuário com todas medidas")
    void deveBuscarUsuarioComTodasMedidas() throws Exception {
        // Given
        UsuarioResponse response = new UsuarioResponse(
                1L,
                "João Silva",
                LocalDate.of(1990, 5, 15),
                "12345-678",
                "joao.silva@email.com",
                Collections.emptyList()
        );
        when(userService.trazerUsuarioPorIdComTodasAsMedidas(1L)).thenReturn(response);

        // When & Then
        mockMvc.perform(get("/usuarios/1/todas-medidas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("João Silva"));

        verify(userService, times(1)).trazerUsuarioPorIdComTodasAsMedidas(1L);
    }

    @Test
    @DisplayName("GET /usuarios/{id}/ultima-medida - Deve buscar usuário com última medida")
    void deveBuscarUsuarioComUltimaMedida() throws Exception {
        // Given
        UsuarioResponse response = new UsuarioResponse(
                1L,
                "João Silva",
                LocalDate.of(1990, 5, 15),
                "12345-678",
                "joao.silva@email.com",
                Collections.emptyList()
        );
        when(userService.trazerUsuarioPorIdComUltimaMedida(1L)).thenReturn(response);

        // When & Then
        mockMvc.perform(get("/usuarios/1/ultima-medida"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("João Silva"));

        verify(userService, times(1)).trazerUsuarioPorIdComUltimaMedida(1L);
    }

    @Test
    @DisplayName("PUT /usuarios/{id} - Deve atualizar usuário completamente")
    void deveAtualizarUsuarioCompletamente() throws Exception {
        // Given
        doNothing().when(userService).atualizarUsuario(eq(1L), any(UserRequest.class));

        // When & Then
        mockMvc.perform(put("/usuarios/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequest)))
                .andExpect(status().isOk())
                .andExpect(content().string("Usuário atualizado com sucesso!"));

        verify(userService, times(1)).atualizarUsuario(eq(1L), any(UserRequest.class));
    }

    @Test
    @DisplayName("PATCH /usuarios/{id} - Deve atualizar usuário parcialmente")
    void deveAtualizarUsuarioParcialmente() throws Exception {
        // Given
        UserPatchRequest patchRequest = new UserPatchRequest(
                "João Silva Atualizado",
                null,
                null,
                null,
                null
        );
        doNothing().when(userService).atualizarParcialUsuario(eq(1L), any(UserPatchRequest.class));

        // When & Then
        mockMvc.perform(patch("/usuarios/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(patchRequest)))
                .andExpect(status().isOk())
                .andExpect(content().string("Usuário atualizado parcialmente com sucesso!"));

        verify(userService, times(1)).atualizarParcialUsuario(eq(1L), any(UserPatchRequest.class));
    }

    @Test
    @DisplayName("DELETE /usuarios/{id} - Deve remover usuário")
    void deveRemoverUsuario() throws Exception {
        // Given
        doNothing().when(userService).removerUsuario(1L);

        // When & Then
        mockMvc.perform(delete("/usuarios/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Usuário removido com sucesso!"));

        verify(userService, times(1)).removerUsuario(1L);
    }

    @Test
    @DisplayName("GET /usuarios/filtro-imc - Deve filtrar usuários por IMC")
    void deveFiltrarUsuariosPorImc() throws Exception {
        // Given
        UsuarioResponse response = new UsuarioResponse(
                1L,
                "João Silva",
                LocalDate.of(1990, 5, 15),
                "12345-678",
                "joao.silva@email.com",
                Collections.emptyList()
        );

        when(userRepository.findAll()).thenReturn(Collections.emptyList());
        when(usuarioMapper.toResponse(any())).thenReturn(response);

        // When & Then
        mockMvc.perform(get("/usuarios/filtro-imc")
                        .param("faixa", "Peso Normal"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("POST /usuarios - Deve validar campo obrigatório email")
    void deveValidarCampoObrigatorioEmail() throws Exception {
        // Given - Request sem email
        UserRequest requestSemEmail = new UserRequest(
                "João Silva",
                LocalDate.of(1990, 5, 15),
                "12345-678",
                "",  // email vazio
                "Senha123"
        );

        // When & Then
        mockMvc.perform(post("/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestSemEmail)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /usuarios - Deve validar formato de senha")
    void deveValidarFormatoDeSenha() throws Exception {
        // Given - Senha sem número
        UserRequest requestSenhaInvalida = new UserRequest(
                "João Silva",
                LocalDate.of(1990, 5, 15),
                "12345-678",
                "joao.silva@email.com",
                "senha"  // senha sem número
        );

        // When & Then
        mockMvc.perform(post("/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestSenhaInvalida)))
                .andExpect(status().isBadRequest());
    }
}