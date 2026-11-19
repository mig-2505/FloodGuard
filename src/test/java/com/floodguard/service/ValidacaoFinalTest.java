package com.floodguard.service;

import com.floodguard.exception.DadosInvalidosException;
import com.floodguard.exception.FonteIndisponivelException;

import com.floodguard.model.Alerta;
import com.floodguard.model.DadosTerritorio;
import com.floodguard.model.Medicao;
import com.floodguard.model.NivelAlerta;
import com.floodguard.model.ResultadoPrevisao;
import com.floodguard.model.TipoSensor;

import com.floodguard.prediction.PrevisaoComparacao;
import com.floodguard.prediction.PrevisaoMediaMovel;
import com.floodguard.prediction.TipoPrevisao;

import com.floodguard.repository.AlertaRepository;
import com.floodguard.repository.MedicaoRepository;

import com.floodguard.sensor.SensorNivelAgua;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class ValidacaoFinalTest {

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
    void naoDeveRegistrarAlertaQuandoRiscoNormal() {

        // ARRANGE
        List<Medicao> historico = List.of(
                new Medicao(TipoSensor.NIVEL_AGUA, 10.0),
                new Medicao(TipoSensor.NIVEL_AGUA, 20.0),
                new Medicao(TipoSensor.NIVEL_AGUA, 15.0),
                new Medicao(TipoSensor.NIVEL_AGUA, 25.0),
                new Medicao(TipoSensor.NIVEL_AGUA, 30.0)
        );

        when(medicaoRepository
                .findTop10ByTipoSensorOrderByIdDesc(
                        TipoSensor.NIVEL_AGUA
                )).thenReturn(historico);

        when(fabricaPrevisao.criarEstrategia(
                TipoPrevisao.MEDIA_MOVEL
        )).thenReturn(new PrevisaoMediaMovel());

        when(configuracaoSistema.classificarRisco(
                TipoSensor.NIVEL_AGUA,
                20.0
        )).thenReturn(NivelAlerta.NORMAL);

        // ACT
        ResultadoPrevisao resultado =
                servico.executarPrevisao(
                        TipoSensor.NIVEL_AGUA,
                        TipoPrevisao.MEDIA_MOVEL
                );

        // ASSERT
        assertEquals(20.0, resultado.valorPrevisto(), 0.001);
        assertEquals(
                NivelAlerta.NORMAL,
                resultado.nivelAlerta()
        );

        verify(alertaRepository, never())
                .save(any(Alerta.class));
    }

    @Test
    void deveRegistrarAlertaComInformacoesCorretas() {

        // ARRANGE
        List<Medicao> historico = List.of(
                new Medicao(TipoSensor.NIVEL_AGUA, 80.0),
                new Medicao(TipoSensor.NIVEL_AGUA, 80.0),
                new Medicao(TipoSensor.NIVEL_AGUA, 80.0),
                new Medicao(TipoSensor.NIVEL_AGUA, 80.0),
                new Medicao(TipoSensor.NIVEL_AGUA, 80.0)
        );

        when(medicaoRepository
                .findTop10ByTipoSensorOrderByIdDesc(
                        TipoSensor.NIVEL_AGUA
                )).thenReturn(historico);

        when(fabricaPrevisao.criarEstrategia(
                TipoPrevisao.MEDIA_MOVEL
        )).thenReturn(new PrevisaoMediaMovel());

        when(configuracaoSistema.classificarRisco(
                TipoSensor.NIVEL_AGUA,
                80.0
        )).thenReturn(NivelAlerta.ALERTA);

        when(configuracaoSistema.consultarLimite(
                TipoSensor.NIVEL_AGUA,
                NivelAlerta.ALERTA
        )).thenReturn(70.0);

        // ACT
        servico.executarPrevisao(
                TipoSensor.NIVEL_AGUA,
                TipoPrevisao.MEDIA_MOVEL
        );

        // ASSERT
        ArgumentCaptor<Alerta> captor =
                ArgumentCaptor.forClass(Alerta.class);

        verify(alertaRepository).save(captor.capture());

        Alerta alerta = captor.getValue();

        assertEquals(
                TipoSensor.NIVEL_AGUA,
                alerta.getTipoSensor()
        );

        assertEquals(NivelAlerta.ALERTA, alerta.getNivel());
        assertEquals(80.0, alerta.getValorPrevisto());
        assertEquals(70.0, alerta.getLimiteAtingido());
        assertFalse(alerta.getJustificativa().isBlank());
    }

    @Test
    void deveRejeitarPrevisaoSemSensor() {

        assertThrows(
                DadosInvalidosException.class,
                () -> servico.executarPrevisao(
                        null,
                        TipoPrevisao.MEDIA_MOVEL
                )
        );

        verifyNoInteractions(
                medicaoRepository,
                alertaRepository
        );
    }

    @Test
    void deveRejeitarValorAcimaDoPermitido() {

        DadosTerritorio dados =
                new DadosTerritorio("1234");

        assertThrows(
                DadosInvalidosException.class,
                () -> dados.validarValor(
                        TipoSensor.UMIDADE_SOLO,
                        110.0
                )
        );
    }

    @Test
    void factoryDeveCriarEstrategiasCorretas() {

        FabricaPrevisao fabrica = new FabricaPrevisao();

        assertInstanceOf(
                PrevisaoMediaMovel.class,
                fabrica.criarEstrategia(
                        TipoPrevisao.MEDIA_MOVEL
                )
        );

        assertInstanceOf(
                PrevisaoComparacao.class,
                fabrica.criarEstrategia(
                        TipoPrevisao.COMPARACAO
                )
        );
    }

    @Test
    void sensorDesligadoDeveLancarExcecao() {

        SensorNivelAgua sensor = new SensorNivelAgua();

        sensor.setFuncionando(false);

        assertThrows(
                FonteIndisponivelException.class,
                sensor::lerValor
        );
    }
}
