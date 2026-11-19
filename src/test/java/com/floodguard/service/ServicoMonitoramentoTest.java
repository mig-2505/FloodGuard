package com.floodguard.service;

import com.floodguard.model.Alerta;
import com.floodguard.model.Medicao;
import com.floodguard.model.NivelAlerta;
import com.floodguard.model.ResultadoPrevisao;
import com.floodguard.model.TipoSensor;
import com.floodguard.prediction.PrevisaoMediaMovel;
import com.floodguard.prediction.TipoPrevisao;
import com.floodguard.repository.AlertaRepository;
import com.floodguard.repository.MedicaoRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ServicoMonitoramentoTest {

    @Mock
    private MedicaoRepository medicaoRepository;

    @Mock
    private AlertaRepository alertaRepository;

    @Mock
    private FabricaPrevisao fabricaPrevisao;

    @Mock
    private ConfiguracaoSistema configuracaoSistema;

    private ServicoMonitoramento servico;

    @BeforeEach
    void preparar() {

        servico = new ServicoMonitoramento(
                medicaoRepository,
                alertaRepository,
                fabricaPrevisao,
                configuracaoSistema
        );
    }

    @Test
    void deveGerarAlertaAutomaticamente() {

        // ARRANGE - Preparar os dados

        List<Medicao> historico = List.of(
                new Medicao(TipoSensor.NIVEL_AGUA, 90.0),
                new Medicao(TipoSensor.NIVEL_AGUA, 85.0),
                new Medicao(TipoSensor.NIVEL_AGUA, 80.0),
                new Medicao(TipoSensor.NIVEL_AGUA, 75.0),
                new Medicao(TipoSensor.NIVEL_AGUA, 70.0)
        );

        when(medicaoRepository
                .findTop10ByTipoSensorOrderByIdDesc(
                        TipoSensor.NIVEL_AGUA
                )).thenReturn(historico);

        when(fabricaPrevisao.criarEstrategia(
                TipoPrevisao.MEDIA_MOVEL
        )).thenReturn(new PrevisaoMediaMovel());

        when(configuracaoSistema.classificarRisco(
                TipoSensor.NIVEL_AGUA, 80.0
        )).thenReturn(NivelAlerta.ALERTA);

        when(configuracaoSistema.consultarLimite(
                TipoSensor.NIVEL_AGUA,
                NivelAlerta.ALERTA
        )).thenReturn(70.0);

        // ACT - Executar a previsao

        ResultadoPrevisao resultado =
                servico.executarPrevisao(
                        TipoSensor.NIVEL_AGUA,
                        TipoPrevisao.MEDIA_MOVEL
                );

        // ASSERT - Verificar o resultado

        assertEquals(80.0, resultado.valorPrevisto());
        assertEquals(
                NivelAlerta.ALERTA,
                resultado.nivelAlerta()
        );

        verify(alertaRepository, times(1))
                .save(any(Alerta.class));
    }

    @Test
    void naoDeveGerarAlertaSemHistoricoSuficiente() {

        when(medicaoRepository
                .findTop10ByTipoSensorOrderByIdDesc(
                        TipoSensor.NIVEL_AGUA
                )).thenReturn(List.of());

        when(fabricaPrevisao.criarEstrategia(
                TipoPrevisao.MEDIA_MOVEL
        )).thenReturn(new PrevisaoMediaMovel());

        assertThrows(
                com.floodguard.exception.DadosInvalidosException.class,
                () -> servico.executarPrevisao(
                        TipoSensor.NIVEL_AGUA,
                        TipoPrevisao.MEDIA_MOVEL
                )
        );

        verify(
                alertaRepository,
                never()
        ).save(any(Alerta.class));
    }
}
