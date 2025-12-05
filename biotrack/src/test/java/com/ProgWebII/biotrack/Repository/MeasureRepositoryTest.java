package com.ProgWebII.biotrack.repository;

import com.ProgWebII.biotrack.model.Measure;
import com.ProgWebII.biotrack.model.User;
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
 * Testes de integração para MeasureRepository.
 */
@DataJpaTest
@DisplayName("Testes de Integração - MeasureRepository")
class MeasureRepositoryTest {

    @Autowired
    private MeasureRepository measureRepository;

    @Autowired
    private TestEntityManager entityManager;

    private User usuarioTeste;
    private Measure medidaTeste;

    @BeforeEach
    void setUp() {
        // Limpa o banco antes de cada teste
        measureRepository.deleteAll();

        // Cria um usuário de teste
        usuarioTeste = User.builder()
                .name("João Silva")
                .birthDate(LocalDate.of(1990, 5, 15))
                .zipCode("12345-678")
                .email("joao.silva@email.com")
                .password("senhaHasheada123")
                .build();
        usuarioTeste = entityManager.persistAndFlush(usuarioTeste);

        // Cria uma medida de teste
        medidaTeste = Measure.builder()
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
    }

    @Test
    @DisplayName("Deve salvar uma medida com sucesso")
    void deveSalvarMedidaComSucesso() {
        // When
        Measure medidaSalva = measureRepository.save(medidaTeste);

        // Then
        assertThat(medidaSalva).isNotNull();
        assertThat(medidaSalva.getId()).isNotNull();
        assertThat(medidaSalva.getWeightKg()).isEqualTo(75.5);
        assertThat(medidaSalva.getUser()).isEqualTo(usuarioTeste);
    }

    @Test
    @DisplayName("Deve buscar medida por ID")
    void deveBuscarMedidaPorId() {
        // Given
        Measure medidaSalva = entityManager.persistAndFlush(medidaTeste);

        // When
        Optional<Measure> resultado = measureRepository.findById(medidaSalva.getId());

        // Then
        assertThat(resultado).isPresent();
        assertThat(resultado.get().getWeightKg()).isEqualTo(75.5);
    }

    @Test
    @DisplayName("Deve buscar medidas por usuário ordenadas por data decrescente")
    void deveBuscarMedidasPorUsuarioOrdenadasPorData() {
        // Given
        Measure medida1 = Measure.builder()
                .measurementDate(LocalDateTime.of(2024, 1, 10, 10, 0))
                .weightKg(75.0)
                .heightCm(175.0)
                .user(usuarioTeste)
                .build();

        Measure medida2 = Measure.builder()
                .measurementDate(LocalDateTime.of(2024, 1, 20, 10, 0))
                .weightKg(76.0)
                .heightCm(175.0)
                .user(usuarioTeste)
                .build();

        Measure medida3 = Measure.builder()
                .measurementDate(LocalDateTime.of(2024, 1, 15, 10, 0))
                .weightKg(75.5)
                .heightCm(175.0)
                .user(usuarioTeste)
                .build();

        entityManager.persist(medida1);
        entityManager.persist(medida2);
        entityManager.persist(medida3);
        entityManager.flush();

        // When
        List<Measure> medidas = measureRepository.findByUserIdOrderByMeasurementDateDesc(usuarioTeste.getId());

        // Then
        assertThat(medidas).hasSize(3);
        assertThat(medidas.get(0).getMeasurementDate()).isAfter(medidas.get(1).getMeasurementDate());
        assertThat(medidas.get(1).getMeasurementDate()).isAfter(medidas.get(2).getMeasurementDate());
    }

    @Test
    @DisplayName("Deve buscar a medida mais recente de um usuário")
    void deveBuscarMedidaMaisRecenteDeUsuario() {
        // Given
        Measure medidaAntiga = Measure.builder()
                .measurementDate(LocalDateTime.of(2024, 1, 10, 10, 0))
                .weightKg(75.0)
                .heightCm(175.0)
                .user(usuarioTeste)
                .build();

        Measure medidaRecente = Measure.builder()
                .measurementDate(LocalDateTime.of(2024, 1, 20, 10, 0))
                .weightKg(76.0)
                .heightCm(175.0)
                .user(usuarioTeste)
                .build();

        entityManager.persist(medidaAntiga);
        entityManager.persist(medidaRecente);
        entityManager.flush();

        // When
        Measure medidaMaisRecente = measureRepository.findTopByUserIdOrderByMeasurementDateDesc(usuarioTeste.getId());

        // Then
        assertThat(medidaMaisRecente).isNotNull();
        assertThat(medidaMaisRecente.getWeightKg()).isEqualTo(76.0);
        assertThat(medidaMaisRecente.getMeasurementDate()).isEqualTo(LocalDateTime.of(2024, 1, 20, 10, 0));
    }

    @Test
    @DisplayName("Deve retornar null ao buscar medida mais recente de usuário sem medidas")
    void deveRetornarNullAoBuscarMedidaMaisRecenteDeUsuarioSemMedidas() {
        // Given
        User usuarioSemMedidas = User.builder()
                .name("Maria Santos")
                .birthDate(LocalDate.of(1992, 8, 20))
                .zipCode("98765-432")
                .email("maria.santos@email.com")
                .password("senhaHasheada456")
                .build();
        usuarioSemMedidas = entityManager.persistAndFlush(usuarioSemMedidas);

        // When
        Measure resultado = measureRepository.findTopByUserIdOrderByMeasurementDateDesc(usuarioSemMedidas.getId());

        // Then
        assertThat(resultado).isNull();
    }

    @Test
    @DisplayName("Deve deletar uma medida")
    void deveDeletarMedida() {
        // Given
        Measure medidaSalva = entityManager.persistAndFlush(medidaTeste);
        Long id = medidaSalva.getId();

        // When
        measureRepository.deleteById(id);
        Optional<Measure> resultado = measureRepository.findById(id);

        // Then
        assertThat(resultado).isEmpty();
    }

    @Test
    @DisplayName("Deve atualizar uma medida")
    void deveAtualizarMedida() {
        // Given
        Measure medidaSalva = entityManager.persistAndFlush(medidaTeste);

        // When
        medidaSalva.setWeightKg(80.0);
        medidaSalva.setBodyFatPercentage(20.0);
        Measure medidaAtualizada = measureRepository.save(medidaSalva);

        // Then
        assertThat(medidaAtualizada.getWeightKg()).isEqualTo(80.0);
        assertThat(medidaAtualizada.getBodyFatPercentage()).isEqualTo(20.0);
    }

    @Test
    @DisplayName("Deve contar medidas corretamente")
    void deveContarMedidasCorretamente() {
        // Given
        entityManager.persist(medidaTeste);

        Measure medida2 = Measure.builder()
                .measurementDate(LocalDateTime.now())
                .weightKg(76.0)
                .heightCm(175.0)
                .user(usuarioTeste)
                .build();
        entityManager.persist(medida2);
        entityManager.flush();

        // When
        long count = measureRepository.count();

        // Then
        assertThat(count).isEqualTo(2);
    }

    @Test
    @DisplayName("Deve retornar lista vazia ao buscar medidas de usuário inexistente")
    void deveRetornarListaVaziaAoBuscarMedidasDeUsuarioInexistente() {
        // When
        List<Measure> medidas = measureRepository.findByUserIdOrderByMeasurementDateDesc(999L);

        // Then
        assertThat(medidas).isEmpty();
    }
}