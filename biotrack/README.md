# 🧪 Guia de Testes - BioTrack

## 📋 Estrutura de Testes

O projeto possui 3 camadas de testes:

### 1. **Testes de Repository** (Integração)
- **Localização:** `src/test/java/com/ProgWebII/biotrack/repository/`
- **Tipo:** Testes de integração com banco H2 em memória
- **Anotação:** `@DataJpaTest`
- **Arquivos:**
    - `UserRepositoryTest.java`
    - `MeasureRepositoryTest.java`

### 2. **Testes de Service** (Unitários)
- **Localização:** `src/test/java/com/ProgWebII/biotrack/service/`
- **Tipo:** Testes unitários com mocks
- **Anotação:** `@ExtendWith(MockitoExtension.class)`
- **Arquivos:**
    - `UserServiceTest.java`
    - `MeasureServiceTest.java`

### 3. **Testes de Controller** (Web)
- **Localização:** `src/test/java/com/ProgWebII/biotrack/controller/`
- **Tipo:** Testes de API com MockMvc
- **Anotação:** `@WebMvcTest`
- **Arquivos:**
    - `UsuarioControllerTest.java`
    - `MeasureControllerTest.java`

---

## 🚀 Como Executar os Testes

### Executar TODOS os testes:
```bash
./mvnw test
```

ou no Windows:
```bash
mvnw.cmd test
```

### Executar testes de uma classe específica:
```bash
./mvnw test -Dtest=UserServiceTest
```

### Executar testes de um pacote específico:
```bash
# Repository
./mvnw test -Dtest=com.ProgWebII.biotrack.repository.*

# Service
./mvnw test -Dtest=com.ProgWebII.biotrack.service.*

# Controller
./mvnw test -Dtest=com.ProgWebII.biotrack.controller.*
```

### Executar um único método de teste:
```bash
./mvnw test -Dtest=UserServiceTest#deveCriarUsuarioComSucesso
```

---

## 📊 Cobertura de Código com Jacoco

### Gerar relatório de cobertura:
```bash
./mvnw clean test jacoco:report
```

### Visualizar o relatório:
1. Após executar o comando acima, abra o arquivo:
   ```
   biotrack/target/site/jacoco/index.html
   ```
2. O relatório mostra:
    - Cobertura por pacote
    - Cobertura por classe
    - Linhas cobertas/não cobertas
    - Branches cobertos/não cobertos

### Verificar se a cobertura está acima de 80%:
```bash
./mvnw verify
```

Este comando irá **falhar** se a cobertura de qualquer pacote (service, controller, repository) estiver abaixo de 80%.

---

## 📈 Cobertura Exigida

O projeto está configurado para exigir **mínimo de 80% de cobertura** nos seguintes pacotes:

- ✅ `com.ProgWebII.biotrack.service`
- ✅ `com.ProgWebII.biotrack.controller`
- ✅ `com.ProgWebII.biotrack.repository`

**Pacotes excluídos** da verificação de cobertura:
- `config` (configurações)
- `dto` (objetos de transferência)
- `model` (entidades)
- `BiotrackApplication` (classe principal)

---

## 🔍 Detalhes dos Testes

### Repository Tests (Integração)

**UserRepositoryTest:**
- ✅ Salvar usuário
- ✅ Buscar por ID
- ✅ Listar todos
- ✅ Deletar usuário
- ✅ Atualizar usuário
- ✅ Buscar usuários sem medidas
- ✅ Contar usuários
- ✅ Verificar existência

**MeasureRepositoryTest:**
- ✅ Salvar medida
- ✅ Buscar por ID
- ✅ Buscar por usuário ordenadas por data
- ✅ Buscar medida mais recente
- ✅ Deletar medida
- ✅ Atualizar medida
- ✅ Contar medidas

### Service Tests (Unitários)

**UserServiceTest:**
- ✅ Criar usuário
- ✅ Listar usuários
- ✅ Buscar por ID
- ✅ Listar usuários sem medidas
- ✅ Buscar com todas as medidas
- ✅ Buscar com última medida
- ✅ Atualizar completamente
- ✅ Atualizar parcialmente
- ✅ Remover usuário
- ✅ Validações de campos
- ✅ Tratamento de exceções

**MeasureServiceTest:**
- ✅ Criar medida
- ✅ Listar medidas de usuário
- ✅ Buscar medida específica
- ✅ Atualizar medida
- ✅ Remover medida
- ✅ Validações de campos
- ✅ Tratamento de exceções

### Controller Tests (Web)

**UsuarioControllerTest:**
- ✅ POST - Criar usuário
- ✅ GET - Listar todos
- ✅ GET - Buscar por ID
- ✅ GET - Listar sem medidas
- ✅ GET - Buscar com todas medidas
- ✅ GET - Buscar com última medida
- ✅ PUT - Atualizar completamente
- ✅ PATCH - Atualizar parcialmente
- ✅ DELETE - Remover usuário
- ✅ GET - Filtrar por IMC
- ✅ Validação de parâmetros obrigatórios
- ✅ Validação de formatos

**MeasureControllerTest:**
- ✅ POST - Criar medida
- ✅ GET - Listar todas medidas
- ✅ GET - Buscar medida específica
- ✅ PUT - Atualizar medida
- ✅ DELETE - Remover medida
- ✅ Validação de parâmetros obrigatórios
- ✅ Validação de valores positivos
- ✅ Campos opcionais aceitos como null

---

## 🎯 Exemplos de Uso

### Rodar testes e ver output detalhado:
```bash
./mvnw test -X
```

### Rodar testes sem compilar novamente:
```bash
./mvnw surefire:test
```

### Limpar e rodar todos os testes:
```bash
./mvnw clean test
```

### Gerar relatório completo (testes + cobertura):
```bash
./mvnw clean verify
```

---

## 📚 Tecnologias de Teste Utilizadas

| Tecnologia | Versão | Uso |
|------------|--------|-----|
| **JUnit 5** | 5.10+ | Framework de testes |
| **Mockito** | 5.7+ | Mocks para testes unitários |
| **AssertJ** | 3.24+ | Assertions fluentes |
| **MockMvc** | 3.5.7 | Testes de controllers |
| **@DataJpaTest** | 3.5.7 | Testes de repositories |
| **Jacoco** | 0.8.11 | Cobertura de código |
| **H2 Database** | 2.2+ | Banco em memória para testes |

---

## 🛠️ Configuração do Jacoco

O Jacoco está configurado no `pom.xml` para:

1. **Preparar o agente** antes dos testes
2. **Gerar relatório** após os testes
3. **Verificar cobertura mínima** de 80% nos pacotes:
    - service
    - controller
    - repository

### Excluir classes da cobertura:

As seguintes classes são excluídas automaticamente:
- Classes de configuração (`config/**`)
- DTOs (`dto/**`)
- Modelos (`model/**`)
- Classe principal (`BiotrackApplication.class`)

---

## 🐛 Troubleshooting

### Problema: Testes falhando com erro de conexão ao banco

**Solução:** Certifique-se de que está usando o perfil de teste:
```bash
./mvnw test -Dspring.profiles.active=test
```

### Problema: Cobertura abaixo de 80%

**Solução:** Execute os testes e veja o relatório:
```bash
./mvnw clean test jacoco:report
```
Abra `target/site/jacoco/index.html` e veja quais linhas não estão cobertas.

### Problema: Teste específico falhando

**Solução:** Execute apenas esse teste com mais detalhes:
```bash
./mvnw test -Dtest=NomeDoTeste -X
```

### Problema: Testes lentos

**Solução:** Execute em paralelo:
```bash
./mvnw test -DforkCount=4
```

---

## ✅ Checklist de Qualidade

- [x] Todos os testes passam
- [x] Cobertura > 80% em service
- [x] Cobertura > 80% em controller
- [x] Cobertura > 80% em repository
- [x] Testes de integração com banco
- [x] Testes unitários com mocks
- [x] Validação de parâmetros obrigatórios
- [x] Tratamento de exceções testado
- [x] Casos de sucesso testados
- [x] Casos de erro testados

---

## 📝 Convenções de Nomenclatura

### Métodos de Teste:
```java
@Test
@DisplayName("Deve [ação esperada] quando [condição]")
void deve[Ação][Condição]() {
    // Given - Arrange
    // When - Act
    // Then - Assert
}
```

### Exemplo:
```java
@Test
@DisplayName("Deve criar usuário com sucesso")
void deveCriarUsuarioComSucesso() {
    // Given
    UserRequest request = new UserRequest(...);
    
    // When
    userService.createUser(request);
    
    // Then
    verify(userRepository, times(1)).save(any(User.class));
}
```

---

## 🎓 Boas Práticas Aplicadas

1. **AAA Pattern:** Arrange, Act, Assert
2. **Given-When-Then:** Clareza no cenário de teste
3. **@DisplayName:** Descrições legíveis em português
4. **Isolamento:** Cada teste é independente
5. **Mocks:** Dependências externas mockadas nos testes unitários
6. **Integration Tests:** Testes de repository usam banco real (H2)
7. **Fast Tests:** Testes rápidos e confiáveis
8. **Meaningful Names:** Nomes descritivos para testes

---

**Desenvolvido com 🧪 e ✅ - 2025**