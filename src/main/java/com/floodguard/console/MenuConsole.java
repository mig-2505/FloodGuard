package com.floodguard.console;

import com.floodguard.exception.DadosInvalidosException;
import com.floodguard.exception.FonteIndisponivelException;

import com.floodguard.model.Alerta;
import com.floodguard.model.Medicao;
import com.floodguard.model.NivelAlerta;
import com.floodguard.model.ResultadoPrevisao;
import com.floodguard.model.TipoSensor;

import com.floodguard.prediction.TipoPrevisao;

import com.floodguard.service.ConfiguracaoSistema;
import com.floodguard.service.ServicoMonitoramento;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

@Component
@ConditionalOnProperty(
        name = "floodguard.console.enabled",
        havingValue = "true",
        matchIfMissing = true
)
public class MenuConsole implements CommandLineRunner {

    private final ServicoMonitoramento servico;
    private final ConfiguracaoSistema configuracaoSistema;

    private final Scanner scanner = new Scanner(System.in);

    private final DateTimeFormatter formatoData =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    public MenuConsole(
            ServicoMonitoramento servico,
            ConfiguracaoSistema configuracaoSistema
    ) {
        this.servico = servico;
        this.configuracaoSistema = configuracaoSistema;
    }

    @Override
    public void run(String... args) {

        boolean executando = true;

        System.out.println();
        System.out.println("Bem-vindo ao FloodGuard!");

        while (executando) {

            try {
                exibirMenu();

                int opcao = lerInteiro("Escolha uma opcao: ");

                switch (opcao) {
                    case 1 -> verificarSensores();
                    case 2 -> realizarMedicoes();
                    case 3 -> executarPrevisao();
                    case 4 -> consultarHistoricoMedicoes();
                    case 5 -> consultarHistoricoAlertas();
                    case 6 -> gerenciarTerritorio();
                    case 7 -> visualizarStatusCompleto();

                    case 0 -> {
                        System.out.println(
                                "\nEncerrando o FloodGuard..."
                        );
                        executando = false;
                    }

                    default -> System.out.println(
                            "\nOpcao invalida. Tente novamente."
                    );
                }

            } catch (DadosInvalidosException |
                     FonteIndisponivelException erro) {

                System.out.println(
                        "\nERRO: " + erro.getMessage()
                );

            } catch (NumberFormatException erro) {

                System.out.println(
                        "\nERRO: Digite um numero valido."
                );

            } catch (DataAccessException erro) {

                System.out.println(
                        "\nERRO: Falha na comunicacao com o banco."
                );

                System.err.println(
                        "Detalhes tecnicos: " + erro.getMessage()
                );

            } catch (java.util.NoSuchElementException erro) {

                System.out.println(
                        "\nEntrada encerrada. Finalizando..."
                );
                executando = false;
            }
        }
    }

    // ========================================
    // MENU PRINCIPAL
    // ========================================

    private void exibirMenu() {

        System.out.println();
        System.out.println("==========================================");
        System.out.println("       FLOODGUARD - MONITORAMENTO");
        System.out.println("==========================================");

        System.out.println("1 - Verificar sensores");
        System.out.println("2 - Realizar novas medicoes");
        System.out.println("3 - Executar previsoes");
        System.out.println("4 - Consultar historico de medicoes");
        System.out.println("5 - Consultar historico de alertas");
        System.out.println("6 - Gerenciar dados do territorio");
        System.out.println("7 - Visualizar status completo");
        System.out.println("0 - Encerrar aplicacao");

        System.out.println("==========================================");
    }

    // ========================================
    // OPCAO 1 - VERIFICAR SENSORES
    // ========================================

    private void verificarSensores() {

        System.out.println("\n=== STATUS DOS SENSORES ===");

        List<String> sensores = servico.verificarSensores();

        for (String sensor : sensores) {
            System.out.println(sensor);
        }
    }

    // ========================================
    // OPCAO 2 - REALIZAR MEDICOES
    // ========================================

    private void realizarMedicoes() {

        System.out.println("\n=== NOVAS MEDICOES ===");

        List<Medicao> medicoes = servico.realizarMedicoes();

        for (Medicao medicao : medicoes) {

            System.out.printf(
                    Locale.forLanguageTag("pt-BR"),
                    "%s: %.2f %s%n",
                    medicao.getTipoSensor(),
                    medicao.getValor(),
                    unidade(medicao.getTipoSensor())
            );
        }

        System.out.println(
                "\nTotal de medicoes nesta rodada: "
                        + medicoes.size()
        );

        System.out.println(
                "Medicoes armazenadas no banco H2."
        );
    }

    // ========================================
    // OPCAO 3 - EXECUTAR PREVISOES
    // ========================================

    private void executarPrevisao() {

        System.out.println("\n=== EXECUTAR PREVISAO ===");

        TipoSensor sensor = selecionarSensor();

        if (sensor == null) {
            return;
        }

        System.out.println("\nEscolha a estrategia:");

        System.out.println("1 - Media movel");
        System.out.println("2 - Comparacao historica");

        int opcao = lerInteiro("Estrategia: ");

        TipoPrevisao tipoPrevisao;

        switch (opcao) {
            case 1 -> tipoPrevisao = TipoPrevisao.MEDIA_MOVEL;
            case 2 -> tipoPrevisao = TipoPrevisao.COMPARACAO;
            default -> {
                System.out.println("Estrategia invalida.");
                return;
            }
        }

        ResultadoPrevisao resultado =
                servico.executarPrevisao(
                        sensor,
                        tipoPrevisao
                );

        System.out.println("\n=== RESULTADO ===");

        System.out.println(
                "Sensor: " + resultado.tipoSensor()
        );

        System.out.println(
                "Estrategia: " + resultado.tipoPrevisao()
        );

        System.out.printf(
                Locale.forLanguageTag("pt-BR"),
                "Valor previsto: %.2f %s%n",
                resultado.valorPrevisto(),
                unidade(sensor)
        );

        System.out.println(
                "Nivel: " + resultado.nivelAlerta()
        );

        System.out.println(
                "Justificativa: " + resultado.justificativa()
        );

        if (resultado.nivelAlerta() != NivelAlerta.NORMAL) {

            System.out.println(
                    "\nAlerta registrado automaticamente no H2."
            );

        } else {

            System.out.println(
                    "\nNenhum alerta foi necessario."
            );
        }

        System.out.println(
                "Aviso: previsao simulada para fins academicos."
        );
    }

    // ========================================
    // OPCAO 4 - HISTORICO DE MEDICOES
    // ========================================

    private void consultarHistoricoMedicoes() {

        System.out.println("\n=== HISTORICO DE MEDICOES ===");

        System.out.println("0 - Todos os sensores");
        System.out.println("1 - Pluviometrico");
        System.out.println("2 - Nivel da agua");
        System.out.println("3 - Umidade do solo");

        int opcao = lerInteiro("Escolha: ");

        List<Medicao> medicoes;

        if (opcao == 0) {

            medicoes = servico.consultarMedicoes();

        } else {

            TipoSensor tipo = converterSensor(opcao);

            if (tipo == null) {
                System.out.println("Sensor invalido.");
                return;
            }

            medicoes = servico.consultarMedicoesPorSensor(tipo);
        }

        if (medicoes.isEmpty()) {
            System.out.println("Nenhuma medicao encontrada.");
            return;
        }

        for (Medicao medicao : medicoes) {

            System.out.printf(
                    Locale.forLanguageTag("pt-BR"),
                    "ID: %d | %s | %.2f %s | %s%n",
                    medicao.getId(),
                    medicao.getTipoSensor(),
                    medicao.getValor(),
                    unidade(medicao.getTipoSensor()),
                    medicao.getDataHora().format(formatoData)
            );
        }

        System.out.println(
                "\nRegistros encontrados: " + medicoes.size()
        );
    }

    // ========================================
    // OPCAO 5 - HISTORICO DE ALERTAS
    // ========================================

    private void consultarHistoricoAlertas() {

        System.out.println("\n=== HISTORICO DE ALERTAS ===");

        List<Alerta> alertas = servico.consultarAlertas();

        if (alertas.isEmpty()) {
            System.out.println("Nenhum alerta registrado.");
            return;
        }

        for (Alerta alerta : alertas) {

            System.out.println("--------------------------------");

            System.out.println("ID: " + alerta.getId());

            System.out.println(
                    "Sensor: " + alerta.getTipoSensor()
            );

            System.out.println(
                    "Nivel: " + alerta.getNivel()
            );

            System.out.printf(
                    Locale.forLanguageTag("pt-BR"),
                    "Valor previsto: %.2f %s%n",
                    alerta.getValorPrevisto(),
                    unidade(alerta.getTipoSensor())
            );

            System.out.printf(
                    Locale.forLanguageTag("pt-BR"),
                    "Limite atingido: %.2f %s%n",
                    alerta.getLimiteAtingido(),
                    unidade(alerta.getTipoSensor())
            );

            System.out.println(
                    "Justificativa: " + alerta.getJustificativa()
            );

            System.out.println(
                    "Data: "
                            + alerta.getDataHora().format(formatoData)
            );
        }

        System.out.println(
                "\nTotal de alertas: " + alertas.size()
        );
    }

    // ========================================
    // OPCAO 6 - GERENCIAR TERRITORIO
    // ========================================

    private void gerenciarTerritorio() {

        System.out.println("\n=== DADOS DO TERRITORIO ===");

        System.out.println("1 - Consultar limites");
        System.out.println("2 - Alterar limite");
        System.out.println("0 - Voltar");

        int opcao = lerInteiro("Escolha: ");

        switch (opcao) {
            case 1 -> consultarLimites();
            case 2 -> alterarLimite();
            case 0 -> { }
            default -> System.out.println("Opcao invalida.");
        }
    }

    private void consultarLimites() {

        System.out.println("\n=== LIMITES CONFIGURADOS ===");

        for (TipoSensor tipo : TipoSensor.values()) {

            System.out.println("\nSensor: " + tipo);

            for (NivelAlerta nivel : List.of(
                    NivelAlerta.ATENCAO,
                    NivelAlerta.ALERTA,
                    NivelAlerta.CRITICO
            )) {

                double valor =
                        configuracaoSistema.consultarLimite(
                                tipo,
                                nivel
                        );

                System.out.printf(
                        Locale.forLanguageTag("pt-BR"),
                        "%s: %.2f %s%n",
                        nivel,
                        valor,
                        unidade(tipo)
                );
            }
        }

        System.out.println(
                "\nLimites ilustrativos para simulacao."
        );
    }

    private void alterarLimite() {

        System.out.println("\n=== ALTERAR LIMITE ===");

        TipoSensor tipo = selecionarSensor();

        if (tipo == null) {
            return;
        }

        System.out.println("\n1 - ATENCAO");
        System.out.println("2 - ALERTA");
        System.out.println("3 - CRITICO");

        int opcao = lerInteiro("Nivel: ");

        NivelAlerta nivel;

        switch (opcao) {
            case 1 -> nivel = NivelAlerta.ATENCAO;
            case 2 -> nivel = NivelAlerta.ALERTA;
            case 3 -> nivel = NivelAlerta.CRITICO;
            default -> {
                System.out.println("Nivel invalido.");
                return;
            }
        }

        double atual =
                configuracaoSistema.consultarLimite(tipo, nivel);

        System.out.printf(
                Locale.forLanguageTag("pt-BR"),
                "Limite atual: %.2f %s%n",
                atual,
                unidade(tipo)
        );

        double novoValor = lerDouble("Novo limite: ");

        System.out.print("Codigo de acesso: ");
        String codigo = scanner.nextLine().trim();

        configuracaoSistema.alterarLimite(
                tipo,
                nivel,
                novoValor,
                codigo
        );

        System.out.println(
                "\nLimite atualizado com sucesso!"
        );
    }

    // ========================================
    // OPCAO 7 - STATUS COMPLETO
    // ========================================

    private void visualizarStatusCompleto() {

        System.out.println("\n=== STATUS COMPLETO ===");

        System.out.println(
                "Sistema: FloodGuard"
        );

        System.out.println(
                "Regiao: Zona Leste (exemplo ficticio)"
        );

        System.out.println("\nSensores:");

        verificarSensores();

        System.out.println(
                "\nTotal de medicoes: " + servico.totalMedicoes()
        );

        System.out.println(
                "Total de alertas: " + servico.totalAlertas()
        );

        List<Medicao> medicoes = servico.consultarMedicoes();

        if (!medicoes.isEmpty()) {

            Medicao ultima = medicoes.get(0);

            System.out.printf(
                    Locale.forLanguageTag("pt-BR"),
                    "\nUltima medicao: %s - %.2f %s%n",
                    ultima.getTipoSensor(),
                    ultima.getValor(),
                    unidade(ultima.getTipoSensor())
            );
        }

        List<Alerta> alertas = servico.consultarAlertas();

        if (!alertas.isEmpty()) {

            Alerta ultimo = alertas.get(0);

            System.out.println(
                    "Ultimo alerta: "
                            + ultimo.getNivel()
                            + " - "
                            + ultimo.getTipoSensor()
            );
        }

        System.out.println(
                "\nMonitoramento demonstrativo, sem dados reais."
        );
    }

    // ========================================
    // METODOS AUXILIARES
    // ========================================

    private TipoSensor selecionarSensor() {

        System.out.println("\nEscolha o sensor:");

        System.out.println("1 - Pluviometrico");
        System.out.println("2 - Nivel da agua");
        System.out.println("3 - Umidade do solo");

        int opcao = lerInteiro("Sensor: ");

        TipoSensor tipo = converterSensor(opcao);

        if (tipo == null) {
            System.out.println("Sensor invalido.");
        }

        return tipo;
    }

    private TipoSensor converterSensor(int opcao) {

        return switch (opcao) {

            case 1 -> TipoSensor.PLUVIOMETRICO;

            case 2 -> TipoSensor.NIVEL_AGUA;

            case 3 -> TipoSensor.UMIDADE_SOLO;

            default -> null;
        };
    }

    private String unidade(TipoSensor tipo) {

        return switch (tipo) {

            case PLUVIOMETRICO -> "mm/h";

            case NIVEL_AGUA -> "cm";

            case UMIDADE_SOLO -> "%";
        };
    }

    private int lerInteiro(String mensagem) {

        System.out.print(mensagem);

        String entrada = scanner.nextLine().trim();

        return Integer.parseInt(entrada);
    }

    private double lerDouble(String mensagem) {

        System.out.print(mensagem);

        String entrada = scanner.nextLine()
                .trim()
                .replace(",", ".");

        return Double.parseDouble(entrada);
    }
}
