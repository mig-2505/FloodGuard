package com.floodguard.service;

import com.floodguard.exception.DadosInvalidosException;
import com.floodguard.model.Alerta;
import com.floodguard.model.Medicao;
import com.floodguard.model.NivelAlerta;
import com.floodguard.model.ResultadoPrevisao;
import com.floodguard.model.TipoSensor;
import com.floodguard.prediction.EstrategiaPrevisao;
import com.floodguard.prediction.TipoPrevisao;
import com.floodguard.repository.AlertaRepository;
import com.floodguard.repository.MedicaoRepository;
import com.floodguard.sensor.Sensor;
import com.floodguard.sensor.SensorNivelAgua;
import com.floodguard.sensor.SensorPluviometrico;
import com.floodguard.sensor.SensorUmidadeSolo;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class ServicoMonitoramento {

    private final MedicaoRepository medicaoRepository;
    private final AlertaRepository alertaRepository;
    private final FabricaPrevisao fabricaPrevisao;
    private final ConfiguracaoSistema configuracaoSistema;

    private final List<Sensor> sensores;

    public ServicoMonitoramento(
            MedicaoRepository medicaoRepository,
            AlertaRepository alertaRepository,
            FabricaPrevisao fabricaPrevisao,
            ConfiguracaoSistema configuracaoSistema
    ) {
        this.medicaoRepository = medicaoRepository;
        this.alertaRepository = alertaRepository;
        this.fabricaPrevisao = fabricaPrevisao;
        this.configuracaoSistema = configuracaoSistema;

        this.sensores = List.of(
                new SensorPluviometrico(),
                new SensorNivelAgua(),
                new SensorUmidadeSolo()
        );
    }

    // VERIFICACAO DOS SENSORES

    public List<String> verificarSensores() {

        List<String> resultado = new ArrayList<>();

        for (Sensor sensor : sensores) {

            String status = sensor.verificarFuncionamento()
                    ? "FUNCIONANDO"
                    : "INDISPONIVEL";

            resultado.add(
                    sensor.getTipo() + " - " + status
            );
        }

        return resultado;
    }

    // COLETA DAS MEDICOES

    public List<Medicao> realizarMedicoes() {

        List<Medicao> medicoes = new ArrayList<>();

        for (Sensor sensor : sensores) {

            double valor = sensor.lerValor();

            configuracaoSistema.validarValor(
                    sensor.getTipo(),
                    valor
            );

            Medicao medicao = new Medicao(
                    sensor.getTipo(),
                    valor
            );

            Medicao salva =
                    medicaoRepository.save(medicao);

            medicoes.add(salva);
        }

        return medicoes;
    }

    // PREVISOES E ALERTAS

    public ResultadoPrevisao executarPrevisao(
            TipoSensor tipoSensor,
            TipoPrevisao tipoPrevisao
    ) {

        if (tipoSensor == null || tipoPrevisao == null) {
            throw new DadosInvalidosException(
                    "Sensor e estrategia sao obrigatorios."
            );
        }

        // Buscar as ultimas dez medicoes no banco
        List<Medicao> historico =
                medicaoRepository
                        .findTop10ByTipoSensorOrderByIdDesc(
                                tipoSensor
                        );

        // Ordenar do mais antigo para o mais recente
        List<Medicao> ordenado =
                new ArrayList<>(historico);

        Collections.reverse(ordenado);

        // Transformar as medicoes em valores numericos
        List<Double> valores = ordenado.stream()
                .map(Medicao::getValor)
                .toList();

        // Criar a estrategia pelo padrao Factory
        EstrategiaPrevisao estrategia =
                fabricaPrevisao.criarEstrategia(
                        tipoPrevisao
                );

        // Calcular a previsao
        double valorPrevisto =
                estrategia.prever(valores);

        // Nao permitir valores negativos ou infinitos
        if (!Double.isFinite(valorPrevisto)
                || valorPrevisto < 0) {
            throw new DadosInvalidosException(
                    "A previsao produziu um valor invalido."
            );
        }

        // Respeitar a faixa do indicador monitorado
        double maximo = switch (tipoSensor) {
            case PLUVIOMETRICO -> 120.0;
            case NIVEL_AGUA -> 150.0;
            case UMIDADE_SOLO -> 100.0;
        };

        valorPrevisto = Math.min(valorPrevisto, maximo);

        // Identificar o nivel de risco
        NivelAlerta nivel =
                configuracaoSistema.classificarRisco(
                        tipoSensor,
                        valorPrevisto
                );

        String justificativa;

        if (nivel == NivelAlerta.NORMAL) {

            justificativa =
                    "O valor previsto esta abaixo "
                            + "dos limites de atencao.";

        } else {

            double limite =
                    configuracaoSistema.consultarLimite(
                            tipoSensor,
                            nivel
                    );

            justificativa = String.format(
                    java.util.Locale.US,
                    "Sensor %s: valor previsto %.2f "
                            + "atingiu o limite %.2f do nivel %s.",
                    tipoSensor,
                    valorPrevisto,
                    limite,
                    nivel
            );

            // Registrar o alerta no banco
            Alerta alerta = new Alerta(
                    tipoSensor,
                    nivel,
                    valorPrevisto,
                    limite,
                    justificativa
            );

            alertaRepository.save(alerta);
        }

        return new ResultadoPrevisao(
                tipoSensor,
                tipoPrevisao,
                valorPrevisto,
                nivel,
                justificativa
        );
    }

    // CONSULTA DE HISTORICOS

    public List<Medicao> consultarMedicoes() {

        return medicaoRepository
                .findAllByOrderByIdDesc();
    }

    public List<Medicao> consultarMedicoesPorSensor(
            TipoSensor tipoSensor
    ) {

        return medicaoRepository
                .findByTipoSensorOrderByIdDesc(
                        tipoSensor
                );
    }

    public List<Alerta> consultarAlertas() {

        return alertaRepository
                .findAllByOrderByIdDesc();
    }

    // INFORMACOES GERAIS

    public long totalMedicoes() {
        return medicaoRepository.count();
    }

    public long totalAlertas() {
        return alertaRepository.count();
    }
}
