# FloodGuard — Sistema de Monitoramento e Prevenção de Alagamentos

**Global Solution 2º Semestre de 2026 — Programação Orientada a Objetos**

**Tema:** CitizenTech — Territory Predictive Insights

## 1. Sobre o Projeto

O **FloodGuard** é um sistema desenvolvido em Java com Spring Boot que tem como objetivo auxiliar no monitoramento de condições ambientais associadas ao risco de alagamentos em áreas urbanas.

A aplicação simula a coleta de dados de sensores ambientais, armazena medições em um banco de dados, realiza previsões simplificadas com base no histórico e gera alertas automáticos quando identifica valores acima dos limites configurados.

O sistema utiliza conceitos de Programação Orientada a Objetos, persistência com Spring Data JPA, tratamento de exceções, padrões de projeto e testes automatizados.

O FloodGuard é um protótipo acadêmico e não deve ser utilizado como sistema oficial de previsão de enchentes ou de emissão de alertas de emergência.

## 2. Problema e Público Beneficiado

### Problema identificado

Alagamentos urbanos podem causar transtornos à mobilidade, danos materiais e riscos à população. O acompanhamento de indicadores ambientais pode ajudar na identificação antecipada de situações que merecem atenção.

### Público beneficiado

O FloodGuard foi idealizado para apoiar:

- Moradores de regiões vulneráveis a alagamentos.
- Equipes da Defesa Civil.
- Gestores municipais responsáveis pelo monitoramento ambiental.
- Organizações comunitárias envolvidas na prevenção de riscos.

### Decisão apoiada pelo sistema

O FloodGuard fornece informações que podem auxiliar na decisão de intensificar o monitoramento de determinada região, investigar mudanças nas condições ambientais ou avaliar medidas preventivas.

Os alertas não substituem a avaliação técnica de profissionais nem determinam automaticamente a necessidade de evacuações ou outras intervenções.

## 3. Funcionalidades do Sistema

O sistema possui um menu interativo no console com as seguintes funcionalidades:

| Opção | Funcionalidade | Descrição |
|---|---|---|
| 1 | Verificar sensores | Consulta o estado de funcionamento dos sensores |
| 2 | Realizar novas medições | Gera leituras simuladas e armazena no banco |
| 3 | Executar previsões | Aplica média móvel ou comparação histórica |
| 4 | Histórico de medições | Consulta os dados registrados pelos sensores |
| 5 | Histórico de alertas | Consulta os alertas emitidos |
| 6 | Gerenciar território | Consulta e modifica os limites de risco |
| 7 | Status completo | Apresenta um resumo geral do sistema |
| 0 | Encerrar | Finaliza a aplicação |

### Sensores implementados

O FloodGuard utiliza três sensores simulados:

**Sensor Pluviométrico**

- Mede a intensidade simulada da precipitação.
- Unidade: milímetros por hora (mm/h).
- Faixa utilizada: 0 a 120 mm/h.

**Sensor de Nível da Água**

- Mede a altura simulada da água em um ponto de monitoramento.
- Unidade: centímetros (cm).
- Faixa utilizada: 0 a 150 cm.

**Sensor de Umidade do Solo**

- Mede a umidade relativa simulada do solo.
- Unidade: porcentagem (%).
- Faixa utilizada: 0% a 100%.

As leituras são geradas por `ThreadLocalRandom`, sem utilização de sensores físicos ou APIs externas.

## 4. Tecnologias Utilizadas

| Tecnologia | Utilização |
|---|---|
| Java 21 | Versão configurada para compilação |
| Spring Boot 4.1.2 | Framework principal |
| Spring Data JPA | Persistência e consultas |
| Hibernate | Implementação JPA |
| H2 Database | Banco de dados relacional |
| Maven | Gerenciamento de dependências e build |
| JUnit 5 | Testes automatizados |
| Mockito | Simulação de dependências nos testes |
| IntelliJ IDEA | Ambiente de desenvolvimento |
| Git e GitHub | Versionamento e disponibilização do código |

A aplicação utiliza um menu de console, sem necessidade de API REST ou interface gráfica.

## 5. Arquitetura e Organização

O projeto segue uma organização por responsabilidades:

```text
src/
├── main/
│   ├── java/com/floodguard/
│   │   ├── console/
│   │   ├── exception/
│   │   ├── model/
│   │   ├── prediction/
│   │   ├── repository/
│   │   ├── sensor/
│   │   ├── service/
│   │   └── FloodguardApplication.java
│   └── resources/
│       └── application.properties
└── test/
    ├── java/com/floodguard/
    │   ├── model/
    │   ├── repository/
    │   └── service/
    └── resources/
        └── application.properties
```

### Responsabilidade dos pacotes

- **model:** classes que representam os componentes territoriais, medições, alertas e limites.
- **sensor:** interface e implementações dos sensores.
- **prediction:** estratégias de previsão e tipos de previsão.
- **exception:** exceções personalizadas.
- **repository:** interfaces de persistência com Spring Data JPA.
- **service:** regras de negócio e coordenação do monitoramento.
- **console:** interação com o usuário pelo terminal.

Essa separação facilita a manutenção do código e evita concentrar todas as responsabilidades em uma única classe.

## 6. Conceitos de Programação Orientada a Objetos

### Abstração e herança

A classe abstrata `ComponenteTerritorial` representa características comuns de um componente territorial, incluindo identificação, nome, região e status.

A classe `RegiaoMonitorada` herda de `ComponenteTerritorial` e implementa o método abstrato `verificarStatus()`.

### Encapsulamento

A classe `DadosTerritorio` possui atributos privados e métodos para validar e modificar os limites de risco.

As alterações exigem um código de acesso e precisam respeitar regras de validação, como manter os limites em ordem crescente.

### Interfaces

A interface `Sensor` define os métodos:

- `lerValor()`
- `verificarFuncionamento()`
- `getTipo()`

As classes `SensorPluviometrico`, `SensorNivelAgua` e `SensorUmidadeSolo` implementam esse contrato.

### Polimorfismo

O polimorfismo é utilizado tanto nos sensores quanto nas previsões.

A classe abstrata `EstrategiaPrevisao` define o método `prever()`, implementado de maneiras diferentes por `PrevisaoMediaMovel` e `PrevisaoComparacao`.

Assim, o sistema pode trabalhar com diferentes comportamentos por meio de uma referência ao tipo abstrato.

## 7. Estratégias de Previsão

O FloodGuard implementa duas estratégias baseadas no histórico de medições armazenadas no banco de dados.

### 7.1. Média Móvel

A estratégia `PrevisaoMediaMovel` calcula a média aritmética das últimas cinco medições de determinado sensor.

Exemplo:

```text
Leituras: 40, 50, 60, 70 e 80 cm

Média = (40 + 50 + 60 + 70 + 80) / 5

Resultado = 60 cm
```

O resultado é utilizado como uma estimativa simplificada do indicador monitorado.

### 7.2. Comparação Histórica

A estratégia `PrevisaoComparacao` utiliza as dez últimas medições, divididas em dois grupos de cinco.

Primeiro, calcula a média do período anterior e a média do período recente.

Depois, calcula a diferença entre elas e projeta essa variação sobre a média recente, impedindo resultados negativos.

Exemplo:

```text
Média anterior: 40 cm
Média recente: 70 cm

Variação: 70 - 40 = 30 cm

Estimativa = 70 + 30 = 100 cm
```

Esses cálculos são modelos didáticos de tendência, não previsões hidrológicas científicas.

O sistema exige um histórico mínimo de cinco medições para a média móvel e dez para a comparação histórica.

## 8. Alertas Automáticos

Os valores previstos são comparados com limites definidos para cada tipo de sensor.

### Limites iniciais

| Sensor | ATENÇÃO | ALERTA | CRÍTICO |
|---|---:|---:|---:|
| Pluviométrico (mm/h) | 20 | 50 | 80 |
| Nível da água (cm) | 40 | 70 | 100 |
| Umidade do solo (%) | 60 | 80 | 95 |

Esses valores são ilustrativos e não representam limites oficiais de segurança.

### Funcionamento

O sistema classifica a previsão em:

- **NORMAL:** abaixo do limite de atenção.
- **ATENÇÃO:** atinge o primeiro limite.
- **ALERTA:** atinge o segundo limite.
- **CRÍTICO:** atinge o limite mais elevado.

Quando identifica um nível diferente de NORMAL, o FloodGuard cria um registro de alerta contendo o tipo de sensor, nível de risco, valor previsto, limite atingido, justificativa e data/hora.

Os alertas são armazenados no H2 e podem ser consultados pelo menu.

## 9. Padrão de Projeto Criacional — Factory

O padrão criacional escolhido foi **Factory**, implementado na classe `FabricaPrevisao`.

### Por que utilizar Factory?

O FloodGuard possui duas estratégias de previsão. Em vez de criar diretamente os objetos dessas estratégias em diferentes partes do código, a responsabilidade de criação foi centralizada em uma única classe.

A `FabricaPrevisao` recebe um `TipoPrevisao` e retorna a implementação correspondente de `EstrategiaPrevisao`.

### Benefícios

- Centralização da criação dos objetos.
- Redução do acoplamento com implementações concretas.
- Melhor organização da lógica de seleção.
- Facilidade para adicionar novas estratégias futuramente.

A Factory foi escolhida especificamente para a criação das estratégias de previsão, pois é nesse ponto que o sistema precisa selecionar entre diferentes implementações de um mesmo contrato abstrato.

## 10. Tratamento de Exceções

O projeto implementa duas exceções personalizadas.

### DadosInvalidosException

Utilizada quando uma informação não atende às regras de validação.

Exemplos:

- Medição negativa.
- Histórico insuficiente para uma previsão.
- Limite inválido.
- Código de acesso incorreto.
- Tipo de sensor não informado.

### FonteIndisponivelException

Utilizada quando uma fonte de dados, como um sensor, está indisponível.

O menu utiliza `try/catch` para apresentar mensagens compreensíveis ao usuário, evitando que erros previstos encerrem inesperadamente a aplicação.

## 11. Persistência com Spring Data JPA e H2

O FloodGuard utiliza Spring Data JPA e Hibernate para armazenar as informações no H2 Database.

### Entidades

**Medicao:** representa as leituras realizadas pelos sensores, armazenando identificador, tipo, valor e data/hora.

**Alerta:** representa os riscos identificados, armazenando identificador, sensor, nível de alerta, valor previsto, limite, justificativa e data/hora.

### Repositories

- `MedicaoRepository`: responsável pela gravação e consulta de medições.
- `AlertaRepository`: responsável pela gravação e consulta de alertas.

Ambos utilizam `JpaRepository` para disponibilizar operações de persistência sem a necessidade de escrever manualmente todos os comandos SQL.

### Armazenamento em arquivo

O H2 utiliza a configuração:

```properties
spring.datasource.url=jdbc:h2:file:./data/floodguard
```

Dessa maneira, os dados são armazenados em arquivo e permanecem disponíveis após reiniciar a aplicação, desde que seja utilizado o mesmo diretório de trabalho.

O arquivo local do banco é gerado automaticamente e não precisa ser incluído no repositório GitHub.

## 12. Como Executar o Projeto

### Pré-requisitos

- JDK 21 ou versão compatível com a aplicação.
- Terminal do Windows, PowerShell ou terminal integrado do IntelliJ.
- Git, para clonar o repositório.

O Maven Wrapper já está incluído no projeto, evitando a necessidade de instalar o Maven manualmente.

### 12.1. Clonar o repositório

```bash
git clone https://github.com/mig-2505/FloodGuard
cd FloodGuard
```

### 12.2. Configuração do banco

O arquivo `src/main/resources/application.properties` contém a configuração do H2 em arquivo.

As propriedades essenciais são:

```properties
spring.main.web-application-type=none

spring.datasource.url=jdbc:h2:file:./data/floodguard
spring.datasource.driver-class-name=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=

spring.jpa.hibernate.ddl-auto=update

spring.jpa.show-sql=false

floodguard.console.enabled=true
floodguard.codigo-acesso=${FLOODGUARD_CODIGO:1234}
```

O H2 cria automaticamente o banco e o Hibernate prepara as tabelas mapeadas pelas entidades.

Para utilização acadêmica local, o código de acesso padrão para alteração dos limites é `1234`, desde que a variável de ambiente `FLOODGUARD_CODIGO` não tenha sido definida com outro valor.

Esse código é apenas demonstrativo e não constitui um sistema seguro de autenticação.

### 12.3. Executar no Windows

Na raiz do projeto, execute:

```powershell
.\mvnw.cmd spring-boot:run
```

Também é possível executar a classe principal `FloodguardApplication.java` pelo IntelliJ IDEA.

### 12.4. Utilizar o menu

Após a inicialização, o programa exibirá:

```text
==========================================
       FLOODGUARD - MONITORAMENTO
==========================================

1 - Verificar sensores
2 - Realizar novas medicoes
3 - Executar previsoes
4 - Consultar historico de medicoes
5 - Consultar historico de alertas
6 - Gerenciar dados do territorio
7 - Visualizar status completo
0 - Encerrar aplicacao
```

Para criar medições, selecione a opção 2.

Para executar previsões, selecione a opção 3 e informe o sensor e a estratégia desejados.

Para consultar alertas, utilize a opção 5.

Os históricos permanecem disponíveis após reiniciar o sistema.

## 13. Testes Automatizados

O FloodGuard utiliza JUnit e Mockito para verificar regras de negócio, cálculos, exceções e persistência.

### Tipos de testes

- Testes das classes de domínio e conceitos de POO.
- Testes das duas estratégias de previsão.
- Testes de exceções personalizadas com `assertThrows`.
- Testes de persistência com Spring Data JPA.
- Testes de serviços com Mockito, simulando Repositories.
- Testes de geração e não geração de alertas.
- Teste de inicialização do contexto Spring Boot.

Os testes seguem o padrão AAA (*Arrange, Act, Assert*): preparação, execução e verificação.

### Executar os testes

No Windows:

```powershell
.\mvnw.cmd clean test
```

Os testes que inicializam o Spring utilizam configuração específica em `src/test/resources/application.properties`, desativando o menu interativo e utilizando um banco H2 de testes.

### Resultados obtidos

Na validação realizada em 19/11/2026:

```text
Tests run: 22
Failures: 0
Errors: 0
Skipped: 0

BUILD SUCCESS
```

Além dos testes automatizados, foram realizados testes manuais das funcionalidades do menu, incluindo geração de medições, previsões, consulta de históricos, alteração de limites, tratamento de entradas inválidas e verificação de persistência após reiniciar.

## 14. Reflexão Crítica

O FloodGuard foi desenvolvido com a proposta de demonstrar como tecnologias de monitoramento e análise de dados podem apoiar a prevenção de problemas urbanos. Entretanto, a utilização responsável de sistemas preditivos exige reconhecer as limitações das informações disponíveis e dos métodos utilizados.

Um dos principais desafios está relacionado à quantidade de dados necessária para produzir estimativas minimamente fundamentadas. No protótipo, a média móvel exige pelo menos cinco medições, enquanto a comparação histórica exige dez. Quando esse histórico não está disponível, o sistema impede o cálculo e informa que os dados são insuficientes. Mesmo quando a quantidade mínima é atingida, isso não significa que a previsão seja confiável para decisões reais. A qualidade dos dados, a frequência de coleta e as condições ambientais também precisam ser consideradas.

Outro ponto importante é a representatividade dos sensores. Uma única leitura de nível da água ou de intensidade da chuva não descreve necessariamente toda a situação de uma região. Alagamentos podem ocorrer de maneira localizada, dependendo de fatores como relevo, drenagem urbana, impermeabilização do solo e distribuição das chuvas. Além disso, os sensores deste projeto são simulados, e seus valores não representam medições reais.

As estratégias de previsão implementadas são simplificações matemáticas e não utilizam modelos hidrológicos validados. Isso significa que os resultados podem apresentar diferenças significativas em relação a acontecimentos reais. Os limites de alerta também são ilustrativos, sendo inadequados para aplicação operacional sem estudos técnicos.

Outro aspecto é que a repetição de uma previsão pode produzir alertas semelhantes, já que o protótipo não possui um mecanismo de deduplicação. Os limites personalizados também permanecem apenas em memória durante a execução.

Por essas razões, o sistema deve atuar como uma ferramenta de apoio à análise, e não como responsável por tomar decisões de segurança automaticamente. Um alerta pode indicar a necessidade de investigar determinada condição, mas decisões que envolvam moradores e infraestrutura devem permanecer sob responsabilidade de profissionais e autoridades competentes.

## 15. Decisões Técnicas da Equipe

Durante o desenvolvimento, foram adotadas as seguintes decisões:

1. **Java e Spring Boot:** escolhidos para estruturar a aplicação e organizar seus componentes.
2. **Menu de console:** utilizado por atender aos requisitos da atividade sem adicionar a complexidade de uma interface gráfica.
3. **Spring Data JPA:** escolhido para simplificar a persistência e a implementação dos Repositories.
4. **H2 em arquivo:** escolhido pela configuração simples e pela capacidade de preservar o histórico após reiniciar.
5. **Sensores simulados:** utilizados para permitir testes sem equipamentos físicos ou APIs externas.
6. **Duas estratégias de previsão:** implementadas para demonstrar herança e polimorfismo.
7. **Factory:** escolhida para centralizar a criação das estratégias.
8. **JUnit e Mockito:** utilizados para verificar a lógica de negócio e isolar dependências durante os testes.
9. **Separação por pacotes:** adotada para organizar as responsabilidades e facilitar a manutenção.

## 16. Divisão de Responsabilidades

**Integrante 1 — Miguel Vanucci Delgado RM:563491**

Responsabilidades: Responsável pela estrutura do projeto, implementação das classes de POO, sensores, exceções, banco de dados e persistência com JPA.

**Integrante 2 — João Vitor Lima Caldeira RM:566541**

Responsabilidades: Responsável pelas estratégias de previsão, regras de negócio, alertas automáticos, menu interativo e testes com JUnit e Mockito.

**Ambos**: Participaram do planejamento, integração, documentação e validação final do projeto.

## 17. Uso de Inteligência Artificial

Ferramentas de inteligência artificial, incluindo o ChatGPT, foram utilizadas como apoio ao desenvolvimento do projeto.

O suporte incluiu explicações sobre a orientação na organização da arquitetura, elaboração e revisão de exemplos de código, configuração de Spring Boot e JPA, interpretação de erros e planejamento de testes.
