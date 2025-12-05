package com.ProgWebII.biotrack.repository;

import com.ProgWebII.biotrack.model.User;
import com.ProgWebII.biotrack.model.Measure;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Testes de integração para UserRepository.
 * Usa @DataJpaTest para configurar um contexto de teste com banco H2 em memória.
 */
@DataJpaTest
@DisplayName("Testes de Integração - UserRepository")
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestEntityManager entityManager;

    private User usuarioTeste;

    @BeforeEach
    void setUp() {
        // Limpa o banco antes de cada teste
        userRepository.deleteAll();

        // Cria um usuário de teste
        usuarioTeste = User.builder()
                .name("João Silva")
                .birthDate(LocalDate.of(1990, 5, 15))
                .zipCode("12345-678")
                .email("joao.silva@email.com")
                .password("senhaHasheada123")
                .build();
    }

    @Test
    @DisplayName("Deve salvar um usuário com sucesso")
    void deveSalvarUsuarioComSucesso() {
        // When
        User usuarioSalvo = userRepository.save(usuarioTeste);

        // Then
        assertThat(usuarioSalvo).isNotNull();
        assertThat(usuarioSalvo.getId()).isNotNull();
        assertThat(usuarioSalvo.getName()).isEqualTo("João Silva");
        assertThat(usuarioSalvo.getEmail()).isEqualTo("joao.silva@email.com");
    }

    @Test
    @DisplayName("Deve buscar usuário por ID")
    void deveBuscarUsuarioPorId() {
        // Given
        User usuarioSalvo = entityManager.persistAndFlush(usuarioTeste);

        // When
        Optional<User> resultado = userRepository.findById(usuarioSalvo.getId());

        // Then
        assertThat(resultado).isPresent();
        assertThat(resultado.get().getName()).isEqualTo("João Silva");
    }

    @Test
    @DisplayName("Deve retornar vazio ao buscar usuário inexistente")
    void deveRetornarVazioAoBuscarUsuarioInexistente() {
        // When
        Optional<User> resultado = userRepository.findById(999L);

        // Then
        assertThat(resultado).isEmpty();
    }

    @Test
    @DisplayName("Deve listar todos os usuários")
    void deveListarTodosUsuarios() {
        // Given
        User usuario2 = User.builder()
                .name("Maria Santos")
                .birthDate(LocalDate.of(1992, 8, 20))
                .zipCode("98765-432")
                .email("maria.santos@email.com")
                .password("senhaHasheada456")
                .build();

        entityManager.persist(usuarioTeste);
        entityManager.persist(usuario2);
        entityManager.flush();

        // When
        List<User> usuarios = userRepository.findAll();

        // Then
        assertThat(usuarios).hasSize(2);
        assertThat(usuarios).extracting(User::getName)
                .containsExactlyInAnyOrder("João Silva", "Maria Santos");
    }

    @Test
    @DisplayName("Deve deletar um usuário")
    void deveDeletarUsuario() {
        // Given
        User usuarioSalvo = entityManager.persistAndFlush(usuarioTeste);
        Long id = usuarioSalvo.getId();

        // When
        userRepository.deleteById(id);
        Optional<User> resultado = userRepository.findById(id);

        // Then
        assertThat(resultado).isEmpty();
    }

    @Test
    @DisplayName("Deve atualizar um usuário")
    void deveAtualizarUsuario() {
        // Given
        User usuarioSalvo = entityManager.persistAndFlush(usuarioTeste);

        // When
        usuarioSalvo.setName("João Silva Atualizado");
        usuarioSalvo.setEmail("joao.atualizado@email.com");
        User usuarioAtualizado = userRepository.save(usuarioSalvo);

        // Then
        assertThat(usuarioAtualizado.getName()).isEqualTo("João Silva Atualizado");
        assertThat(usuarioAtualizado.getEmail()).isEqualTo("joao.atualizado@email.com");
    }

    @Test
    @DisplayName("Deve buscar usuários sem medidas")
    void deveBuscarUsuariosSemMedidas() {
        // Given - Usuário sem medidas
        entityManager.persist(usuarioTeste);

        // Usuário com medidas
        User usuarioComMedida = User.builder()
                .name("Maria Santos")
                .birthDate(LocalDate.of(1992, 8, 20))
                .zipCode("98765-432")
                .email("maria.santos@email.com")
                .password("senhaHasheada456")
                .build();
        usuarioComMedida = entityManager.persist(usuarioComMedida);

        Measure medida = Measure.builder()
                .measurementDate(LocalDateTime.now())
                .weightKg(70.0)
                .heightCm(170.0)
                .user(usuarioComMedida)
                .build();
        entityManager.persist(medida);
        entityManager.flush();

        // When
        List<User> usuariosSemMedidas = userRepository.findUsersWithoutMeasures();

        // Then
        assertThat(usuariosSemMedidas).hasSize(1);
        assertThat(usuariosSemMedidas.get(0).getName()).isEqualTo("João Silva");
    }

    @Test
    @DisplayName("Deve contar usuários corretamente")
    void deveContarUsuariosCorretamente() {
        // Given
        entityManager.persist(usuarioTeste);
        User usuario2 = User.builder()
                .name("Maria Santos")
                .birthDate(LocalDate.of(1992, 8, 20))
                .zipCode("98765-432")
                .email("maria.santos@email.com")
                .password("senhaHasheada456")
                .build();
        entityManager.persist(usuario2);
        entityManager.flush();

        // When
        long count = userRepository.count();

        // Then
        assertThat(count).isEqualTo(2);
    }

    @Test
    @DisplayName("Deve verificar se usuário existe por ID")
    void deveVerificarSeUsuarioExistePorId() {
        // Given
        User usuarioSalvo = entityManager.persistAndFlush(usuarioTeste);

        // When
        boolean existe = userRepository.existsById(usuarioSalvo.getId());
        boolean naoExiste = userRepository.existsById(999L);

        // Then
        assertThat(existe).isTrue();
        assertThat(naoExiste).isFalse();
    }
}